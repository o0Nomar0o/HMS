package project.hotelsystem.database.controller;

import project.hotelsystem.database.models.booking;
import project.hotelsystem.database.models.order_service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import project.hotelsystem.database.connection.DBConnection;
import project.hotelsystem.database.models.room;
import project.hotelsystem.database.models.service;


public class orderServiceController {

    public static boolean batchOrder(List<order_service> orderedServices){

        String sql = "INSERT INTO service_order_detail (booking_id, room_no, service_id, order_times, service_charges) VALUES(?,?,?,?,?,?)";

        try(Connection con = DBConnection.getConnection();
            PreparedStatement psmt = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);){

            for(order_service os : orderedServices){
                psmt.setString(1,os.getBk().getBooking_id());
                psmt.setString(2, os.getRoom_no().getRoom_no());
                psmt.setInt(3, os.getSid().getId());
                psmt.setInt(4,os.getQnt());
                double unitPrice = serviceController.getServicePrice(os.getSid().getId());
                if(unitPrice<0) return false;
                psmt.setDouble(5,unitPrice * os.getQnt());

                psmt.addBatch();

            }

            psmt.executeLargeBatch();

            return true;

        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }


    }

}
