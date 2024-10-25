package project.hotelsystem.database.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import project.hotelsystem.database.connection.DBConnection;
import project.hotelsystem.database.models.order_service;
import project.hotelsystem.database.models.service;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Nomar
 * @author Zin Min Oo
 */

public class serviceController {
    public static double getServicePrice(int id) {
        String sql = "SELECT service_price from service WHERE service_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement psmt = con.prepareStatement(sql)) {

            psmt.setInt(1, id);
            ResultSet rs = psmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("service_price");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static List<service> getAllServices() {
        String sql = "SELECT * FROM service";
        List<service> list = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement psmt = con.prepareStatement(sql)) {
            ResultSet rs = psmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("service_id");
                String name = rs.getString("service_name");
                double price = rs.getDouble("service_price");
                Blob image = rs.getBlob("service_image");
                String des = rs.getString("service_description");
                if(des.matches("NIL")) continue;
                list.add(new service(id, name, price, des, image));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static service getServiceByID(String serviceId) {
        String sql = "SELECT * FROM service WHERE service_id = ?";
        service service = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, serviceId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("service_name");
                String des = rs.getString("service_description");
                double price = rs.getDouble("service_price");
                Blob image = rs.getBlob("service_image");

                if(des.matches("NIL")) return null;

                return new service(name, price, des, image);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return service;
    }

    private void deleteServiceFromDatabase(String serviceId) {
        // Implement the logic to delete the service from the database using serviceId
        String sql = "DELETE FROM service WHERE service_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, serviceId);
            pstmt.executeUpdate();
            new Alert(Alert.AlertType.INFORMATION, "Service Deleted Successfully", ButtonType.OK).showAndWait();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static boolean updateServiceInDatabase(int serviceId, String serviceName,
                                                  String description, double price, File imageFile) {
        // Implement logic to update the service in the database using the given parameters
        String sql;
        if (imageFile != null) {
            sql = "UPDATE service SET service_name = ?,  service_price = ?,service_description = ?, service_image = ? WHERE service_id = ?";
        } else {
            sql = "UPDATE service SET service_name = ?,  service_price = ?,service_description = ? WHERE service_id = ?";
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, serviceName);
            pstmt.setString(3, description);
            pstmt.setDouble(2, price);

            if (imageFile != null) {
                FileInputStream fis = new FileInputStream(imageFile);
                pstmt.setBinaryStream(4, fis, (int) imageFile.length());
                pstmt.setInt(5, serviceId);
            } else {
                pstmt.setInt(4, serviceId);
            }

            pstmt.executeUpdate();

            return true;

        } catch (SQLException | FileNotFoundException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static boolean deleteService(int sid) {
        String query = "UPDATE service SET service_description = 'NIL', service_image = null where service_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement psmt = connection.prepareStatement(query);) {

            psmt.setInt(1, sid);

            psmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void saveService(String serviceName, String description, double price, File imageFile) {
        String query = "INSERT INTO service (service_name, service_price, service_description, service_image) VALUES ( ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
             FileInputStream fis = new FileInputStream(imageFile)) {

            statement.setString(1, serviceName);
            statement.setDouble(2, price);
            statement.setString(3, description);

            // Set image as BLOB
            statement.setBlob(4, fis);

            int rowsAffected = statement.executeUpdate();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean batchOrder(List<service> orderedServices, String rmno) {
        String sql = "{CALL add_service_order(?,?)}";
        try (Connection connection = DBConnection.getConnection();
             CallableStatement csmt = connection.prepareCall(sql);) {

            for (service s : orderedServices) {
                csmt.setString(1,rmno);
                csmt.setString(2,s.getName());
                csmt.addBatch();
            }

            csmt.executeBatch();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
