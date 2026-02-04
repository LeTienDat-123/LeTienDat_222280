package view;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import java.util.*;
import dao.*;
import java.sql.*;

public class InvoiceDetailReportFrame extends JFrame {
    
    public InvoiceDetailReportFrame() {
        setTitle("Báo Cáo Chi Tiết Hóa Đơn");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(240, 242, 245));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Header
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Content with tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        tabbedPane.addTab("Tiền Điện", createElectricityTab());
        tabbedPane.addTab("Tiền Nước", createWaterTab());
        tabbedPane.addTab("Phí Quản Lý", createManagementTab());
        tabbedPane.addTab("Thống Kê", createStatisticsTab());
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        setContentPane(mainPanel);
        setVisible(true);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(25, 103, 210));
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("BÁO CÁO CHI TIẾT HÓA ĐƠN");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel descLabel = new JLabel("Quản lý và theo dõi các hóa đơn tiền điện, nước và phí quản lý");
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
    
    private JPanel createElectricityTab() {
        return createInvoiceTabPanel("hoa_don_dien", "Tiền Điện");
    }
    
    private JPanel createWaterTab() {
        return createInvoiceTabPanel("hoa_don_nuoc", "Tiền Nước");
    }
    
    private JPanel createManagementTab() {
        return createInvoiceTabPanel("hoa_don_quan_ly_chung", "Phí Quản Lý");
    }
    
    private JPanel createInvoiceTabPanel(String tableName, String title) {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(240, 242, 245));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Summary cards
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        summaryPanel.setBackground(new Color(240, 242, 245));
        
        int[] stats = getInvoiceStats(tableName);
        summaryPanel.add(createSummaryCard("Đã Thanh Toán", stats[0], new Color(76, 175, 80)));
        summaryPanel.add(createSummaryCard("Chưa Thanh Toán", stats[1], new Color(255, 152, 0)));
        summaryPanel.add(createSummaryCard("Quá Hạn", stats[2], new Color(244, 67, 54)));
        
        panel.add(summaryPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"ID", "Căn Hộ", "Tháng/Năm", "Số Tiền (VNĐ)", "Trạng Thái", "Ngày Thanh Toán"};
        Object[][] data = getInvoiceTableData(tableName);
        
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
        
        table.getColumnModel().getColumn(4).setCellRenderer(new StatusCellRenderer());
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(new Color(220, 220, 220)));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createStatisticsTab() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 15, 15));
        panel.setBackground(new Color(240, 242, 245));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Get statistics data
        int[] elecStats = getInvoiceStats("hoa_don_dien");
        int[] waterStats = getInvoiceStats("hoa_don_nuoc");
        int[] mgmtStats = getInvoiceStats("hoa_don_quan_ly_chung");
        
        double[] elecRevenue = getRevenueStats("hoa_don_dien");
        double[] waterRevenue = getRevenueStats("hoa_don_nuoc");
        double[] mgmtRevenue = getRevenueStats("hoa_don_quan_ly_chung");
        
        // Add statistics panels
        panel.add(createStatPanel("Trạng Thái Thanh Toán", 
            new String[]{"Điện", "Nước", "Quản Lý"},
            new int[][]{elecStats, waterStats, mgmtStats}));
        
        panel.add(createRevenuePanel("Doanh Thu Theo Loại (VNĐ)",
            new String[]{"Tiền Điện", "Tiền Nước", "Phí Quản Lý"},
            new double[][]{elecRevenue, waterRevenue, mgmtRevenue}));
        
        panel.add(createTotalPanel("Tổng Quan Hóa Đơn", elecStats, waterStats, mgmtStats));
        
        panel.add(createMonthlyPanel("Xu Hướng Thanh Toán"));
        
        return panel;
    }
    
    private JPanel createStatPanel(String title, String[] categories, int[][] data) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new CompoundBorder(
            new LineBorder(new Color(220, 220, 220)),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(25, 103, 210));
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Create table for statistics
        String[] columns = {"Loại", "Đã TT", "Chưa TT", "Quá Hạn", "Tổng"};
        Object[][] tableData = new Object[categories.length][5];
        
        for (int i = 0; i < categories.length; i++) {
            tableData[i][0] = categories[i];
            tableData[i][1] = data[i][0];
            tableData[i][2] = data[i][1];
            tableData[i][3] = data[i][2];
            tableData[i][4] = data[i][0] + data[i][1] + data[i][2];
        }
        
        JTable table = new JTable(tableData, columns);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        table.setEnabled(false);
        
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createRevenuePanel(String title, String[] categories, double[][] data) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new CompoundBorder(
            new LineBorder(new Color(220, 220, 220)),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(25, 103, 210));
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Create table for revenue
        String[] columns = {"Loại", "Đã Thu (VNĐ)", "Chưa Thu (VNĐ)", "Quá Hạn (VNĐ)", "Tổng (VNĐ)"};
        Object[][] tableData = new Object[categories.length][5];
        
        for (int i = 0; i < categories.length; i++) {
            tableData[i][0] = categories[i];
            tableData[i][1] = String.format("%,.0f", data[i][0]);
            tableData[i][2] = String.format("%,.0f", data[i][1]);
            tableData[i][3] = String.format("%,.0f", data[i][2]);
            tableData[i][4] = String.format("%,.0f", data[i][0] + data[i][1] + data[i][2]);
        }
        
        JTable table = new JTable(tableData, columns);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        table.setEnabled(false);
        
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createTotalPanel(String title, int[] elec, int[] water, int[] mgmt) {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new CompoundBorder(
            new LineBorder(new Color(220, 220, 220)),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(25, 103, 210));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        panel.add(titleLabel);
        panel.add(new JLabel("")); // Empty cell
        
        int totalPaid = elec[0] + water[0] + mgmt[0];
        int totalUnpaid = elec[1] + water[1] + mgmt[1];
        int totalOverdue = elec[2] + water[2] + mgmt[2];
        int total = totalPaid + totalUnpaid + totalOverdue;
        
        addStatRow(panel, "Đã Thanh Toán:", totalPaid, new Color(76, 175, 80));
        addStatRow(panel, "Chưa Thanh Toán:", totalUnpaid, new Color(255, 152, 0));
        addStatRow(panel, "Quá Hạn:", totalOverdue, new Color(244, 67, 54));
        addStatRow(panel, "Tổng Cộng:", total, new Color(25, 103, 210));
        
        return panel;
    }
    
    private void addStatRow(JPanel panel, String label, int value, Color color) {
        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JLabel lblValue = new JLabel(String.valueOf(value) + " hóa đơn");
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblValue.setForeground(color);
        
        panel.add(lblLabel);
        panel.add(lblValue);
    }
    
    private JPanel createMonthlyPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new CompoundBorder(
            new LineBorder(new Color(220, 220, 220)),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(25, 103, 210));
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        textArea.setEditable(false);
        textArea.setText(
            "Xu hướng thanh toán:\n\n" +
            "• Tỷ lệ thanh toán đúng hạn: Đang cải thiện\n" +
            "• Số hóa đơn quá hạn: Cần theo dõi\n" +
            "• Tháng có nhiều hóa đơn nhất: Tháng hiện tại\n\n" +
            "Khuyến nghị:\n" +
            "- Gửi nhắc nhở thanh toán sớm hơn\n" +
            "- Theo dõi chặt chẽ các hóa đơn quá hạn\n" +
            "- Tăng cường liên lạc với cư dân nợ phí"
        );
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        
        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        
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
        
        JLabel unitLbl = new JLabel("hóa đơn");
        unitLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        unitLbl.setForeground(new Color(117, 117, 117));
        
        card.add(titleLbl);
        card.add(Box.createVerticalStrut(5));
        card.add(countLbl);
        card.add(Box.createVerticalStrut(3));
        card.add(unitLbl);
        
        return card;
    }
    
    private int[] getInvoiceStats(String tableName) {
        int[] stats = new int[3];
        
        try (Connection conn = config.DatabaseConnection.getConnection()) {
            String query = String.format(
                "SELECT trang_thai, COUNT(*) as count FROM %s GROUP BY trang_thai",
                tableName
            );
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    String status = rs.getString("trang_thai");
                    int count = rs.getInt("count");
                    
                    if ("da_thanh_toan".equals(status)) stats[0] = count;
                    else if ("chua_thanh_toan".equals(status)) stats[1] = count;
                    else if ("qua_han".equals(status)) stats[2] = count;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return stats;
    }
    
    private double[] getRevenueStats(String tableName) {
        double[] stats = new double[3];
        
        try (Connection conn = config.DatabaseConnection.getConnection()) {
            String amountField = "hoa_don_quan_ly_chung".equals(tableName) ? "so_tien" : "tong_tien";
            String query = String.format(
                "SELECT trang_thai, SUM(%s) as total FROM %s GROUP BY trang_thai",
                amountField, tableName
            );
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    String status = rs.getString("trang_thai");
                    double total = rs.getDouble("total");
                    
                    if ("da_thanh_toan".equals(status)) stats[0] = total;
                    else if ("chua_thanh_toan".equals(status)) stats[1] = total;
                    else if ("qua_han".equals(status)) stats[2] = total;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return stats;
    }
    
    private Object[][] getInvoiceTableData(String tableName) {
        List<Object[]> dataList = new ArrayList<>();
        
        try (Connection conn = config.DatabaseConnection.getConnection()) {
            String query = "";
            
            if ("hoa_don_dien".equals(tableName)) {
                query = "SELECT id_hoa_don, id_can_ho, thang, nam, tong_tien, trang_thai, ngay_thanh_toan " +
                       "FROM hoa_don_dien ORDER BY nam DESC, thang DESC";
            } else if ("hoa_don_nuoc".equals(tableName)) {
                query = "SELECT id_hoa_don, id_can_ho, thang, nam, tong_tien, trang_thai, ngay_thanh_toan " +
                       "FROM hoa_don_nuoc ORDER BY nam DESC, thang DESC";
            } else if ("hoa_don_quan_ly_chung".equals(tableName)) {
                query = "SELECT id_hoa_don, id_can_ho, thang, nam, so_tien, trang_thai, ngay_thanh_toan " +
                       "FROM hoa_don_quan_ly_chung ORDER BY nam DESC, thang DESC";
            }
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    dataList.add(new Object[]{
                        rs.getInt("id_hoa_don"),
                        rs.getInt("id_can_ho"),
                        rs.getInt("thang") + "/" + rs.getInt("nam"),
                        String.format("%,.0f", 
                            "hoa_don_quan_ly_chung".equals(tableName) ? 
                            rs.getDouble("so_tien") : rs.getDouble("tong_tien")),
                        getStatusDisplay(rs.getString("trang_thai")),
                        rs.getTimestamp("ngay_thanh_toan") != null ? 
                        new java.text.SimpleDateFormat("dd/MM/yyyy").format(rs.getTimestamp("ngay_thanh_toan")) : 
                        "Chưa thanh toán"
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return dataList.toArray(new Object[0][0]);
    }
    
    private String getStatusDisplay(String status) {
        switch (status) {
            case "da_thanh_toan":
                return "Đã Thanh Toán";
            case "chua_thanh_toan":
                return "Chưa Thanh Toán";
            case "qua_han":
                return "Quá Hạn";
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
            if (status.contains("Đã Thanh Toán")) {
                label.setBackground(new Color(200, 230, 201));
                label.setForeground(new Color(27, 94, 32));
            } else if (status.contains("Chưa Thanh Toán")) {
                label.setBackground(new Color(255, 235, 205));
                label.setForeground(new Color(230, 126, 34));
            } else if (status.contains("Quá Hạn")) {
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