package project.hotelsystem.database.controller;

import project.hotelsystem.database.models.*;
import project.hotelsystem.database.connection.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class bookingController {

    public static booking getBookingById(String bookingId) {

        String sql = "select * from booking where booking_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement psmt = con.prepareStatement(sql)) {

            psmt.setString(1, bookingId);
            ResultSet rs = psmt.executeQuery();

            if (rs.next()) {
                return new booking(rs.getString(1),
                        guestController.getGuestNameById(rs.getString(2)),
                        new room(rs.getString(3)),
                        rs.getTimestamp(4).toLocalDateTime(),
                        rs.getTimestamp(5).toLocalDateTime(),
                        rs.getTimestamp(6).toLocalDateTime());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean checkOut(String bkid, String room_no) {
        String sql = "UPDATE booking_room_detail SET booking_status = 'Checked-Out' WHERE booking_id = ? AND room_no = ?;";
        String sql2 = "UPDATE room SET room_status = 'Available' WHERE room_no = ?;";
        String sql3 = "UPDATE booking SET check-out = ? WHERE booking_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement psmt = con.prepareStatement(sql);
             PreparedStatement psmt2 = con.prepareStatement(sql2);
             PreparedStatement psmt3 = con.prepareStatement(sql3)) {

            psmt.setString(1, bkid);
            psmt.setString(2, room_no);
            psmt.executeUpdate();

            psmt2.setString(1, room_no);
            psmt2.executeUpdate();

            psmt3.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            psmt3.setString(2,bkid);
            psmt3.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<booking> getAllBookings() {
        List<booking> bookings = new ArrayList<>();
        String sql = "SELECT " +
                "b.booking_id, g.customer_name, r.room_no, b.booking_date, b.check_in, b.check_out, brd.booking_status " +
                "FROM booking b " +
                "INNER JOIN booking_room_detail brd ON b.booking_id = brd.booking_id " +
                "INNER JOIN room r ON brd.room_no = r.room_no " +
                "INNER JOIN customer g ON b.customer_id = g.customer_id;";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                // Create a guest object
                guest guestData = new guest();
                guestData.setGuest_name(rs.getString("customer_name"));

                // Create a room object
                room roomData = new room();
                roomData.setRoom_no(rs.getString("room_no"));

                // Create a booking object
                booking bookingData = new booking();
                bookingData.setBooking_id(rs.getString("booking_id"));
                bookingData.setGuest(guestData);
                bookingData.setRoom(roomData);
                bookingData.setBooking_date(rs.getTimestamp("booking_date").toLocalDateTime());
                bookingData.setCheck_in(rs.getTimestamp("check_in").toLocalDateTime());
                bookingData.setCheck_out(rs.getTimestamp("check_out").toLocalDateTime());
                bookingData.setBooking_status(rs.getString("booking_status"));


                // Add the booking object to the list
                bookings.add(bookingData);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bookings;
    }
    public static boolean cancelBooking(String bkid, String room_no) {
        // Update booking_room_detail to set booking_status to 'Cancelled'
        String sql = "UPDATE booking_room_detail SET booking_status = 'Cancelled' WHERE booking_id = ? AND room_no = ?;";

        // Update room status to 'Available'
        String sql2 = "UPDATE room SET room_status = 'Available' WHERE room_no = ?;";

        // Optionally, if you want to keep track of when the booking was cancelled, you could update a cancel_date column
        String sql3 = "UPDATE booking SET stay_duration_night = 0 WHERE booking_id = ?;"; // Assuming 'cancel_date' column exists

        try (Connection con = DBConnection.getConnection();
             PreparedStatement psmt = con.prepareStatement(sql);
             PreparedStatement psmt2 = con.prepareStatement(sql2);
             PreparedStatement psmt3 = con.prepareStatement(sql3)) {

            // Set parameters and execute the first update to cancel the booking
            psmt.setString(1, bkid);
            psmt.setString(2, room_no);
            psmt.executeUpdate();

            // Set parameters and execute the second update to mark the room as available
            psmt2.setString(1, room_no);
            psmt2.executeUpdate();

            // Optionally, set the cancellation timestamp (if you're using a cancel_date column)
            psmt3.setString(1, bkid);
            psmt3.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



}
