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
            } else if (action.equals("2")) {
                // Manage insurance plans
            } else if (action.equals("3")) {
                // Manage add-on items
            } else if (action.equals("4")) {
                // Manage vehicles
            } else if (action.equals("5")) {
                // Manage rent centers
            } else if (action.equals("6")) {
                // Manage rental rate
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
}