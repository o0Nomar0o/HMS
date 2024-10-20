package project.hotelsystem.database.connection;

import java.sql.*;

public class DBConnection {

    private static boolean db = false;

    public static void setDb(boolean db) {
        DBConnection.db = db;
    }

    public static boolean isDb() {
        return db;
    }

    public static Connection getConnection() throws SQLException {

        if(!db){
            String url = "jdbc:mysql://localhost/snowy_resort";
            String username = "root";
            String password = "12345678";

            return DriverManager.getConnection(url, username, password);
        }

        String url = "jdbc:mysql://pleaseinputyourcloudhere";
        String username = "cloudcloud";
        String password = "quite a secure password";

        return DriverManager.getConnection(url, username, password);

    }

    public static Connection getLocalConnection() throws SQLException {
        String url = "jdbc:mysql://localhost/hotelmanagementdb";
        String username = "root";
        String password = "12345678";

        return DriverManager.getConnection(url, username, password);

    }

}
