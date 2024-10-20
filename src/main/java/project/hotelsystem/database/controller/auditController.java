package project.hotelsystem.database.controller;

import project.hotelsystem.database.connection.DBConnection;
import project.hotelsystem.database.models.audit_logs;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class auditController {
    public static List<audit_logs> getAudits() {
        List<audit_logs> logs = new ArrayList<>();
        String query = "SELECT * FROM audit_log ORDER BY timestamp DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                String action = rs.getString(2);
                String user = rs.getString(3);
                LocalDateTime time = rs.getTimestamp(4).toLocalDateTime();

                audit_logs l = new audit_logs(action,user,time);
                logs.add(l);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return logs;
    }

    public static List<audit_logs> getAuditsByID(String ID) {
        List<audit_logs> logs = new ArrayList<>();
        String query = "SELECT * FROM audit_log WHERE user_id = ? ORDER BY timestamp DESC ";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement psmt = conn.prepareStatement(query);) {

            psmt.setString(1, ID);
            ResultSet rs = psmt.executeQuery();

            while (rs.next()) {

                String action = rs.getString(2);
                String user = rs.getString(3);
                LocalDateTime time = rs.getTimestamp(4).toLocalDateTime();

                audit_logs l = new audit_logs(action,user,time);
                logs.add(l);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return logs;
    }

    public static boolean addLogs(String action, String user){
        String sql = "INSERT INTO `audit_log`(action, user) VALUES(?, ?)";
        try(Connection con = DBConnection.getConnection();
            PreparedStatement psmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){

            psmt.setString(1,action);
            psmt.setString(2, user);

            return psmt.execute();

        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        addLogs("Added a new user","A1");
        addLogs("Delete an existing booking","A1");
        addLogs("Create new order","A1");


    }


}

