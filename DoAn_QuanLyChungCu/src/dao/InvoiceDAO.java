package dao;

import config.DatabaseConnection;
import java.sql.*;
import java.util.*;

public class InvoiceDAO {
    
    /**
     * Tạo hóa đơn điện tự động cho tháng mới
     */
    public static boolean createElectricityInvoice(int apartmentId, int month, int year,
                                                   int oldReading, int newReading, double pricePerUnit) {
        int consumption = newReading - oldReading;
        double totalAmount = consumption * pricePerUnit;
        
        String query = "INSERT INTO hoa_don_dien (id_can_ho, thang, nam, chi_so_cu, " +
                      "chi_so_moi, tien_tieu_thụ, gia_tien_dien, tong_tien, trang_thai) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'chua_thanh_toan')";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, apartmentId);
            pstmt.setInt(2, month);
            pstmt.setInt(3, year);
            pstmt.setInt(4, oldReading);
            pstmt.setInt(5, newReading);
            pstmt.setInt(6, consumption);
            pstmt.setDouble(7, pricePerUnit);
            pstmt.setDouble(8, totalAmount);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi tạo hóa đơn điện: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Tạo hóa đơn nước tự động
     */
    public static boolean createWaterInvoice(int apartmentId, int month, int year,
                                            int oldReading, int newReading, double pricePerUnit) {
        int consumption = newReading - oldReading;
        double totalAmount = consumption * pricePerUnit;
        
        String query = "INSERT INTO hoa_don_nuoc (id_can_ho, thang, nam, chi_so_cu, " +
                      "chi_so_moi, tien_tieu_thụ, gia_tien_nuoc, tong_tien, trang_thai) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'chua_thanh_toan')";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, apartmentId);
            pstmt.setInt(2, month);
            pstmt.setInt(3, year);
            pstmt.setInt(4, oldReading);
            pstmt.setInt(5, newReading);
            pstmt.setInt(6, consumption);
            pstmt.setDouble(7, pricePerUnit);
            pstmt.setDouble(8, totalAmount);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi tạo hóa đơn nước: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Tạo hóa đơn quản lý chung
     */
    public static boolean createManagementFeeInvoice(int apartmentId, int month, int year, double amount) {
        String query = "INSERT INTO hoa_don_quan_ly_chung (id_can_ho, thang, nam, so_tien, trang_thai) " +
                      "VALUES (?, ?, ?, ?, 'chua_thanh_toan')";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, apartmentId);
            pstmt.setInt(2, month);
            pstmt.setInt(3, year);
            pstmt.setDouble(4, amount);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi tạo hóa đơn quản lý: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Thanh toán hóa đơn điện
     */
    public static boolean payElectricityInvoice(int invoiceId) {
        String query = "UPDATE hoa_don_dien SET trang_thai = 'da_thanh_toan', " +
                      "ngay_thanh_toan = NOW() WHERE id_hoa_don = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, invoiceId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi thanh toán hóa đơn: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Thanh toán hóa đơn nước
     */
    public static boolean payWaterInvoice(int invoiceId) {
        String query = "UPDATE hoa_don_nuoc SET trang_thai = 'da_thanh_toan', " +
                      "ngay_thanh_toan = NOW() WHERE id_hoa_don = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, invoiceId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi thanh toán hóa đơn: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Thanh toán hóa đơn quản lý
     */
    public static boolean payManagementFeeInvoice(int invoiceId) {
        String query = "UPDATE hoa_don_quan_ly_chung SET trang_thai = 'da_thanh_toan', " +
                      "ngay_thanh_toan = NOW() WHERE id_hoa_don = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, invoiceId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi thanh toán hóa đơn: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Lấy danh sách hóa đơn chưa thanh toán của căn hộ
     */
    public static Map<String, List<Map<String, Object>>> getUnpaidInvoices(int apartmentId) {
        Map<String, List<Map<String, Object>>> invoices = new HashMap<>();
        invoices.put("electricity", new ArrayList<>());
        invoices.put("water", new ArrayList<>());
        invoices.put("management", new ArrayList<>());
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Hóa đơn điện
            String elecQuery = "SELECT * FROM hoa_don_dien WHERE id_can_ho = ? " +
                             "AND trang_thai IN ('chua_thanh_toan', 'qua_han') ORDER BY nam DESC, thang DESC";
            try (PreparedStatement pstmt = conn.prepareStatement(elecQuery)) {
                pstmt.setInt(1, apartmentId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> invoice = new HashMap<>();
                        invoice.put("id", rs.getInt("id_hoa_don"));
                        invoice.put("month", rs.getInt("thang"));
                        invoice.put("year", rs.getInt("nam"));
                        invoice.put("amount", rs.getDouble("tong_tien"));
                        invoice.put("status", rs.getString("trang_thai"));
                        invoices.get("electricity").add(invoice);
                    }
                }
            }
            
            // Hóa đơn nước
            String waterQuery = "SELECT * FROM hoa_don_nuoc WHERE id_can_ho = ? " +
                              "AND trang_thai IN ('chua_thanh_toan', 'qua_han') ORDER BY nam DESC, thang DESC";
            try (PreparedStatement pstmt = conn.prepareStatement(waterQuery)) {
                pstmt.setInt(1, apartmentId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> invoice = new HashMap<>();
                        invoice.put("id", rs.getInt("id_hoa_don"));
                        invoice.put("month", rs.getInt("thang"));
                        invoice.put("year", rs.getInt("nam"));
                        invoice.put("amount", rs.getDouble("tong_tien"));
                        invoice.put("status", rs.getString("trang_thai"));
                        invoices.get("water").add(invoice);
                    }
                }
            }
            
            // Hóa đơn quản lý
            String mgmtQuery = "SELECT * FROM hoa_don_quan_ly_chung WHERE id_can_ho = ? " +
                             "AND trang_thai IN ('chua_thanh_toan', 'qua_han') ORDER BY nam DESC, thang DESC";
            try (PreparedStatement pstmt = conn.prepareStatement(mgmtQuery)) {
                pstmt.setInt(1, apartmentId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> invoice = new HashMap<>();
                        invoice.put("id", rs.getInt("id_hoa_don"));
                        invoice.put("month", rs.getInt("thang"));
                        invoice.put("year", rs.getInt("nam"));
                        invoice.put("amount", rs.getDouble("so_tien"));
                        invoice.put("status", rs.getString("trang_thai"));
                        invoices.get("management").add(invoice);
                    }
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi lấy hóa đơn: " + e.getMessage());
            e.printStackTrace();
        }
        
        return invoices;
    }
    
    /**
     * Tạo hóa đơn định kỳ cho căn hộ có hợp đồng hoạt động
     */
    public static boolean createMonthlyInvoicesForApartment(int apartmentId, int month, int year) {
        // Tạo hóa đơn điện (giả sử chỉ số cũ = chỉ số mới của tháng trước)
        int oldElecReading = getLastElectricityReading(apartmentId);
        int newElecReading = oldElecReading; // Sẽ được cập nhật sau
        createElectricityInvoice(apartmentId, month, year, oldElecReading, newElecReading, 3000);
        
        // Tạo hóa đơn nước
        int oldWaterReading = getLastWaterReading(apartmentId);
        int newWaterReading = oldWaterReading; // Sẽ được cập nhật sau
        createWaterInvoice(apartmentId, month, year, oldWaterReading, newWaterReading, 20000);
        
        // Tạo hóa đơn quản lý (50,000 VND/m²)
        double area = getApartmentArea(apartmentId);
        createManagementFeeInvoice(apartmentId, month, year, area * 50000);
        
        return true;
    }
    
    private static int getLastElectricityReading(int apartmentId) {
        String query = "SELECT chi_so_moi FROM hoa_don_dien WHERE id_can_ho = ? " +
                      "ORDER BY nam DESC, thang DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, apartmentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("chi_so_moi");
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    private static int getLastWaterReading(int apartmentId) {
        String query = "SELECT chi_so_moi FROM hoa_don_nuoc WHERE id_can_ho = ? " +
                      "ORDER BY nam DESC, thang DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, apartmentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("chi_so_moi");
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    private static double getApartmentArea(int apartmentId) {
        String query = "SELECT dien_tich_m2 FROM can_ho WHERE id_can_ho = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, apartmentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("dien_tich_m2");
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Lấy chi tiết tất cả hóa đơn của căn hộ (bao gồm cả đã thanh toán)
     */
    public static Map<String, List<Map<String, Object>>> getAllInvoices(int apartmentId) {
        Map<String, List<Map<String, Object>>> invoices = new HashMap<>();
        invoices.put("electricity", new ArrayList<>());
        invoices.put("water", new ArrayList<>());
        invoices.put("management", new ArrayList<>());
        invoices.put("other", new ArrayList<>());
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Hóa đơn điện
            String elecQuery = "SELECT * FROM hoa_don_dien WHERE id_can_ho = ? ORDER BY nam DESC, thang DESC";
            try (PreparedStatement pstmt = conn.prepareStatement(elecQuery)) {
                pstmt.setInt(1, apartmentId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> invoice = new HashMap<>();
                        invoice.put("id", rs.getInt("id_hoa_don"));
                        invoice.put("month", rs.getInt("thang"));
                        invoice.put("year", rs.getInt("nam"));
                        invoice.put("oldReading", rs.getInt("chi_so_cu"));
                        invoice.put("newReading", rs.getInt("chi_so_moi"));
                        invoice.put("consumption", rs.getInt("tien_tieu_thụ"));
                        invoice.put("amount", rs.getDouble("tong_tien"));
                        invoice.put("status", rs.getString("trang_thai"));
                        invoice.put("paymentDate", rs.getTimestamp("ngay_thanh_toan"));
                        invoices.get("electricity").add(invoice);
                    }
                }
            }
            
            // Hóa đơn nước
            String waterQuery = "SELECT * FROM hoa_don_nuoc WHERE id_can_ho = ? ORDER BY nam DESC, thang DESC";
            try (PreparedStatement pstmt = conn.prepareStatement(waterQuery)) {
                pstmt.setInt(1, apartmentId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> invoice = new HashMap<>();
                        invoice.put("id", rs.getInt("id_hoa_don"));
                        invoice.put("month", rs.getInt("thang"));
                        invoice.put("year", rs.getInt("nam"));
                        invoice.put("oldReading", rs.getInt("chi_so_cu"));
                        invoice.put("newReading", rs.getInt("chi_so_moi"));
                        invoice.put("consumption", rs.getInt("tien_tieu_thụ"));
                        invoice.put("amount", rs.getDouble("tong_tien"));
                        invoice.put("status", rs.getString("trang_thai"));
                        invoice.put("paymentDate", rs.getTimestamp("ngay_thanh_toan"));
                        invoices.get("water").add(invoice);
                    }
                }
            }
            
            // Hóa đơn quản lý
            String mgmtQuery = "SELECT * FROM hoa_don_quan_ly_chung WHERE id_can_ho = ? ORDER BY nam DESC, thang DESC";
            try (PreparedStatement pstmt = conn.prepareStatement(mgmtQuery)) {
                pstmt.setInt(1, apartmentId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> invoice = new HashMap<>();
                        invoice.put("id", rs.getInt("id_hoa_don"));
                        invoice.put("month", rs.getInt("thang"));
                        invoice.put("year", rs.getInt("nam"));
                        invoice.put("amount", rs.getDouble("so_tien"));
                        invoice.put("status", rs.getString("trang_thai"));
                        invoice.put("paymentDate", rs.getTimestamp("ngay_thanh_toan"));
                        invoices.get("management").add(invoice);
                    }
                }
            }
            
            // Hóa đơn dịch vụ khác
            String otherQuery = "SELECT * FROM hoa_don_dich_vu_khac WHERE id_can_ho = ? ORDER BY nam DESC, thang DESC";
            try (PreparedStatement pstmt = conn.prepareStatement(otherQuery)) {
                pstmt.setInt(1, apartmentId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> invoice = new HashMap<>();
                        invoice.put("id", rs.getInt("id_hoa_don"));
                        invoice.put("serviceName", rs.getString("ten_dich_vu"));
                        invoice.put("month", rs.getInt("thang"));
                        invoice.put("year", rs.getInt("nam"));
                        invoice.put("amount", rs.getDouble("so_tien"));
                        invoice.put("status", rs.getString("trang_thai"));
                        invoice.put("paymentDate", rs.getTimestamp("ngay_thanh_toan"));
                        invoices.get("other").add(invoice);
                    }
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi lấy hóa đơn: " + e.getMessage());
            e.printStackTrace();
        }
        
        return invoices;
    }
    
    
    /**
     * Cập nhật chỉ số điện mới
     */
    public static boolean updateElectricityReading(int invoiceId, int newReading, double pricePerUnit) {
        String getQuery = "SELECT chi_so_cu FROM hoa_don_dien WHERE id_hoa_don = ?";
        String updateQuery = "UPDATE hoa_don_dien SET chi_so_moi = ?, tien_tieu_thụ = ?, tong_tien = ? WHERE id_hoa_don = ?";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            int oldReading = 0;
            try (PreparedStatement pstmt = conn.prepareStatement(getQuery)) {
                pstmt.setInt(1, invoiceId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        oldReading = rs.getInt("chi_so_cu");
                    }
                }
            }
            
            int consumption = newReading - oldReading;
            double totalAmount = consumption * pricePerUnit;
            
            try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
                pstmt.setInt(1, newReading);
                pstmt.setInt(2, consumption);
                pstmt.setDouble(3, totalAmount);
                pstmt.setInt(4, invoiceId);
                
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi cập nhật chỉ số điện: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cập nhật chỉ số nước mới
     */
    public static boolean updateWaterReading(int invoiceId, int newReading, double pricePerUnit) {
        String getQuery = "SELECT chi_so_cu FROM hoa_don_nuoc WHERE id_hoa_don = ?";
        String updateQuery = "UPDATE hoa_don_nuoc SET chi_so_moi = ?, tien_tieu_thụ = ?, tong_tien = ? WHERE id_hoa_don = ?";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            int oldReading = 0;
            try (PreparedStatement pstmt = conn.prepareStatement(getQuery)) {
                pstmt.setInt(1, invoiceId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        oldReading = rs.getInt("chi_so_cu");
                    }
                }
            }
            
            int consumption = newReading - oldReading;
            double totalAmount = consumption * pricePerUnit;
            
            try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
                pstmt.setInt(1, newReading);
                pstmt.setInt(2, consumption);
                pstmt.setDouble(3, totalAmount);
                pstmt.setInt(4, invoiceId);
                
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi cập nhật chỉ số nước: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Thanh toán hóa đơn dịch vụ khác
     */
    public static boolean payOtherServiceInvoice(int invoiceId) {
        String query = "UPDATE hoa_don_dich_vu_khac SET trang_thai = 'da_thanh_toan', " +
                      "ngay_thanh_toan = NOW() WHERE id_hoa_don = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, invoiceId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi thanh toán hóa đơn: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    
    /**
     * Cập nhật hóa đơn điện
     */
    public static boolean updateElectricityInvoice(int invoiceId, int month, int year,
                                                   int oldReading, int newReading, double pricePerUnit) {
        int consumption = newReading - oldReading;
        double totalAmount = consumption * pricePerUnit;
        
        String query = "UPDATE hoa_don_dien SET thang = ?, nam = ?, chi_so_cu = ?, " +
                      "chi_so_moi = ?, tien_tieu_thụ = ?, gia_tien_dien = ?, tong_tien = ? " +
                      "WHERE id_hoa_don = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, month);
            pstmt.setInt(2, year);
            pstmt.setInt(3, oldReading);
            pstmt.setInt(4, newReading);
            pstmt.setInt(5, consumption);
            pstmt.setDouble(6, pricePerUnit);
            pstmt.setDouble(7, totalAmount);
            pstmt.setInt(8, invoiceId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi cập nhật hóa đơn điện: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Cập nhật hóa đơn nước
     */
    public static boolean updateWaterInvoice(int invoiceId, int month, int year,
                                            int oldReading, int newReading, double pricePerUnit) {
        int consumption = newReading - oldReading;
        double totalAmount = consumption * pricePerUnit;
        
        String query = "UPDATE hoa_don_nuoc SET thang = ?, nam = ?, chi_so_cu = ?, " +
                      "chi_so_moi = ?, tien_tieu_thụ = ?, gia_tien_nuoc = ?, tong_tien = ? " +
                      "WHERE id_hoa_don = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, month);
            pstmt.setInt(2, year);
            pstmt.setInt(3, oldReading);
            pstmt.setInt(4, newReading);
            pstmt.setInt(5, consumption);
            pstmt.setDouble(6, pricePerUnit);
            pstmt.setDouble(7, totalAmount);
            pstmt.setInt(8, invoiceId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi cập nhật hóa đơn nước: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Cập nhật hóa đơn phí quản lý
     */
    public static boolean updateManagementFeeInvoice(int invoiceId, int month, int year, double amount) {
        String query = "UPDATE hoa_don_quan_ly_chung SET thang = ?, nam = ?, so_tien = ? " +
                      "WHERE id_hoa_don = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, month);
            pstmt.setInt(2, year);
            pstmt.setDouble(3, amount);
            pstmt.setInt(4, invoiceId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi cập nhật hóa đơn phí quản lý: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Cập nhật hóa đơn dịch vụ khác
     */
    public static boolean updateOtherServiceInvoice(int invoiceId, String serviceName,
                                                   int month, int year, double amount) {
        String query = "UPDATE hoa_don_dich_vu_khac SET ten_dich_vu = ?, thang = ?, nam = ?, so_tien = ? " +
                      "WHERE id_hoa_don = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, serviceName);
            pstmt.setInt(2, month);
            pstmt.setInt(3, year);
            pstmt.setDouble(4, amount);
            pstmt.setInt(5, invoiceId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi cập nhật hóa đơn dịch vụ khác: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Xóa hóa đơn điện
     */
    public static boolean deleteElectricityInvoice(int invoiceId) {
        String query = "DELETE FROM hoa_don_dien WHERE id_hoa_don = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, invoiceId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi xóa hóa đơn điện: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Xóa hóa đơn nước
     */
    public static boolean deleteWaterInvoice(int invoiceId) {
        String query = "DELETE FROM hoa_don_nuoc WHERE id_hoa_don = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, invoiceId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi xóa hóa đơn nước: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Xóa hóa đơn phí quản lý
     */
    public static boolean deleteManagementFeeInvoice(int invoiceId) {
        String query = "DELETE FROM hoa_don_quan_ly_chung WHERE id_hoa_don = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, invoiceId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi xóa hóa đơn phí quản lý: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Xóa hóa đơn dịch vụ khác
     */
    public static boolean deleteOtherServiceInvoice(int invoiceId) {
        String query = "DELETE FROM hoa_don_dich_vu_khac WHERE id_hoa_don = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, invoiceId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi khi xóa hóa đơn dịch vụ khác: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Lấy thông tin chi tiết hóa đơn điện
     */
    public static Map<String, Object> getElectricityInvoiceDetail(int invoiceId) {
        String query = "SELECT * FROM hoa_don_dien WHERE id_hoa_don = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, invoiceId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> invoice = new HashMap<>();
                    invoice.put("id", rs.getInt("id_hoa_don"));
                    invoice.put("apartmentId", rs.getInt("id_can_ho"));
                    invoice.put("month", rs.getInt("thang"));
                    invoice.put("year", rs.getInt("nam"));
                    invoice.put("oldReading", rs.getInt("chi_so_cu"));
                    invoice.put("newReading", rs.getInt("chi_so_moi"));
                    invoice.put("consumption", rs.getInt("tien_tieu_thụ"));
                    invoice.put("pricePerUnit", rs.getDouble("gia_tien_dien"));
                    invoice.put("amount", rs.getDouble("tong_tien"));
                    invoice.put("status", rs.getString("trang_thai"));
                    invoice.put("paymentDate", rs.getTimestamp("ngay_thanh_toan"));
                    return invoice;
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Lấy thông tin chi tiết hóa đơn nước
     */
    public static Map<String, Object> getWaterInvoiceDetail(int invoiceId) {
        String query = "SELECT * FROM hoa_don_nuoc WHERE id_hoa_don = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, invoiceId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> invoice = new HashMap<>();
                    invoice.put("id", rs.getInt("id_hoa_don"));
                    invoice.put("apartmentId", rs.getInt("id_can_ho"));
                    invoice.put("month", rs.getInt("thang"));
                    invoice.put("year", rs.getInt("nam"));
                    invoice.put("oldReading", rs.getInt("chi_so_cu"));
                    invoice.put("newReading", rs.getInt("chi_so_moi"));
                    invoice.put("consumption", rs.getInt("tien_tieu_thụ"));
                    invoice.put("pricePerUnit", rs.getDouble("gia_tien_nuoc"));
                    invoice.put("amount", rs.getDouble("tong_tien"));
                    invoice.put("status", rs.getString("trang_thai"));
                    invoice.put("paymentDate", rs.getTimestamp("ngay_thanh_toan"));
                    return invoice;
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Lấy thông tin chi tiết hóa đơn phí quản lý
     */
    public static Map<String, Object> getManagementFeeInvoiceDetail(int invoiceId) {
        String query = "SELECT * FROM hoa_don_quan_ly_chung WHERE id_hoa_don = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, invoiceId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> invoice = new HashMap<>();
                    invoice.put("id", rs.getInt("id_hoa_don"));
                    invoice.put("apartmentId", rs.getInt("id_can_ho"));
                    invoice.put("month", rs.getInt("thang"));
                    invoice.put("year", rs.getInt("nam"));
                    invoice.put("amount", rs.getDouble("so_tien"));
                    invoice.put("status", rs.getString("trang_thai"));
                    invoice.put("paymentDate", rs.getTimestamp("ngay_thanh_toan"));
                    return invoice;
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Lấy thông tin chi tiết hóa đơn dịch vụ khác
     */
    public static Map<String, Object> getOtherServiceInvoiceDetail(int invoiceId) {
        String query = "SELECT * FROM hoa_don_dich_vu_khac WHERE id_hoa_don = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, invoiceId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> invoice = new HashMap<>();
                    invoice.put("id", rs.getInt("id_hoa_don"));
                    invoice.put("apartmentId", rs.getInt("id_can_ho"));
                    invoice.put("serviceName", rs.getString("ten_dich_vu"));
                    invoice.put("month", rs.getInt("thang"));
                    invoice.put("year", rs.getInt("nam"));
                    invoice.put("amount", rs.getDouble("so_tien"));
                    invoice.put("status", rs.getString("trang_thai"));
                    invoice.put("paymentDate", rs.getTimestamp("ngay_thanh_toan"));
                    return invoice;
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
    
    // ----------------------
}