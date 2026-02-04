package view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;
import java.util.*;
import dao.BuildingDAO;
import model.Building;
import java.sql.*;

import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;

public class StatisticsFrame extends JFrame {
    
    public StatisticsFrame() {
        setTitle("Thống Kê & Báo Cáo Hệ Thống");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(240, 242, 245));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Main content with tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabbedPane.setBackground(Color.WHITE);
        
        tabbedPane.addTab("Căn Hộ", createApartmentTab());
        tabbedPane.addTab("Hóa Đơn", createInvoiceTab());
        tabbedPane.addTab("Hợp Đồng", createContractTab());
        tabbedPane.addTab("Doanh Thu", createRevenueTab());
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        setContentPane(mainPanel);
        setVisible(true);
    }
    
    // ==================== HEADER ====================
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBackground(new Color(25, 103, 210));
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));
        panel.setPreferredSize(new Dimension(0, 80));
        
        JLabel titleLabel = new JLabel("THỐNG KÊ & BÁO CÁO HỆ THỐNG");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel dateLabel = new JLabel("Cập nhật: " + new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date()));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLabel.setForeground(new Color(200, 220, 255));
        
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(new Color(25, 103, 210));
        leftPanel.add(titleLabel);
        leftPanel.add(dateLabel);
        
        panel.add(leftPanel, BorderLayout.WEST);
        
        // Refresh button
        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnRefresh.setBackground(new Color(76, 175, 80));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.setPreferredSize(new Dimension(120, 40));
        btnRefresh.addActionListener(e -> {
            dispose();
            new StatisticsFrame();
        });
        
        panel.add(btnRefresh, BorderLayout.EAST);
        
        return panel;
    }
    
    // ==================== TABS ====================
    
    private JPanel createApartmentTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(240, 242, 245));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Stats cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        statsPanel.setBackground(new Color(240, 242, 245));
        
        Map<String, Integer> stats = BuildingDAO.getAllApartmentStats();
        int total = stats.get("total");
        
        statsPanel.add(createProgressCard("Có Người Ở", 
            stats.get("occupied"), total, new Color(25, 103, 210)));
        statsPanel.add(createProgressCard("Trống", 
            stats.get("available"), total, new Color(76, 175, 80)));
        statsPanel.add(createProgressCard("Bảo Trì", 
            stats.get("maintenance"), total, new Color(255, 152, 0)));
        statsPanel.add(createProgressCard("Tỷ Lệ Lấp Đầy", 
            stats.get("occupied"), total, new Color(156, 39, 176)));
        
        panel.add(statsPanel, BorderLayout.NORTH);
        
        // Pie chart
        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setBorder(new LineBorder(new Color(220, 220, 220)));
        
        try {
            XChartPanel<PieChart> pieChartPanel = new XChartPanel<>(createApartmentPieChart(stats));
            chartPanel.add(pieChartPanel, BorderLayout.CENTER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        panel.add(chartPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createInvoiceTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(240, 242, 245));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Summary Cards
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        summaryPanel.setBackground(new Color(240, 242, 245));
        
        int[] invoiceStats = getInvoiceStats();
        summaryPanel.add(createInvoiceCard("Đã Thanh Toán", invoiceStats[0], new Color(76, 175, 80)));
        summaryPanel.add(createInvoiceCard("Chưa Thanh Toán", invoiceStats[1], new Color(255, 152, 0)));
        summaryPanel.add(createInvoiceCard("Quá Hạn", invoiceStats[2], new Color(244, 67, 54)));
        
        panel.add(summaryPanel, BorderLayout.NORTH);
        
        // Bar chart
        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setBorder(new LineBorder(new Color(220, 220, 220)));
        
        try {
            XChartPanel<CategoryChart> barChartPanel = new XChartPanel<>(createInvoiceBarChart());
            chartPanel.add(barChartPanel, BorderLayout.CENTER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        panel.add(chartPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createContractTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(240, 242, 245));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Summary
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        summaryPanel.setBackground(new Color(240, 242, 245));
        
        int[] contractStats = getContractStats();
        summaryPanel.add(createContractCard("Đang Hoạt Động", contractStats[0], new Color(76, 175, 80)));
        summaryPanel.add(createContractCard("Hết Hạn", contractStats[1], new Color(255, 152, 0)));
        summaryPanel.add(createContractCard("Đã Hủy", contractStats[2], new Color(244, 67, 54)));
        
        panel.add(summaryPanel, BorderLayout.NORTH);
        
        // Pie chart
        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setBorder(new LineBorder(new Color(220, 220, 220)));
        
        try {
            XChartPanel<PieChart> pieChartPanel = new XChartPanel<>(createContractPieChart(contractStats));
            chartPanel.add(pieChartPanel, BorderLayout.CENTER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        panel.add(chartPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createRevenueTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(240, 242, 245));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Total revenue
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(new Color(240, 242, 245));
        
        Map<Integer, Double> revenue = BuildingDAO.getRevenueByBuilding();
        double totalRevenue = revenue.values().stream().mapToDouble(Double::doubleValue).sum();
        
        JLabel totalLabel = new JLabel(String.format("Tổng Doanh Thu: %,.0f VNĐ", totalRevenue));
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalLabel.setForeground(new Color(25, 103, 210));
        
        topPanel.add(totalLabel);
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Bar chart
        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setBorder(new LineBorder(new Color(220, 220, 220)));
        
        try {
            XChartPanel<CategoryChart> barChartPanel = new XChartPanel<>(createRevenueBarChart());
            chartPanel.add(barChartPanel, BorderLayout.CENTER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        panel.add(chartPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    // ==================== XCHART METHODS ====================
    
    /**
     * Tạo biểu đồ Pie - Trạng thái căn hộ
     */
    private PieChart createApartmentPieChart(Map<String, Integer> stats) {
        PieChart chart = new PieChartBuilder()
            .width(600)
            .height(400)
            .title("Trạng Thái Căn Hộ")
            .build();
        
        // Styling
        chart.getStyler().setChartTitleVisible(true);
        chart.getStyler().setLegendPosition(Styler.LegendPosition.OutsideS);
        chart.getStyler().setPlotContentSize(0.7);
        chart.getStyler().setStartAngleInDegrees(90);
        chart.getStyler().setDefaultSeriesRenderStyle(PieSeries.PieSeriesRenderStyle.Donut);
        
        // Colors
        Color[] colors = {
            new Color(25, 103, 210),
            new Color(76, 175, 80),
            new Color(255, 152, 0)
        };
        chart.getStyler().setSeriesColors(colors);
        
        // Add data
        chart.addSeries("Có Người Ở", stats.get("occupied"));
        chart.addSeries("Trống", stats.get("available"));
        chart.addSeries("Bảo Trì", stats.get("maintenance"));
        
        return chart;
    }
    
    /**
     * Tạo biểu đồ Bar - Hóa đơn
     */
    private CategoryChart createInvoiceBarChart() {
        int[] elecStats = new int[3];
        int[] waterStats = new int[3];
        int[] mgmtStats = new int[3];
        
        getInvoiceDetailStats(elecStats, waterStats, mgmtStats);
        
        CategoryChart chart = new CategoryChartBuilder()
            .width(700)
            .height(400)
            .title("Trạng Thái Thanh Toán Hóa Đơn")
            .xAxisTitle("Loại Hóa Đơn")
            .yAxisTitle("Số Lượng")
            .build();
        
        // Styling
        chart.getStyler().setChartTitleVisible(true);
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        chart.getStyler().setPlotGridVerticalLinesVisible(false);
        chart.getStyler().setXAxisLabelRotation(0);
        
        // Colors
        Color[] colors = {
            new Color(76, 175, 80),
            new Color(255, 152, 0),
            new Color(244, 67, 54)
        };
        chart.getStyler().setSeriesColors(colors);
        
        // Data
        List<String> categories = Arrays.asList("Tiền Điện", "Tiền Nước", "Phí Quản Lý");
        
        List<Integer> paid = Arrays.asList(elecStats[0], waterStats[0], mgmtStats[0]);
        List<Integer> unpaid = Arrays.asList(elecStats[1], waterStats[1], mgmtStats[1]);
        List<Integer> overdue = Arrays.asList(elecStats[2], waterStats[2], mgmtStats[2]);
        
        chart.addSeries("Đã Thanh Toán", categories, paid);
        chart.addSeries("Chưa Thanh Toán", categories, unpaid);
        chart.addSeries("Quá Hạn", categories, overdue);
        
        return chart;
    }
    
    /**
     * Tạo biểu đồ Pie - Hợp đồng
     */
    private PieChart createContractPieChart(int[] stats) {
        PieChart chart = new PieChartBuilder()
            .width(600)
            .height(400)
            .title("Trạng Thái Hợp Đồng")
            .build();
        
        // Styling
        chart.getStyler().setChartTitleVisible(true);
        chart.getStyler().setLegendPosition(Styler.LegendPosition.OutsideS);
        chart.getStyler().setPlotContentSize(0.7);
        chart.getStyler().setStartAngleInDegrees(90);
        chart.getStyler().setDefaultSeriesRenderStyle(PieSeries.PieSeriesRenderStyle.Donut);
        
        // Colors
        Color[] colors = {
            new Color(76, 175, 80),
            new Color(255, 152, 0),
            new Color(244, 67, 54)
        };
        chart.getStyler().setSeriesColors(colors);
        
        // Add data
        chart.addSeries("Đang Hoạt Động", stats[0]);
        chart.addSeries("Hết Hạn", stats[1]);
        chart.addSeries("Đã Hủy", stats[2]);
        
        return chart;
    }
    
    /**
     * Tạo biểu đồ Bar - Doanh thu theo tòa nhà
     */
    private CategoryChart createRevenueBarChart() {
        Map<Integer, Double> revenue = BuildingDAO.getRevenueByBuilding();
        List<Building> buildings = BuildingDAO.getAllBuildings();
        
        CategoryChart chart = new CategoryChartBuilder()
            .width(700)
            .height(400)
            .title("Doanh Thu Theo Tòa Nhà")
            .xAxisTitle("Tòa Nhà")
            .yAxisTitle("Doanh Thu (Triệu VNĐ)")
            .build();
        
        // Styling
        chart.getStyler().setChartTitleVisible(true);
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        chart.getStyler().setPlotGridVerticalLinesVisible(false);
        chart.getStyler().setXAxisLabelRotation(45);
        
        // Colors
        Color[] colors = {new Color(25, 103, 210)};
        chart.getStyler().setSeriesColors(colors);
        
        // Prepare data
        List<String> buildingNames = new ArrayList<>();
        List<Double> revenues = new ArrayList<>();
        
        for (Building b : buildings) {
            buildingNames.add(b.getName());
            revenues.add(revenue.getOrDefault(b.getId(), 0.0) / 1_000_000); // Convert to millions
        }
        
        chart.addSeries("Doanh Thu", buildingNames, revenues);
        
        return chart;
    }
    
    // ==================== CARD COMPONENTS ====================
    
    private JPanel createProgressCard(String title, int current, int total, Color color) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                
                g2d.setColor(new Color(220, 220, 220));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            }
        };
        
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(12, 12, 12, 12));
        
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        titleLbl.setForeground(new Color(33, 33, 33));
        
        JLabel valueLbl = new JLabel(String.format("%d / %d", current, total));
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        valueLbl.setForeground(color);
        
        double percentage = total > 0 ? (current * 100.0 / total) : 0;
        JLabel percentLbl = new JLabel(String.format("%.1f%%", percentage));
        percentLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        percentLbl.setForeground(new Color(117, 117, 117));
        
        JProgressBar progressBar = new JProgressBar(0, total);
        progressBar.setValue(current);
        progressBar.setForeground(color);
        progressBar.setBackground(new Color(240, 240, 240));
        progressBar.setPreferredSize(new Dimension(0, 6));
        
        card.add(titleLbl);
        card.add(Box.createVerticalStrut(5));
        card.add(valueLbl);
        card.add(Box.createVerticalStrut(8));
        card.add(progressBar);
        card.add(Box.createVerticalStrut(3));
        card.add(percentLbl);
        
        return card;
    }
    
    private JPanel createInvoiceCard(String title, int count, Color color) {
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
        countLbl.setFont(new Font("Segoe UI", Font.BOLD, 32));
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
    
    private JPanel createContractCard(String title, int count, Color color) {
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
        countLbl.setFont(new Font("Segoe UI", Font.BOLD, 32));
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
    
    // ==================== DATA METHODS ====================
    
    private int[] getInvoiceStats() {
        int[] stats = new int[3];
        try (Connection conn = config.DatabaseConnection.getConnection()) {
            String query = "SELECT trang_thai, COUNT(*) as count FROM (" +
                "SELECT trang_thai FROM hoa_don_dien " +
                "UNION ALL SELECT trang_thai FROM hoa_don_nuoc " +
                "UNION ALL SELECT trang_thai FROM hoa_don_quan_ly_chung" +
                ") t GROUP BY trang_thai";
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    String status = rs.getString("trang_thai");
                    int count = rs.getInt("count");
                    if ("da_thanh_toan".equals(status)) stats[0] += count;
                    else if ("chua_thanh_toan".equals(status)) stats[1] += count;
                    else if ("qua_han".equals(status)) stats[2] += count;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stats;
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
    
    private void getInvoiceDetailStats(int[] elecStats, int[] waterStats, int[] mgmtStats) {
        try (Connection conn = config.DatabaseConnection.getConnection()) {
            String query = "SELECT trang_thai, COUNT(*) as count FROM hoa_don_dien GROUP BY trang_thai";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    String status = rs.getString("trang_thai");
                    int count = rs.getInt("count");
                    if ("da_thanh_toan".equals(status)) elecStats[0] = count;
                    else if ("chua_thanh_toan".equals(status)) elecStats[1] = count;
                    else if ("qua_han".equals(status)) elecStats[2] = count;
                }
            }
            
            query = "SELECT trang_thai, COUNT(*) as count FROM hoa_don_nuoc GROUP BY trang_thai";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    String status = rs.getString("trang_thai");
                    int count = rs.getInt("count");
                    if ("da_thanh_toan".equals(status)) waterStats[0] = count;
                    else if ("chua_thanh_toan".equals(status)) waterStats[1] = count;
                    else if ("qua_han".equals(status)) waterStats[2] = count;
                }
            }
            
            query = "SELECT trang_thai, COUNT(*) as count FROM hoa_don_quan_ly_chung GROUP BY trang_thai";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    String status = rs.getString("trang_thai");
                    int count = rs.getInt("count");
                    if ("da_thanh_toan".equals(status)) mgmtStats[0] = count;
                    else if ("chua_thanh_toan".equals(status)) mgmtStats[1] = count;
                    else if ("qua_han".equals(status)) mgmtStats[2] = count;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}