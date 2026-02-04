package view;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import config.DatabaseConnection;

public class ApartmentManagementForm extends JFrame {
    private JTable apartmentTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> cboStatusFilter, cboFloorFilter;
    private int buildingId;
    private String buildingName;
    
    public ApartmentManagementForm(int buildingId) {
        this.buildingId = buildingId;
        loadBuildingName();
        
        setTitle("Quản lý Căn hộ - " + buildingName);
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
        loadApartmentData();
        setVisible(true);
    }
    
    private void loadBuildingName() {
        String query = "SELECT ten_toa_nha FROM toa_nha WHERE id_toa_nha = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, buildingId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                buildingName = rs.getString("ten_toa_nha");
            }
        } catch (Exception e) {
            buildingName = "Tòa nhà #" + buildingId;
        }
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(240, 242, 245));
        
        JLabel titleLabel = new JLabel("QUẢN LÝ CĂN HỘ - " + buildingName);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(25, 103, 210));
        
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterPanel.setBackground(new Color(240, 242, 245));
        
        cboFloorFilter = new JComboBox<>();
        cboFloorFilter.addItem("Tất cả tầng");
        loadFloors();
        cboFloorFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cboFloorFilter.addActionListener(e -> filterData());
        
        cboStatusFilter = new JComboBox<>(new String[]{"Tất cả", "Trống", "Có người ở", "Bảo trì"});
        cboStatusFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cboStatusFilter.addActionListener(e -> filterData());
        
        filterPanel.add(new JLabel("Tầng:"));
        filterPanel.add(cboFloorFilter);
        filterPanel.add(new JLabel("Trạng thái:"));
        filterPanel.add(cboStatusFilter);
        
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(filterPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private void loadFloors() {
        String query = "SELECT DISTINCT so_tang FROM can_ho WHERE id_toa_nha = ? ORDER BY so_tang DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, buildingId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                cboFloorFilter.addItem("Tầng " + rs.getInt("so_tang"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        
        String[] columns = {"ID", "Số căn hộ", "Tầng", "Diện tích (m²)", "Phòng ngủ", "Phòng tắm", "Trạng thái", "Người ở"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        apartmentTable = new JTable(tableModel);
        apartmentTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        apartmentTable.setRowHeight(30);
        apartmentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        apartmentTable.getTableHeader().setBackground(new Color(25, 103, 210));
        apartmentTable.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(apartmentTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(new Color(240, 242, 245));
        
        JButton btnAdd = createButton("Thêm căn hộ", new Color(76, 175, 80));
        JButton btnEdit = createButton("Sửa thông tin", new Color(255, 152, 0));
        JButton btnDelete = createButton("Xóa", new Color(244, 67, 54));
        JButton btnChangeStatus = createButton("Đổi trạng thái", new Color(103, 58, 183));
        JButton btnRefresh = createButton("Làm mới", new Color(33, 150, 243));
        
        btnAdd.addActionListener(e -> addApartment());
        btnEdit.addActionListener(e -> editApartment());
        btnDelete.addActionListener(e -> deleteApartment());
        btnChangeStatus.addActionListener(e -> changeStatus());
        btnRefresh.addActionListener(e -> loadApartmentData());
        
        panel.add(btnAdd);
        panel.add(btnEdit);
        panel.add(btnDelete);
        panel.add(btnChangeStatus);
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
        btn.setPreferredSize(new Dimension(160, 35));
        return btn;
    }
    
    private void loadApartmentData() {
        tableModel.setRowCount(0);
        String query = "SELECT c.id_can_ho, c.so_can_ho, c.so_tang, c.dien_tich_m2, " +
                      "c.so_phong_ngu, c.so_phong_tam, c.trang_thai, u.ho_ten " +
                      "FROM can_ho c " +
                      "LEFT JOIN hop_dong_thue h ON c.id_can_ho = h.id_can_ho AND h.trang_thai = 'hoat_dong' " +
                      "LEFT JOIN nguoi_dung u ON h.id_cu_dan = u.id_nguoi_dung " +
                      "WHERE c.id_toa_nha = ? " +
                      "ORDER BY c.so_tang DESC, c.so_can_ho";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, buildingId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id_can_ho"),
                    rs.getString("so_can_ho"),
                    rs.getInt("so_tang"),
                    rs.getFloat("dien_tich_m2"),
                    rs.getInt("so_phong_ngu"),
                    rs.getInt("so_phong_tam"),
                    getStatusDisplay(rs.getString("trang_thai")),
                    rs.getString("ho_ten") != null ? rs.getString("ho_ten") : ""
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
        }
    }
    
    private void filterData() {
        String selectedFloor = (String) cboFloorFilter.getSelectedItem();
        String selectedStatus = (String) cboStatusFilter.getSelectedItem();
        
        if ("Tất cả tầng".equals(selectedFloor) && "Tất cả".equals(selectedStatus)) {
            loadApartmentData();
            return;
        }
        
        tableModel.setRowCount(0);
        StringBuilder queryBuilder = new StringBuilder(
            "SELECT c.id_can_ho, c.so_can_ho, c.so_tang, c.dien_tich_m2, " +
            "c.so_phong_ngu, c.so_phong_tam, c.trang_thai, u.ho_ten " +
            "FROM can_ho c " +
            "LEFT JOIN hop_dong_thue h ON c.id_can_ho = h.id_can_ho AND h.trang_thai = 'hoat_dong' " +
            "LEFT JOIN nguoi_dung u ON h.id_cu_dan = u.id_nguoi_dung " +
            "WHERE c.id_toa_nha = ?"
        );
        
        if (!"Tất cả tầng".equals(selectedFloor)) {
            int floor = Integer.parseInt(selectedFloor.replace("Tầng ", ""));
            queryBuilder.append(" AND c.so_tang = ").append(floor);
        }
        
        if (!"Tất cả".equals(selectedStatus)) {
            String statusValue = getStatusValue(selectedStatus);
            queryBuilder.append(" AND c.trang_thai = '").append(statusValue).append("'");
        }
        
        queryBuilder.append(" ORDER BY c.so_tang DESC, c.so_can_ho");
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(queryBuilder.toString())) {
            
            pstmt.setInt(1, buildingId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id_can_ho"),
                    rs.getString("so_can_ho"),
                    rs.getInt("so_tang"),
                    rs.getFloat("dien_tich_m2"),
                    rs.getInt("so_phong_ngu"),
                    rs.getInt("so_phong_tam"),
                    getStatusDisplay(rs.getString("trang_thai")),
                    rs.getString("ho_ten") != null ? rs.getString("ho_ten") : ""
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi lọc dữ liệu: " + e.getMessage());
        }
    }
    
    private void addApartment() {
        ApartmentDialog dialog = new ApartmentDialog(this, "Thêm căn hộ mới", buildingId, null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            loadApartmentData();
        }
    }
    
    private void editApartment() {
        int selectedRow = apartmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn căn hộ cần sửa!");
            return;
        }
        
        int apartmentId = (int) tableModel.getValueAt(selectedRow, 0);
        ApartmentDialog dialog = new ApartmentDialog(this, "Chỉnh sửa căn hộ", buildingId, apartmentId);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            loadApartmentData();
        }
    }
    
    private void deleteApartment() {
        int selectedRow = apartmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn căn hộ cần xóa!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn xóa căn hộ này?", 
            "Xác nhận", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int apartmentId = (int) tableModel.getValueAt(selectedRow, 0);
            
            if (dao.ApartmentDAO.deleteApartment(apartmentId)) {
                JOptionPane.showMessageDialog(this, "Xóa căn hộ thành công!");
                loadApartmentData();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể xóa căn hộ có hợp đồng đang hoạt động!");
            }
        }
    }
    
    private void changeStatus() {
        int selectedRow = apartmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn căn hộ!");
            return;
        }
        
        String[] statuses = {"Trống", "Có người ở", "Bảo trì"};
        String selected = (String) JOptionPane.showInputDialog(
            this,
            "Chọn trạng thái mới:",
            "Đổi trạng thái",
            JOptionPane.QUESTION_MESSAGE,
            null,
            statuses,
            statuses[0]
        );
        
        if (selected != null) {
            int apartmentId = (int) tableModel.getValueAt(selectedRow, 0);
            String statusValue = getStatusValue(selected);
            
            if (dao.ApartmentDAO.updateApartmentStatus(apartmentId, statusValue)) {
                JOptionPane.showMessageDialog(this, "Cập nhật trạng thái thành công!");
                loadApartmentData();
            }
        }
    }
    
    private String getStatusDisplay(String status) {
        switch (status) {
            case "trong": return "Trống";
            case "co_nguoi_o": return "Có người ở";
            case "bao_tri": return "Bảo trì";
            default: return status;
        }
    }
    
    private String getStatusValue(String display) {
        switch (display) {
            case "Trống": return "trong";
            case "Có người ở": return "co_nguoi_o";
            case "Bảo trì": return "bao_tri";
            default: return "";
        }
    }
}

