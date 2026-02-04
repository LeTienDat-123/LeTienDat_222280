package dao;

import model.Apartment;
import config.DatabaseConnection;
import java.sql.*;
import java.util.*;

public class ApartmentDAO {
    
    /**
     * Lấy thông tin căn hộ theo ID
     */
    public static Apartment getApartmentById(int apartmentId) {
        String query = "SELECT id_can_ho, id_toa_nha, so_can_ho, so_tang, dien_tich_m2, " +
                      "so_phong_ngu, so_phong_tam, trang_thai " +
                      "FROM can_ho WHERE id_can_ho = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, apartmentId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Apartment(
                        rs.getInt("id_can_ho"),
                        rs.getInt("id_toa_nha"),
                        rs.getString("so_can_ho"),
                        rs.getInt("so_tang"),
                        rs.getFloat("dien_tich_m2"),
                        rs.getInt("so_phong_ngu"),
                        rs.getInt("so_phong_tam"),
                        rs.getString("trang_thai")
                    );
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi lấy thông tin căn hộ: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Cập nhật trạng thái căn hộ
     */
    public static boolean updateApartmentStatus(int apartmentId, String newStatus) {
        // Kiểm tra trạng thái hợp lệ
        if (!isValidStatus(newStatus)) {
            System.out.println("✗ Trạng thái không hợp lệ: " + newStatus);
            return false;
        }
        
        String query = "UPDATE can_ho SET trang_thai = ? WHERE id_can_ho = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, apartmentId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✓ Cập nhật trạng thái căn hộ thành công: ID=" + apartmentId + ", Status=" + newStatus);
                return true;
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi cập nhật trạng thái căn hộ: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Cập nhật thông tin căn hộ
     */
    public static boolean updateApartment(int apartmentId, float area, int bedrooms, int bathrooms) {
        String query = "UPDATE can_ho SET dien_tich_m2 = ?, so_phong_ngu = ?, so_phong_tam = ? " +
                      "WHERE id_can_ho = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setFloat(1, area);
            pstmt.setInt(2, bedrooms);
            pstmt.setInt(3, bathrooms);
            pstmt.setInt(4, apartmentId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✓ Cập nhật thông tin căn hộ thành công: ID=" + apartmentId);
                return true;
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi cập nhật thông tin căn hộ: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Thêm căn hộ mới
     */
    public static boolean addApartment(int buildingId, String apartmentNumber, int floor,
                                      float area, int bedrooms, int bathrooms) {
        String query = "INSERT INTO can_ho (id_toa_nha, so_can_ho, so_tang, dien_tich_m2, " +
                      "so_phong_ngu, so_phong_tam, trang_thai) " +
                      "VALUES (?, ?, ?, ?, ?, ?, 'trong')";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, buildingId);
            pstmt.setString(2, apartmentNumber);
            pstmt.setInt(3, floor);
            pstmt.setFloat(4, area);
            pstmt.setInt(5, bedrooms);
            pstmt.setInt(6, bathrooms);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✓ Thêm căn hộ mới thành công: " + apartmentNumber);
                return true;
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi thêm căn hộ: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Xóa căn hộ
     */
    public static boolean deleteApartment(int apartmentId) {
        // Kiểm tra xem căn hộ có hợp đồng đang hoạt động không
        if (hasActiveContract(apartmentId)) {
            System.out.println("✗ Không thể xóa căn hộ có hợp đồng đang hoạt động");
            return false;
        }
        
        String query = "DELETE FROM can_ho WHERE id_can_ho = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, apartmentId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✓ Xóa căn hộ thành công: ID=" + apartmentId);
                return true;
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi xóa căn hộ: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Kiểm tra căn hộ có hợp đồng đang hoạt động không
     */
    public static boolean hasActiveContract(int apartmentId) {
        String query = "SELECT COUNT(*) as count FROM hop_dong_thue " +
                      "WHERE id_can_ho = ? AND trang_thai = 'hoat_dong'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, apartmentId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi kiểm tra hợp đồng: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Lấy danh sách căn hộ theo trạng thái
     */
    public static List<Apartment> getApartmentsByStatus(String status) {
        List<Apartment> apartments = new ArrayList<>();
        String query = "SELECT id_can_ho, id_toa_nha, so_can_ho, so_tang, dien_tich_m2, " +
                      "so_phong_ngu, so_phong_tam, trang_thai " +
                      "FROM can_ho WHERE trang_thai = ? ORDER BY id_toa_nha, so_tang DESC, so_can_ho";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, status);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Apartment apartment = new Apartment(
                        rs.getInt("id_can_ho"),
                        rs.getInt("id_toa_nha"),
                        rs.getString("so_can_ho"),
                        rs.getInt("so_tang"),
                        rs.getFloat("dien_tich_m2"),
                        rs.getInt("so_phong_ngu"),
                        rs.getInt("so_phong_tam"),
                        rs.getString("trang_thai")
                    );
                    apartments.add(apartment);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi lấy danh sách căn hộ theo trạng thái: " + e.getMessage());
            e.printStackTrace();
        }
        return apartments;
    }
    
    /**
     * Tìm kiếm căn hộ theo số căn hộ
     */
    public static List<Apartment> searchApartmentByNumber(String apartmentNumber) {
        List<Apartment> apartments = new ArrayList<>();
        String query = "SELECT id_can_ho, id_toa_nha, so_can_ho, so_tang, dien_tich_m2, " +
                      "so_phong_ngu, so_phong_tam, trang_thai " +
                      "FROM can_ho WHERE so_can_ho LIKE ? ORDER BY id_toa_nha, so_tang DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, "%" + apartmentNumber + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Apartment apartment = new Apartment(
                        rs.getInt("id_can_ho"),
                        rs.getInt("id_toa_nha"),
                        rs.getString("so_can_ho"),
                        rs.getInt("so_tang"),
                        rs.getFloat("dien_tich_m2"),
                        rs.getInt("so_phong_ngu"),
                        rs.getInt("so_phong_tam"),
                        rs.getString("trang_thai")
                    );
                    apartments.add(apartment);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi tìm kiếm căn hộ: " + e.getMessage());
            e.printStackTrace();
        }
        return apartments;
    }
    
    /**
     * Kiểm tra trạng thái hợp lệ
     */
    private static boolean isValidStatus(String status) {
        return "trong".equals(status) || 
               "co_nguoi_o".equals(status) || 
               "bao_tri".equals(status);
    }
    
    /**
     * Lấy thống kê căn hộ theo tòa nhà và trạng thái
     */
    public static Map<String, Integer> getApartmentStatsByBuildingAndStatus(int buildingId) {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("total", 0);
        stats.put("occupied", 0);
        stats.put("available", 0);
        stats.put("maintenance", 0);
        
        String query = "SELECT trang_thai, COUNT(*) as count FROM can_ho " +
                      "WHERE id_toa_nha = ? GROUP BY trang_thai";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, buildingId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String status = rs.getString("trang_thai");
                    int count = rs.getInt("count");
                    
                    if ("co_nguoi_o".equals(status)) {
                        stats.put("occupied", count);
                    } else if ("trong".equals(status)) {
                        stats.put("available", count);
                    } else if ("bao_tri".equals(status)) {
                        stats.put("maintenance", count);
                    }
                    stats.put("total", stats.get("total") + count);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi lấy thống kê căn hộ: " + e.getMessage());
            e.printStackTrace();
        }
        return stats;
    }
}