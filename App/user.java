
/**
 * CSE241 Final Project
 * Author: Yuming Tian
 */

import java.util.Scanner;
import java.util.Date;
import java.util.ArrayList;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
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
                    actions = "12q?";
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
                            } else {
                                System.out.println("Canceled");
                            }
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
        System.out.println("    [2] Create a new order");
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
}