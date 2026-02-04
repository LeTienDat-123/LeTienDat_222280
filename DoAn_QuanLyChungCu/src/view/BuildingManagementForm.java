package view;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import config.DatabaseConnection;

public class BuildingManagementForm extends JFrame {
    private JTable buildingTable;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    
    public BuildingManagementForm() {
        setTitle("Quản lý Tòa nhà");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 242, 245));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createTablePanel(), BorderLayout.CENTER);
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        loadBuildingData();
        setVisible(true);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(240, 242, 245));
        
        JLabel titleLabel = new JLabel("QUẢN LÝ TÒA NHÀ");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(25, 103, 210));
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setBackground(new Color(240, 242, 245));
        
        txtSearch = new JTextField(25);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnSearch.addActionListener(e -> searchBuildings());
        
        searchPanel.add(new JLabel("Tìm theo tên/địa chỉ:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(searchPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        
        String[] columns = {"ID", "Tên tòa nhà", "Địa chỉ", "Số tầng", "Năm xây dựng", "Quản lý", "Tổng căn hộ"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        buildingTable = new JTable(tableModel);
        buildingTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        buildingTable.setRowHeight(30);
        buildingTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        buildingTable.getTableHeader().setBackground(new Color(25, 103, 210));
        buildingTable.getTableHeader().setForeground(Color.WHITE);
        buildingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(buildingTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(new Color(240, 242, 245));
        
        JButton btnAdd = createButton("Thêm tòa nhà", new Color(76, 175, 80));
        JButton btnEdit = createButton("Sửa thông tin", new Color(255, 152, 0));
        JButton btnDelete = createButton("Xóa", new Color(244, 67, 54));
        JButton btnViewApartments = createButton("Xem căn hộ", new Color(103, 58, 183));
        JButton btnRefresh = createButton("Làm mới", new Color(33, 150, 243));
        
        btnAdd.addActionListener(e -> addBuilding());
        btnEdit.addActionListener(e -> editBuilding());
        btnDelete.addActionListener(e -> deleteBuilding());
        btnViewApartments.addActionListener(e -> viewApartments());
        btnRefresh.addActionListener(e -> loadBuildingData());
        
        panel.add(btnAdd);
        panel.add(btnEdit);
        panel.add(btnDelete);
        panel.add(btnViewApartments);
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
    
    private void loadBuildingData() {
        tableModel.setRowCount(0);
        String query = "SELECT t.id_toa_nha, t.ten_toa_nha, t.dia_chi, t.so_tang, t.nam_xay_dung, " +
                      "u.ho_ten, COUNT(c.id_can_ho) as total_apartments " +
                      "FROM toa_nha t " +
                      "LEFT JOIN nguoi_dung u ON t.id_quan_ly = u.id_nguoi_dung " +
                      "LEFT JOIN can_ho c ON t.id_toa_nha = c.id_toa_nha " +
                      "GROUP BY t.id_toa_nha " +
                      "ORDER BY t.id_toa_nha DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id_toa_nha"),
                    rs.getString("ten_toa_nha"),
                    rs.getString("dia_chi"),
                    rs.getInt("so_tang"),
                    rs.getInt("nam_xay_dung"),
                    rs.getString("ho_ten") != null ? rs.getString("ho_ten") : "Chưa có",
                    rs.getInt("total_apartments")
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void searchBuildings() {
        String keyword = txtSearch.getText().trim();
        tableModel.setRowCount(0);
        
        String query = "SELECT t.id_toa_nha, t.ten_toa_nha, t.dia_chi, t.so_tang, t.nam_xay_dung, " +
                      "u.ho_ten, COUNT(c.id_can_ho) as total_apartments " +
                      "FROM toa_nha t " +
                      "LEFT JOIN nguoi_dung u ON t.id_quan_ly = u.id_nguoi_dung " +
                      "LEFT JOIN can_ho c ON t.id_toa_nha = c.id_toa_nha " +
                      "WHERE t.ten_toa_nha LIKE ? OR t.dia_chi LIKE ? " +
                      "GROUP BY t.id_toa_nha";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Object[] row = {
                        rs.getInt("id_toa_nha"),
                        rs.getString("ten_toa_nha"),
                        rs.getString("dia_chi"),
                        rs.getInt("so_tang"),
                        rs.getInt("nam_xay_dung"),
                        rs.getString("ho_ten") != null ? rs.getString("ho_ten") : "Chưa có",
                        rs.getInt("total_apartments")
                    };
                    tableModel.addRow(row);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tìm kiếm: " + e.getMessage());
        }
    }
    
    private void addBuilding() {
        BuildingDialog dialog = new BuildingDialog(this, "Thêm tòa nhà mới", null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            loadBuildingData();
        }
    }
    
    private void editBuilding() {
        int selectedRow = buildingTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tòa nhà cần sửa!");
            return;
        }
        
        int buildingId = (int) tableModel.getValueAt(selectedRow, 0);
        BuildingDialog dialog = new BuildingDialog(this, "Chỉnh sửa tòa nhà", buildingId);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            loadBuildingData();
        }
    }
    
    private void deleteBuilding() {
        int selectedRow = buildingTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tòa nhà cần xóa!");
            return;
        }
        
        int apartmentCount = (int) tableModel.getValueAt(selectedRow, 6);
        if (apartmentCount > 0) {
            JOptionPane.showMessageDialog(this, 
                "Không thể xóa tòa nhà có căn hộ!\nVui lòng xóa hết căn hộ trước.", 
                "Cảnh báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn xóa tòa nhà này?", 
            "Xác nhận", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int buildingId = (int) tableModel.getValueAt(selectedRow, 0);
            String query = "DELETE FROM toa_nha WHERE id_toa_nha = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                
                pstmt.setInt(1, buildingId);
                pstmt.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Xóa tòa nhà thành công!");
                loadBuildingData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
            }
        }
    }
    
    private void viewApartments() {
        int selectedRow = buildingTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tòa nhà!");
            return;
        }
        
        int buildingId = (int) tableModel.getValueAt(selectedRow, 0);
        new ApartmentManagementForm(buildingId);
    }
}

