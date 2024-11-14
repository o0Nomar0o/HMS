package project.hotelsystem.database.controller;

import project.hotelsystem.database.models.*;
import project.hotelsystem.database.connection.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Nomar
 * @author Zin Min Oo
 */

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
                "b.booking_id, g.customer_name, g.phone_no, r.room_no, b.booking_date, b.check_in, b.check_out, brd.booking_status " +
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
                guestData.setPhone_no(rs.getString("phone_no"));

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
        String sql = "UPDATE booking_room_detail SET booking_status = 'Cancelled' WHERE booking_id = ? AND room_no = ?;";

        String sql2 = "UPDATE room SET room_status = 'Available' WHERE room_no = ?;";

        String sql3 = "UPDATE booking SET stay_duration_night = 0 WHERE booking_id = ?;";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement psmt = con.prepareStatement(sql);
             PreparedStatement psmt2 = con.prepareStatement(sql2);
             PreparedStatement psmt3 = con.prepareStatement(sql3)) {

            psmt.setString(1, bkid);
            psmt.setString(2, room_no);
            psmt.executeUpdate();

            psmt2.setString(1, room_no);
            psmt2.executeUpdate();

            psmt3.setString(1, bkid);
            psmt3.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean arrived(String bkid, String room_no) {
        String sql = "UPDATE booking_room_detail SET booking_status = 'Arrived' WHERE booking_id = ? AND room_no = ?;";

        String sql2 = "UPDATE room SET room_status = 'Unavailable' WHERE room_no = ?;";

        String sql3 = "UPDATE booking SET check_in = ? WHERE booking_id = ?;";

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
            psmt3.setString(2, bkid);
            psmt3.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<bookingDetails> getBookingDetails(){

        List<bookingDetails> bk_details = new ArrayList<>();
        String query = "SELECT DISTINCT \n" +
                "    b.booking_id,\n" +
                "    c.customer_id,\n" +
                "    c.customer_name,\n" +
                "    c.phone_no,\n" +
                "    c.id_card,\n" +
                "    c.email,\n" +
                "    r.room_no,\n" +
                "    r.room_type_id,\n" +
                "    rt.description AS room_type_description,\n" +
                "    r.floor,\n" +
                "    r.room_status,\n" +
                "    b.check_in,\n" +
                "    (SELECT p.payment_method \n" +
                "     FROM payment p \n" +
                "     WHERE p.booking_id = b.booking_id \n" +
                "       AND p.payment_date = b.check_in) AS ci_payment_method,\n" +
                "    b.check_out,\n" +
                "    (SELECT p.payment_method \n" +
                "     FROM payment p \n" +
                "     WHERE p.booking_id = b.booking_id \n" +
                "       AND p.payment_date = b.check_out) AS co_payment_method\n" +
                "FROM \n" +
                "    booking b\n" +
                "JOIN \n" +
                "    customer c ON b.customer_id = c.customer_id\n" +
                "JOIN \n" +
                "    booking_room_detail brd ON b.booking_id = brd.booking_id\n" +
                "JOIN \n" +
                "    room r ON brd.room_no = r.room_no\n" +
                "JOIN \n" +
                "    room_type rt ON r.room_type_id = rt.room_type_id\n" +
                "LEFT JOIN \n" +
                "    payment p_ci ON b.booking_id = p_ci.booking_id AND p_ci.payment_date = b.check_in\n" +
                "LEFT JOIN \n" +
                "    payment p_co ON b.booking_id = p_co.booking_id AND p_co.payment_date = b.check_out\n" +
                "WHERE \n" +
                "    brd.booking_status IN ('leaved', 'Checked-Out')\n" +
                "ORDER BY \n" +
                "    b.check_out DESC;";
        try(Connection con = DBConnection.getConnection();
        PreparedStatement psmt = con.prepareStatement(query)){

            ResultSet rs = psmt.executeQuery();
            while(rs.next()){

                String bkid = rs.getString("booking_id");
                String c_name = rs.getString("customer_name");
                String r_no = rs.getString("room_no");
                String ph = rs.getString("phone_no");
                System.out.println(ph);
                LocalDateTime c_in = rs.getTimestamp("check_in").toLocalDateTime();
                LocalDateTime c_out = rs.getTimestamp("check_out").toLocalDateTime();
                String ci_payment = rs.getString("ci_payment_method");
                String co_payment = rs.getString("co_payment_method");

                bk_details.add(new bookingDetails
                        (new booking(bkid),
                        new customer(c_name,ph),
                        new room(r_no),
                        c_in,ci_payment,
                        c_out,co_payment));
            }

            return bk_details;

        }catch (SQLException e){
            return null;
        }

    }


}
