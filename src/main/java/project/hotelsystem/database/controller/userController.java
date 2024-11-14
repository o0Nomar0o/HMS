package project.hotelsystem.database.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import project.hotelsystem.database.models.user;
import project.hotelsystem.database.connection.DBConnection;

/**
 * @author Nomar
 * @author Zin Min Oo
 */

public class userController {

	public static boolean addUser(user s) {

		String sql = "insert into user values(?,?,?,?,?,?,?)";

		try(Connection con = DBConnection.getConnection();
				PreparedStatement psmt = con.prepareStatement(sql);){
			psmt.setString(1, s.getUid());
			psmt.setString(2, s.getUsername());
			psmt.setString(3, s.getPrivilege());
			psmt.setString(4, s.getPassword());
			psmt.setString(5, s.getEmail());
			psmt.setString(6, s.getPhone_no());
			psmt.setString(7, s.getStatus());

			int r = psmt.executeUpdate();
			con.close();
			return r>0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}


	public static ArrayList<user> getAllUsers(){

		String sql = "SELECT * FROM user";
		ArrayList<user> list = new ArrayList<>();
		try (Connection con = DBConnection.getConnection();
				PreparedStatement psmt = con.prepareStatement(sql)){

			ResultSet rs = psmt.executeQuery();

			while (rs.next()) {
				String id = rs.getString(1);
				String username = rs.getString(2);
				String privilege = rs.getString(3);
				String email = rs.getString(5);
				String ph = rs.getString(6);
				String status = rs.getString(7);
				if(!status.matches("NIL")) {
					user u = new user(id, username, privilege, email, ph, status);
					list.add(u);
				}
			}

			rs.close();

		}catch(SQLException e) {
			e.printStackTrace();
		}

		return list;
	}

	public static boolean authLogin(String id) {

		String sql = "SELECT EXISTS(SELECT 1 FROM `user` WHERE binary user_id = ? AND status != 'NIL')";

		try (Connection con = DBConnection.getConnection();
				PreparedStatement psmt = con.prepareStatement(sql)){

			psmt.setString(1, id);
			ResultSet rs = psmt.executeQuery();
			boolean exists = false;

			while(rs.next()) {
				return rs.getBoolean(1);
			}


			rs.close();

		}catch(SQLException e) {
			e.printStackTrace();
			
		}
		return false;
	}

	public static boolean user_exist_email(String email) {

		String sql = "SELECT EXISTS(SELECT 1 FROM `user` WHERE binary email = ? AND status != 'NIL')";

		try (Connection con = DBConnection.getConnection();
			 PreparedStatement psmt = con.prepareStatement(sql)){

			psmt.setString(1, email);
			ResultSet rs = psmt.executeQuery();
			boolean exists = false;

			while(rs.next()) {
				return rs.getBoolean(1);
			}

			rs.close();

		}catch(SQLException e) {
			e.printStackTrace();

		}
		return false;
	}

	public static user getUserByEmail(String email) {
		String sql = "select user_id, user_name, privilege, password, status from user where binary email=?";
		try (Connection con = DBConnection.getConnection();
			 PreparedStatement psmt = con.prepareStatement(sql)){

			psmt.setString(1, email);
			ResultSet rs = psmt.executeQuery();

			while(rs.next()) {
				String uid = rs.getString(1);
				String username = rs.getString(2);
				String privilege = rs.getString(3);
				String password = rs.getString(4);
				String status = rs.getString(5);

				user u = new user(uid,username, privilege, password, status);

				if(u.getStatus().equals("NIL")) {
					throw new SQLException("User no longer exist");
				}

				return u;
			}
		}

		catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static boolean updatePassword_email(String email , String pw ) {
		String sql = "update user set password=? where email=?";
		try (Connection con = DBConnection.getConnection();) {

			PreparedStatement psmt = con.prepareStatement(sql);

			psmt.setString(1, pw);
			psmt.setString(2, email);
			psmt.executeUpdate();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static user getUserById(String id) {
		String sql = "select user_id, user_name, privilege, password, status from user where binary user_id=?";
		try (Connection con = DBConnection.getConnection();
				PreparedStatement psmt = con.prepareStatement(sql)){

			psmt.setString(1, id);
			ResultSet rs = psmt.executeQuery();

			while(rs.next()) {
				String uid = rs.getString(1);
				String username = rs.getString(2);
				String privilege = rs.getString(3);
				String password = rs.getString(4);
				String status = rs.getString(5);

				user u = new user(uid,username, privilege, password, status);

				if(u.getStatus().equals("NIL")) {
					throw new SQLException("User no longer exist");
				}

				return u;
			}
		}

		catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static boolean updatePassword(String id , String pw ) {
		String sql = "update user set password=? where user_id=?";
		try (Connection con = DBConnection.getConnection();) {
			
			PreparedStatement psmt = con.prepareStatement(sql);
			
			psmt.setString(1, pw);
			psmt.setString(2, id);
			psmt.executeUpdate();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean deleteEmployee(String id) {

		if (id == null || id.isEmpty()) {
			System.err.println("Invalid ID: " + id);
			return false;
		}

		String sql = "UPDATE `user` SET status = 'NIL' WHERE user_id = ?";
		try(Connection con = DBConnection.getConnection();
			PreparedStatement psmt = con.prepareStatement(sql)) {

			psmt.setString(1, id);

			psmt.execute();
			return true;

		}catch(SQLException e){
			return false;
		}
	}

	public static boolean updateStatus(String id, String status){
		String sql = "UPDATE `user` SET status = ? WHERE user_id = ?";
		try(Connection con = DBConnection.getConnection();
			PreparedStatement psmt = con.prepareStatement(sql)) {

			psmt.setString(1, status);
			psmt.setString(2, id);

			return psmt.execute();

		}catch(SQLException e){
			return false;
		}
	}

	public static boolean updateUser(String id, String un,
									 String priv, String pass,
									 String ph, String email){
		String sql = "UPDATE `user` SET username = ?, " +
				"privilege =? , password = ?, email = ?, phone_no = ? WHERE user_id = ?";
		try(Connection con = DBConnection.getConnection();
			PreparedStatement psmt = con.prepareStatement(sql)) {

			psmt.setString(1, un);
			psmt.setString(2, priv);
			psmt.setString(3, pass);
			psmt.setString(6, id);
			psmt.setString(5,email);
			psmt.setString(5,ph);

			return psmt.execute();

		}catch(SQLException e){
			return false;
		}
	}
	public static boolean updateUser(String id, String un,
									 String priv,
									 String ph, String email){
		String sql = "UPDATE user SET user_name = ?, " +
				"privilege =? , email = ?, phone_no = ? WHERE user_id = ?";
		try(Connection con = DBConnection.getConnection();
			PreparedStatement psmt = con.prepareStatement(sql)) {

			psmt.setString(1, un);
			psmt.setString(2, priv);
			psmt.setString(5, id);
			psmt.setString(3,email);
			psmt.setString(4,ph);


			psmt.executeUpdate();
			return true;

		}catch(SQLException e){
			return false;
		}
	}

	public static boolean isUserTableEmpty() {

		String sql = "SELECT COUNT(*) AS user_count FROM user";

		try (Connection connection = DBConnection.getConnection();
				PreparedStatement stmt = connection.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {

			if (rs.next()) {
				int count = rs.getInt("user_count");
				return count == 0;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

}