
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

public class user {
    public static void main(String args[]) {
        Database db = null;

        Scanner s = new Scanner(System.in);
        String usr = "";
        String pwd = "";

        int customer_id = -1;
        boolean quitApp = false;

        // connect to database
        while (db == null) {
            System.out.print("enter Oracle user id: ");
            usr = s.nextLine();
            System.out.print("enter Oracle password for " + usr + ": ");
            pwd = s.nextLine();
            db = Database.getDatabase("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241", usr, pwd);
        }

        // requires valid customer id
        while (customer_id < 0) {
            System.out.print("Please enter your customer id: ");
            if (s.hasNextInt()) {
                customer_id = Integer.parseInt(s.nextLine());
                ArrayList<String> res = db.selectOneUser(customer_id);
                if (res.size() == 0) {
                    System.out.println("Customer " + customer_id + " does not exist. Contact a staff for help");
                    System.out.println("Do you want to re-enter customer id? (yes/no)");
                    String action = s.nextLine();
                    if (action.equals("yes") || action.equals("y")) {
                        customer_id = -1;
                    } else {
                        customer_id = 0;
                        quitApp = true;
                    }
                } else {
                    System.out.println("Logged in as customer " + customer_id);
                }
            } else {
                System.out.println("Invalid input '" + s.nextLine() + "', please enter an integer");
            }
        }

        if (!quitApp)
            menu();

        // if not quit, keep looping
        while (!quitApp) {
            // valid actions
            String actions = "12q?";
            System.out.print("[" + actions + "] :> ");
            String action = s.nextLine();
            if (action.equals("1")) {
                info_menu();
                // personal info
                boolean goBack = false;
                while (!goBack) {
                    actions = "1234q?";
                    System.out.print("[" + actions + "] :> ");
                    action = s.nextLine();
                    if (action.equals("1")) {
                        // view information
                        ArrayList<String> res = db.selectOneUser(customer_id);
                        System.out.printf("%-12s %-14s %-12s %s\n", "CUSTOMER_ID", "CUSTOMER_NAME", "ADDRESS", "DRIVER_LICENSE");
                        System.out.println("-------------------------------------------------------");
                        System.out.printf("%-12s %-14s %-12s %s\n", res.get(0), res.get(1), res.get(2), res.get(3));
                    } else if (action.equals("2")) {
                        // edit name
                        System.out.print("Please enter the name you want to change to: ");
                        String customer_name = s.nextLine();
                        int count = db.updateUserName(customer_id, customer_name);
                        System.out.println(count + " row(s) updated");
                    } else if (action.equals("3")) {
                        //edit address
                        System.out.print("Please enter the address you want to change to: ");
                        String address = s.nextLine();
                        int count = db.updateUserAddress(customer_id, address);
                        System.out.println(count + " row(s) updated");
                    } else if (action.equals("4")) {
                        //edit driver license
                        System.out.print("Please enter the driver license you want to change to: ");
                        String driverLicense = s.nextLine();
                        int count = db.updateUserDriverLicense(customer_id, driverLicense);
                        System.out.println(count + " row(s) updated");
                    } else if (action.equals("q")) {
                        // go back to main menu
                        goBack = true;
                    } else if (action.equals("?")) {
                        // show menu
                        info_menu();
                    } else {
                        System.out.println("Invalid command '" + "', enter '?' for help or 'q' to go back");
                    }
                }
            } else if (action.equals("2")) {
                order_menu();
                // manage orders
                boolean goBack = false;
                while (!goBack) {
                    actions = "1234q?";
                    System.out.print("[" + actions + "] :> ");
                    action = s.nextLine();
                    if (action.equals("1")) {
                        // view orders
                        ArrayList<ArrayList<String>> res = db.selectUserOrders(customer_id);
                        System.out.printf("%-8s %-14s %-14s %-8s %-14s %-9s %-4s %-12s %-20s %s\n", "ORDER_ID", "DISCOUNT_CODE", "INSURANCE_TYPE", "PLATE_NO", "INCLUDED_MILES", "TOT_MILES", "TANK", "DROPOFF_LOC", "START_TIME", "END_TIME");
                        System.out.println("------------------------------------------------------------------------------------------------------------------------------------");
                        for (ArrayList<String> rd:res) {
                            System.out.printf("%-8s %-14s %-14s %-8s %-14s %-9s %-4s %-12s %-20s %s\n", rd.get(0), rd.get(1), rd.get(2), rd.get(3), rd.get(4), rd.get(5), rd.get(6), rd.get(7), rd.get(8), rd.get(9));
                        }
                    } else if (action.equals("2")) {
                        // view add-ons of an order
                        System.out.print("Please enter an order_id: ");
                        String target_order = s.nextLine();
                        ArrayList<ArrayList<String>> all_user_orders = db.selectUserOrders(customer_id);
                        ArrayList<String> all_user_order_id = new ArrayList<String>();
                        for (ArrayList<String> rd:all_user_orders) {
                            all_user_order_id.add(rd.get(0));
                        }
                        if (all_user_order_id.contains(target_order)) {
                            // valid input order_id
                            ArrayList<ArrayList<String>> res = db.selectAddOns(Integer.parseInt(target_order));
                            if (res.size() == 0) {
                                System.out.println("Order " + target_order + " does not have any add on items");
                            } else {
                                System.out.printf("%-15s %s\n", "ITEM", "NUM_OF_ITEM");
                                System.out.println("---------------------------");
                                for (ArrayList<String> rd:res) {
                                    System.out.printf("%-15s %s\n", rd.get(0), rd.get(1));
                                }
                            }
                        } else {
                            System.out.println("Order '" + target_order + "' does not exist, please enter a valid order id");
                        }
                    } else if (action.equals("3")) {
                        // create a new order

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
                    } else if (action.equals("4")) {
                        // estimate price
                        System.out.print("Please enter an order_id: ");
                        String target_order = s.nextLine();
                        ArrayList<ArrayList<String>> all_user_orders = db.selectUserOrders(customer_id);
                        ArrayList<String> all_user_order_id = new ArrayList<String>();
                        for (ArrayList<String> rd:all_user_orders) {
                            all_user_order_id.add(rd.get(0));
                        }
                        if (all_user_order_id.contains(target_order)) {
                            // valid input order_id
                            int order_id = Integer.parseInt(target_order);
                            ArrayList<String> order_info = db.selectOneOrder(order_id);
                            ArrayList<Timestamp> order_time = db.selectOneOrderTime(order_id);
                            ArrayList<String> addOns = new ArrayList<String>();
                            ArrayList<ArrayList<String>> addOnsDb = db.selectAddOns(order_id);
                            for (ArrayList<String> rd:addOnsDb) {
                                String item = rd.get(0);
                                int number = Integer.parseInt(rd.get(1));
                                for (int i = 0; i < number; i++) {
                                    addOns.add(item);
                                }
                            }
                            // print out estimated price
                            Double price = estimatePrice(db, order_time.get(0), order_time.get(1), order_info.get(2), addOns, order_info.get(4), order_info.get(3));
                            System.out.println("Estimated price: " + price);
                        } else {
                            System.out.println("Order '" + target_order + "' does not exist, please enter a valid order id");
                        }
                    } else if (action.equals("q")) {
                        // go back to main menu
                        goBack = true;
                    } else if (action.equals("?")) {
                        // show menu
                        order_menu();
                    } else {
                        System.out.println("Invalid command '" + action + "', enter '?' for help or 'q' to go back");
                    }
                }
            } else if (action.equals("3")) {
                // show user's groups
                ArrayList<ArrayList<String>> res = db.selectUserGroups(customer_id);
                System.out.printf("%-14s %s\n", "DISCOUNT_CODE", "GROUP_NAME");
                System.out.println("-------------------------");
                for (ArrayList<String> rd:res) {
                    System.out.printf("%-14s %s\n", rd.get(0), rd.get(1));
                }
            } else if (action.equals("q")) {
                // quit
                quitApp = true;
            } else if (action.equals("?")) {
                // show menu
                menu();
            } else if (action.equals("T")) {
                // for debug
                System.out.print("Input time (yyyy-MM-dd HH:mm:ss): ");
                String inputTime = s.nextLine();
                Timestamp ts = toTimestamp(inputTime);
                System.out.println(ts);
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
        System.out.println("    [1] Personal Info");
        System.out.println("    [2] Manager Orders");
        System.out.println("    [3] Show user groups");
        System.out.println("    [q] Quit app");
        System.out.println("    [?] SHow this menu");
    }

    // show information menu
    static void info_menu() {
        System.out.println("Personal Info");
        System.out.println("    [1] View information");
        System.out.println("    [2] Edit name");
        System.out.println("    [3] Edit address");
        System.out.println("    [4] Edit driver license");
        System.out.println("    [q] Go back to main menu");
        System.out.println("    [?] Show this menu");
    }

    // show order menu
    static void order_menu() {
        System.out.println("Manage Orders");
        System.out.println("    [1] View Your Orders");
        System.out.println("    [2] View add on items of an order");
        System.out.println("    [3] Create a new order");
        System.out.println("    [4] Check rental of an order");
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