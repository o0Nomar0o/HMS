package project.hotelsystem.database.controller;

import project.hotelsystem.database.connection.DBConnection;
import project.hotelsystem.database.models.booking;
import project.hotelsystem.database.models.guest;
import project.hotelsystem.database.models.room;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class guestController {
    public static guest getGuestNameById(String gID) {
        String sql = "select * from customer where customer_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement psmt = con.prepareStatement(sql)){
            psmt.setString(1, gID);
            ResultSet rs = psmt.executeQuery();
            if (rs.next()) {
                return new guest(rs.getString("customer_id"),rs.getString("customer_name"));
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
