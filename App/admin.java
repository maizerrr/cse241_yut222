/**
 * CSE241 Final Project
 * Author: Yuming Tian
 */

import java.util.Scanner;
import java.util.Date;
import java.util.ArrayList;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;

public class admin {
    public static void main(String argsp[]) {
        Database db = null;

        Scanner s = new Scanner(System.in);
        String usr = "";
        String pwd = "";

        boolean quitApp = false;

        // connect to database
        while (db == null) {
            System.out.print("enter Oracle user id: ");
            usr = s.nextLine();
            System.out.print("enter Oracle password for " + usr + ": ");
            pwd = s.nextLine();
            db = Database.getDatabase("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241", usr, pwd);
        }

        // if not quit, keep looping
        while (!quitApp) {
            String actions = "123456q?";
            System.out.print("[" + actions + "] :> ");
            String action = s.nextLine();
            if (action.equals("1")) {
                // Manage user groups
                boolean goBack = false;
                while (!goBack) {
                    actions = "12q?";
                    System.out.print("[" + actions + "] :> ");
                    action = s.nextLine();
                    if (action.equals("1")) {
                        // create a new group
                        String discount_code = "";
                        String group_name = "";
                        double discount_rate = -1;
                        ArrayList<ArrayList<String>> allGroups = db.selectAllGroups();
                        ArrayList<String> existedCode = new ArrayList<String>();
                        for (ArrayList<String> rd:allGroups) {
                            existedCode.add(rd.get(1));
                        }
                        while (discount_code.length() != 8) {
                            System.out.print("Please enter a unique discount code (8 char) for the new group: ");
                            String d_code = s.nextLine();
                            if (d_code.length() != 8) {
                                System.out.println("Please enter a 8-char code");
                            } else if (existedCode.contains(d_code)) {
                                System.out.println("Discount code '" + d_code + "' already exists, please re-enter");
                            } else {
                                discount_code = d_code;
                            }
                        }
                        while (group_name.length() == 0) {
                            System.out.print("Please enter a group name for the new group: ");
                            group_name = s.nextLine();
                        }
                        while (discount_rate < 0 || discount_rate > 1) {
                            System.out.print("Please enter the discount_rate for the new group (float between 0 to 1): ");
                            try {
                                discount_rate = Double.parseDouble(s.nextLine());
                            } catch (NumberFormatException e) {
                                System.err.println("Cannot parse input, please re-enter");
                                discount_rate = -1;
                            }
                        }

                        System.out.printf("\n%-14s %-15s %s\n", "DISCOUNT_CODE", "GROUP_NAME", "DISCOUNT_RATE");
                        System.out.println("--------------------------------------------");
                        System.out.printf("%-14s %-15s %s\n\n", discount_code, group_name, discount_rate);
                        System.out.print("Is this ok? (yes/no): ");
                        action = s.nextLine();
                        if (action.equals("y") || action.equals("yes")) {
                            if (db.insertOneGroup(discount_code, group_name, discount_rate) > 0) {
                                System.out.println("Successfully created user group");
                            } else {
                                System.out.println("Cannot create user group, internal failure");
                            }
                        } else {
                            System.out.println("Canceled");
                        }
                    } else if (action.equals("2")) {
                        // edit a group
                        String discount_code = "";
                        while (discount_code.length() == 0) {
                            ArrayList<ArrayList<String>> allGroups = db.selectAllGroups();
                            ArrayList<String> validCode = new ArrayList<String>();
                            System.out.println("Select a group to edit:");
                            System.out.printf("%-14s %-15s %s\n", "DISCOUNT_CODE", "GROUP_NAME", "DISCOUNT_RATE");
                            System.out.println("--------------------------------------------");
                            for (ArrayList<String> rd:allGroups) {
                                System.out.printf("%-14s %-15s %s\n", rd.get(0), rd.get(1), rd.get(2));
                                validCode.add(rd.get(0));
                            }
                            System.out.print("Please enter a valid discount code: ");
                            discount_code = s.nextLine();
                            if (!validCode.contains(discount_code)) {
                                System.out.println("Invalid discount code '" + discount_code + "', please re-enter");
                                discount_code = "";
                            }
                        }
                        boolean done = false;
                        while (!done) {
                            System.out.print("[1] Edit group name [2] Edit discount rate [q] Finish editing: ");
                            action = s.nextLine();
                            if (action.equals("1")) {
                                // edit group name
                                String group_name = "";
                                while (group_name.length() == 0) {
                                    System.out.print("Please enter the new group name: ");
                                    group_name = s.nextLine();
                                }
                                if (db.updateOneGroup(discount_code, group_name, -1) > 0) {
                                    System.out.println("Successfully updated group name");
                                } else {
                                    System.out.println("Cannot update group name, internal failure");
                                }
                            } else if (action.equals("2")) {
                                // edit discount rate
                                double discount_rate = -1;
                                while (discount_rate < 0 || discount_rate > 1) {
                                    System.out.print("Please enter the new discount rate (float between 0 to 1): ");
                                    try {
                                        discount_rate = Double.parseDouble(s.nextLine());
                                    } catch (NumberFormatException e) {
                                        System.out.println("Cannot parse input");
                                        discount_rate = -1;
                                    }
                                }
                                if (db.updateOneGroup(discount_code, "", discount_rate) > 0) {
                                    System.out.println("Successfully updated discount rate");
                                } else {
                                    System.out.println("Cannot update discount rate, internal failure");
                                }
                            } else if (action.equals("q")) {
                                // done
                                done = true;
                            } else {
                                System.out.println("Invalid command");
                            }
                        }
                    } else if (action.equals("q")) {
                        // go back
                        goBack = true;
                    } else if (action.equals("?")) {
                        // show menu
                        group_menu();
                    } else {
                        System.out.println("Invalid command '" + action + "', enter '?' for help or 'q' to go back");
                    }
                }
            } else if (action.equals("2")) {
                // Manage insurance plans
                boolean goBack = false;
                while (!goBack) {
                    actions = "12q?";
                    System.out.print("[" + actions + "] :> ");
                    action = s.nextLine();
                    if (action.equals("1")) {
                        // create a plan
                        String insurance_type = "";
                        double price_per_hour = -1;
                        double price_per_day = -1;
                        double price_per_week = -1;
                        ArrayList<ArrayList<String>> allInsurance = db.selectAllInsurance();
                        ArrayList<String> existsType = new ArrayList<String>();
                        for (ArrayList<String> rd:allInsurance) {
                            existsType.add(rd.get(0));
                        }
                        while (insurance_type.length() == 0) {
                            System.out.print("Please enter a unique name for the new insurance plan: ");
                            insurance_type = s.nextLine();
                            if (existsType.contains(insurance_type)) {
                                System.out.println("Insurance plan '" + insurance_type + "' already exists, please enter another name");
                                insurance_type = "";
                            }
                        }
                        while (price_per_hour < 0) {
                            System.out.print("Please enter price per hour (float >= 0): ");
                            try {
                                price_per_hour = Double.parseDouble(s.nextLine());
                            } catch (NumberFormatException e) {
                                System.out.println("Cannot pares input");
                            }
                        }
                        while (price_per_day < 0) {
                            System.out.print("Please enter price per day (float >= 0): ");
                            try {
                                price_per_day = Double.parseDouble(s.nextLine());
                            } catch (NumberFormatException e) {
                                System.out.println("Cannot pares input");
                            }
                        }
                        while (price_per_week < 0) {
                            System.out.print("Please enter price per week (float >= 0): ");
                            try {
                                price_per_week = Double.parseDouble(s.nextLine());
                            } catch (NumberFormatException e) {
                                System.out.println("Cannot pares input");
                            }
                        }

                        System.out.printf("\n%-20s %-14s %-14s %-14s\n", "INSURANCE_TYPE", "PRICE_PER_HOUR", "PRICE_PER_DAY", "PRICE_PER_WEEK");
                        System.out.println("-----------------------------------------------------------------");
                        System.out.printf("%-20s %-14s %-14s %-14s\n\n", insurance_type, price_per_hour, price_per_day, price_per_week);
                        System.out.print("Is that ok? (yes/no): ");
                        action = s.nextLine();
                        if (action.equals("y") || action.equals("yes")) {
                            // insert
                            if (db.insertOneInsurance(insurance_type, price_per_hour, price_per_day, price_per_week) > 0) {
                                System.out.println("Successfully added insurance plan");
                            } else {
                                System.out.println("Cannot add insurance plan, internal failure");
                            }
                        } else {
                            System.out.println("Canceled");
                        }
                    } else if (action.equals("2")) {
                        // edit a plan
                        String insurance_type = "";
                        while (insurance_type.length() == 0) {
                            System.out.println("Choose a plan to edit:");
                            System.out.printf("%-20s %-14s %-14s %-14s\n", "INSURANCE_TYPE", "PRICE_PER_HOUR", "PRICE_PER_DAY", "PRICE_PER_WEEK");
                            System.out.println("-----------------------------------------------------------------");
                            ArrayList<ArrayList<String>> allInsurance = db.selectAllInsurance();
                            ArrayList<String> validType = new ArrayList<String>();
                            for (ArrayList<String> rd:allInsurance) {
                                System.out.printf("%-20s %-14s %-14s %-14s\n", rd.get(0), rd.get(1), rd.get(2), rd.get(3));
                                validType.add(rd.get(0));
                            }
                            System.out.print("Please enter the insurance type you want to edit: ");
                            insurance_type = s.nextLine();
                            if (!validType.contains(insurance_type)) {
                                System.out.println("Invalid insurance type '" + insurance_type + "'");
                                insurance_type = "";
                            }
                        }
                        boolean done = false;
                        while (!done) {
                            System.out.print("[1] Edit price_per_hour [2] Edit price_per_day [3] Edit price_per_week [q] Finish editing: ");
                            action = s.nextLine();
                            if (action.equals("1")) {
                                // edit price per hour
                                double price_per_hour = -1;
                                while (price_per_hour < 0) {
                                    System.out.print("Please enter the new price per hour: ");
                                    try {
                                        price_per_hour = Double.parseDouble(s.nextLine());
                                        if (price_per_hour < 0)
                                            System.out.println("Price can not be negative");
                                    } catch (NumberFormatException e) {
                                        System.out.println("Cannot parse input");
                                        price_per_hour = -1;
                                    } 
                                }
                                if (db.updateOneInsurance(insurance_type, price_per_hour, -1, -1) > 0) {
                                    System.out.println("Successfully updated price per hour");
                                } else {
                                    System.out.println("Cannot update price per hour, internal failure");
                                }
                            } else if (action.equals("2")) {
                                // edit price per day
                                double price_per_day = -1;
                                while (price_per_day < 0) {
                                    System.out.print("Please enter the new price per day: ");
                                    try {
                                        price_per_day = Double.parseDouble(s.nextLine());
                                        if (price_per_day < 0)
                                            System.out.println("Price can not be negative");
                                    } catch (NumberFormatException e) {
                                        System.out.println("Cannot parse input");
                                        price_per_day = -1;
                                    } 
                                }
                                if (db.updateOneInsurance(insurance_type, -1, price_per_day, -1) > 0) {
                                    System.out.println("Successfully updated price per day");
                                } else {
                                    System.out.println("Cannot update price per day, internal failure");
                                }
                            } else if (action.equals("3")) {
                                // edit price per week
                                double price_per_week = -1;
                                while (price_per_week < 0) {
                                    System.out.print("Please enter the new price per week: ");
                                    try {
                                        price_per_week = Double.parseDouble(s.nextLine());
                                        if (price_per_week < 0)
                                            System.out.println("Price can not be negative");
                                    } catch (NumberFormatException e) {
                                        System.out.println("Cannot parse input");
                                        price_per_week = -1;
                                    } 
                                }
                                if (db.updateOneInsurance(insurance_type, -1, -1, price_per_week) > 0) {
                                    System.out.println("Successfully updated price per weeek");
                                } else {
                                    System.out.println("Cannot update price per week, internal failure");
                                }
                            } else if (action.equals("q")) {
                                // done
                                done = true;
                            } else {
                                System.out.println("Invalid command");
                            }
                        }
                    } else if (action.equals("q")) {
                        goBack = true;
                    } else if (action.equals("?")) {
                        insurnce_menu();
                    } else {
                        System.out.println("Invalid command '" + action + "', enter '?' for help or 'q' to go back");
                    }
                }
            } else if (action.equals("3")) {
                // Manage add-on items
                boolean goBack = false;
                while (!goBack) {
                    actions = "12q?";
                    System.out.print("[" + actions + "] :> ");
                    action = s.nextLine();
                    if (action.equals("1")) {
                        // add a new item
                        String item = "";
                        double price = -1;
                        ArrayList<ArrayList<String>> allItems = db.selectAllItems();
                        ArrayList<String> existsItems = new ArrayList<String>();
                        for (ArrayList<String> rd:allItems) {
                            existsItems.add(rd.get(0));
                        }
                        while (item.length() == 0) {
                            System.out.print("Please enter the name of the item you want to add: ");
                            item = s.nextLine();
                            if (existsItems.contains(item)) {
                                System.out.println("An item called '" + item + "' already exists, maybe you want to edit it");
                                item = "";
                            }
                        }
                        while (price < 0) {
                            System.out.print("Please enter the price of the new item (fload >= 0): ");
                            try {
                                price = Double.parseDouble(s.nextLine());
                                if (price < 0)
                                    System.out.println("Price can not be negative");
                            } catch (NumberFormatException e) {
                                System.out.println("Cannot parse input");
                                price = -1;
                            }
                        }
                        System.out.printf("%-15s %s\n", "ITEM", "PRICE");
                        System.out.println("---------------------");
                        System.out.printf("%-15s %s\n", item, price);
                        System.out.print("Is that ok? (yes/no): ");
                        action = s.nextLine();
                        if (action.equals("y") || action.equals("yes")) {
                            // insert
                            if (db.insertOneItem(item, price) > 0) {
                                System.out.println("Successfully added an item");
                            } else {
                                System.out.println("Cannot add the item, internal failure");
                            }
                        } else {
                            System.out.println("Canceled");
                        }
                    } else if (action.equals("2")) {
                        // edit an item
                        String item = "";
                        ArrayList<ArrayList<String>> allItems = db.selectAllItems();
                        ArrayList<String> validItems = new ArrayList<String>();
                        System.out.println("Please choose an item you want to edit:");
                        System.out.printf("%-15s %s\n", "ITEM", "PRICE");
                        System.out.println("---------------------");
                        for (ArrayList<String> rd:allItems) {
                            System.out.printf("%-15s %s\n", rd.get(0), rd.get(1));
                            validItems.add(rd.get(0));
                        }
                        while (item.length() == 0) {
                            System.out.print("enter an item: ");
                            item = s.nextLine();
                            if (!validItems.contains(item)) {
                                System.out.println("Item '" + item + "' does not exist");
                                item = "";
                            }
                        }
                        double price = -1;
                        while (price < 0) {
                            System.out.print("Please enter the new price for this item (float >= 0): ");
                            try {
                                price = Double.parseDouble(s.nextLine());
                                if (price < 0)
                                    System.out.println("Price can not be negative");
                            } catch (NumberFormatException e) {
                                System.out.println("Cannot parse input");
                                price = -1;
                            }
                        }
                        if (db.updateOneItem(item, price) > 0) {
                            System.out.println("Successfully updated price");
                        } else {
                            System.out.println("Cannot update price, internal failure");
                        }
                    } else if (action.equals("q")) {
                        // go back
                        goBack = true;
                    } else if (action.equals("?")) {
                        // show menu
                        item_menu();
                    } else {
                        System.out.println("Invalid command '" + action + "', enter '?' for help or 'q' to go back");
                    }
                }
            } else if (action.equals("4")) {
                // Manage vehicles
                boolean goBack = false;
                while (!goBack) {
                    actions = "12q?";
                    System.out.print("[" + actions + "] :> ");
                    action = s.nextLine();
                    if (action.equals("1")) {
                        // add a new vehicle
                        ArrayList<ArrayList<String>> allVehicles = db.selectAllVehicles();
                        ArrayList<String> existsPlate = new ArrayList<String>();
                        ArrayList<String> existsType = new ArrayList<String>();
                        for (ArrayList<String> rd:allVehicles) {
                            existsPlate.add(rd.get(0));
                            existsType.add(rd.get(3));
                        }
                        String plate_no = "";
                        while (plate_no.length() == 0) {
                            System.out.print("Please enter the plate no. of the new vehicle: ");
                            plate_no = s.nextLine();
                            if (existsPlate.contains(plate_no)) {
                                System.out.println("Vehicle '" + plate_no + "' already exists, please re-enter");
                                plate_no = "";
                            }
                        }
                        String make = "";
                        while (make.length() == 0) {
                            System.out.print("Make: ");
                            make = s.nextLine();
                        }
                        String model = "";
                        while (model.length() == 0) {
                            System.out.print("Model: ");
                            model = s.nextLine();
                        }
                        String type = "";
                        while (type.length() == 0) {
                            System.out.print("Type (sedan, SUV, minivan, etc): ");
                            type = s.nextLine();
                            if (!existsType.contains(type)) {
                                System.out.println("'" + type + "' does not match any record in vehicle rental, please add this type to rental first");
                                type = "";
                            }
                        }
                        String rent_center = "";
                        while (rent_center.length() == 0) {
                            System.out.print("This vehicle belongs to rent center: ");
                            rent_center = s.nextLine();
                            if (db.selectOneRentCenter(rent_center).size() == 0) {
                                System.out.println("'" + rent_center + "' does not match any existing rent center");
                                rent_center = "";
                            }
                        }
                        double odometer = -1;
                        while (odometer < 0) {
                            System.out.print("Odometer (float >= 0): ");
                            try {
                                odometer = Double.parseDouble(s.nextLine());
                                if (odometer < 0)
                                    System.out.println("Odometer can not be smaller than 0");
                            } catch (NumberFormatException e) {
                                System.out.println("Cannot parse input");
                                odometer = -1;
                            }
                        }
                        System.out.printf("\n%-8s %-8s %-8s %-12s %-12s %s\n", "PLATE_NO", "MAKE", "MODEL", "TYPE", "RENT_CENTER", "ODOMETER");
                        System.out.println("------------------------------------------------------------");
                        System.out.printf("%-8s %-8s %-8s %-12s %-12s %s\n\n", plate_no, make, model, type, rent_center, odometer);
                        System.out.print("Is this ok? (yes/no)");
                        action = s.nextLine();
                        if (action.equals("y") || action.equals("yes")) {
                            // insert
                            if (db.insertOneVehicle(plate_no, make, model, type, rent_center, odometer) > 0) {
                                System.out.println("Successfully added vehicle");
                            } else {
                                System.out.println("Cannot add vehicle, internal failure");
                            }
                        } else {
                            System.out.println("Canceled");
                        }
                    } else if (action.equals("2")) {
                        // view vehicle info
                        String rent_center = "";
                        while (rent_center.length() == 0) {
                            System.out.print("Enter a rent center name to check its vehicles: ");
                            rent_center = s.nextLine();
                            if (db.selectOneRentCenter(rent_center).size() == 0) {
                                System.out.println("Rent center '" + rent_center + "' does not exists, please re-enter");
                                rent_center = "";
                            }
                        }
                        ArrayList<ArrayList<String>> res = db.selectCenterVehicles(rent_center);
                        System.out.printf("%-8s %-8s %-8s %-12s %-12s %s\n", "PLATE_NO", "MAKE", "MODEL", "TYPE", "RENT_CENTER", "ODOMETER");
                        System.out.println("------------------------------------------------------------");
                        for (ArrayList<String> rd:res) {
                            System.out.printf("%-8s %-8s %-8s %-12s %-12s %s\n", rd.get(0), rd.get(1), rd.get(2), rd.get(3), rd.get(4), rd.get(5));
                        }
                    } else if (action.equals("q")) {
                        // go back
                        goBack = true;
                    } else if (action.equals("?")) {
                        // show menu
                        vehicle_menu();
                    } else {
                        System.out.println("Invalid command '" + action + "', enter '?' for help or 'q' to go back");
                    }
                }
            } else if (action.equals("5")) {
                // Manage rent centers
                boolean goBack = false;
                while (!goBack) {
                    actions = "12q?";
                    System.out.print("[" + actions + "] :> ");
                    action = s.nextLine();
                    if (action.equals("1")) {
                        // add a rent center
                        String center_name = "";
                        while (center_name.length() == 0) {
                            System.out.print("Please enter the name of the new rent center (must be unique): ");
                            center_name = s.nextLine();
                            if (db.selectOneRentCenter(center_name).size() != 0) {
                                System.out.println("Rent Center '" + center_name + "' already exist");
                                center_name = "";
                            }
                        }
                        String loc = "";
                        while (loc.length() == 0) {
                            System.out.print("Please enter the location of the new center: ");
                            loc = s.nextLine();
                        }
                        System.out.printf("\n%-12s %s\n", "CENTER_NAME", "LOCATION");
                        System.out.println("---------------------");
                        System.out.printf("%-12s %s\n\n", center_name, loc);
                        System.out.print("Is this ok? (yes/no): ");
                        action = s.nextLine();
                        if (action.equals("y") || action.equals("yes")) {
                            // insert a rent center
                            if (db.insertOneRentCenter(center_name, loc) > 0) {
                                System.out.println("Successfully added rent center");
                            } else {
                                System.out.println("Cannot add rent center, internal failure");
                            }
                        } else {
                            System.out.println("Canceled");
                        }
                    } else if (action.equals("2")) {
                        // edit a rent center
                        String center_name = "";
                        while (center_name.length() == 0) {
                            System.out.println("Choose a rent center to edit:");
                            ArrayList<ArrayList<String>> allCenters = db.selectAllRentCenters();
                            System.out.printf("%-12s %s\n", "CENTER_NAME", "LOCATION");
                            System.out.println("---------------------");
                            for (ArrayList<String> rd:allCenters) {
                                System.out.printf("%-12s %s\n", rd.get(0), rd.get(1));
                            }
                            System.out.print("Please enter a center name: ");
                            center_name = s.nextLine();
                            if (db.selectOneRentCenter(center_name).size() == 0) {
                                System.out.println("Rent center '" + center_name + "' does not exist");
                                center_name = "";
                            }
                        }
                        String loc = "";
                        while (loc.length() == 0) {
                            System.out.print("Please enter the location: ");
                            loc = s.nextLine();
                        }
                        if (db.updateOneRentCenter(center_name, loc) > 0) {
                            System.out.println("Successfully updated center's location");
                        } else {
                            System.out.println("Cannot update, internal failure");
                        }
                    } else if (action.equals("q")) {
                        // go back
                        goBack = true;
                    } else if (action.equals("?")) {
                        // show menu
                        center_menu();
                    } else {
                        System.out.println("Invalid command '" + action + "', enter '?' for help or 'q' to go back");
                    }
                }
            } else if (action.equals("6")) {
                // Manage rental rate
                boolean goBack = false;
                while (!goBack) {
                    actions = "12q?";
                    System.out.print("[" + actions + "] :> ");
                    action = s.nextLine();
                    if (action.equals("1")) {
                        // add a type
                        String type = "";
                        while (type.length() == 0) {
                            System.out.print("Please enter the new type to add: ");
                            type = s.nextLine();
                            if (db.selectVehicleRental(type).size() != 0) {
                                System.out.println("Vehicle tyle '" + type + "' already exists");
                                type = "";
                            }
                        }
                        double rental_by_hours = -1;
                        while (rental_by_hours < 0) {
                            System.out.print("Please enter the rental per hour (float >= 0): ");
                            try {
                                rental_by_hours = Double.parseDouble(s.nextLine());
                                if (rental_by_hours < 0)
                                    System.out.println("Rental can not be less than 0");
                            } catch (NumberFormatException e) {
                                System.out.println("Cannot parse input");
                                rental_by_hours = -1;
                            }
                        }
                        double rental_by_days = -1;
                        while (rental_by_days < 0) {
                            System.out.print("Please enter the rental per day (float >= 0): ");
                            try {
                                rental_by_days = Double.parseDouble(s.nextLine());
                                if (rental_by_days < 0)
                                    System.out.println("Rental can not be less than 0");
                            } catch (NumberFormatException e) {
                                System.out.println("Cannot parse input");
                                rental_by_days = -1;
                            }
                        }
                        double rental_by_weeks = -1;
                        while (rental_by_weeks < 0) {
                            System.out.print("Please enter the rental per week (float >= 0): ");
                            try {
                                rental_by_weeks = Double.parseDouble(s.nextLine());
                                if (rental_by_weeks < 0)
                                    System.out.println("Rental can not be less than 0");
                            } catch (NumberFormatException e) {
                                System.out.println("Cannot parse input");
                                rental_by_weeks = -1;
                            }
                        }
                        System.out.printf("\n%-14s %-14s %-14s %s\n", "TYPE", "RENTAL_PER_HOUR", "RENTAL_PER_DAY", "RENTAL_PER_WEEK");
                        System.out.println("------------------------------------------------------");
                        System.out.printf("%-14s %-14s %-14s %s\n\n", type, rental_by_hours, rental_by_days, rental_by_weeks);
                        System.out.print("Is this ok? (yes/no): ");
                        action = s.nextLine();
                        if (action.equals("y") || action.equals("yes")) {
                            // insert
                            if (db.insertVehicleRental(type, rental_by_hours, rental_by_days, rental_by_weeks) > 0) {
                                System.out.println("Successfully added");
                            } else {
                                System.out.println("Cannot insert, internal failure");
                            }
                        } else {
                            System.out.println("Canceled");
                        }
                    } else if (action.equals("2")) {
                        // edit a type
                        String type = "";
                        while (type.length() == 0) {
                            ArrayList<ArrayList<String>> allRental = db.selectAllRental();
                            System.out.println("Choose a type to edit:");
                            System.out.printf("%-14s %-14s %-14s %s\n", "TYPE", "RENTAL_PER_HOUR", "RENTAL_PER_DAY", "RENTAL_PER_WEEK");
                            System.out.println("------------------------------------------------------");
                            for (ArrayList<String> rd:allRental) {
                                System.out.printf("%-14s %-14s %-14s %s\n\n", rd.get(0), rd.get(1), rd.get(2), rd.get(3));
                            }
                            System.out.print("enter a type: ");
                            type = s.nextLine();
                            if (db.selectVehicleRental(type).size() == 0) {
                                System.out.println("Type '" + type + "' does not exist");
                                type = "";
                            }
                        }
                        boolean done = false;
                        while(!done) {
                            System.out.print("[1] Edit rental per hour [2] Edit rental per day [3] Edit rental per week [q] Finsih editing: ");
                            action = s.nextLine();
                            if (action.equals("1")) {
                                // edit r_hour
                                double rental_by_hours = -1;
                                while (rental_by_hours < 0) {
                                    System.out.print("Please enter the rental per hour (float >= 0): ");
                                    try {
                                        rental_by_hours = Double.parseDouble(s.nextLine());
                                        if (rental_by_hours < 0)
                                            System.out.println("Rental can not be less than 0");
                                    } catch (NumberFormatException e) {
                                        System.out.println("Cannot parse input");
                                        rental_by_hours = -1;
                                    }
                                }
                                if (db.updateVehicleRental(type, rental_by_hours, -1, -1) > 0) {
                                    System.out.println("Successfully updated");
                                } else {
                                    System.out.println("Cannot update, internal failure");
                                }
                            } else if (action.equals("2")) {
                                // edit r_day
                                double rental_by_days = -1;
                                while (rental_by_days < 0) {
                                    System.out.print("Please enter the rental per day (float >= 0): ");
                                    try {
                                        rental_by_days = Double.parseDouble(s.nextLine());
                                        if (rental_by_days < 0)
                                            System.out.println("Rental can not be less than 0");
                                    } catch (NumberFormatException e) {
                                        System.out.println("Cannot parse input");
                                        rental_by_days = -1;
                                    }
                                }
                                if (db.updateVehicleRental(type, -1, rental_by_days, -1) > 0) {
                                    System.out.println("Successfully updated");
                                } else {
                                    System.out.println("Cannot update, internal failure");
                                }
                            } else if (action.equals("3")) {
                                // edit r_week
                                double rental_by_weeks = -1;
                                while (rental_by_weeks < 0) {
                                    System.out.print("Please enter the rental per week (float >= 0): ");
                                    try {
                                        rental_by_weeks = Double.parseDouble(s.nextLine());
                                        if (rental_by_weeks < 0)
                                            System.out.println("Rental can not be less than 0");
                                    } catch (NumberFormatException e) {
                                        System.out.println("Cannot parse input");
                                        rental_by_weeks = -1;
                                    }
                                }
                                if (db.updateVehicleRental(type, -1, -1, rental_by_weeks) > 0) {
                                    System.out.println("Successfully updated");
                                } else {
                                    System.out.println("Cannot update, internal failure");
                                }
                            } else if (action.equals("q")) {
                                // finish
                                done = true;
                            } else {
                                System.out.println("Invalid command");
                            }
                        }
                    } else if (action.equals("q")) {
                        // go back
                        goBack = true;
                    } else if ( action.equals("?")) {
                        // show menu
                        rental_menu();
                    } else {
                        System.out.println("Invalid command '" + action + "', enter '?' for help or 'q' to go back");
                    }
                }
            } else if (action.equals("q")) {
                // Quit app
                quitApp = true;
            } else if (action.equals("?")) {
                // Show main menu
                menu();
            } else {
                System.out.println("Invalid command '" + action + "', enter '?' for help or 'q' to quit");
            }
        }

        // clean up and quit
        s.close();
        db.disconnect();
    }

