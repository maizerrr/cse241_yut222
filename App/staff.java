/**
 * CSE241 Final Project
 * Author: Yuming Tian
 */

import java.util.Scanner;
import java.util.ArrayList;

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
                // TODO: implement customer menu
            } else if (action.equals("2")) {
                // manage orders
                //TODO: implement order menu
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

        System.out.println("    [q] Go back to main menu");
        System.out.println("    [?] Show this menu");
    }

    static void order_menu() {
        System.out.println("Manage Orders");

        System.out.println("    [q] Go back to main menu");
        System.out.println("    [?] Show this menu");
    }
}