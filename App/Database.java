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
     */
    PreparedStatement listAllTables;

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

    ArrayList<String> listAllTables(String usr) {
        ArrayList<String> res = new ArrayList<String>();
        try {
            listAllTables.setString(1, usr);
            ResultSet rs = listAllTables.executeQuery();
            while (rs.next()) {
                res.add(rs.getString("table_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }
}