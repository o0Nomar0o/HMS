package project.hotelsystem.database.connection;

import project.hotelsystem.settings.databaseSettings;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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


        String url = dbs.getLocal_url();
        String username = dbs.getLocal_user();
        String password = dbs.getLocal_password();


        return DriverManager.getConnection(url, username, password);

    }


}
