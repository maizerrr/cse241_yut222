/**
 * CSE241 Final Project
 * Author: Yuming Tian
 */

import java.sql.*;
import java.util.ArrayList;

public class Database {
    /**
     * The connection to oracle database
     */
    Connection conn;

    /**
     * To ist all tables created by the current user
     * Just for testing
     */
    PreparedStatement listAllTables;

    /**
     * List all users, including their uid and personal info
     */
    PreparedStatement selectAllUsers;

    /**
     * Select a specific user
     */
    PreparedStatement selectOneUser;

    /**
     * List all groups that contain a certain user
     */
    PreparedStatement selectUserGroups;

    /**
     * List all orders created by a certain user
     */
    PreparedStatement selectUserOrders;

    /**
     * List all user groups
     */
    PreparedStatement selectAllGroups;

    /**
     * Select a specific group
     */
    PreparedStatement selectOneGroup;

    /**
     * List all members of a certain group
     */
    PreparedStatement selectGroupMembers;

    /**
     * List all orders
     */
    PreparedStatement selectAllOrders;

    /**
     * Select a specific order
     */
    PreparedStatement selectOneOrder;

    private Database() {

    }

    static Database getDatabase(String url, String usr, String pwd) {
        Database db = new Database();

        // init connection to db
        try {
            Class.forName ("oracle.jdbc.driver.OracleDriver");
            Connection con = DriverManager.getConnection(url, usr, pwd);
            db.conn = con;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        // test prepared statement
        try {
            db.listAllTables = db.conn.prepareStatement("SELECT table_name FROM all_tables WHERE owner = ?");
            db.selectAllUsers = db.conn.prepareStatement("SELECT * FROM customers");
            db.selectOneUser = db.conn.prepareStatement("SELECT * FROM customers WHERE customer_id = ?");
            db.selectUserGroups = db.conn.prepareStatement("SELECT discount_code, group_name FROM members NATURAL JOIN user_groups WHERE customer_id = ?");
            db.selectUserOrders = db.conn.prepareStatement("SELECT order_id FROM orders NATURAL JOIN members WHERE customer_id = ?");
            db.selectAllGroups = db.conn.prepareStatement("SELECT * FROM user_groups");
            db.selectOneGroup = db.conn.prepareStatement("SELECT * FROM user_groups WHERE discount_code = ?");
            db.selectGroupMembers = db.conn.prepareStatement("SELECT customer_id, customers_name FROM members NATURAL JOIN customers WHERE discount_code = ?");
            db.selectAllOrders = db.conn.prepareStatement("SELECT * FROM orders NATURAL JOIN members");
            db.selectOneOrder = db.conn.prepareStatement("SELECT * FROM orders NATURAL JOIN members WHERE order_id = ?");
        } catch (Exception e) {
            e.printStackTrace();
            db.disconnect();
            return null;
        }

        return db;
    }

    /**
     * Disconnect with db
     * @return true if successfully closed the connection, false otherwise
     */
    boolean disconnect() {
        if (conn == null) {
            System.err.println("Unable to close connection: Connection was null");
            return false;
        }
        try {
            conn.close();
        } catch (SQLException e) {
            System.err.println("Error: Connection.close() threw a SQLException");
            e.printStackTrace();
            conn = null;
            return false;
        }
        conn = null;
        return true;
    }

    /**
     * List all tables created by the current user
     * @param usr
     * @return ArrayList of table_name
     */
    ArrayList<String> listAllTables(String usr) {
        ArrayList<String> res = new ArrayList<String>();
        try {
            listAllTables.setString(1, usr.toUpperCase());
            ResultSet rs = listAllTables.executeQuery();
            while (rs.next()) {
                res.add(rs.getString("table_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * List all customers and their basic info
     * @return ArrayList of String list
     */
    ArrayList<ArrayList<String>> selectAllUsers() {
        ArrayList<ArrayList<String>> res = new ArrayList<ArrayList<String>>();
        try {
            ResultSet rs = selectAllUsers.executeQuery();
            while (rs.next()) {
                ArrayList<String> row = new ArrayList<String>();
                row.add("" + rs.getInt("customer_id"));
                row.add(rs.getString("customer_name"));
                row.add(rs.getString("address"));
                row.add(rs.getString("driver_license"));
                res.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * List info of a user
     * @param customer_id target user
     * @return ArrayList of String
     */
    ArrayList<String> selectOneUser(int customer_id) {
        ArrayList<String> res = new ArrayList<String>();
        try {
            selectOneUser.setInt(1, customer_id);
            ResultSet rs = selectAllUsers.executeQuery();
            if (rs.next()) {
                res.add("" + rs.getInt("customer_id"));
                res.add(rs.getString("customer_name"));
                res.add(rs.getString("address"));
                res.add(rs.getString("driver_license"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * List discount_code and group_name of all groups that contain a certain customer
     * @param customer_id the target customer
     * @return ArrayList of String list
     */
    ArrayList<ArrayList<String>> selectUserGroups(int customer_id) {
        ArrayList<ArrayList<String>> res = new ArrayList<ArrayList<String>>();
        try {
            selectUserGroups.setInt(1, customer_id);
            ResultSet rs = selectUserGroups.executeQuery();
            while (rs.next()) {
                ArrayList<String> row = new ArrayList<String>();
                row.add(rs.getString("discount_code"));
                row.add(rs.getString("group_name"));
                res.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * List all orders created by a certain customer
     * @param customer_id the target customer
     * @return ArrayList of order_id
     */
    ArrayList<Integer> selectUserOrders(int customer_id) {
        ArrayList<Integer> res = new ArrayList<Integer>();
        try {
            selectUserOrders.setInt(1, customer_id);
            ResultSet rs = selectUserOrders.executeQuery();
            while (rs.next()) {
                res.add(rs.getInt("order_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * List all groups
     * @return Arraylist of String list containning discount_code and group_name
     */
    ArrayList<ArrayList<String>> selectAllGroups() {
        ArrayList<ArrayList<String>> res = new ArrayList<ArrayList<String>>();
        try {
            ResultSet rs = selectAllGroups.executeQuery();
            while (rs.next()) {
                ArrayList<String> row = new ArrayList<String>();
                row.add(rs.getString("discount_code"));
                row.add(rs.getString("group_name"));
                res.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * List all members in a certain group
     * @param discount_code the target group
     * @return ArrayList of String list containning customer_id and customer_name
     */
    ArrayList<ArrayList<String>> selectGroupMembers(String discount_code) {
        ArrayList<ArrayList<String>> res = new ArrayList<ArrayList<String>>();
        try {
            selectGroupMembers.setString(1, discount_code);
            ResultSet rs = selectGroupMembers.executeQuery();
            while (rs.next()) {
                ArrayList<String> row = new ArrayList<String>();
                row.add(rs.getString("customer_id"));
                row.add(rs.getString("customer_name"));
                res.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * List all orders
     * @return ArrayList of String list containning all info in orders table
     */
    ArrayList<ArrayList<String>> selectAllOrders() {
        ArrayList<ArrayList<String>> res = new ArrayList<ArrayList<String>>();
        try {
            ResultSet rs = selectAllOrders.executeQuery();
            while (rs.next()) {
                ArrayList<String> row = new ArrayList<String>();
                row.add("" + rs.getInt("order_id"));
                row.add(rs.getString("customer_id"));
                row.add(rs.getString("discount_code"));
                row.add(rs.getString("insurance_type"));
                row.add("" + rs.getInt("included_miles"));
                row.add("" + rs.getInt("tot_miles"));
                row.add("" + rs.getInt("tank"));
                row.add(rs.getString("dropoff_loc"));
                res.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }
}