package project.hotelsystem.database.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import java.sql.*;
import project.hotelsystem.database.models.room;
import project.hotelsystem.database.models.room_type;
import project.hotelsystem.database.connection.DBConnection;
import project.hotelsystem.web.WebSocketCon;


/**
 * @author Nomar
 * @author Zin Min Oo
 */

public class roomController {


	public static boolean addRoom(room s) {
		String sql = "call add_room(?,?,?)";
		try(Connection con = DBConnection.getConnection(); 
			CallableStatement psmt = (CallableStatement) con.prepareCall(sql);){
			psmt.setString(1, s.getRoom_no());
			psmt.setString(2, s.getRoom_type().getRtid());
			psmt.setInt(3, s.getFloor());
			
			
			int r = psmt.executeUpdate();
			return r>0;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}


	public static ArrayList<room> getAllRooms(){
		String sql = "SELECT * FROM room";
		ArrayList<room> list = new ArrayList<>();

		try (Connection con = DBConnection.getConnection();
			 PreparedStatement psmt = con.prepareStatement(sql)){
			ResultSet rs = psmt.executeQuery();

			while (rs.next()) {
				String room_no = rs.getString(1);
				room_type roomType = roomTypeController.getRoomTypeById(room_no);
				int floor = rs.getInt(3);
				String status = rs.getString(4);

				list.add(new room(room_no, roomType, floor, status));
			}
			rs.close();
		}catch(SQLException e) {
			e.printStackTrace();
		}

		return list;
	}
	public static ArrayList<room> getAllOccupiedRooms(){
		String sql = "SELECT * FROM booking_room_detail WHERE booking_status = 'Arrived'";
		ArrayList<room> list = new ArrayList<>();

		try (Connection con = DBConnection.getConnection();
			 PreparedStatement psmt = con.prepareStatement(sql)){
			ResultSet rs = psmt.executeQuery();

			while (rs.next()) {
				String room_no = rs.getString(2);

				list.add(new room(room_no));
			}
			rs.close();
		}catch(SQLException e) {
			e.printStackTrace();
		}

		return list;
	}

	public static ArrayList<room> getFloors(){
		String sql = "SELECT DISTINCT floor from room";
		ArrayList<room> list = new ArrayList<>();

		try (Connection con = DBConnection.getConnection();
			 PreparedStatement psmt = con.prepareStatement(sql)){
			ResultSet rs = psmt.executeQuery();

			while (rs.next()) {
				int floor = rs.getInt(1);

				list.add(new room(floor));
			}
			rs.close();
		}catch(SQLException e) {
			e.printStackTrace();
		}

		return list;
	}
	public static boolean saveRoom(String roomNo, String rtid, int floor, String status){
		String sql = "{call add_new_room(?,?,?)}";

		WebSocketCon.getWebSocketClient().sendID();

		try(Connection con = DBConnection.getConnection();
			CallableStatement clst = con.prepareCall(sql);){

			clst.setString(1, roomNo);
			clst.setString(2, rtid);
			clst.setInt(3, floor);

			clst.execute();

			return true;

		}catch(SQLException e){
			e.printStackTrace();
			return false;
		}
	}

	public static boolean editRoom(String roomNo, String rtid, int floor, String status){
		String sql = "UPDATE room SET room_type_id = ?, floor = ? , room_status = ? " +
				"        WHERE room_no = ?;";

		try(Connection con = DBConnection.getConnection();
			PreparedStatement clst = con.prepareStatement(sql);){

			clst.setString(4, roomNo);
			clst.setString(1, rtid);
			clst.setInt(2, floor);
			clst.setString(3,status);
			clst.execute();

			return true;

		}catch(SQLException e){
			e.printStackTrace();
			return false;
		}
	}


}