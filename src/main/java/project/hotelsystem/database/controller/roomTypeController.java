package project.hotelsystem.database.controller;

import java.sql.*;
import java.util.ArrayList;

import project.hotelsystem.database.models.room_type_details;
import project.hotelsystem.database.models.room_type;

import project.hotelsystem.database.connection.DBConnection;

/**
 * @author Nomar
 * @author Zin Min Oo
 */

public class roomTypeController {

	public static boolean saveroom_type(room_type s) {
		String sql = "insert into room values(? , ? )";
		try(Connection con = DBConnection.getConnection();
			PreparedStatement psmt = con.prepareStatement(sql);){
			psmt.setString(1, s.getRtid());
			psmt.setString(2, s.getDescription());
			int r = psmt.executeUpdate();
			return r>0;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}
	public static ArrayList<room_type_details> getAllRoomType(){
		String sql = "SELECT * FROM room_type rt JOIN room_price rp ON rt.room_type_id = rp.room_type_id ";
		ArrayList<room_type_details> list = new ArrayList<>();
		try (Connection con = DBConnection.getConnection();
				PreparedStatement psmt = con.prepareStatement(sql)){
			ResultSet rs = psmt.executeQuery();
			while (rs.next()) {
				String room_type = rs.getString("room_type_id");
				String roomdes = rs.getString("description");
				double npp = rs.getDouble("price_per_night");
				double hpp = rs.getDouble("price_per_hour");
				list.add(new room_type_details(room_type,roomdes,npp, hpp));
			}
			rs.close();
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static ArrayList<String> getRoomTypeID(){
		String sql = "SELECT room_type_id from room_type; ";
		ArrayList<String> list = new ArrayList<>();
		try (Connection con = DBConnection.getConnection();
			 PreparedStatement psmt = con.prepareStatement(sql)){
			ResultSet rs = psmt.executeQuery();
			while (rs.next()) {
				String room_type = rs.getString("room_type_id");

				list.add(room_type);
			}
			rs.close();
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	public static room_type getRoomTypeById(String room_type) {
		String sql = "SELECT rt.room_type_id, rt.description " +
				"FROM room r " +
				"INNER JOIN room_type rt ON r.room_type_id = rt.room_type_id " +
				"WHERE r.room_no = ?";


		try (Connection con = DBConnection.getConnection();
				PreparedStatement psmt = con.prepareStatement(sql)){

			System.out.println(room_type);
			psmt.setString(1, room_type);

			ResultSet rs = psmt.executeQuery();
			while (rs.next()) {
				room_type rt = new room_type(rs.getString(1), rs.getString(2));
				System.out.println(rt.getRtid());
				return rt;
			}
			
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean saveRoomTypeAndPrice(String rtid, String desc, double np, double hp){
		String sql = "{call add_new_room_type_and_price(?,?,?,?)}";

		try(Connection con = DBConnection.getConnection();
			CallableStatement clst = con.prepareCall(sql);){

			clst.setString(1, rtid);
			clst.setString(2, desc);
			clst.setDouble(3, np);
			clst.setDouble(4, hp);

			clst.execute();
			return true;

		}catch(SQLException e){
			e.printStackTrace();
			return false;
		}
	}
	public static boolean updateRoomType(String rtid, String desc, double np, double hp){
		String sql = "{call change_room_type(?,?,?,?)}";

		try(Connection con = DBConnection.getConnection();
			CallableStatement clst = con.prepareCall(sql);){

			clst.setString(1, rtid);
			clst.setString(2, desc);
			clst.setDouble(3, np);
			clst.setDouble(4, hp);

			clst.execute();
			return true;

		}catch(SQLException e){
			e.printStackTrace();
			return false;
		}
	}
}