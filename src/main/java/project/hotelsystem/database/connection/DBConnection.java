package project.hotelsystem.database.connection;

import java.sql.*;
import project.hotelsystem.settings.databaseSettings;

public class DBConnection {

    private static databaseSettings dbs = databaseSettings.getInstance();

    private static boolean db = false;

    public static void setDb(boolean db) {
        DBConnection.db = db;
    }

    public static boolean isDb() {
        return db;
    }

    public static Connection getConnection() throws SQLException {

        dbs.loadSettings();

        if(!db){
            String url = dbs.getLocal_url();
            String username = dbs.getLocal_user();
            String password = dbs.getLocal_password();

            return DriverManager.getConnection(url, username, password);
        }

        String url = "jdbc:mysql://pleaseinputyourcloudhere";
        String username = "cloudcloud";
        String password = "quite a secure password";

        return DriverManager.getConnection(url, username, password);

    }


}
