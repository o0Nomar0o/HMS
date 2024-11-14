package project.hotelsystem.database.controller;

import javafx.scene.chart.XYChart;
import project.hotelsystem.database.connection.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class revenueController {

    public static Map getMonthly(){
        Map<Integer, List<XYChart.Data<String, Number>>> data = new HashMap<>();
        String sql = "SELECT years, months, revenue FROM revenue_monthly ORDER BY years, months";

        try(Connection con = DBConnection.getConnection();
            PreparedStatement psmt = con.prepareStatement(sql)){

            ResultSet resultSet = psmt.executeQuery(sql);

            while (resultSet.next()) {
                int year = resultSet.getInt("years");
                int month = resultSet.getInt("months");
                double revenue = resultSet.getDouble("revenue");

                System.out.println("Year: " + year + ", Month: " + month + ", Revenue: " + revenue);

                data.putIfAbsent(year, new ArrayList<>());
                data.get(year).add(new XYChart.Data<>(getMonthName(month), revenue));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    private static String getMonthName(int month) {
        return switch (month) {
            case 1 -> "Jan";
            case 2 -> "Feb";
            case 3 -> "Mar";
            case 4 -> "Apr";
            case 5 -> "May";
            case 6 -> "Jun";
            case 7 -> "Jul";
            case 8 -> "Aug";
            case 9 -> "Sep";
            case 10 -> "Oct";
            case 11 -> "Nov";
            case 12 -> "Dec";
            default -> "Unknown";
        };
    }

    public static List getYearRev()
    {
        List<XYChart.Data<String, Number>> data = new ArrayList<>();

        String sql = "SELECT years, revenue_per_year FROM revenue_yearly ORDER BY years";

        try(Connection con = DBConnection.getConnection();
            PreparedStatement psmt = con.prepareStatement(sql)){

            ResultSet resultSet = psmt.executeQuery(sql);

            while (resultSet.next()) {
                int year = resultSet.getInt("years");
                double revenue = resultSet.getDouble("revenue_per_year");
                data.add(new XYChart.Data<>(String.valueOf(year), revenue));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }


}