// Dialog for Add/Edit Apartment
class ApartmentDialog extends JDialog {
    private JTextField txtNumber, txtFloor, txtArea, txtBedrooms, txtBathrooms;
    private JComboBox<String> cboStatus;
    private boolean confirmed = false;
    private int buildingId;
    private Integer apartmentId;
    
    public ApartmentDialog(JFrame parent, String title, int buildingId, Integer apartmentId) {
        super(parent, title, true);
        this.buildingId = buildingId;
        this.apartmentId = apartmentId;
        setSize(500, 450);
        setLocationRelativeTo(parent);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Apartment Number
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Số căn hộ:"), gbc);
        gbc.gridx = 1;
        txtNumber = new JTextField(20);
        panel.add(txtNumber, gbc);
        
        // Floor
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Tầng:"), gbc);
        gbc.gridx = 1;
        txtFloor = new JTextField(20);
        panel.add(txtFloor, gbc);
        
        // Area
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Diện tích (m²):"), gbc);
        gbc.gridx = 1;
        txtArea = new JTextField(20);
        panel.add(txtArea, gbc);
        
        // Bedrooms
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Số phòng ngủ:"), gbc);
        gbc.gridx = 1;
        txtBedrooms = new JTextField(20);
        panel.add(txtBedrooms, gbc);
        
        // Bathrooms
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Số phòng tắm:"), gbc);
        gbc.gridx = 1;
        txtBathrooms = new JTextField(20);
        panel.add(txtBathrooms, gbc);
        
        // Status
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 1;
        cboStatus = new JComboBox<>(new String[]{"Trống", "Có người ở", "Bảo trì"});
        panel.add(cboStatus, gbc);
        
        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnSave = new JButton("💾 Lưu");
        JButton btnCancel = new JButton("❌ Hủy");
        
        btnSave.addActionListener(e -> save());
        btnCancel.addActionListener(e -> dispose());
        
        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        panel.add(btnPanel, gbc);
        
        add(panel);
        
        if (apartmentId != null) {
            loadApartmentData();
            txtNumber.setEnabled(false);
            txtFloor.setEnabled(false);
        }
    }
    
