/**
 * CSE241 Final Project
 * Author: Yuming Tian
 */

import java.util.Scanner;
import java.util.ArrayList;

public class App {
    public static void main(String args[]) {
        Database db = null;

        Scanner s = new Scanner(System.in);
        String usr = "";
        String pwd = "";

        // Hard-coded username / password for testing
        // TODO: Remove this line
        System.out.println("Automatically login with yut222");
        db = Database.getDatabase("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241", "yut222", "sunlab#2019");

        // connect to database
        while(db == null) {
            System.out.print("enter Oracle user id: ");
            usr = s.nextLine();
            System.out.print("enter Oracle password for " + usr + ": ");
            pwd = s.nextLine();
            db = Database.getDatabase("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241", usr, pwd);
        }

        // If user does not want to quit, keep looping
        boolean quitApp = false;

        while (!quitApp) {
            // valid actions
            String actions = "LCcGgOq?";
            System.out.print("[" + actions + "] :> " );
            String action = s.nextLine();
            if (action.equals("L")) {
                // List all tables
                ArrayList<String> res = db.listAllTables(usr);
                System.out.println("Tables");
                System.out.println("----------------------------------------");
                for (String rd:res) {
                    System.out.println(rd);
                }
            } else if (action.equals("C")) {
                // List all users
                ArrayList<ArrayList<String>> res = db.selectAllUsers();
                System.out.printf("%-12s %-14s %-12s %s\n", "CUSTOMER_ID", "CUSTOMER_NAME", "ADDRESS", "DRIVER_LICENSE");
                System.out.println("--------------------------------------------------------");
                for (ArrayList<String> rs:res) {
                    System.out.printf("%-12s %-14s %-12s %s\n", rs.get(0), rs.get(1), rs.get(2), rs.get(3));
                }
            } else if (action.equals("c")) {
                // Select a customer
                int customer_id;
                boolean goBack = false;
                System.out.println("Please enter a valid customer id");
                customer_id = Integer.parseInt(s.nextLine());
                ArrayList<String> result = db.selectOneUser(customer_id);
                if (result.size() == 0) {
                    System.out.println("Customer " + customer_id + " does not exist");
                    goBack = true;
                } else {
                    System.out.println("Customer " + customer_id + " selected");
                }
                while (!goBack) {
                    actions = "12q?";
                    System.out.print("[" + actions + "] :> ");
                    action = s.nextLine();
                    if (action.equals("1")) {
                        // op 1
                        ArrayList<ArrayList<String>> res = db.selectUserGroups(customer_id);
                        System.out.printf("%-14s %s\n", "DISCOUNT_CODE", "GROUP_NAME");
                        System.out.println("-------------------------");
                        for (ArrayList<String> rs:res) {
                            System.out.printf("%-14s %s\n", rs.get(0), rs.get(1));
                        }
                    } else if (action.equals("2")) {
                        //op 2
                        ArrayList<Integer> res = db.selectUserOrders(customer_id);
                        System.out.printf("%s\n", "ORDER_ID");
                        System.out.println("--------");
                        for (int rd:res) {
                            System.out.printf("%d\n", rd);
                        }
                    } else if (action.equals("q")) {
                        // quit
                        goBack = true;
                    } else if (action.equals("?")) {
                        // show menu
                        customer_menu();
                    } else {
                        // invalid action
                        System.out.println("Invalid action '" + action + "', enter '?' for help or 'q' to go back");
                    }
                }
            } else if (action.equals("G")) {
                // List all groups
                ArrayList<ArrayList<String>> res = db.selectAllGroups();
                System.out.printf("%-14s %s\n", "DISCOUNT_CODE", "GROUP_NAME");
                System.out.println("-------------------------");
                for (ArrayList<String> rs:res) {
                    System.out.printf("%-14s %s\n", rs.get(0), rs.get(1));
                }
            } else if (action.equals("g")) {
                // select a group
                String discount_code;
                boolean goBack = false;
                System.out.println("Please enter a valid discount_code");
                discount_code = s.nextLine();
                ArrayList<String> result = db.selectOneGroup(discount_code);
                if (result.size() == 0) {
                    System.out.println("Group with discount code '" + discount_code + "' does not exist");
                    goBack = true;
                } else {
                    System.out.println("Group '" + discount_code + "' selected");
                }
                while (!goBack) {
                    actions = "1q?";
                    System.out.print("[" + actions + "] :> ");
                    action = s.nextLine();
                    if (action.equals("1")) {
                        // List all customers in this group
                        ArrayList<ArrayList<String>> res = db.selectGroupMembers(discount_code);
                        System.out.printf("%-12s %s\n", "CUSTOMER_ID", "CUSTOMER_NAME");
                        System.out.println("--------------------------");
                        for (ArrayList<String> rs:res) {
                            System.out.printf("%-12s %s\n", rs.get(0), rs.get(1));
                        }
                    } else if (action.equals("q")) {
                        // Go back to main menu
                        goBack = true;
                    } else if (action.equals("?")) {
                        // Show help msg
                        group_menu();
                    } else {
                        System.out.println("Invalid action '" + action + "', enter '?' for help or 'q' to go back");
                    }
                }
            } else if (action.equals("O")) {
                // List all orders
                ArrayList<ArrayList<String>> res = db.selectAllOrders();
                System.out.printf("%-8s %-12s %-14 %-14s %-14s %-10s %-4s %s\n", "ORDER_ID", "CUSTOMER_ID", "DISCOUNT_CODE", "INSURANCE_TYPE", "INCLUDED_MILES", "TOT_MILES", "TANK", "DROPOFF_LOC");
                System.out.println("----------------------------------------------------------------------------------------------");
                for (ArrayList<String> rs:res) {
                    System.out.printf("%-8s %-12s %-14 %-14s %-14s %-10s %-4s %s\n", rs.get(0), rs.get(1), rs.get(2), rs.get(3), rs.get(4), rs.get(5), rs.get(6), rs.get(7));
                }
            } else if (action.equals("q")) {
                // quit app
                quitApp = true;
            } else if (action.equals("?")) {
                // show help msg
                menu();
            } else {
                // invalid action
                System.out.println("Invalid action '" + action + "', enter '?' for help or 'q' to quit");
            }
        }

        s.close();
    }

    static void menu() {
        System.out.println("Main Menu");
        System.out.println("    [L] List all tables");
        System.out.println("    [C] List all customers");
        System.out.println("    [c] Select a customer");
        System.out.println("    [G] List all groups");
        System.out.println("    [g] Select a group");
        System.out.println("    [O] List all orders");
        System.out.println("    [q] Quit app");
        System.out.println("    [?] Show this menu");
    }

    static void customer_menu() {
        System.out.println("Customer Menu");
        System.out.println("    [1] List all groups that contain this customer");
        System.out.println("    [2] List all orders created by this customer");
        System.out.println("    [q] Go back to main menu");
        System.out.println("    [?] Show this menu");
    }

    static void group_menu() {
        System.out.println("Group Menu");
        System.out.println("    [1] Lis all members in this group");
        System.out.println("    [q] Go back to main menu");
        System.out.println("    [?] Show this menu");
    }
}