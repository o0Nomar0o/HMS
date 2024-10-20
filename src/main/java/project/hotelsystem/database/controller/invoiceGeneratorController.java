package project.hotelsystem.database.controller;

import project.hotelsystem.database.models.InvoiceData;
import project.hotelsystem.database.models.food;
import project.hotelsystem.database.models.service;
import project.hotelsystem.database.models.order_food;
import project.hotelsystem.database.models.order_service;
import project.hotelsystem.database.connection.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class invoiceGeneratorController {

    public static InvoiceData getData(String bkid) {
        System.out.println("in controller");
        String sql = "SELECT " +
                "g.customer_name, g.phone_no, g.id_card, g.email, " +
                "b.booking_date, b.check_in, b.check_out, " +
                "r.room_no, rt.description as room_type_desc, " +
                "os.order_service_id, os.order_time,s.service_name, s.service_price, os.service_charges, " +
                "orf.order_food_id, orf.food_quantity, f.food_name,food_price, orf.food_charges   " +
                "FROM booking b " +
                "INNER JOIN customer g ON b.customer_id = g.customer_id " +
                "INNER JOIN booking_room_detail brd ON b.booking_id = brd.booking_id " +
                "INNER JOIN room r ON r.room_no = brd.room_no " +
                "INNER JOIN room_type rt ON r.room_type_id = rt.room_type_id " +
                "LEFT JOIN service_order_detail os ON b.booking_id = os.booking_id " +
                "LEFT JOIN service s ON os.service_id = s.service_id " +
                "LEFT JOIN food_order_detail orf ON b.booking_id = orf.booking_id " +
                "LEFT JOIN food f ON orf.food_id = f.food_id " +
                "WHERE b.booking_id = ?;";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, bkid);
            ResultSet rs = pstmt.executeQuery();

            InvoiceData invoiceData = null;

            while (rs.next()) {
                if (invoiceData == null) {
                    invoiceData = new InvoiceData();

                    invoiceData.setGuestName(rs.getString("customer_name"));
                    invoiceData.setPhoneNo(rs.getString("phone_no"));
                    invoiceData.setEmail(rs.getString("email"));
                    invoiceData.setBookingDate(rs.getTimestamp("booking_date").toLocalDateTime());
                    invoiceData.setCheckIn(rs.getTimestamp("check_in").toLocalDateTime());
                    invoiceData.setCheckOut(rs.getTimestamp("check_out").toLocalDateTime());
                    invoiceData.setRoomNo(rs.getString("room_no"));
                    invoiceData.setRoomType(rs.getString("room_type_desc"));

                }
                if (rs.getString("service_name") != null) {
                    order_service os = new order_service(rs.getInt("order_service_id"),
                            new service(rs.getString("service_name"), rs.getDouble("service_price")),
                            rs.getTimestamp("order_time").toLocalDateTime(),
                            rs.getDouble("service_charges"));
                    invoiceData.addService(os);
                }
                if (rs.getString("food_name") != null) {
                    order_food of = new order_food(rs.getInt("order_food_id"),
                            new food(rs.getString("food_name"), rs.getDouble("food_price")),
                            rs.getInt("food_quantity"),
                            rs.getDouble("food_charges"));
                    invoiceData.addFood(of);
                }
            }
            System.out.println("quitting controller");
            return invoiceData;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