    private void loadApartmentData() {
        String query = "SELECT * FROM can_ho WHERE id_can_ho = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, apartmentId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                txtNumber.setText(rs.getString("so_can_ho"));
                txtFloor.setText(String.valueOf(rs.getInt("so_tang")));
                txtArea.setText(String.valueOf(rs.getFloat("dien_tich_m2")));
                txtBedrooms.setText(String.valueOf(rs.getInt("so_phong_ngu")));
                txtBathrooms.setText(String.valueOf(rs.getInt("so_phong_tam")));
                
                String status = rs.getString("trang_thai");
                switch (status) {
                    case "trong": cboStatus.setSelectedIndex(0); break;
                    case "co_nguoi_o": cboStatus.setSelectedIndex(1); break;
                    case "bao_tri": cboStatus.setSelectedIndex(2); break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void save() {
        if (txtNumber.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số căn hộ!");
            return;
        }
        
        try {
            int floor = Integer.parseInt(txtFloor.getText().trim());
            float area = Float.parseFloat(txtArea.getText().trim());
            int bedrooms = Integer.parseInt(txtBedrooms.getText().trim());
            int bathrooms = Integer.parseInt(txtBathrooms.getText().trim());
            
            String statusValue = "";
            switch (cboStatus.getSelectedIndex()) {
                case 0: statusValue = "trong"; break;
                case 1: statusValue = "co_nguoi_o"; break;
                case 2: statusValue = "bao_tri"; break;
            }
            
            if (apartmentId == null) {
                if (dao.ApartmentDAO.addApartment(buildingId, txtNumber.getText().trim(), floor, area, bedrooms, bathrooms)) {
                    confirmed = true;
                    JOptionPane.showMessageDialog(this, "Thêm căn hộ thành công!");
                    dispose();
                }
            } else {
                if (dao.ApartmentDAO.updateApartment(apartmentId, area, bedrooms, bathrooms)) {
                    dao.ApartmentDAO.updateApartmentStatus(apartmentId, statusValue);
                    confirmed = true;
                    JOptionPane.showMessageDialog(this, "Cập nhật căn hộ thành công!");
                    dispose();
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        }
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
}