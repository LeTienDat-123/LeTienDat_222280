package dao;

import model.User;
import config.DatabaseConnection;
import java.sql.*;

public class UserDAO {
    
    /**
     * Xác thực người dùng bằng username và password
     */
    public static User authenticateUser(String username, String password) {
        String query = "SELECT id_nguoi_dung, ten_dang_nhap, ho_ten, email, so_dien_thoai, vai_tro " +
                      "FROM nguoi_dung WHERE ten_dang_nhap = ? AND mat_khau = ? AND trang_thai_hoat_dong = TRUE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id_nguoi_dung");
                    String username_db = rs.getString("ten_dang_nhap");
                    String fullName = rs.getString("ho_ten");
                    String email = rs.getString("email");
                    String phone = rs.getString("so_dien_thoai");
                    String role = rs.getString("vai_tro");
                    
                    return new User(id, username_db, fullName, email, phone, role);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi xác thực người dùng: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Lấy thông tin người dùng theo ID
     */
    public static User getUserById(int userId) {
        String query = "SELECT id_nguoi_dung, ten_dang_nhap, ho_ten, email, so_dien_thoai, vai_tro " +
                      "FROM nguoi_dung WHERE id_nguoi_dung = ? AND trang_thai_hoat_dong = TRUE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id_nguoi_dung");
                    String username = rs.getString("ten_dang_nhap");
                    String fullName = rs.getString("ho_ten");
                    String email = rs.getString("email");
                    String phone = rs.getString("so_dien_thoai");
                    String role = rs.getString("vai_tro");
                    
                    return new User(id, username, fullName, email, phone, role);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi lấy thông tin người dùng: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Kiểm tra tên đăng nhập đã tồn tại
     */
    public static boolean usernameExists(String username) {
        String query = "SELECT COUNT(*) as count FROM nguoi_dung WHERE ten_dang_nhap = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi kiểm tra tên đăng nhập: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Cập nhật mật khẩu
     */
    public static boolean updatePassword(int userId, String newPassword) {
        String query = "UPDATE nguoi_dung SET mat_khau = ? WHERE id_nguoi_dung = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, userId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi cập nhật mật khẩu: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Cập nhật thông tin người dùng
     */
    public static boolean updateUserInfo(int userId, String fullName, String email, String phone) {
        String query = "UPDATE nguoi_dung SET ho_ten = ?, email = ?, so_dien_thoai = ? WHERE id_nguoi_dung = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, fullName);
            pstmt.setString(2, email);
            pstmt.setString(3, phone);
            pstmt.setInt(4, userId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi cập nhật thông tin người dùng: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Đăng ký người dùng mới
     */
    public static boolean registerUser(String username, String password, String fullName, 
                                       String email, String phone) {
        String query = "INSERT INTO nguoi_dung (ten_dang_nhap, mat_khau, ho_ten, email, so_dien_thoai, vai_tro) " +
                      "VALUES (?, ?, ?, ?, ?, 'cu_dan')";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, fullName);
            pstmt.setString(4, email);
            pstmt.setString(5, phone);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi đăng ký người dùng: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}