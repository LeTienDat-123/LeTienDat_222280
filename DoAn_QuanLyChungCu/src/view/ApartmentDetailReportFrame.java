package view;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import java.util.*;
import dao.*;
import model.*;

public class ApartmentDetailReportFrame extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> cboBuilding;
    private JComboBox<String> cboStatus;
    
    public ApartmentDetailReportFrame() {
        setTitle("Báo Cáo Chi Tiết Căn Hộ");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(240, 242, 245));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Header
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Filter panel
        mainPanel.add(createFilterPanel(), BorderLayout.SOUTH);
        
        // Content
        mainPanel.add(createContentPanel(), BorderLayout.CENTER);
        
        setContentPane(mainPanel);
        setVisible(true);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(25, 103, 210));
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("BÁO CÁO CHI TIẾT CĂN HỘ");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel descLabel = new JLabel("Thông tin chi tiết về các căn hộ trong hệ thống");
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
    
    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new LineBorder(new Color(220, 220, 220)));
        
        JLabel filterLabel = new JLabel("Bộ lọc:");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        
        cboBuilding = new JComboBox<>();
        cboBuilding.addItem("Tất cả tòa nhà");
        for (Building b : BuildingDAO.getAllBuildings()) {
            cboBuilding.addItem(b.getName());
        }
        cboBuilding.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cboBuilding.setPreferredSize(new Dimension(200, 30));
        cboBuilding.addActionListener(e -> filterData());
        
        cboStatus = new JComboBox<>(new String[]{"Tất cả", "Trống", "Có người ở", "Bảo trì"});
        cboStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cboStatus.setPreferredSize(new Dimension(150, 30));
        cboStatus.addActionListener(e -> filterData());
        
        JButton btnReset = new JButton("Làm mới");
        btnReset.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnReset.setBackground(new Color(76, 175, 80));
        btnReset.setForeground(Color.WHITE);
        btnReset.setFocusPainted(false);
        btnReset.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReset.setPreferredSize(new Dimension(100, 30));
        btnReset.addActionListener(e -> {
            cboBuilding.setSelectedIndex(0);
            cboStatus.setSelectedIndex(0);
            filterData();
        });
        
        panel.add(filterLabel);
        panel.add(cboBuilding);
        panel.add(new JLabel("Trạng thái:"));
        panel.add(cboStatus);
        panel.add(btnReset);
        
        return panel;
    }
    
    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 242, 245));
        
        // Statistics
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        statsPanel.setBackground(new Color(240, 242, 245));
        statsPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        Map<String, Integer> stats = BuildingDAO.getAllApartmentStats();
        statsPanel.add(createStatBox("Tổng Căn Hộ", String.valueOf(stats.get("total")), new Color(25, 103, 210)));
        statsPanel.add(createStatBox("Có Người Ở", String.valueOf(stats.get("occupied")), new Color(76, 175, 80)));
        statsPanel.add(createStatBox("Trống", String.valueOf(stats.get("available")), new Color(255, 152, 0)));
        statsPanel.add(createStatBox("Bảo Trì", String.valueOf(stats.get("maintenance")), new Color(244, 67, 54)));
        
        panel.add(statsPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Tòa Nhà", "Căn Hộ", "Tầng", "Diện Tích (m²)", "Phòng Ngủ/Tắm", "Trạng Thái", "Ghi Chú"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        table.setRowHeight(28);
        table.getTableHeader().setBackground(new Color(25, 103, 210));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        table.getTableHeader().setPreferredSize(new Dimension(0, 35));
        
        // Custom cell renderer for status
        table.getColumnModel().getColumn(5).setCellRenderer(new StatusCellRenderer());
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(new Color(220, 220, 220)));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Load initial data
        loadTableData(null, null);
        
        return panel;
    }
    
    private void filterData() {
        String selectedBuilding = (String) cboBuilding.getSelectedItem();
        String selectedStatus = (String) cboStatus.getSelectedItem();
        
        String buildingFilter = "Tất cả tòa nhà".equals(selectedBuilding) ? null : selectedBuilding;
        String statusFilter = "Tất cả".equals(selectedStatus) ? null : selectedStatus;
        
        loadTableData(buildingFilter, statusFilter);
    }
    
    private void loadTableData(String buildingFilter, String statusFilter) {
        tableModel.setRowCount(0);
        
        List<Building> buildings = BuildingDAO.getAllBuildings();
        
        for (Building b : buildings) {
            // Filter by building
            if (buildingFilter != null && !b.getName().equals(buildingFilter)) {
                continue;
            }
            
            List<Apartment> apartments = BuildingDAO.getApartmentsByBuilding(b.getId());
            for (Apartment apt : apartments) {
                // Filter by status
                if (statusFilter != null) {
                    String aptStatus = apt.getStatusDisplay();
                    if (!aptStatus.equals(statusFilter)) {
                        continue;
                    }
                }
                
                tableModel.addRow(new Object[]{
                    b.getName(),
                    apt.getNumber(),
                    apt.getFloor(),
                    String.format("%.1f", apt.getArea()),
                    apt.getBedrooms() + "/" + apt.getBathrooms(),
                    apt.getStatusDisplay(),
                    getApartmentNotes(apt.getId())
                });
            }
        }
    }
    
    private JPanel createStatBox(String title, String value, Color color) {
        JPanel box = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                
                g2d.setColor(color);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
            }
        };
        
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(Color.WHITE);
        box.setBorder(new EmptyBorder(12, 12, 12, 12));
        
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        titleLbl.setForeground(new Color(100, 100, 100));
        
        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valueLbl.setForeground(color);
        
        box.add(titleLbl);
        box.add(Box.createVerticalStrut(5));
        box.add(valueLbl);
        
        return box;
    }
    
    private String getApartmentNotes(int apartmentId) {
        RentalContract contract = RentalContractDAO.getActiveContractByApartment(apartmentId);
        if (contract != null) {
            User resident = UserDAO.getUserById(contract.getResidentId());
            return resident != null ? "Cư dân: " + resident.getFullName() : "";
        }
        return "";
    }
    
    // Custom cell renderer for status
    static class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            String status = (String) value;
            if ("Có người ở".equals(status)) {
                label.setBackground(new Color(200, 230, 201));
                label.setForeground(new Color(27, 94, 32));
            } else if ("Trống".equals(status)) {
                label.setBackground(new Color(255, 235, 205));
                label.setForeground(new Color(230, 126, 34));
            } else if ("Bảo trì".equals(status)) {
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