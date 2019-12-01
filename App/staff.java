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

public class staff {
    public static void main(String args[]) {
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
            String actions = "12q?";
            System.out.print("[" + actions + "] :> ");
            String action = s.nextLine();
            if (action.equals("1")) {
                // manage customers
                boolean goBack = false;
                while (!goBack) {
                    actions = "123q?";
                    System.out.print("[" + actions + "] :> ");
                    action = s.nextLine();
                    if (action.equals("1")) {
                        // create a new customer account
                        String customer_name = "";
                        String address = "";
                        String driver_license = "";
                        while (customer_name.length() == 0) {
                            System.out.print("Please enter customer's full name: ");
                            customer_name = s.nextLine();
                        }
                        while (address.length() == 0) {
                            System.out.print("Please enter customer's address: ");
                            address = s.nextLine();
                        }
                        while (driver_license.length() == 0) {
                            System.out.print("Please enter customer's driver license: ");
                            driver_license = s.nextLine();
                        }
                        int customer_id = db.insertOneUser(customer_name, address, driver_license);
                        if (customer_id == -1) {
                            System.err.println("Cannot insert customer, internal failure");
                        } else {
                            System.out.println("Successfully created customer account, customer_id: " + customer_id);
                        }
                    } else if (action.equals("2")) {
                        // edit a customer account
                        ArrayList<ArrayList<String>> allUsers = db.selectAllUsers();
                        ArrayList<String> allUserIDs = new ArrayList<String>();
                        System.out.printf("%-12s %-14s %-15s %s\n", "CUSTOMER_ID", "CUSTOMER_NAME", "ADDRESS", "DRIVER_LICENSE");
                        System.out.println("----------------------------------------------------------");
                        for (ArrayList<String> rd:allUsers) {
                            System.out.printf("%-12s %-14s %-15s %s\n", rd.get(0), rd.get(1), rd.get(2), rd.get(3));
                            allUserIDs.add(rd.get(0));
                        }
                        System.out.print("Please enter the customer_id of the account you want to edit: ");
                        String c_id = s.nextLine();
                        if (allUserIDs.contains(c_id)) {
                            int customer_id = Integer.parseInt(c_id);
                            System.out.println("Customer " + customer_id + " selected");
                            boolean done = false;
                            while (!done) {
                                System.out.print("[1] Edit Name [2] Edit Address [3] Edit Driver License [q] Done: ");
                                action = s.nextLine();
                                if (action.equals("1")) {
                                    // edit name
                                    String customer_name = "";
                                    while (customer_name.length() == 0) {
                                        System.out.print("Please enter the name you want to change to: ");
                                        customer_name = s.nextLine();
                                    }
                                    int count = db.updateUserName(customer_id, customer_name);
                                    System.out.println(count + " row(s) updated");
                                } else if (action.equals("2")) {
                                    // edit address
                                    String address = "";
                                    while (address.length() == 0) {
                                        System.out.print("Please enter the address you want to change to: ");
                                        address = s.nextLine();
                                    }
                                    int count = db.updateUserAddress(customer_id, address);
                                    System.out.println(count + " row(s) updated");
                                } else if (action.equals("3")) {
                                    // edit driver_license
                                    String driverLicense = "";
                                    while (driverLicense.length() == 0) {
                                        System.out.print("Please enter the driver license you want to change to: ");
                                        driverLicense = s.nextLine();
                                    }
                                    int count = db.updateUserDriverLicense(customer_id, driverLicense);
                                    System.out.println(count + " row(s) updated");
                                } else if (action.equals("q")) {
                                    // done
                                    done = true;
                                } else {
                                    System.out.println("Invalid command '" + action + "'");
                                }
                            }
                        } else {
                            System.out.println("Invalid customer id '" + c_id + "'");
                        }
                    } else if (action.equals("3")) {
                        // add customer to a user group
                        ArrayList<ArrayList<String>> allUsers = db.selectAllUsers();
                        ArrayList<String> allUserIDs = new ArrayList<String>();
                        System.out.printf("%-12s %-14s %-15s %s\n", "CUSTOMER_ID", "CUSTOMER_NAME", "ADDRESS", "DRIVER_LICENSE");
                        System.out.println("----------------------------------------------------------");
                        for (ArrayList<String> rd:allUsers) {
                            System.out.printf("%-12s %-14s %-15s %s\n", rd.get(0), rd.get(1), rd.get(2), rd.get(3));
                            allUserIDs.add(rd.get(0));
                        }
                        System.out.print("Please enter a valid customer id: ");
                        String c_id = s.nextLine();

                        if (allUserIDs.contains(c_id)) {
                            int customer_id = Integer.parseInt(c_id);
                            ArrayList<String> allGroups = new ArrayList<String>();
                            ArrayList<String> userGroups = new ArrayList<String>();
                            ArrayList<ArrayList<String>> allGroupsDB = db.selectAllGroups();
                            ArrayList<ArrayList<String>> userGroupsDB = db.selectUserGroups(customer_id);
                            System.out.printf("%-14s %s\n", "DISCOUNT_CODE", "GROUP_NAME");
                            System.out.println("-------------------------");
                            for (ArrayList<String> rd:allGroupsDB) {
                                allGroups.add(rd.get(0));
                                System.out.printf("%-14s %s\n", rd.get(0), rd.get(1));
                            }
                            for (ArrayList<String> rd:userGroupsDB) {
                                userGroups.add(rd.get(0));
                            }

                            System.out.print("Enter a discount code: ");
                            String d_code = s.nextLine();
                            if (allGroups.contains(d_code)) {
                                // valid discount code
                                if (userGroups.contains(d_code)) {
                                    System.out.println("Customer " + customer_id + " is already in user group '" + d_code + "'");
                                } else if (db.insertOneMembership(customer_id, d_code)) {
                                    System.out.println("Successfully added customer " + customer_id + " into user group '" + d_code + "'");
                                } else {
                                    System.out.println("Cannot add customer " + customer_id + " into user group '" + d_code + "', internal failure");
                                }
                            } else {
                                System.out.println("User group '" + d_code + "' does not exist");
                            }
                        } else {
                            System.out.println("Invalid customer id '" + c_id + "'");
                        }
                    } else if (action.equals("q")) {
                        // go back
                        goBack = true;
                    } else if (action.equals("?")) {
                        // show menu
                        customer_menu();
                    } else {
                        System.out.println("Invalid command '" + action +"', enter '?' for help or 'q' to go back");
                    }
                }
            } else if (action.equals("2")) {
                // manage orders
                boolean goBack = false;
                while (!goBack) {
                    actions = "12q?";
                    System.out.print("[" + actions + "] :> ");
                    action = s.nextLine();
                    if (action.equals("1")) {
                        // Create a new order
                        ArrayList<ArrayList<String>> allUsers = db.selectAllUsers();
                        ArrayList<String> allUserIDs = new ArrayList<String>();
                        System.out.printf("%-12s %-14s %-15s %s\n", "CUSTOMER_ID", "CUSTOMER_NAME", "ADDRESS", "DRIVER_LICENSE");
                        System.out.println("----------------------------------------------------------");
                        for (ArrayList<String> rd:allUsers) {
                            System.out.printf("%-12s %-14s %-15s %s\n", rd.get(0), rd.get(1), rd.get(2), rd.get(3));
                            allUserIDs.add(rd.get(0));
                        }
                        System.out.print("Please enter the customer_id of the account you want to edit: ");
                        String c_id = s.nextLine();
                        if (allUserIDs.contains(c_id)) {
                            // valid customer_id
                            int customer_id = Integer.parseInt(c_id);

                            // get membership
                            int membership = -1;
                            String discount_code = "00000000";
                            while (membership == -1) {
                                System.out.println("Available discount code");
                                ArrayList<ArrayList<String>> res = db.selectUserGroups(customer_id);
                                System.out.printf("%-14s %s\n", "DISCOUNT_CODE", "GROUP_NAME");
                                System.out.println("-------------------------");
                                for (ArrayList<String> rd:res) {
                                    System.out.printf("%-14s %s\n", rd.get(0), rd.get(1));
                                }
                                System.out.print("Please enter a valid discount code (default 00000000): ");
                                String d_code = s.nextLine();
                                if (d_code.length() == 0 || db.selectAMembership(customer_id, "00000000").size() == 3) {
                                    membership = Integer.parseInt(db.selectAMembership(customer_id, "00000000").get(0));
                                } else if (db.selectAMembership(customer_id, d_code).size() == 3) {
                                    // valid discount code
                                    membership = Integer.parseInt(db.selectAMembership(customer_id, d_code).get(0));
                                    discount_code = d_code;
                                } else {
                                    System.out.println("Invalid discount code '" + d_code + "', please re-enter");    
                                }
                            }

                            // get insurance
                            String insurance_type = "";
                            while (insurance_type.length() == 0) {
                                System.out.println("Please choose an insurance from below:");
                                System.out.printf("%-14s %-14s %-14s %s\n", "INSURANCE_TYPE", "PRICE_PER_HOUR", "PRICE_PER_DAY", "PRICE_PER_WEEK");
                                System.out.println("-----------------------------------------------------------");
                                ArrayList<ArrayList<String>> res = db.selectAllInsurance();
                                ArrayList<String> i_types = new ArrayList<String>();
                                for (ArrayList<String> rd:res) {
                                    System.out.printf("%-14s %-14s %-14s %s\n", rd.get(0), rd.get(1), rd.get(2), rd.get(3));
                                    i_types.add(rd.get(0));
                                }
                                System.out.print("enter an insurance type: ");
                                String i_type = s.nextLine();
                                if (i_types.contains(i_type)) {
                                    insurance_type = i_type;
                                    System.out.println("Insurance '" + insurance_type + "' selected");
                                } else {
                                    System.out.print("Invalid insurance type entered\n\n");
                                }
                            }

                            // get rent_center
                            String center_name = "";
                            while (center_name.length() == 0) {
                                System.out.println("Please choose a rent center from below:");
                                System.out.printf("%-12s %s\n", "CENTER_NAME", "LOC");
                                System.out.println("----------------");
                                ArrayList<ArrayList<String>> res = db.selectAllRentCenters();
                                ArrayList<String> c_names = new ArrayList<String>();
                                for (ArrayList<String> rd:res) {
                                    System.out.printf("%-12s %s\n", rd.get(0), rd.get(1));
                                    c_names.add(rd.get(0));
                                }
                                System.out.print("enter a center name: ");
                                String c_name = s.nextLine();
                                if (c_names.contains(c_name)) {
                                    center_name = c_name;
                                    System.out.println("Rent center '" + center_name + "' selected");
                                }
                            }

                            // get period
                            Timestamp start_time = null;
                            Timestamp end_time = null;
                            while (start_time == null) {
                                System.out.print("Please enter start time (yyyy-MM-dd HH24:mm:ss): ");
                                String inputTime = s.nextLine();
                                start_time = toTimestamp(inputTime);
                            }
                            while (end_time == null) {
                                System.out.print("Please enter end time (yyyy-MM-dd HH24:mm:ss): ");
                                String inputTime = s.nextLine();
                                Timestamp e_time = toTimestamp(inputTime);
                                if (e_time != null) {
                                    if (e_time.after(start_time)) {
                                        end_time = e_time;
                                    } else {
                                        System.out.println("End time must after start time, please re-enter");
                                    }
                                }
                            }

                            ArrayList<ArrayList<String>> availableVehicles = db.selectAllAvailableVehicles(start_time, end_time, center_name);
                            if (availableVehicles.size() == 0) {
                                System.out.println("No vehicles available in " + center_name + " from " + start_time + " to " + end_time);
                                System.out.println("Cannot create order");
                            } else {
                                // get vehicle
                                String plate_no = "";
                                while (plate_no.length() == 0) {
                                    ArrayList<String> plate_nos = new ArrayList<String>();
                                    System.out.println("Please choose a vehicle from all available ones in " + center_name + ":");
                                    System.out.printf("%-8s %-8s %-8s %-8s %s\n", "PLATE_NO", "MAKE", "MODEL", "TYPE", "ODOMETER");
                                    System.out.println("--------------------------------------------");
                                    for (ArrayList<String> rd:availableVehicles) {
                                        System.out.printf("%-8s %-8s %-8s %-8s %s\n", rd.get(0), rd.get(1), rd.get(2), rd.get(3), rd.get(4));
                                        plate_nos.add(rd.get(0));
                                    }
                                    System.out.print("enter a plate_no: ");
                                    String p_no = s.nextLine();
                                    if (plate_nos.contains(p_no)) {
                                        plate_no = p_no;
                                        System.out.println("Vehicle '" + plate_no + "' selected");
                                    } else {
                                        System.out.println("Please re-enter a valid plate_no");
                                    }
                                }

                                // get add-on times
                                boolean readyToCreate = false;
                                ArrayList<String> addOns = new ArrayList<String>();
                                while (!readyToCreate) {
                                    System.out.print("Do you want to add itmes? (yes/no): ");
                                    action = s.nextLine();
                                    if (action.equals("y") || action.equals("yes")) {
                                        // add an itme
                                        System.out.println("Please select an item to add:");
                                        System.out.printf("%-20s %s\n", "ITEM", "PRICE");
                                        System.out.println("--------------------------");
                                        ArrayList<ArrayList<String>> res = db.selectAllItems();
                                        ArrayList<String> items = new ArrayList<String>();
                                        for (ArrayList<String> rd:res) {
                                            System.out.printf("%-10s %s\n", rd.get(0), rd.get(1));
                                            items.add(rd.get(0));
                                        }
                                        System.out.print("enter an item: ");
                                        String inputItem = s.nextLine();
                                        if (items.contains(inputItem)) {
                                            System.out.print("How many " + inputItem + " do you need? (Integer larger than 0): ");
                                            int inputNumber = Integer.parseInt(s.nextLine());
                                            if (inputNumber > 0) {
                                                for (int i = 0; i < inputNumber; i++) {
                                                    addOns.add(inputItem);
                                                }
                                                System.out.println("Itmes successfully added to list");
                                            } else {
                                                System.out.println("Invalid number");
                                            }
                                        } else {
                                            System.out.println("Item '" + inputItem + "' does not exist, please re-enter");
                                        }
                                    } else {
                                        // stop adding items
                                        readyToCreate = true;
                                    }
                                }

                                // create order based on user input
                                System.out.print("Please confirm your order\n\n");
                                System.out.printf("%-8s %-14s %-14s %-8s %-14s %-9s %-8s %-12s %-20s %s\n", "ORDER_ID", "DISCOUNT_CODE", "INSURANCE_TYPE", "PLATE_NO", "INCLUDED_MILES", "TOT_MILES", "TANK", "DROPOFF_LOC", "START_TIME", "END_TIME");
                                System.out.println("------------------------------------------------------------------------------------------------------------------------------------");
                                System.out.printf("%-8s %-14s %-14s %-8s %-14s %-9s %-8s %-12s %-20s %s\n\n", "default", discount_code, insurance_type, plate_no, "1000", "(null)", "(null)", "(null)", start_time, end_time);
                                System.out.print("Is that ok? (yes/no): ");
                                action = s.nextLine();
                                if (action.equals("y") || action.equals("yes")) {
                                    // create order
                                    int order_id = -1;
                                    // try to get order id
                                    if (addOns.size() == 0) {
                                        order_id = db.insertOneOrder(membership, insurance_type, 1000, plate_no, start_time, end_time);
                                    } else {
                                        order_id = db.insertOneOrder(membership, insurance_type, 1000, plate_no, start_time, end_time, addOns);
                                    }
                                    // evaluate order id
                                    if (order_id == -1) {
                                        System.out.println("Cannot create order, internal failure");
                                    } else {
                                        System.out.println("Successfully created, order id: " + order_id);

                                        double price = estimatePrice(db, start_time, end_time, discount_code, addOns, plate_no, insurance_type);

                                        System.out.println("Estimated price: " + price);

                                    }
                                } else {
                                    System.out.println("Canceled");
                                }
                            }
                        } else {
                            System.out.println("Invalid customer id '" + c_id + "'");
                        }
                    } else if (action.equals("2")) {
                        // Complete an order
                        ArrayList<ArrayList<String>> allUsers = db.selectAllUsers();
                        ArrayList<String> allUserIDs = new ArrayList<String>();
                        System.out.printf("%-12s %-14s %-15s %s\n", "CUSTOMER_ID", "CUSTOMER_NAME", "ADDRESS", "DRIVER_LICENSE");
                        System.out.println("----------------------------------------------------------");
                        for (ArrayList<String> rd:allUsers) {
                            System.out.printf("%-12s %-14s %-15s %s\n", rd.get(0), rd.get(1), rd.get(2), rd.get(3));
                            allUserIDs.add(rd.get(0));
                        }
                        System.out.print("Please enter the customer_id of the account you want to edit: ");
                        String c_id = s.nextLine();
                        if (allUserIDs.contains(c_id)) {
                            // valid customer_id
                            int customer_id = Integer.parseInt(c_id);

                            // List all incomplete order of the customer
                            System.out.println("All incomplete orders of customer" + customer_id + ": ");
                            ArrayList<ArrayList<String>> incompleteOrders = db.selectUserIncompleteOrders(customer_id);
                            ArrayList<String> validOrderID = new ArrayList<String>();
                            System.out.printf("%-8s %-14s %-14s %-8s %-14s %-9s %-4s %-12s %-20s %s\n", "ORDER_ID", "DISCOUNT_CODE", "INSURANCE_TYPE", "PLATE_NO", "INCLUDED_MILES", "TOT_MILES", "TANK", "DROPOFF_LOC", "START_TIME", "END_TIME");
                            System.out.println("------------------------------------------------------------------------------------------------------------------------------------");
                            for (ArrayList<String> rd:incompleteOrders) {
                                System.out.printf("%-8s %-14s %-14s %-8s %-14s %-9s %-4s %-12s %-20s %s\n", rd.get(0), rd.get(1), rd.get(2), rd.get(3), rd.get(4), rd.get(5), rd.get(6), rd.get(7), rd.get(8), rd.get(9));
                                validOrderID.add(rd.get(0));
                            }
                            System.out.print("Enter an order id: ");
                            String o_id = s.nextLine();
                            if (validOrderID.contains(o_id)) {
                                int order_id = Integer.parseInt(o_id);

                                double included_miles = -1;
                                double tot_miles = -1;
                                double tank = -1;
                                String dropoff_loc = "";
                                while (included_miles == -1) {
                                    System.out.print("Enter the included miles of current order: ");
                                    try {
                                        included_miles = Double.parseDouble(s.nextLine());
                                        if (included_miles < 0) {
                                            System.out.println("Invalid input, included miles must larger than or equal to zero");
                                            included_miles = -1;
                                        }
                                    } catch (NumberFormatException e) {
                                        System.err.println("Invalid input");
                                        included_miles = -1;
                                    }
                                }
                                while (tot_miles == -1) {
                                    System.out.print("Enter the total miles of this order: ");
                                    try {
                                        tot_miles = Double.parseDouble(s.nextLine());
                                        if (tot_miles <= 0) {
                                            System.out.println("Invalid input, total miles must larger than zero");
                                            tot_miles = -1;
                                        }
                                    } catch (NumberFormatException e) {
                                        System.err.println("Invalid input");
                                        tot_miles = -1;
                                    }
                                }
                                while (tank == -1) {
                                    System.out.print("Enter the amount of gasoline used: ");
                                    try {
                                        tank = Double.parseDouble(s.nextLine());
                                        if (tank < 0) {
                                            System.out.println("Invalid input, gasoline used must larger than or equal to zero");
                                            tank = -1;
                                        }
                                    } catch (NumberFormatException e) {
                                        System.err.println("Invalid input");
                                        tot_miles = -1;
                                    }
                                }
                                while (dropoff_loc.length() == 0) {
                                    System.out.print("Enter the loc where customer returns the vehicle (or center name): ");
                                    dropoff_loc = s.nextLine();
                                }

                                ArrayList<String> thisOrder = db.selectOneOrder(order_id);
                                String rentCenter = db.selectAVehicle(thisOrder.get(4)).get(4);

                                System.out.printf("\n%-8s %-14s %-14s %-8s %-14s %-9s %-8s %-12s %-20s %s\n", "ORDER_ID", "DISCOUNT_CODE", "INSURANCE_TYPE", "PLATE_NO", "INCLUDED_MILES", "TOT_MILES", "TANK", "DROPOFF_LOC", "START_TIME", "END_TIME");
                                System.out.println("------------------------------------------------------------------------------------------------------------------------------------");
                                System.out.printf("%-8s %-14s %-14s %-8s %-14s %-9s %-8s %-12s %-20s %s\n\n", order_id, thisOrder.get(2), thisOrder.get(3), thisOrder.get(4), included_miles, tot_miles, tank, dropoff_loc, thisOrder.get(9), thisOrder.get(10));

                                System.out.println("Is this ok? (yes/no): ");
                                action = s.nextLine();
                                if (action.equals("y") || action.equals("yes")) {
                                    // Calculate rental and update
                                    if (db.updateOneOrder(order_id, included_miles, tot_miles, tank, dropoff_loc) != -1) {
                                        System.out.println("Order & odometer updated");

                                        // calculate estimate price
                                        Timestamp start_time = db.selectOneOrderTime(order_id).get(0);
                                        Timestamp end_time = db.selectOneOrderTime(order_id).get(1);
                                        String discount_code = thisOrder.get(2);
                                        String plate_no = thisOrder.get(4);
                                        String insurance_type = thisOrder.get(3);
                                        ArrayList<String> addOns = new ArrayList<String>();
                                        ArrayList<ArrayList<String>> addOnsDb = db.selectAddOns(order_id);
                                        for (ArrayList<String> rd:addOnsDb) {
                                            String item = rd.get(0);
                                            int number = Integer.parseInt(rd.get(1));
                                            for (int i = 0; i < number; i++) {
                                                addOns.add(item);
                                            }
                                        }
                                        double price1 = estimatePrice(db, start_time, end_time, discount_code, addOns, plate_no, insurance_type);

                                        // calculate final price (rental)
                                        double price_per_mile = -1;
                                        double gasoline_price = -1;
                                        double drop_off_charge = -1;
                                        while (price_per_mile < 0) {
                                            System.out.print("Please enter price per extra miles (0 if plan contains unlimited miles): ");
                                            try {
                                                price_per_mile = Double.parseDouble(s.nextLine());
                                                if (price_per_mile < 0) {
                                                    System.out.println("Invalid input '" + price_per_mile + "', price cannot less than zero");
                                                    price_per_mile = -1;
                                                }
                                            } catch (NumberFormatException e) {
                                                System.out.println("Invalid input");
                                                price_per_mile = -1;
                                            }
                                        }
                                        while (gasoline_price < 0) {
                                            System.out.print("Please enter current gasoline price: ");
                                            try {
                                                gasoline_price = Double.parseDouble(s.nextLine());
                                                if (gasoline_price < 0) {
                                                    System.out.println("Invalid input '" + gasoline_price + "', price cannot less than zero");
                                                    gasoline_price = -1;
                                                }
                                            } catch (NumberFormatException e) {
                                                System.out.println("Invalid input");
                                                gasoline_price = -1;
                                            }
                                        }
                                        while (drop_off_charge < 0) {
                                            if (dropoff_loc.equals(rentCenter)) {
                                                // Returned to rent center, no charge
                                                drop_off_charge = 0;
                                            } else {
                                                System.out.print("Vehicle returned to " + dropoff_loc + ", enter an appropriate drop off charge: ");
                                                try {
                                                    drop_off_charge = Double.parseDouble(s.nextLine());
                                                    if (drop_off_charge < 0) {
                                                        System.out.println("Invalid input '" + drop_off_charge + "', charge cannot less than zero");
                                                        drop_off_charge = -1;
                                                    }
                                                } catch (NumberFormatException e) {
                                                    System.out.println("Invalid input");
                                                    drop_off_charge = -1;
                                                }
                                            }
                                        }
                                        double extra_miles = 0;
                                        if (tot_miles > included_miles)
                                            extra_miles = tot_miles - included_miles;

                                        double discount_rate = Double.parseDouble(db.selectOneGroup(discount_code).get(2));
                                        double price2 = discount_rate * (extra_miles * price_per_mile + tank * gasoline_price + drop_off_charge);
                                        
                                        System.out.print("Rented from " + start_time + " to " + end_time + ", is this 'busy period'? (yes/no): ");
                                        action = s.nextLine();
                                        if (action.equals("y") || action.equals("yes")) {
                                            System.out.println("Busy period, rental is 20% higher");
                                            System.out.println("Rental: " + 1.2 * (price1 + price2));
                                        } else {
                                            System.out.println("Rental: " + (price1 + price2));
                                        }
                                    } else {
                                        System.err.println("Cannot update order, internal failure");
                                    }
                                } else {
                                    // Cancle
                                    System.out.println("Canceled");
                                }
                            } else {
                                System.out.println("Invalid order id '" + o_id + "'");
                            }
                        } else {
                            System.out.println("Invalid customer id '" + c_id + "'");
                        }
                    } else if (action.equals("q")) {
                        // go back
                        goBack = true;
                    } else if (action.equals("?")) {
                        // show menu
                        order_menu();
                    } else {
                        System.out.println("Invalid command '" + action +"', enter '?' for help or 'q' to go back");
                    }
                }
            } else if (action.equals("q")) {
                // quit app
                quitApp = true;
            } else if (action.equals("?")) {
                // show menu
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
        System.out.println("    [1] Manage Customers");
        System.out.println("    [2] Manage Orders");
        System.out.println("    [q] Quit app");
        System.out.println("    [?] Show this menu");
    }

    // show customer menu
    static void customer_menu() {
        System.out.println("Manage Customers");
        System.out.println("    [1] Create a new customer account");
        System.out.println("    [2] Edit a customer account");
        System.out.println("    [3] Add customer to a user group");
        System.out.println("    [q] Go back to main menu");
        System.out.println("    [?] Show this menu");
    }

    // show order menu
    static void order_menu() {
        System.out.println("Manage Orders");
        System.out.println("    [1] Create a new order");
        System.out.println("    [2] Complete an order (return vehicle)");
        System.out.println("    [q] Go back to main menu");
        System.out.println("    [?] Show this menu");
    }

    // convert user input into Timestamp
    static Timestamp toTimestamp(String time) {
        try {
            DateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d = dFormat.parse(time);
            Timestamp res = new Timestamp(d.getTime());
            return res;
        } catch (Exception e) {
            System.err.println("Cannot convert user input into Timestamp");
            System.out.println("Example: 2019-09-10 14:00:00");
            return null;
        }
    }

    // estimate price
    static double estimatePrice(Database db, Timestamp start_time, Timestamp end_time, String discount_code, ArrayList<String> addOns, String plate_no, String insurance_type) {
        // estimate price
        long rent_length = end_time.getTime() - start_time.getTime();
        // System.out.println("(" + length_of_rent + ") - " + (end_time.getTime() - start_time.getTime()));
        double discount_rate = Double.parseDouble(db.selectOneGroup(discount_code).get(2));
        double items_price = 0;
        for (String addOn:addOns) {
            items_price += Double.parseDouble(db.selectAnItem(addOn).get(1));
        }
        double insurance_price = 0;
        double vehicle_rental = 0;
        String vehicle_type = db.selectAVehicle(plate_no).get(3);
        if (rent_length < 86400000) {
            // charge by hours
            int hours = (int)(rent_length / 3600000);
            if (rent_length % 3600000 != 0)
                hours++;
            insurance_price = Double.parseDouble(db.selectAnInsurance(insurance_type).get(1)) * hours;
            vehicle_rental = Double.parseDouble(db.selectVehicleRental(vehicle_type).get(1)) * hours;
        } else if (rent_length < 604800000) {
            // charge by days
            int days = (int)(rent_length / 86400000);
            if (rent_length % 86400000 != 0)
                days++;
            insurance_price = Double.parseDouble(db.selectAnInsurance(insurance_type).get(2)) * days;
            vehicle_rental = Double.parseDouble(db.selectVehicleRental(vehicle_type).get(2)) * days;
        } else {
            // charge by weeks
            long weeks = rent_length / 604800000;
            if (rent_length % 604800000 != 0)
                weeks++;
            insurance_price = Double.parseDouble(db.selectAnInsurance(insurance_type).get(3)) * weeks;
            vehicle_rental = Double.parseDouble(db.selectVehicleRental(vehicle_type).get(3)) * weeks;
        }

        return discount_rate * (items_price + insurance_price + vehicle_rental);
    }
}