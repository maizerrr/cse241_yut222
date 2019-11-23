/**
 * CSE241 Final Project
 * Author: Yuming Tian
 */

import java.util.Scanner;
import java.util.ArrayList;

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
            }
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
        }

        // if not quit, keep looping
        while (!quitApp) {
            // valid actions
            String actions = "12q?";
            System.out.print("[" + actions + "] :> ");
            String action = s.nextLine();
            if (action.equals("1")) {
                // personal info
                boolean goBack = false;
                while (!goBack) {
                    // TODO: implement info_menu
                }
            } else if (action.equals("2")) {
                // manage orders
                boolean goBack = false;
                while (!goBack) {
                    // TODO: implement order_menu
                }
            } else if (action.equals("q")) {
                // quit
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
        System.out.println("    [1] Personal Info");
        System.out.println("    [2] Manager Orders");
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
}