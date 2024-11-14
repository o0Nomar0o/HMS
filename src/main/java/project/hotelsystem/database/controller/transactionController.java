package project.hotelsystem.database.controller;


import project.hotelsystem.database.connection.DBConnection;

import java.sql.*;

public class transactionController {

    public static boolean newTransaction(String uuid, String uid){
        String op = "INSERT INTO `transactions` VALUES(?, ?) ";
        try(Connection con = DBConnection.getConnection();
        PreparedStatement psmt = con.prepareStatement(op)){
            psmt.setString(1, uuid);
            psmt.setString(2, uid);

            psmt.execute();
            con.close();
            return true;
        }catch (SQLException e){
            return false;
        }
    }
}
