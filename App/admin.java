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
            String actions = "1234q?";
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
                        if (db.insertOneGroup(discount_code, group_name, discount_rate) > 0) {
                            System.out.println("Successfully created user group");
                        } else {
                            System.out.println("Cannot create user group, internal failure");
                        }
                    } else if (action.equals("2")) {
                        // edit a group
                        String discount_code = "";
                        while (discount_code.length() == 0) {
                            ArrayList<ArrayList<String>> allGroups = db.selectAllGroups();
                            ArrayList<String> validCode = new ArrayList<String>();
                            System.out.println("Select a group to edit:");
                            System.out.printf("%-14s %-15s %s\n", "DISCOUNT_CODE", "GROUP_NAME", "DISCOUNT_RATE");
                            System.out.println("------------------------------------------");
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
                }
            } else if (action.equals("3")) {
                // Manage add-on items
                boolean goBack = false;
                while (!goBack) {
                    actions = "12q?";
                    System.out.print("[" + actions + "] :> ");
                }
            } else if (action.equals("4")) {
                // Manage vehicles
                boolean goBack = false;
                while (!goBack) {
                    actions = "12q?";
                    System.out.print("[" + actions + "] :> ");
                }
            } else if (action.equals("5")) {
                // Manage rent centers
                boolean goBack = false;
                while (!goBack) {
                    actions = "12q?";
                    System.out.print("[" + actions + "] :> ");
                }
            } else if (action.equals("6")) {
                // Manage rental rate
                boolean goBack = false;
                while (!goBack) {
                    actions = "12q?";
                    System.out.print("[" + actions + "] :> ");
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
    }
}