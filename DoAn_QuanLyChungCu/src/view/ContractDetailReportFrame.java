package view;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import java.util.*;
import dao.*;
import model.*;
import java.sql.*;

public class ContractDetailReportFrame extends JFrame {
    
    public ContractDetailReportFrame() {
        setTitle("📜 Báo Cáo Chi Tiết Hợp Đồng");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(240, 242, 245));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Header
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Content with tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        tabbedPane.addTab("Danh Sách Hợp Đồng", createContractListTab());
        tabbedPane.addTab("Thống Kê", createStatisticsTab());
        tabbedPane.addTab("Chi Tiết Tài Chính", createFinancialTab());
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        setContentPane(mainPanel);
        setVisible(true);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(25, 103, 210));
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("📜 BÁO CÁO CHI TIẾT HỢP ĐỒNG THUÊ");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel descLabel = new JLabel("Quản lý và theo dõi tất cả các hợp đồng thuê trong hệ thống");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        descLabel.setForeground(new Color(200, 220, 255));
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(new Color(25, 103, 210));
        textPanel.add(titleLabel);
        textPanel.add(descLabel);
        
        panel.add(textPanel, BorderLayout.WEST);
        
        return panel;
    }
    
    private JPanel createContractListTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(240, 242, 245));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Summary cards
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        summaryPanel.setBackground(new Color(240, 242, 245));
        
        int[] stats = getContractStats();
        summaryPanel.add(createSummaryCard("Đang Hoạt Động", stats[0], new Color(76, 175, 80)));
        summaryPanel.add(createSummaryCard("Hết Hạn", stats[1], new Color(255, 152, 0)));
        summaryPanel.add(createSummaryCard("Đã Hủy", stats[2], new Color(244, 67, 54)));
        
        panel.add(summaryPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"ID", "Căn Hộ", "Người Thuê", "Ngày Bắt Đầu", "Ngày Kết Thúc", 
                           "Tiền Thuê/Tháng", "Trạng Thái", "Ghi Chú"};
        Object[][] data = getContractTableData();
        
        JTable table = new JTable(new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        
        table.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        table.setRowHeight(28);
        table.getTableHeader().setBackground(new Color(25, 103, 210));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        table.getTableHeader().setPreferredSize(new Dimension(0, 35));
        
        table.getColumnModel().getColumn(6).setCellRenderer(new StatusCellRenderer());
        table.getColumnModel().getColumn(5).setPreferredWidth(120);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(new Color(220, 220, 220)));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createStatisticsTab() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 15, 15));
        panel.setBackground(new Color(240, 242, 245));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Statistics cards
        int[] stats = getContractStats();
        int total = stats[0] + stats[1] + stats[2];
        
        panel.add(createStatCard("Tổng Hợp Đồng", String.valueOf(total), new Color(25, 103, 210)));
        panel.add(createStatCard("Độ Tin Cậy", 
            String.format("%.1f%%", (stats[0] * 100.0 / (total > 0 ? total : 1))),
            new Color(76, 175, 80)));
        panel.add(createStatCard("Hợp Đồng Sắp Hết Hạn", String.valueOf(stats[1]), new Color(255, 152, 0)));
        panel.add(createStatCard("Doanh Thu Tháng", String.format("%,.0f VNĐ", getMonthlyRevenue()), 
            new Color(156, 39, 176)));
        
        return panel;
    }
    
    private JPanel createFinancialTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(240, 242, 245));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Financial summary
        JPanel summaryPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        summaryPanel.setBackground(new Color(240, 242, 245));
        
        Map<String, Double> financial = getFinancialStats();
        summaryPanel.add(createFinancialCard("Tổng Doanh Thu", financial.get("totalRevenue"), new Color(76, 175, 80)));
        summaryPanel.add(createFinancialCard("Tổng Tiền Cọc", financial.get("totalDeposit"), new Color(25, 103, 210)));
        
        panel.add(summaryPanel, BorderLayout.NORTH);
        
        // Revenue by apartment/resident
        String[] columns = {"Căn Hộ", "Người Thuê", "Tiền Thuê/Tháng (VNĐ)", "Tiền Cọc (VNĐ)", "Tổng Có Thể Thu (VNĐ)"};
        Object[][] data = getFinancialTableData();
        
        JTable table = new JTable(new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        
        table.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        table.setRowHeight(28);
        table.getTableHeader().setBackground(new Color(25, 103, 210));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(new Color(220, 220, 220)));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createSummaryCard(String title, int count, Color color) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                
                g2d.setColor(color);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            }
        };
        
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLbl.setForeground(new Color(33, 33, 33));
        
        JLabel countLbl = new JLabel(String.valueOf(count));
        countLbl.setFont(new Font("Segoe UI", Font.BOLD, 28));
        countLbl.setForeground(color);
        
        JLabel unitLbl = new JLabel("hợp đồng");
        unitLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        unitLbl.setForeground(new Color(117, 117, 117));
        
        card.add(titleLbl);
        card.add(Box.createVerticalStrut(5));
        card.add(countLbl);
        card.add(Box.createVerticalStrut(3));
        card.add(unitLbl);
        
        return card;
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                
                g2d.setColor(color);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            }
        };
        
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLbl.setForeground(new Color(33, 33, 33));
        
        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLbl.setForeground(color);
        
        card.add(titleLbl);
        card.add(Box.createVerticalStrut(10));
        card.add(valueLbl);
        
        return card;
    }
    
    private JPanel createFinancialCard(String title, double amount, Color color) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                
                g2d.setColor(color);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            }
        };
        
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLbl.setForeground(new Color(33, 33, 33));
        
        JLabel amountLbl = new JLabel(String.format("%,.0f VNĐ", amount));
        amountLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        amountLbl.setForeground(color);
        
        card.add(titleLbl);
        card.add(Box.createVerticalStrut(10));
        card.add(amountLbl);
        
        return card;
    }
    
    private int[] getContractStats() {
        int[] stats = new int[3];
        
        try (Connection conn = config.DatabaseConnection.getConnection()) {
            String query = "SELECT trang_thai, COUNT(*) as count FROM hop_dong_thue GROUP BY trang_thai";
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    String status = rs.getString("trang_thai");
                    int count = rs.getInt("count");
                    
                    if ("hoat_dong".equals(status)) stats[0] = count;
                    else if ("het_han".equals(status)) stats[1] = count;
                    else if ("huy".equals(status)) stats[2] = count;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return stats;
    }
    
    private Object[][] getContractTableData() {
        List<Object[]> dataList = new ArrayList<>();
        
        try (Connection conn = config.DatabaseConnection.getConnection()) {
            String query = "SELECT h.id_hop_dong, c.so_can_ho, u.ho_ten, h.ngay_bat_dau, " +
                          "h.ngay_ket_thuc, h.tien_thue_hang_thang, h.trang_thai, h.ghi_chu " +
                          "FROM hop_dong_thue h " +
                          "JOIN can_ho c ON h.id_can_ho = c.id_can_ho " +
                          "JOIN nguoi_dung u ON h.id_cu_dan = u.id_nguoi_dung " +
                          "ORDER BY h.ngay_bat_dau DESC";
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    dataList.add(new Object[]{
                        rs.getInt("id_hop_dong"),
                        rs.getString("so_can_ho"),
                        rs.getString("ho_ten"),
                        new java.text.SimpleDateFormat("dd/MM/yyyy").format(rs.getDate("ngay_bat_dau")),
                        rs.getDate("ngay_ket_thuc") != null ? 
                        new java.text.SimpleDateFormat("dd/MM/yyyy").format(rs.getDate("ngay_ket_thuc")) : 
                        "Chưa xác định",
                        String.format("%,.0f", rs.getDouble("tien_thue_hang_thang")),
                        getStatusDisplay(rs.getString("trang_thai")),
                        rs.getString("ghi_chu") != null ? rs.getString("ghi_chu") : ""
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return dataList.toArray(new Object[0][0]);
    }
    
    private Object[][] getFinancialTableData() {
        List<Object[]> dataList = new ArrayList<>();
        
        try (Connection conn = config.DatabaseConnection.getConnection()) {
            String query = "SELECT c.so_can_ho, u.ho_ten, h.tien_thue_hang_thang, " +
                          "h.tien_canh_bao_dao, (h.tien_thue_hang_thang + h.tien_canh_bao_dao) as total " +
                          "FROM hop_dong_thue h " +
                          "JOIN can_ho c ON h.id_can_ho = c.id_can_ho " +
                          "JOIN nguoi_dung u ON h.id_cu_dan = u.id_nguoi_dung " +
                          "WHERE h.trang_thai = 'hoat_dong' " +
                          "ORDER BY h.tien_thue_hang_thang DESC";
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    dataList.add(new Object[]{
                        rs.getString("so_can_ho"),
                        rs.getString("ho_ten"),
                        String.format("%,.0f", rs.getDouble("tien_thue_hang_thang")),
                        String.format("%,.0f", rs.getDouble("tien_canh_bao_dao")),
                        String.format("%,.0f", rs.getDouble("total"))
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return dataList.toArray(new Object[0][0]);
    }
    
    private Map<String, Double> getFinancialStats() {
        Map<String, Double> stats = new HashMap<>();
        stats.put("totalRevenue", 0.0);
        stats.put("totalDeposit", 0.0);
        
        try (Connection conn = config.DatabaseConnection.getConnection()) {
            String query = "SELECT SUM(tien_thue_hang_thang) as revenue, " +
                          "SUM(tien_canh_bao_dao) as deposit " +
                          "FROM hop_dong_thue WHERE trang_thai = 'hoat_dong'";
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                if (rs.next()) {
                    stats.put("totalRevenue", rs.getDouble("revenue") != 0 ? rs.getDouble("revenue") : 0);
                    stats.put("totalDeposit", rs.getDouble("deposit") != 0 ? rs.getDouble("deposit") : 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return stats;
    }
    
    private double getMonthlyRevenue() {
        Map<String, Double> stats = getFinancialStats();
        return stats.get("totalRevenue");
    }
    
    private String getStatusDisplay(String status) {
        switch (status) {
            case "hoat_dong":
                return "✓ Đang Hoạt Động";
            case "het_han":
                return "⏳ Hết Hạn";
            case "huy":
                return "✗ Đã Hủy";
            default:
                return status;
        }
    }
    
    static class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            String status = (String) value;
            if (status.contains("Đang Hoạt Động")) {
                label.setBackground(new Color(200, 230, 201));
                label.setForeground(new Color(27, 94, 32));
            } else if (status.contains("Hết Hạn")) {
                label.setBackground(new Color(255, 235, 205));
                label.setForeground(new Color(230, 126, 34));
            } else if (status.contains("Đã Hủy")) {
                label.setBackground(new Color(255, 205, 210));
                label.setForeground(new Color(192, 57, 43));
            }
            label.setOpaque(true);
            label.setBorder(new EmptyBorder(5, 10, 5, 10));
            label.setFont(new Font("Segoe UI", Font.BOLD, 10));
            
            return label;
        }
    }
}