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
            String actions = "Lq?";
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
        System.out.println("    [q] Quit app");
        System.out.println("    [?] Show this table");
    }
}