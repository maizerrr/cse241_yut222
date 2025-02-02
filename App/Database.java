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
     * List all incomplete orders (order with null fields) create by a certain user
     */
    PreparedStatement selectUserIncompleteOrders;

    /**
     * Update user info
     */
    PreparedStatement updateUserName;

    PreparedStatement updateUserAddress;

    PreparedStatement updateUserDriverLicense;

    /**
     * Create a new user
     */
    PreparedStatement insertOneUser;

    /**
     * List all user groups
     */
    PreparedStatement selectAllGroups;

    /**
     * Select a specific group
     */
    PreparedStatement selectOneGroup;

    /**
     * Insert a new user group
     */
    PreparedStatement insertOneGroup;

    /**
     * Update info of a group
     */
    PreparedStatement updateOneGroup;

    /**
     * List all members of a certain group
     */
    PreparedStatement selectGroupMembers;

    /**
     * List all memberships
     */
    PreparedStatement selectAllMemberships;

    /** 
     * Select a membership based on customer_id and discount_code
     */
    PreparedStatement selectAMembership;

    /**
     * Add a user to a user group
     */
    PreparedStatement insertOneMembership;

    /**
     * List all orders
     */
    PreparedStatement selectAllOrders;

    /**
     * Select a specific order
     */
    PreparedStatement selectOneOrder;

    /**
     * Create a new order
     */
    PreparedStatement insertOneOrder;

    /**
     * Update an order
     */
    PreparedStatement updateOneOrder;

    /**
     * List all available insurance plans
     */
    PreparedStatement selectAllInsurance;

    /**
     * Select an insurance plan based on insurance_type
     */
    PreparedStatement selectAnInsurance;

    /**
     * Insert a new insurance plan
     */
    PreparedStatement insertOneInsurance;

    /**
     * Update an insurance plan
     */
    PreparedStatement updateOneInsurance;

    /**
     * List all add-on items
     */
    PreparedStatement selectAllItems;

    /**
     * Select an item based on item name
     */
    PreparedStatement selectAnItem;

    /**
     * Add an item
     */
    PreparedStatement insertOneItem;

    /**
     * Update the price of an item
     */
    PreparedStatement updateOneItem;

    /**
     * List all add-ons of a specific order
     */
    PreparedStatement selectAddOns;

    /**
     * insert an add-on item to an order
     */
    PreparedStatement insertAddOns;

    /**
     * List all rent centers
     */
    PreparedStatement selectAllRentCenters;

    /**
     * Select a rent center
     */
    PreparedStatement selectOneRentCenter;

    /**
     * Add a rent center
     */
    PreparedStatement insertOneRentCenter;

    /**
     * Update a rent center
     */
    PreparedStatement updateOneRentCenter;

    /**
     * List all vehicles
     */
    PreparedStatement selectAllVehicles;

    /**
     * List all available vehicles at a specific rent center
     */
    PreparedStatement selectAllAvaliableVehicles;

    /**
     * List all vehicles at a rent center
     */
    PreparedStatement selectCenterVehicles;

    /**
     * Select a vehicle based on plate_no
     */
    PreparedStatement selectAVehicle;

    /**
     * Insert a new vehicle
     */
    PreparedStatement insertOneVehicle;

    /**
     * Update odometer after a vehicle is returned
     */
    PreparedStatement updateOdometer;

    /**
     * List order records of all vehicles
     */
    PreparedStatement selectAllVehicleOrder;

    

    /**
     * Insert a record, for creating new order
     */
    PreparedStatement insertVehicleOrder;

    /**
     * List rental for all types of vehicles
     */
    PreparedStatement selectAllRental;

    /**
     * List vehicle rental based on type
     */
    PreparedStatement selectVehicleRental;

    /**
     * Add a new type of vehicle and define rental
     */
    PreparedStatement insertVehicleRental;

    /**
     * Edit rental of a type of vehicles
     */
    PreparedStatement updateVehicleRental;

    private Database() {

    }

    static Database getDatabase(String url, String usr, String pwd) {
        Database db = new Database();

        // init connection to db
        try {
            Class.forName ("oracle.jdbc.driver.OracleDriver");
            Connection con = DriverManager.getConnection(url, usr, pwd);
            db.conn = con;
            db.conn.setAutoCommit(false);
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
            db.selectUserOrders = db.conn.prepareStatement("SELECT order_id, discount_code, insurance_type, plate_no, included_miles, tot_miles, tank, dropoff_loc, start_time, end_time FROM orders NATURAL JOIN members NATURAL JOIN vehicle_order WHERE customer_id = ?");
            db.selectUserIncompleteOrders = db.conn.prepareStatement("SELECT order_id, discount_code, insurance_type, plate_no, included_miles, tot_miles, tank, dropoff_loc, start_time, end_time FROM orders NATURAL JOIN members NATURAL JOIN vehicle_order WHERE customer_id = ? AND order_id NOT IN (SELECT order_id FROM orders WHERE tot_miles > 0)");
            db.updateUserName = db.conn.prepareStatement("UPDATE customers SET customer_name = ? WHERE customer_id = ?");
            db.updateUserAddress = db.conn.prepareStatement("UPDATE customers SET address = ? WHERE customer_id = ?");
            db.updateUserDriverLicense = db.conn.prepareStatement("UPDATE customers SET driver_license = ? WHERE customer_id = ?");
            db.insertOneUser = db.conn.prepareStatement("INSERT INTO customers VALUES (default, ?, ?, ?)", new String[]{"customer_id"});

            db.selectAllGroups = db.conn.prepareStatement("SELECT * FROM user_groups");
            db.selectOneGroup = db.conn.prepareStatement("SELECT * FROM user_groups WHERE discount_code = ?");
            db.selectGroupMembers = db.conn.prepareStatement("SELECT customer_id, customer_name FROM members NATURAL JOIN customers WHERE discount_code = ?");
            db.insertOneGroup = db.conn.prepareStatement("INSERT INTO user_groups VALUES (?, ?, ?)");
            db.updateOneGroup = db.conn.prepareStatement("UPDATE user_groups SET group_name = ?, discount_rate = ? WHERE discount_code = ?");

            db.selectAllMemberships = db.conn.prepareStatement("SELECT * FROM members");
            db.selectAMembership = db.conn.prepareStatement("SELECT * FROM members WHERE customer_id = ? AND discount_code = ?");
            db.insertOneMembership = db.conn.prepareStatement("INSERT INTO members VALUES (default, ?, ?)");
            
            db.selectAllOrders = db.conn.prepareStatement("SELECT order_id, discount_code, insurance_type, plate_no, included_miles, tot_miles, tank, dropoff_loc, start_time, end_time FROM orders NATURAL JOIN members NATURAL JOIN vehicle_order");
            db.selectOneOrder = db.conn.prepareStatement("SELECT order_id, customer_id, discount_code, insurance_type, plate_no, included_miles, tot_miles, tank, dropoff_loc, start_time, end_time FROM orders NATURAL JOIN members NATURAL JOIN vehicle_order WHERE order_id = ?");
            db.insertOneOrder = db.conn.prepareStatement("INSERT INTO orders VALUES (default, ?, ?, ?, ?, ?, ?)", new String[]{"order_id"});
            db.updateOneOrder = db.conn.prepareStatement("UPDATE orders SET included_miles = ?, tot_miles = ?, tank = ?, dropoff_loc = ? WHERE order_id = ?");

            db.selectAllInsurance = db.conn.prepareStatement("SELECT * FROM insurance");
            db.selectAnInsurance = db.conn.prepareStatement("SELECT * FROM insurance WHERE insurance_type = ?");
            db.insertOneInsurance = db.conn.prepareStatement("INSERT INTO insurance VALUES (?, ?, ?, ?)");
            db.updateOneInsurance = db.conn.prepareStatement("UPDATE insurance SET price_per_hour = ?, price_per_day = ?, price_per_week = ? WHERE insurance_type = ?");

            db.selectAllItems = db.conn.prepareStatement("SELECT * FROM items");
            db.selectAnItem = db.conn.prepareStatement("SELECT * FROM items WHERE item =?");
            db.insertOneItem = db.conn.prepareStatement("INSERT INTO items VALUES (?, ?)");
            db.updateOneItem = db.conn.prepareStatement("UPDATE items SET price = ? WHERE item = ?");

            db.selectAddOns = db.conn.prepareStatement("SELECT * FROM add_ons WHERE order_id = ?");
            db.insertAddOns = db.conn.prepareStatement("INSERT INTO add_ons VALUES (?, ?, ?)");

            db.selectAllRentCenters = db.conn.prepareStatement("SELECT * FROM rent_centers");
            db.selectOneRentCenter = db.conn.prepareStatement("SELECT * FROM rent_centers WHERE center_name = ?");
            db.insertOneRentCenter = db.conn.prepareStatement("INSERT INTO rent_centers VALUES (?, ?)");
            db.updateOneRentCenter = db.conn.prepareStatement("UPDATE rent_centers SET loc = ? WHERE center_name = ?");

            db.selectAllVehicles = db.conn.prepareStatement("SELECT * FROM vehicle");
            db.selectAllAvaliableVehicles = db.conn.prepareStatement("SELECT * FROM vehicle WHERE plate_no NOT IN (SELECT plate_no FROM vehicle_order WHERE (start_time <= ? AND end_time > ?) OR (start_time > ? AND start_time < ?)) AND rent_center = ?");
            db.selectCenterVehicles = db.conn.prepareStatement("SELECT * FROM vehicle WHERE rent_center = ?");
            db.selectAVehicle = db.conn.prepareStatement("SELECT * FROM vehicle WHERE plate_no = ?");
            db.insertOneVehicle = db.conn.prepareStatement("INSERT INTO vehicle VALUES (?, ?, ?, ?, ?, ?)");
            db.updateOdometer = db.conn.prepareStatement("UPDATE vehicle SET odometer = ? WHERE plate_no = ?");

            db.selectAllVehicleOrder = db.conn.prepareStatement("SELECT * FROM vehicle_order");
            db.insertVehicleOrder = db.conn.prepareStatement("INSERT INTO vehicle_order VALUES (?, ?, ?, ?)");

            db.selectAllRental = db.conn.prepareStatement("SELECT * FROM rental");
            db.selectVehicleRental = db.conn.prepareStatement("SELECT * FROM rental WHERE type = ?");
            db.insertVehicleRental = db.conn.prepareStatement("INSERT INTO rental VALUES (?, ?, ?, ?)");
            db.updateVehicleRental = db.conn.prepareStatement("UPDATE rental SET rental_by_hours = ?, rental_by_days = ?, rental_by_weeks = ? WHERE type = ?");
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

    /*
    boolean init() {
        try {

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    */

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
            ResultSet rs = selectOneUser.executeQuery();
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
    ArrayList<ArrayList<String>> selectUserOrders(int customer_id) {
        ArrayList<ArrayList<String>> res = new ArrayList<ArrayList<String>>();
        try {
            selectUserOrders.setInt(1, customer_id);
            ResultSet rs = selectUserOrders.executeQuery();
            while (rs.next()) {
                ArrayList<String> row = new ArrayList<String>();
                row.add("" + rs.getInt("order_id"));
                row.add(rs.getString("discount_code"));
                row.add(rs.getString("insurance_type"));
                row.add(rs.getString("plate_no"));
                row.add("" + rs.getInt("included_miles"));
                row.add("" + rs.getInt("tot_miles"));
                row.add("" + rs.getInt("tank"));
                row.add(rs.getString("dropoff_loc"));
                row.add(rs.getString("start_time"));
                row.add(rs.getString("end_time"));
                res.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Select orders created by a user which have null fields
     * @param customer_id
     * @return
     */
    ArrayList<ArrayList<String>> selectUserIncompleteOrders(int customer_id) {
        ArrayList<ArrayList<String>> res = new ArrayList<ArrayList<String>>();
        try {
            selectUserIncompleteOrders.setInt(1, customer_id);
            ResultSet rs = selectUserIncompleteOrders.executeQuery();
            while (rs.next()) {
                ArrayList<String> row = new ArrayList<String>();
                row.add("" + rs.getInt("order_id"));
                row.add(rs.getString("discount_code"));
                row.add(rs.getString("insurance_type"));
                row.add(rs.getString("plate_no"));
                row.add("" + rs.getInt("included_miles"));
                row.add("" + rs.getInt("tot_miles"));
                row.add("" + rs.getInt("tank"));
                row.add(rs.getString("dropoff_loc"));
                row.add(rs.getString("start_time"));
                row.add(rs.getString("end_time"));
                res.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Update customer's name
     * @param customer_id the target customer
     * @param customer_name the name user wants to change to
     * @return number of rows updated
     */
    int updateUserName(int customer_id, String customer_name) {
        int count = 0;
        try {
            updateUserName.setInt(2, customer_id);
            updateUserName.setString(1, customer_name);
            count += updateUserName.executeUpdate();
            this.conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Update customer's address
     * @param customer_id the target customer
     * @param customer_name the address user wants to change to
     * @return number of rows updated
     */
    int updateUserAddress(int customer_id, String address) {
        int count = 0;
        try {
            updateUserAddress.setInt(2, customer_id);
            updateUserAddress.setString(1, address);
            count += updateUserAddress.executeUpdate();
            this.conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Update customer's driver license
     * @param customer_id the target customer
     * @param customer_name the driver license user wants to change to
     * @return number of rows updated
     */
    int updateUserDriverLicense(int customer_id, String driver_license) {
        int count = 0;
        try {
            updateUserDriverLicense.setInt(2, customer_id);
            updateUserDriverLicense.setString(1, driver_license);
            count += updateUserDriverLicense.executeUpdate();
            this.conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Insert a customer to customers and add that customer to default user group
     * @param customer_name
     * @param address
     * @param driver_license
     * @return customer_id
     */
    int insertOneUser(String customer_name, String address, String driver_license) {
        try {
            // try to create default user group
            if (selectOneGroup("00000000").size() == 0) {
                if (insertOneGroup("00000000", "default", 1) > 0) {
                    System.out.println("Successfully init default user group");
                } else {
                    return -1;
                }
            }

            // insert one customer into customers
            insertOneUser.setString(1, customer_name);
            insertOneUser.setString(2, address);
            insertOneUser.setString(3, driver_license);
            insertOneUser.executeUpdate();
            ResultSet rs = insertOneUser.getGeneratedKeys();
            int customer_id = -1;
            if (rs.next()) {
                customer_id = Integer.parseInt(rs.getString(1));
            } else {
                this.conn.rollback();
                return -1;
            }

            // add that customer to default user group
            insertOneMembership.setInt(1, customer_id);
            insertOneMembership.setString(2, "00000000");
            if (insertOneMembership.executeUpdate() < 1) {
                this.conn.rollback();
                return -1;
            }
            this.conn.commit();
            return customer_id;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    //-----------------------------------------------------------------------------------

    /**
     * List all groups
     * @return Arraylist of String list containning discount_code, group_name, and discount_rate
     */
    ArrayList<ArrayList<String>> selectAllGroups() {
        ArrayList<ArrayList<String>> res = new ArrayList<ArrayList<String>>();
        try {
            ResultSet rs = selectAllGroups.executeQuery();
            while (rs.next()) {
                ArrayList<String> row = new ArrayList<String>();
                row.add(rs.getString("discount_code"));
                row.add(rs.getString("group_name"));
                row.add(rs.getString("discount_rate"));
                res.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * List info of a group
     * @param discount_code the target group
     * @return ArrayList of String
     */
    ArrayList<String> selectOneGroup(String discount_code) {
        ArrayList<String> res = new ArrayList<String>();
        try {
            selectOneGroup.setString(1, discount_code);
            ResultSet rs = selectOneGroup.executeQuery();
            if (rs.next()) {
                res.add(rs.getString("discount_code"));
                res.add(rs.getString("group_name"));
                res.add(rs.getString("discount_rate"));
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
     * Add a new group
     * @param discount_code
     * @param group_name
     * @param discount_rate
     * @return num of rows inserted, or -1 if failed
     */
    int insertOneGroup(String discount_code, String group_name, double discount_rate) {
        try {
            insertOneGroup.setString(1, discount_code);
            insertOneGroup.setString(2, group_name);
            insertOneGroup.setDouble(3, discount_rate);
            int res = insertOneGroup.executeUpdate();
            if (res > 0) {
                this.conn.commit();
                return res;
            } else {
                this.conn.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Update a group
     * @param discount_code
     * @param group_name
     * @param discount_rate
     * @return num of rows updated, or -1 if failed
     */
    int updateOneGroup(String discount_code, String group_name, double discount_rate) {
        try {
            selectOneGroup.setString(1, discount_code);
            ResultSet rs = selectOneGroup.executeQuery();
            if (rs.next()) {
                String g_name = rs.getString("group_name");
                Double d_rate = rs.getDouble("discount_rate");
                if (group_name.length() == 0)
                    group_name = g_name;
                if (discount_rate == -1)
                    discount_rate = d_rate;
                updateOneGroup.setString(1, group_name);
                updateOneGroup.setDouble(2, discount_rate);
                updateOneGroup.setString(3, discount_code);
                int res = updateOneGroup.executeUpdate();
                if (res > 0) {
                    this.conn.commit();
                    return res;
                } else {
                    this.conn.rollback();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    //-----------------------------------------------------------------------------------
    /**
     * Select a membership based on customer_id and discount_code
     * @param customer_id
     * @param discount_code
     * @return
     */
    ArrayList<String> selectAMembership (int customer_id, String discount_code) {
        ArrayList<String> res = new ArrayList<String>();
        try {
            selectAMembership.setInt(1, customer_id);
            selectAMembership.setString(2, discount_code);
            ResultSet rs = selectAMembership.executeQuery();
            if (rs.next()) {
                res.add(rs.getString("membership"));
                res.add(rs.getString("customer_id"));
                res.add(rs.getString("discount_code"));
            }   
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Add a customer to a user grou[]
     * @param customer_id
     * @param discount_code
     * @return true if successfully inserted, false otherwise
     */
    boolean insertOneMembership (int customer_id, String discount_code) {
        try {
            insertOneMembership.setInt(1, customer_id);
            insertOneMembership.setString(2, discount_code);
            if (insertOneMembership.executeUpdate() > 0) {
                this.conn.commit();
                return true;
            } else {
                this.conn.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    //-----------------------------------------------------------------------------------

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

    /**
     * List info of an order
     * @param order_id the target order
     * @return ArrayList of String
     */
    ArrayList<String> selectOneOrder(int order_id) {
        ArrayList<String> res = new ArrayList<String>();
        try {
            selectOneOrder.setInt(1, order_id);
            ResultSet rs = selectOneOrder.executeQuery();
            if (rs.next()) {
                res.add(rs.getString("order_id"));
                res.add(rs.getString("customer_id"));
                res.add(rs.getString("discount_code"));
                res.add(rs.getString("insurance_type"));
                res.add(rs.getString("plate_no"));
                res.add(rs.getString("included_miles"));
                res.add(rs.getString("tot_miles"));
                res.add(rs.getString("tank"));
                res.add(rs.getString("dropoff_loc"));
                res.add(rs.getString("start_time"));
                res.add(rs.getString("end_time"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    ArrayList<Timestamp> selectOneOrderTime(int order_id) {
        ArrayList<Timestamp> res = new ArrayList<Timestamp>();
        try {
            selectOneOrder.setInt(1, order_id);
            ResultSet rs = selectOneOrder.executeQuery();
            if (rs.next()) {
                res.add(rs.getTimestamp("start_time"));
                res.add(rs.getTimestamp("end_time"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Create a new order
     * @param membership
     * @param insurance_type
     * @param included_miles
     * @return order id, or -1 if failed
     */
    int insertOneOrder(int membership, String insurance_type, double included_miles, String plate_no, Timestamp start_time, Timestamp end_time) {
        try {
            // modify orders
            insertOneOrder.setInt(1, membership);
            insertOneOrder.setString(2, insurance_type);
            insertOneOrder.setDouble(3, included_miles);
            insertOneOrder.setNull(4, Types.NUMERIC);
            insertOneOrder.setNull(5, Types.NUMERIC);
            insertOneOrder.setNull(6, Types.VARCHAR);
            insertOneOrder.executeUpdate();
            ResultSet rs = insertOneOrder.getGeneratedKeys();
            int order_id = -1;
            if (rs.next())
                order_id = Integer.parseInt(rs.getString(1));
            if (order_id == -1) {
                this.conn.rollback();
                return -1;
            }

            // modify vehicle_order
            insertVehicleOrder.setInt(1, order_id);
            insertVehicleOrder.setString(2, plate_no);
            insertVehicleOrder.setTimestamp(3, start_time);
            insertVehicleOrder.setTimestamp(4, end_time);
            if (insertVehicleOrder.executeUpdate() < 1) {
                this.conn.rollback();
                return -1;
            }
            this.conn.commit();
            return order_id;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Create a new order with ad-ons
     * @param membership
     * @param insurance_type
     * @param included_miles
     * @param plate_no
     * @param start_time
     * @param end_time
     * @param items
     * @return order id, or -1 if failed
     */
    int insertOneOrder(int membership, String insurance_type, double included_miles, String plate_no, Timestamp start_time, Timestamp end_time, ArrayList<String> items) {
        try {
            // modify orders
            insertOneOrder.setInt(1, membership);
            insertOneOrder.setString(2, insurance_type);
            insertOneOrder.setDouble(3, included_miles);
            insertOneOrder.setNull(4, Types.NUMERIC);
            insertOneOrder.setNull(5, Types.NUMERIC);
            insertOneOrder.setNull(6, Types.VARCHAR);
            insertOneOrder.executeUpdate();
            ResultSet rs = insertOneOrder.getGeneratedKeys();
            int order_id = -1;
            if (rs.next())
                order_id = Integer.parseInt(rs.getString(1));
            if (order_id == -1) {
                this.conn.rollback();
                return -1;
            }

            // modify vehicle_order
            insertVehicleOrder.setInt(1, order_id);
            insertVehicleOrder.setString(2, plate_no);
            insertVehicleOrder.setTimestamp(3, start_time);
            insertVehicleOrder.setTimestamp(4, end_time);
            if (insertVehicleOrder.executeUpdate() < 1) {
                this.conn.rollback();
                return -1;
            }

            // modify add_ons
            while (items.size() > 0) {
                String item = items.get(0);
                int number = 1;
                for (int i = 1; i < items.size(); i++) {
                    if (items.get(i).equals(item)) {
                        number++;
                        i--;
                        items.remove(i);
                    }
                }
                insertAddOns.setInt(1, order_id);
                insertAddOns.setString(2, item);
                insertAddOns.setInt(3, number);
                if (insertAddOns.executeUpdate() < 1) {
                    this.conn.rollback();
                    return -1;
                }
                items.remove(0);
            }
            this.conn.commit();
            return order_id;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Update an order
     * @param membership
     * @param insurance_type
     * @param included_miles
     * @param tot_miles
     * @param tank
     * @param dropoff_loc
     * @return num of rows updated, or -1 if failed to update
     */
    int updateOneOrder(int order_id, double included_miles, double tot_miles, double tank, String dropoff_loc) {
        try {
            updateOneOrder.setDouble(1, included_miles);
            updateOneOrder.setDouble(2, tot_miles);
            updateOneOrder.setDouble(3, tank);
            updateOneOrder.setString(4, dropoff_loc);
            updateOneOrder.setInt(5, order_id);
            int res = updateOneOrder.executeUpdate();
            selectOneOrder.setInt(1, order_id);
            ResultSet rs = selectOneOrder.executeQuery();
            if (rs.next()) {
                String plate_no = rs.getString("plate_no");
                if (res > 0 && updateOdometer(plate_no, tot_miles) > 0) {
                    this.conn.commit();
                    return res;
                } else {
                    this.conn.rollback();
                    return -1;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    //-----------------------------------------------------------------------------------
    /**
     * List all insurance plans and their price
     * @return ArrayList of String list containning all info
     */
    ArrayList<ArrayList<String>> selectAllInsurance() {
        ArrayList<ArrayList<String>> res = new ArrayList<ArrayList<String>>();
        try {
            ResultSet rs = selectAllInsurance.executeQuery();
            while (rs.next()) {
                ArrayList<String> row = new ArrayList<String>();
                row.add(rs.getString("insurance_type"));
                row.add(rs.getString("price_per_hour"));
                row.add(rs.getString("price_per_day"));
                row.add(rs.getString("price_per_week"));
                res.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Select an insurance plan based on insurance_type
     * @param insurance_type
     * @return
     */
    ArrayList<String> selectAnInsurance(String insurance_type) {
        ArrayList<String> res = new ArrayList<String>();
        try {
            selectAnInsurance.setString(1, insurance_type);
            ResultSet rs = selectAnInsurance.executeQuery();
            if (rs.next()) {
                res.add(rs.getString("insurance_type"));
                res.add(rs.getString("price_per_hour"));
                res.add(rs.getString("price_per_day"));
                res.add(rs.getString("price_per_week"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Insert a new insurance plan
     * @param insurance_type
     * @param price_per_hour
     * @param price_per_day
     * @param price_per_week
     * @return num of rows inserted, or -1 if failed
     */
    int insertOneInsurance(String insurance_type, double price_per_hour, double price_per_day, double price_per_week) {
        try {
            insertOneInsurance.setString(1, insurance_type);
            insertOneInsurance.setDouble(2, price_per_hour);
            insertOneInsurance.setDouble(3, price_per_day);
            insertOneInsurance.setDouble(4, price_per_week);
            int res = insertOneInsurance.executeUpdate();
            if (res > 0) {
                this.conn.commit();
                return res;
            } else {
                this.conn.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Update an insurance
     * @param insurance_type
     * @param price_per_hour
     * @param price_per_day
     * @param price_per_week
     * @return num of rows updated, or -1 if failed
     */
    int updateOneInsurance(String insurance_type, double price_per_hour, double price_per_day, double price_per_week) {
        try {
            selectAnInsurance.setString(1, insurance_type);
            ResultSet rs = selectAnInsurance.executeQuery();
            if (rs.next()) {
                double p_hour = rs.getDouble("price_per_hour");
                double p_day = rs.getDouble("price_per_day");
                double p_week = rs.getDouble("price_per_week");
                if (price_per_hour < 0)
                    price_per_hour = p_hour;
                if (price_per_day < 0)
                    price_per_day = p_day;
                if (price_per_week < 0)
                    price_per_week = p_week;
                updateOneInsurance.setDouble(1, price_per_hour);
                updateOneInsurance.setDouble(2, price_per_day);
                updateOneInsurance.setDouble(3, price_per_week);
                updateOneInsurance.setString(4, insurance_type);
                int res = updateOneInsurance.executeUpdate();
                if (res > 0) {
                    this.conn.commit();
                    return res;
                } else {
                    this.conn.rollback();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    //-----------------------------------------------------------------------------------
    /**
     * List all itmes
     * @return
     */
    ArrayList<ArrayList<String>> selectAllItems() {
        ArrayList<ArrayList<String>> res = new ArrayList<ArrayList<String>>();
        try {
            ResultSet rs = selectAllItems.executeQuery();
            while (rs.next()) {
                ArrayList<String> row = new ArrayList<String>();
                row.add(rs.getString("item"));
                row.add(rs.getString("price"));
                res.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Select an item based on item name
     * @param item
     * @return
     */
    ArrayList<String> selectAnItem(String item) {
        ArrayList<String> res = new ArrayList<String>();
        try {
            selectAnItem.setString(1, item);
            ResultSet rs = selectAnItem.executeQuery();
            if (rs.next()) {
                res.add(rs.getString("item"));
                res.add(rs.getString("price"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Add a new item
     * @param item
     * @param price
     * @return num of rows inserted, or -1 if failed
     */
    int insertOneItem(String item, double price) {
        try {
            insertOneItem.setString(1, item);
            insertOneItem.setDouble(2, price);
            int res = insertOneItem.executeUpdate();
            if (res > 0) {
                this.conn.commit();
                return res;
            } else {
                this.conn.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Update the price of an item
     * @param item
     * @param price
     * @return num of rows updated, or -1 if failed
     */
    int updateOneItem(String item, double price) {
        try {
            updateOneItem.setDouble(1, price);
            updateOneItem.setString(2, item);
            int res = updateOneItem.executeUpdate();
            if (res > 0) {
                this.conn.commit();
                return res;
            } else {
                this.conn.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    //-----------------------------------------------------------------------------------
    /**
     * List add_on items of a given order
     * @param order_id
     * @return
     */
    ArrayList<ArrayList<String>> selectAddOns(int order_id) {
        ArrayList<ArrayList<String>> res = new ArrayList<ArrayList<String>>();
        try {
            selectAddOns.setInt(1, order_id);
            ResultSet rs = selectAddOns.executeQuery();
            while (rs.next()) {
                ArrayList<String> row = new ArrayList<String>();
                row.add(rs.getString("item"));
                row.add(rs.getString("num_of_item"));
                res.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    //-----------------------------------------------------------------------------------
    /**
     * List all rent centers
     * @return center_name and loc
     */
    ArrayList<ArrayList<String>> selectAllRentCenters() {
        ArrayList<ArrayList<String>> res = new ArrayList<ArrayList<String>>();
        try {
            ResultSet rs = selectAllRentCenters.executeQuery();
            while (rs.next()) {
                ArrayList<String> row = new ArrayList<String>();
                row.add(rs.getString("center_name"));
                row.add(rs.getString("loc"));
                res.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Select one rent center
     * @param center_name
     * @return
     */
    ArrayList<String> selectOneRentCenter(String center_name) {
        ArrayList<String> res = new ArrayList<String>();
        try {
            selectOneRentCenter.setString(1, center_name);
            ResultSet rs = selectOneRentCenter.executeQuery();
            if (rs.next()) {
                res.add(rs.getString("center_name"));
                res.add(rs.getString("loc"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Insert one rent center
     * @param center_name
     * @param loc
     * @return num of rows inserted, or -1 if failed
     */
    int insertOneRentCenter(String center_name, String loc) {
        try {
            insertOneRentCenter.setString(1, center_name);
            insertOneRentCenter.setString(2, loc);
            int res = insertOneRentCenter.executeUpdate();
            if (res > 0) {
                this.conn.commit();
                return res;
            } else {
                this.conn.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Update the location of a rent center
     * @param center_name
     * @param loc
     * @return num of rows updated, or -1 if failed
     */
    int updateOneRentCenter(String center_name, String loc) {
        try {
            updateOneRentCenter.setString(1, loc);
            updateOneRentCenter.setString(2, center_name);
            int res = updateOneRentCenter.executeUpdate();
            if (res > 0) {
                this.conn.commit();
                return res;
            } else {
                this.conn.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    //-----------------------------------------------------------------------------------
    /**
     * List all vehicles
     * @return plate_no, make, model, type, odometer
     */
    ArrayList<ArrayList<String>> selectAllVehicles() {
        ArrayList<ArrayList<String>> res = new ArrayList<ArrayList<String>>();
        try {
            ResultSet rs = selectAllVehicles.executeQuery();
            while (rs.next()) {
                ArrayList<String> row = new ArrayList<String>();
                row.add(rs.getString("plate_no"));
                row.add(rs.getString("make"));
                row.add(rs.getString("model"));
                row.add(rs.getString("type"));
                row.add(rs.getString("rent_center"));
                row.add(rs.getString("odometer"));
                res.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * List all avaliable vehicles at a specific rent center
     * @param start_time The time the rent period starts
     * @param end_time The time the rent period ends
     * @param center_name The rent center from where user wants to rent the vehicle
     * @return ArrayList of String list containning the vehicle info
     */
    ArrayList<ArrayList<String>> selectAllAvailableVehicles(Timestamp start_time, Timestamp end_time, String center_name) {
        ArrayList<ArrayList<String>> res = new ArrayList<ArrayList<String>>();
        try {
            selectAllAvaliableVehicles.setTimestamp(1, start_time);
            selectAllAvaliableVehicles.setTimestamp(2, start_time);
            selectAllAvaliableVehicles.setTimestamp(3, start_time);
            selectAllAvaliableVehicles.setTimestamp(4, end_time);
            selectAllAvaliableVehicles.setString(5, center_name);
            ResultSet rs = selectAllAvaliableVehicles.executeQuery();
            while (rs.next()) {
                ArrayList<String> row = new ArrayList<String>();
                row.add(rs.getString("plate_no"));
                row.add(rs.getString("make"));
                row.add(rs.getString("model"));
                row.add(rs.getString("type"));
                row.add(rs.getString("rent_center"));
                row.add(rs.getString("odometer"));
                res.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * List all vehicles at a rent center
     * @param rent_center
     * @return
     */
    ArrayList<ArrayList<String>> selectCenterVehicles(String rent_center) {
        ArrayList<ArrayList<String>> res = new ArrayList<ArrayList<String>>();
        try {
            selectCenterVehicles.setString(1, rent_center);
            ResultSet rs = selectCenterVehicles.executeQuery();
            while (rs.next()) {
                ArrayList<String> row = new ArrayList<String>();
                row.add(rs.getString("plate_no"));
                row.add(rs.getString("make"));
                row.add(rs.getString("model"));
                row.add(rs.getString("type"));
                row.add(rs.getString("rent_center"));
                row.add(rs.getString("odometer"));
                res.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Select a vehicle based on given plate_no
     * @param plate_no
     * @return
     */
    ArrayList<String> selectAVehicle(String plate_no) {
        ArrayList<String> res = new ArrayList<String>();
        try {
            selectAVehicle.setString(1, plate_no);
            ResultSet rs = selectAVehicle.executeQuery();
            if (rs.next()) {
                res.add(rs.getString("plate_no"));
                res.add(rs.getString("make"));
                res.add(rs.getString("model"));
                res.add(rs.getString("type"));
                res.add(rs.getString("rent_center"));
                res.add(rs.getString("odometer"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Insert a new vehicle
     * @param plate_no
     * @param make
     * @param model
     * @param type
     * @param rent_center
     * @param odometer
     * @return num of rows inserted, or -1 if failed
     */
    int insertOneVehicle(String plate_no, String make, String model, String type, String rent_center, double odometer) {
        try {
            insertOneVehicle.setString(1, plate_no);
            insertOneVehicle.setString(2, make);
            insertOneVehicle.setString(3, model);
            insertOneVehicle.setString(4, type);
            insertOneVehicle.setString(5, rent_center);
            insertOneVehicle.setDouble(6, odometer);
            int res = insertOneVehicle.executeUpdate();
            if (res > 0) {
                this.conn.commit();
                return res;
            } else {
                this.conn.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Update odometer
     * @param plate_no
     * @param tot_miles
     * @return num of rows updated
     */
    int updateOdometer(String plate_no, double tot_miles) {
        try {
            selectAVehicle.setString(1, plate_no);
            ResultSet rs = selectAVehicle.executeQuery();
            if (rs.next()) {
                double odometer = rs.getDouble("odometer");
                odometer += tot_miles;
                updateOdometer.setDouble(1, odometer);
                updateOdometer.setString(2, plate_no);
                int res = updateOdometer.executeUpdate();
                if (res > 0) {
                    this.conn.commit();
                    return res;
                } else {
                    this.conn.rollback();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    //-----------------------------------------------------------------------------------
    /**
     * Select all rental
     * @return
     */
    ArrayList<ArrayList<String>> selectAllRental() {
        ArrayList<ArrayList<String>> res = new ArrayList<ArrayList<String>>();
        try {
            ResultSet rs = selectAllRental.executeQuery();
            while (rs.next()) {
                ArrayList<String> row = new ArrayList<String>();
                row.add(rs.getString("type"));
                row.add(rs.getString("rental_by_hours"));
                row.add(rs.getString("rental_by_days"));
                row.add(rs.getString("rental_by_weeks"));
                res.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }
    
    /**
     * Select vehicle rental based on vehicle type
     * @param type
     * @return
     */
    ArrayList<String> selectVehicleRental(String type) {
        ArrayList<String> res = new ArrayList<String>();
        try {
            selectVehicleRental.setString(1, type);
            ResultSet rs = selectVehicleRental.executeQuery();
            if (rs.next()) {
                res.add(rs.getString("type"));
                res.add(rs.getString("rental_by_hours"));
                res.add(rs.getString("rental_by_days"));
                res.add(rs.getString("rental_by_weeks"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Insert a vehicle rental
     * @param type
     * @param rental_by_hours
     * @param rental_by_days
     * @param rental_by_weeks
     * @return num of rows inserted, or -1 if failed
     */
    int insertVehicleRental(String type, double rental_by_hours, double rental_by_days, double rental_by_weeks) {
        try {
            insertVehicleRental.setString(1, type);
            insertVehicleRental.setDouble(2, rental_by_hours);
            insertVehicleRental.setDouble(3, rental_by_days);
            insertVehicleRental.setDouble(4, rental_by_weeks);
            int res = insertVehicleRental.executeUpdate();
            if (res > 0) {
                this.conn.commit();
                return res;
            } else {
                this.conn.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Update a rental
     * @param type
     * @param rental_by_hours
     * @param rental_by_days
     * @param rental_by_weeks
     * @return num of rows updated or -1 if failed
     */
    int updateVehicleRental(String type, double rental_by_hours, double rental_by_days, double rental_by_weeks) {
        try {
            selectVehicleRental.setString(1, type);
            ResultSet rs = selectVehicleRental.executeQuery();
            if (rs.next()) {
                double r_hours = rs.getDouble("rental_by_hours");
                double r_days = rs.getDouble("rental_by_days");
                double r_weeks = rs.getDouble("rental_by_weeks");
                if (rental_by_hours < 0)
                    rental_by_hours = r_hours;
                if (rental_by_days < 0)
                    rental_by_days = r_days;
                if (rental_by_weeks < 0)
                    rental_by_weeks = r_weeks;
                updateVehicleRental.setDouble(1, rental_by_hours);
                updateVehicleRental.setDouble(2, rental_by_days);
                updateVehicleRental.setDouble(3, rental_by_weeks);
                updateVehicleRental.setString(4, type);
                int res = updateVehicleRental.executeUpdate();
                if (res > 0) {
                    this.conn.commit();
                    return res;
                } else {
                    this.conn.rollback();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}