// Dialog for Add/Edit Building
class BuildingDialog extends JDialog {
    private JTextField txtName, txtAddress, txtFloors, txtYear;
    private JComboBox<String> cboManager;
    private boolean confirmed = false;
    private Integer buildingId;
    
    public BuildingDialog(JFrame parent, String title, Integer buildingId) {
        super(parent, title, true);
        this.buildingId = buildingId;
        setSize(550, 400);
        setLocationRelativeTo(parent);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Name
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Tên tòa nhà:"), gbc);
        gbc.gridx = 1;
        txtName = new JTextField(20);
        panel.add(txtName, gbc);
        
        // Address
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Địa chỉ:"), gbc);
        gbc.gridx = 1;
        txtAddress = new JTextField(20);
        panel.add(txtAddress, gbc);
        
        // Floors
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Số tầng:"), gbc);
        gbc.gridx = 1;
        txtFloors = new JTextField(20);
        panel.add(txtFloors, gbc);
        
        // Year Built
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Năm xây dựng:"), gbc);
        gbc.gridx = 1;
        txtYear = new JTextField(20);
        panel.add(txtYear, gbc);
        
        // Manager
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Quản lý:"), gbc);
        gbc.gridx = 1;
        cboManager = new JComboBox<>();
        cboManager.addItem("0 - Chưa có");
        loadManagers();
        panel.add(cboManager, gbc);
        
        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnSave = new JButton("💾 Lưu");
        JButton btnCancel = new JButton("❌ Hủy");
        
        btnSave.addActionListener(e -> save());
        btnCancel.addActionListener(e -> dispose());
        
        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        panel.add(btnPanel, gbc);
        
        add(panel);
        
        if (buildingId != null) {
            loadBuildingData();
        }
    }
    
    private void loadManagers() {
        String query = "SELECT id_nguoi_dung, ho_ten FROM nguoi_dung WHERE vai_tro IN ('quan_ly_he_thong', 'quan_ly_toa_nha') AND trang_thai_hoat_dong = TRUE";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                cboManager.addItem(rs.getInt("id_nguoi_dung") + " - " + rs.getString("ho_ten"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadBuildingData() {
        String query = "SELECT * FROM toa_nha WHERE id_toa_nha = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, buildingId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                txtName.setText(rs.getString("ten_toa_nha"));
                txtAddress.setText(rs.getString("dia_chi"));
                txtFloors.setText(String.valueOf(rs.getInt("so_tang")));
                txtYear.setText(String.valueOf(rs.getInt("nam_xay_dung")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void save() {
        if (txtName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên tòa nhà!");
            return;
        }
        
        try {
            int floors = Integer.parseInt(txtFloors.getText().trim());
            int year = Integer.parseInt(txtYear.getText().trim());
            
            String managerStr = (String) cboManager.getSelectedItem();
            int managerId = Integer.parseInt(managerStr.split(" - ")[0]);
            
            try (Connection conn = DatabaseConnection.getConnection()) {
                if (buildingId == null) {
                    String query = "INSERT INTO toa_nha (ten_toa_nha, dia_chi, so_tang, nam_xay_dung, id_quan_ly) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                        pstmt.setString(1, txtName.getText().trim());
                        pstmt.setString(2, txtAddress.getText().trim());
                        pstmt.setInt(3, floors);
                        pstmt.setInt(4, year);
                        pstmt.setInt(5, managerId == 0 ? null : managerId);
                        pstmt.executeUpdate();
                    }
                } else {
                    String query = "UPDATE toa_nha SET ten_toa_nha = ?, dia_chi = ?, so_tang = ?, nam_xay_dung = ?, id_quan_ly = ? WHERE id_toa_nha = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                        pstmt.setString(1, txtName.getText().trim());
                        pstmt.setString(2, txtAddress.getText().trim());
                        pstmt.setInt(3, floors);
                        pstmt.setInt(4, year);
                        if (managerId == 0) {
                            pstmt.setNull(5, Types.INTEGER);
                        } else {
                            pstmt.setInt(5, managerId);
                        }
                        pstmt.setInt(6, buildingId);
                        pstmt.executeUpdate();
                    }
                }
                
                confirmed = true;
                JOptionPane.showMessageDialog(this, "Lưu thành công!");
                dispose();
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