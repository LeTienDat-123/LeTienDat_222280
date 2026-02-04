package dao;

import model.RentalContract;
import config.DatabaseConnection;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.ArrayList;

public class RentalContractDAO {
    
    /**
     * Tạo hợp đồng thuê mới
     */
    public static int createRentalContract(int apartmentId, int residentId, Date startDate,
                                          Date endDate, double monthlyRent, double deposit, String notes) {
        String query = "INSERT INTO hop_dong_thue (id_can_ho, id_cu_dan, ngay_bat_dau, " +
                      "ngay_ket_thuc, tien_thue_hang_thang, tien_canh_bao_dao, trang_thai, ghi_chu) " +
                      "VALUES (?, ?, ?, ?, ?, ?, 'hoat_dong', ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, apartmentId);
            pstmt.setInt(2, residentId);
            pstmt.setDate(3, (java.sql.Date) startDate);
            pstmt.setDate(4, (java.sql.Date) endDate);
            pstmt.setDouble(5, monthlyRent);
            pstmt.setDouble(6, deposit);
            pstmt.setString(7, notes);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int contractId = generatedKeys.getInt(1);
                        System.out.println("✓ Tạo hợp đồng thuê thành công: ID=" + contractId);
                        return contractId;
                    }
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi tạo hợp đồng thuê: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }
    
    /**
     * Lấy hợp đồng đang hoạt động của căn hộ
     */
    public static RentalContract getActiveContractByApartment(int apartmentId) {
        String query = "SELECT * FROM hop_dong_thue " +
                      "WHERE id_can_ho = ? AND trang_thai = 'hoat_dong' " +
                      "ORDER BY ngay_bat_dau DESC LIMIT 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, apartmentId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new RentalContract(
                        rs.getInt("id_hop_dong"),
                        rs.getInt("id_can_ho"),
                        rs.getInt("id_cu_dan"),
                        rs.getDate("ngay_bat_dau"),
                        rs.getDate("ngay_ket_thuc"),
                        rs.getDouble("tien_thue_hang_thang"),
                        rs.getDouble("tien_canh_bao_dao"),
                        rs.getString("trang_thai"),
                        rs.getString("ghi_chu")
                    );
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi lấy hợp đồng: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Lấy tất cả hợp đồng của căn hộ
     */
    public static List<RentalContract> getContractsByApartment(int apartmentId) {
        List<RentalContract> contracts = new ArrayList<>();
        String query = "SELECT * FROM hop_dong_thue WHERE id_can_ho = ? ORDER BY ngay_bat_dau DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, apartmentId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    RentalContract contract = new RentalContract(
                        rs.getInt("id_hop_dong"),
                        rs.getInt("id_can_ho"),
                        rs.getInt("id_cu_dan"),
                        rs.getDate("ngay_bat_dau"),
                        rs.getDate("ngay_ket_thuc"),
                        rs.getDouble("tien_thue_hang_thang"),
                        rs.getDouble("tien_canh_bao_dao"),
                        rs.getString("trang_thai"),
                        rs.getString("ghi_chu")
                    );
                    contracts.add(contract);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi lấy danh sách hợp đồng: " + e.getMessage());
            e.printStackTrace();
        }
        return contracts;
    }
    
    /**
     * Chấm dứt hợp đồng
     */
    public static boolean terminateContract(int contractId) {
        String query = "UPDATE hop_dong_thue SET trang_thai = 'het_han' WHERE id_hop_dong = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, contractId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✓ Chấm dứt hợp đồng thành công: ID=" + contractId);
                return true;
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi chấm dứt hợp đồng: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Hủy hợp đồng
     */
    public static boolean cancelContract(int contractId) {
        String query = "UPDATE hop_dong_thue SET trang_thai = 'huy' WHERE id_hop_dong = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, contractId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✓ Hủy hợp đồng thành công: ID=" + contractId);
                return true;
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi hủy hợp đồng: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Cập nhật thông tin hợp đồng
     */
    public static boolean updateContract(int contractId, Date endDate, double monthlyRent, String notes) {
        String query = "UPDATE hop_dong_thue SET ngay_ket_thuc = ?, " +
                      "tien_thue_hang_thang = ?, ghi_chu = ? WHERE id_hop_dong = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setDate(1,(java.sql.Date) endDate);
            pstmt.setDouble(2, monthlyRent);
            pstmt.setString(3, notes);
            pstmt.setInt(4, contractId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✓ Cập nhật hợp đồng thành công: ID=" + contractId);
                return true;
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi cập nhật hợp đồng: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Lấy hợp đồng theo ID
     */
    public static RentalContract getContractById(int contractId) {
        String query = "SELECT * FROM hop_dong_thue WHERE id_hop_dong = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, contractId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new RentalContract(
                        rs.getInt("id_hop_dong"),
                        rs.getInt("id_can_ho"),
                        rs.getInt("id_cu_dan"),
                        rs.getDate("ngay_bat_dau"),
                        rs.getDate("ngay_ket_thuc"),
                        rs.getDouble("tien_thue_hang_thang"),
                        rs.getDouble("tien_canh_bao_dao"),
                        rs.getString("trang_thai"),
                        rs.getString("ghi_chu")
                    );
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi lấy thông tin hợp đồng: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}