    // show main menu
    static void menu() {
        System.out.println("Main Menu");
        System.out.println("    [1] Manage user groups");
        System.out.println("    [2] Manage insurance plans");
        System.out.println("    [3] Manage add-on items");
        System.out.println("    [4] Manage vehicles");
        System.out.println("    [5] Manage rent centers");
        System.out.println("    [6] Manage rental rate");
        System.out.println("    [q] Quit");
        System.out.println("    [?] Show this menu");
    }

    // show manage group menu
    static void group_menu() {
        System.out.println("Manage User Groups");
        System.out.println("    [1] Create a new group");
        System.out.println("    [2] Edit a group");
        System.out.println("    [q] Go back to main menu");
        System.out.println("    [?] Show this menu");
    }

    // show manage insurance menu
    static void insurnce_menu() {
        System.out.println("Manage Insurance Plans");
        System.out.println("    [1] Create a new plan");
        System.out.println("    [2] Edit a plan");
        System.out.println("    [q] Go back to main menu");
        System.out.println("    [?] Show this menu");
    }

    // show manage add-on menu
    static void item_menu() {
        System.out.println("Manage Add-On Items");
        System.out.println("    [1] Add a new item to the list");
        System.out.println("    [2] Edit the price of an item");
        System.out.println("    [q] Go back to main menu");
        System.out.println("    [?] Show this menu");
    }

    // show manage vehicle menu
    static void vehicle_menu() {
        System.out.println("Manage Vehicles");
        System.out.println("    [1] Add a new vehicle to a rent center");
        System.out.println("    [2] Show vehicle info");
        System.out.println("    [q] Go back to main menu");
        System.out.println("    [?] Show this menu");
    }

    // show manage rent center menu
    static void center_menu() {
        System.out.println("Manage Rent Centers");
        System.out.println("    [1] Add a rent center");
        System.out.println("    [2] Edit a rent center");
        System.out.println("    [q] Go back to main menu");
        System.out.println("    [?] Show this menu");
    }

    // show manage rental menu
    static void rental_menu() {
        System.out.println("Manage Vehicle Rental Rate");
        System.out.println("    [1] Add a new vehicle type");
        System.out.println("    [2] Edit rental of a certain type of vehicles");
        System.out.println("    [q] Go back to main menu");
        System.out.println("    [?] Show this menu");
    }
}