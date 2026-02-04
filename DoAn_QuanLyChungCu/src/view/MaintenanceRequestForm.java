package view;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import config.DatabaseConnection;

public class MaintenanceRequestForm extends JFrame {
    private JTable requestTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> cboStatusFilter, cboPriorityFilter;
    
    public MaintenanceRequestForm() {
        setTitle("Quản lý Yêu cầu Bảo trì");
        setSize(1400, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 242, 245));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createTablePanel(), BorderLayout.CENTER);
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        loadRequestData();
        setVisible(true);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(240, 242, 245));
        
        JLabel titleLabel = new JLabel("QUẢN LÝ YÊU CẦU BẢO TRÌ");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(25, 103, 210));
        
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterPanel.setBackground(new Color(240, 242, 245));
        
        cboStatusFilter = new JComboBox<>(new String[]{"Tất cả", "Chờ xử lý", "Đang tiến hành", "Hoàn thành", "Hủy"});
        cboStatusFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cboStatusFilter.addActionListener(e -> filterData());
        
        cboPriorityFilter = new JComboBox<>(new String[]{"Tất cả mức độ", "Thấp", "Trung bình", "Cao"});
        cboPriorityFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cboPriorityFilter.addActionListener(e -> filterData());
        
        filterPanel.add(new JLabel("Trạng thái:"));
        filterPanel.add(cboStatusFilter);
        filterPanel.add(new JLabel("Mức độ:"));
        filterPanel.add(cboPriorityFilter);
        
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(filterPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        
        String[] columns = {"ID", "Căn hộ", "Cư dân", "Danh mục", "Tiêu đề", "Mức độ", "Trạng thái", "Chi phí", "Ngày tạo"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        requestTable = new JTable(tableModel);
        requestTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        requestTable.setRowHeight(30);
        requestTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        requestTable.getTableHeader().setBackground(new Color(25, 103, 210));
        requestTable.getTableHeader().setForeground(Color.WHITE);
        
        // Double click to view details
        requestTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    viewDetails();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(requestTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(new Color(240, 242, 245));
        
        JButton btnAdd = createButton("Tạo yêu cầu", new Color(76, 175, 80));
        JButton btnProcess = createButton("Xử lý", new Color(255, 152, 0));
        JButton btnComplete = createButton("Hoàn thành", new Color(33, 150, 243));
        JButton btnCancel = createButton("Hủy", new Color(244, 67, 54));
        JButton btnRefresh = createButton("Làm mới", new Color(103, 58, 183));
        
        btnAdd.addActionListener(e -> addRequest());
        btnProcess.addActionListener(e -> processRequest());
        btnComplete.addActionListener(e -> completeRequest());
        btnCancel.addActionListener(e -> cancelRequest());
        btnRefresh.addActionListener(e -> loadRequestData());
        
        panel.add(btnAdd);
        panel.add(btnProcess);
        panel.add(btnComplete);
        panel.add(btnCancel);
        panel.add(btnRefresh);
        
        return panel;
    }
    
    private JButton createButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 35));
        return btn;
    }
    
    private void loadRequestData() {
        tableModel.setRowCount(0);
        String query = "SELECT y.id_yeu_cau, c.so_can_ho, u.ho_ten, y.danh_muc, y.tieu_de, " +
                      "y.muc_do_uu_tien, y.trang_thai, y.chi_phi, y.ngay_yeu_cau " +
                      "FROM yeu_cau_bao_tri y " +
                      "JOIN can_ho c ON y.id_can_ho = c.id_can_ho " +
                      "JOIN nguoi_dung u ON y.id_cu_dan = u.id_nguoi_dung " +
                      "ORDER BY y.id_yeu_cau DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id_yeu_cau"),
                    rs.getString("so_can_ho"),
                    rs.getString("ho_ten"),
                    rs.getString("danh_muc"),
                    rs.getString("tieu_de"),
                    getPriorityDisplay(rs.getString("muc_do_uu_tien")),
                    getStatusDisplay(rs.getString("trang_thai")),
                    rs.getDouble("chi_phi") > 0 ? String.format("%,.0f đ", rs.getDouble("chi_phi")) : "Chưa có",
                    rs.getTimestamp("ngay_yeu_cau")
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
        }
    }
    
    private void filterData() {
        String selectedStatus = (String) cboStatusFilter.getSelectedItem();
        String selectedPriority = (String) cboPriorityFilter.getSelectedItem();
        
        if ("Tất cả".equals(selectedStatus) && "Tất cả mức độ".equals(selectedPriority)) {
            loadRequestData();
            return;
        }
        
        tableModel.setRowCount(0);
        StringBuilder queryBuilder = new StringBuilder("SELECT y.id_yeu_cau, c.so_can_ho, u.ho_ten, y.danh_muc, y.tieu_de, " +
                      "y.muc_do_uu_tien, y.trang_thai, y.chi_phi, y.ngay_yeu_cau " +
                      "FROM yeu_cau_bao_tri y " +
                      "JOIN can_ho c ON y.id_can_ho = c.id_can_ho " +
                      "JOIN nguoi_dung u ON y.id_cu_dan = u.id_nguoi_dung WHERE 1=1");
        
        if (!"Tất cả".equals(selectedStatus)) {
            queryBuilder.append(" AND y.trang_thai = ?");
        }
        if (!"Tất cả mức độ".equals(selectedPriority)) {
            queryBuilder.append(" AND y.muc_do_uu_tien = ?");
        }
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(queryBuilder.toString())) {
            
            int paramIndex = 1;
            if (!"Tất cả".equals(selectedStatus)) {
                pstmt.setString(paramIndex++, getStatusValue(selectedStatus));
            }
            if (!"Tất cả mức độ".equals(selectedPriority)) {
                pstmt.setString(paramIndex++, getPriorityValue(selectedPriority));
            }
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id_yeu_cau"),
                    rs.getString("so_can_ho"),
                    rs.getString("ho_ten"),
                    rs.getString("danh_muc"),
                    rs.getString("tieu_de"),
                    getPriorityDisplay(rs.getString("muc_do_uu_tien")),
                    getStatusDisplay(rs.getString("trang_thai")),
                    rs.getDouble("chi_phi") > 0 ? String.format("%,.0f đ", rs.getDouble("chi_phi")) : "Chưa có",
                    rs.getTimestamp("ngay_yeu_cau")
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi lọc dữ liệu: " + e.getMessage());
        }
    }
    
    private void addRequest() {
        MaintenanceRequestDialog dialog = new MaintenanceRequestDialog(this, "Tạo yêu cầu bảo trì", null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            loadRequestData();
        }
    }
    
    private void processRequest() {
        int selectedRow = requestTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn yêu cầu!");
            return;
        }
        
        int requestId = (int) tableModel.getValueAt(selectedRow, 0);
        String query = "UPDATE yeu_cau_bao_tri SET trang_thai = 'dang_tien_hanh' WHERE id_yeu_cau = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, requestId);
            pstmt.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "Đã chuyển sang trạng thái đang xử lý!");
            loadRequestData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        }
    }
    
    private void completeRequest() {
        int selectedRow = requestTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn yêu cầu!");
            return;
        }
        
        String costStr = JOptionPane.showInputDialog(this, "Nhập chi phí bảo trì:");
        if (costStr != null && !costStr.trim().isEmpty()) {
            try {
                double cost = Double.parseDouble(costStr);
                int requestId = (int) tableModel.getValueAt(selectedRow, 0);
                
                String query = "UPDATE yeu_cau_bao_tri SET trang_thai = 'hoan_thanh', chi_phi = ?, ngay_hoan_thanh = NOW() WHERE id_yeu_cau = ?";
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(query)) {
                    
                    pstmt.setDouble(1, cost);
                    pstmt.setInt(2, requestId);
                    pstmt.executeUpdate();
                    
                    JOptionPane.showMessageDialog(this, "Hoàn thành yêu cầu!");
                    loadRequestData();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
            }
        }
    }
    
    private void cancelRequest() {
        int selectedRow = requestTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn yêu cầu!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn hủy yêu cầu này?", 
            "Xác nhận", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int requestId = (int) tableModel.getValueAt(selectedRow, 0);
            String query = "UPDATE yeu_cau_bao_tri SET trang_thai = 'huy' WHERE id_yeu_cau = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                
                pstmt.setInt(1, requestId);
                pstmt.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Đã hủy yêu cầu!");
                loadRequestData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
            }
        }
    }
    
    private void viewDetails() {
        int selectedRow = requestTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        int requestId = (int) tableModel.getValueAt(selectedRow, 0);
        
        String query = "SELECT * FROM yeu_cau_bao_tri WHERE id_yeu_cau = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, requestId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String details = "Thông tin chi tiết yêu cầu #" + requestId + "\n\n" +
                    "Danh mục: " + rs.getString("danh_muc") + "\n" +
                    "Tiêu đề: " + rs.getString("tieu_de") + "\n" +
                    "Mô tả: " + rs.getString("mo_ta") + "\n" +
                    "Mức độ: " + getPriorityDisplay(rs.getString("muc_do_uu_tien")) + "\n" +
                    "Trạng thái: " + getStatusDisplay(rs.getString("trang_thai")) + "\n" +
                    "Chi phí: " + (rs.getDouble("chi_phi") > 0 ? String.format("%,.0f đ", rs.getDouble("chi_phi")) : "Chưa có") + "\n" +
                    "Ghi chú: " + (rs.getString("ghi_chu") != null ? rs.getString("ghi_chu") : "");
                
                JOptionPane.showMessageDialog(this, details, "Chi tiết", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private String getStatusDisplay(String status) {
        switch (status) {
            case "cho_xu_ly": return "Chờ xử lý";
            case "dang_tien_hanh": return "Đang tiến hành";
            case "hoan_thanh": return "Hoàn thành";
            case "huy": return "Hủy";
            default: return status;
        }
    }
    
    private String getStatusValue(String display) {
        switch (display) {
            case "Chờ xử lý": return "cho_xu_ly";
            case "Đang tiến hành": return "dang_tien_hanh";
            case "Hoàn thành": return "hoan_thanh";
            case "Hủy": return "huy";
            default: return "";
        }
    }
    
    private String getPriorityDisplay(String priority) {
        switch (priority) {
            case "thap": return "Thấp";
            case "trung_binh": return "Trung bình";
            case "cao": return "Cao";
            default: return priority;
        }
    }
    
    private String getPriorityValue(String display) {
        switch (display) {
            case "Thấp": return "thap";
            case "Trung bình": return "trung_binh";
            case "Cao": return "cao";
            default: return "";
        }
    }
}

// Dialog for Add Maintenance Request
class MaintenanceRequestDialog extends JDialog {
    private JComboBox<String> cboApartment, cboResident, cboCategory, cboPriority;
    private JTextField txtTitle;
    private JTextArea txtDescription;
    private boolean confirmed = false;
    
    public MaintenanceRequestDialog(JFrame parent, String title, Integer requestId) {
        super(parent, title, true);
        setSize(600, 500);
        setLocationRelativeTo(parent);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Apartment
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Căn hộ:"), gbc);
        gbc.gridx = 1;
        cboApartment = new JComboBox<>();
        loadApartments();
        panel.add(cboApartment, gbc);
        
        // Resident
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Cư dân:"), gbc);
        gbc.gridx = 1;
        cboResident = new JComboBox<>();
        loadResidents();
        panel.add(cboResident, gbc);
        
        // Category
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Danh mục:"), gbc);
        gbc.gridx = 1;
        cboCategory = new JComboBox<>(new String[]{"Điện", "Nước", "Thang máy", "Cửa", "Khóa", "Điều hòa", "Khác"});
        panel.add(cboCategory, gbc);
        
        // Title
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Tiêu đề:"), gbc);
        gbc.gridx = 1;
        txtTitle = new JTextField(20);
        panel.add(txtTitle, gbc);
        
        // Priority
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Mức độ:"), gbc);
        gbc.gridx = 1;
        cboPriority = new JComboBox<>(new String[]{"Thấp", "Trung bình", "Cao"});
        cboPriority.setSelectedIndex(1);
        panel.add(cboPriority, gbc);
        
        // Description
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.NORTH;
        panel.add(new JLabel("Mô tả:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        txtDescription = new JTextArea(5, 20);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(txtDescription);
        panel.add(scrollPane, gbc);
        
        // Buttons
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnSave = new JButton("Lưu");
        JButton btnCancel = new JButton("Hủy");
        
        btnSave.addActionListener(e -> save());
        btnCancel.addActionListener(e -> dispose());
        
        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        panel.add(btnPanel, gbc);
        
        add(panel);
    }
    
    private void loadApartments() {
        String query = "SELECT id_can_ho, so_can_ho FROM can_ho";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                cboApartment.addItem(rs.getInt("id_can_ho") + " - " + rs.getString("so_can_ho"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadResidents() {
        String query = "SELECT id_nguoi_dung, ho_ten FROM nguoi_dung WHERE vai_tro = 'cu_dan' AND trang_thai_hoat_dong = TRUE";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                cboResident.addItem(rs.getInt("id_nguoi_dung") + " - " + rs.getString("ho_ten"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void save() {
        if (txtTitle.getText().trim().isEmpty() || txtDescription.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }
        
        try {
            String apartmentStr = (String) cboApartment.getSelectedItem();
            String residentStr = (String) cboResident.getSelectedItem();
            
            int apartmentId = Integer.parseInt(apartmentStr.split(" - ")[0]);
            int residentId = Integer.parseInt(residentStr.split(" - ")[0]);
            
            String priorityValue = "";
            switch (cboPriority.getSelectedIndex()) {
                case 0: priorityValue = "thap"; break;
                case 1: priorityValue = "trung_binh"; break;
                case 2: priorityValue = "cao"; break;
            }
            
            String query = "INSERT INTO yeu_cau_bao_tri (id_can_ho, id_cu_dan, danh_muc, tieu_de, mo_ta, muc_do_uu_tien, trang_thai) VALUES (?, ?, ?, ?, ?, ?, 'cho_xu_ly')";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                
                pstmt.setInt(1, apartmentId);
                pstmt.setInt(2, residentId);
                pstmt.setString(3, (String) cboCategory.getSelectedItem());
                pstmt.setString(4, txtTitle.getText().trim());
                pstmt.setString(5, txtDescription.getText().trim());
                pstmt.setString(6, priorityValue);
                pstmt.executeUpdate();
                
                confirmed = true;
                JOptionPane.showMessageDialog(this, "Tạo yêu cầu thành công!");
                dispose();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        }
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
}