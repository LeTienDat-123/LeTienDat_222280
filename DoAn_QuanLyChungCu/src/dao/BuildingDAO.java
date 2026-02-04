package dao;
import model.Building;
import model.Apartment;
import config.DatabaseConnection;
import java.sql.*;
import java.util.*;
public class BuildingDAO {
    
    /**
     * Lấy danh sách tất cả tòa nhà
     */
    public static List<Building> getAllBuildings() {
        List<Building> buildings = new ArrayList<>();
        String query = "SELECT id_toa_nha, ten_toa_nha, dia_chi, so_tang, nam_xay_dung, id_quan_ly FROM toa_nha";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Building building = new Building(
                    rs.getInt("id_toa_nha"),
                    rs.getString("ten_toa_nha"),
                    rs.getString("dia_chi"),
                    rs.getInt("so_tang"),
                    rs.getInt("nam_xay_dung"),
                    rs.getInt("id_quan_ly")
                );
                buildings.add(building);
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi lấy danh sách tòa nhà: " + e.getMessage());
            e.printStackTrace();
        }
        return buildings;
    }
    
    /**
     * Lấy thông tin tòa nhà theo ID
     */
    public static Building getBuildingById(int buildingId) {
        String query = "SELECT id_toa_nha, ten_toa_nha, dia_chi, so_tang, nam_xay_dung, id_quan_ly FROM toa_nha WHERE id_toa_nha = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, buildingId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Building(
                        rs.getInt("id_toa_nha"),
                        rs.getString("ten_toa_nha"),
                        rs.getString("dia_chi"),
                        rs.getInt("so_tang"),
                        rs.getInt("nam_xay_dung"),
                        rs.getInt("id_quan_ly")
                    );
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi lấy thông tin tòa nhà: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Lấy danh sách căn hộ theo tòa nhà
     */
    public static List<Apartment> getApartmentsByBuilding(int buildingId) {
        List<Apartment> apartments = new ArrayList<>();
        String query = "SELECT id_can_ho, id_toa_nha, so_can_ho, so_tang, dien_tich_m2, " +
                      "so_phong_ngu, so_phong_tam, trang_thai FROM can_ho WHERE id_toa_nha = ? ORDER BY so_tang DESC, so_can_ho";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, buildingId);
            
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
            System.out.println("✗ Lỗi khi lấy danh sách căn hộ: " + e.getMessage());
            e.printStackTrace();
        }
        return apartments;
    }
    
    /**
     * Lấy thống kê căn hộ theo tòa nhà
     */
    public static Map<String, Integer> getApartmentStatsByBuilding(int buildingId) {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("total", 0);
        stats.put("occupied", 0);
        stats.put("available", 0);
        stats.put("maintenance", 0);
        
        String query = "SELECT trang_thai, COUNT(*) as count FROM can_ho WHERE id_toa_nha = ? GROUP BY trang_thai";
        
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
    
    /**
     * Lấy thống kê tất cả căn hộ
     */
    public static Map<String, Integer> getAllApartmentStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("total", 0);
        stats.put("occupied", 0);
        stats.put("available", 0);
        stats.put("maintenance", 0);
        
        String query = "SELECT trang_thai, COUNT(*) as count FROM can_ho GROUP BY trang_thai";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
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
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi lấy thống kê căn hộ toàn hệ thống: " + e.getMessage());
            e.printStackTrace();
        }
        return stats;
    }
    
    /**
     * Lấy doanh thu theo tòa nhà
     */
    public static Map<Integer, Double> getRevenueByBuilding() {
        Map<Integer, Double> revenue = new HashMap<>();
        String query = "SELECT t.id_toa_nha, t.ten_toa_nha, " +
                      "COALESCE(SUM(h.tien_thue_hang_thang), 0) as tong_tien " +
                      "FROM toa_nha t " +
                      "LEFT JOIN can_ho c ON t.id_toa_nha = c.id_toa_nha " +
                      "LEFT JOIN hop_dong_thue h ON c.id_can_ho = h.id_can_ho AND h.trang_thai = 'hoat_dong' " +
                      "GROUP BY t.id_toa_nha";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                int buildingId = rs.getInt("id_toa_nha");
                double amount = rs.getDouble("tong_tien");
                revenue.put(buildingId, amount);
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi lấy doanh thu theo tòa nhà: " + e.getMessage());
            e.printStackTrace();
        }
        return revenue;
    }
}