package dao;

import model.Household;
import config.DatabaseConnection;
import java.sql.*;
import java.util.*;

public class HouseholdDAO {
    
    /**
     * Tạo hộ gia đình mới
     */
    public static int createHousehold(int apartmentId, int headOfHouseholdId, 
                                     int numberOfMembers, String contactPhone, String contactEmail) {
        String query = "INSERT INTO ho_gia_dinh (id_can_ho, id_chu_ho, so_nhan_khau, " +
                      "so_dien_thoai_lien_he, email_lien_he) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, apartmentId);
            pstmt.setInt(2, headOfHouseholdId);
            pstmt.setInt(3, numberOfMembers);
            pstmt.setString(4, contactPhone);
            pstmt.setString(5, contactEmail);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int householdId = generatedKeys.getInt(1);
                        System.out.println("✓ Tạo hộ gia đình thành công: ID=" + householdId);
                        return householdId;
                    }
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi tạo hộ gia đình: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }
    
    /**
     * Lấy hộ gia đình theo căn hộ
     */
    public static Household getHouseholdByApartment(int apartmentId) {
        String query = "SELECT * FROM ho_gia_dinh WHERE id_can_ho = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, apartmentId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Household(
                        rs.getInt("id_ho_gia_dinh"),
                        rs.getInt("id_can_ho"),
                        rs.getInt("id_chu_ho"),
                        rs.getInt("so_nhan_khau"),
                        rs.getString("so_dien_thoai_lien_he"),
                        rs.getString("email_lien_he")
                    );
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi lấy thông tin hộ gia đình: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Kiểm tra căn hộ đã có hộ gia đình chưa
     */
    public static boolean hasHousehold(int apartmentId) {
        String query = "SELECT COUNT(*) as count FROM ho_gia_dinh WHERE id_can_ho = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, apartmentId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi kiểm tra hộ gia đình: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Cập nhật thông tin hộ gia đình
     */
    public static boolean updateHousehold(int householdId, int numberOfMembers, 
                                         String contactPhone, String contactEmail) {
        String query = "UPDATE ho_gia_dinh SET so_nhan_khau = ?, " +
                      "so_dien_thoai_lien_he = ?, email_lien_he = ? WHERE id_ho_gia_dinh = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, numberOfMembers);
            pstmt.setString(2, contactPhone);
            pstmt.setString(3, contactEmail);
            pstmt.setInt(4, householdId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✓ Cập nhật hộ gia đình thành công: ID=" + householdId);
                return true;
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi cập nhật hộ gia đình: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Xóa hộ gia đình
     */
    public static boolean deleteHousehold(int apartmentId) {
        String query = "DELETE FROM ho_gia_dinh WHERE id_can_ho = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, apartmentId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✓ Xóa hộ gia đình thành công");
                return true;
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi xóa hộ gia đình: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}