package view;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import config.DatabaseConnection;

public class HouseholdManagementForm extends JFrame {
    private JTable householdTable;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    
    public HouseholdManagementForm() {
        setTitle("Quản lý Hộ gia đình");
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
        loadHouseholdData();
        setVisible(true);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(240, 242, 245));
        
        JLabel titleLabel = new JLabel("QUẢN LÝ HỘ GIA ĐÌNH");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(25, 103, 210));
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setBackground(new Color(240, 242, 245));
        
        txtSearch = new JTextField(25);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnSearch.addActionListener(e -> searchHouseholds());
        
        searchPanel.add(new JLabel("Tìm theo tên chủ hộ/căn hộ:"));
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
        
        String[] columns = {"ID Hộ", "Căn hộ", "Chủ hộ", "Số nhân khẩu", "SĐT liên hệ", "Email", "Ngày tạo"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        householdTable = new JTable(tableModel);
        householdTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        householdTable.setRowHeight(30);
        householdTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        householdTable.getTableHeader().setBackground(new Color(25, 103, 210));
        householdTable.getTableHeader().setForeground(Color.WHITE);
        householdTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Double click to view members
        householdTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewMembers();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(householdTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(new Color(240, 242, 245));
        
        JButton btnAdd = createButton("Thêm hộ mới", new Color(76, 175, 80));
        JButton btnEdit = createButton("Sửa thông tin", new Color(255, 152, 0));
        JButton btnDelete = createButton("Xóa hộ", new Color(244, 67, 54));
        JButton btnViewMembers = createButton("Xem thành viên", new Color(103, 58, 183));
        JButton btnRefresh = createButton("Làm mới", new Color(33, 150, 243));
        
        btnAdd.addActionListener(e -> addHousehold());
        btnEdit.addActionListener(e -> editHousehold());
        btnDelete.addActionListener(e -> deleteHousehold());
        btnViewMembers.addActionListener(e -> viewMembers());
        btnRefresh.addActionListener(e -> loadHouseholdData());
        
        panel.add(btnAdd);
        panel.add(btnEdit);
        panel.add(btnDelete);
        panel.add(btnViewMembers);
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
    
    private void loadHouseholdData() {
        tableModel.setRowCount(0);
        String query = "SELECT h.id_ho_gia_dinh, c.so_can_ho, u.ho_ten, h.so_nhan_khau, " +
                      "h.so_dien_thoai_lien_he, h.email_lien_he, h.ngay_tao " +
                      "FROM ho_gia_dinh h " +
                      "JOIN can_ho c ON h.id_can_ho = c.id_can_ho " +
                      "JOIN nguoi_dung u ON h.id_chu_ho = u.id_nguoi_dung " +
                      "ORDER BY h.id_ho_gia_dinh DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id_ho_gia_dinh"),
                    rs.getString("so_can_ho"),
                    rs.getString("ho_ten"),
                    rs.getInt("so_nhan_khau"),
                    rs.getString("so_dien_thoai_lien_he"),
                    rs.getString("email_lien_he"),
                    rs.getTimestamp("ngay_tao")
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void searchHouseholds() {
        String keyword = txtSearch.getText().trim();
        tableModel.setRowCount(0);
        
        String query = "SELECT h.id_ho_gia_dinh, c.so_can_ho, u.ho_ten, h.so_nhan_khau, " +
                      "h.so_dien_thoai_lien_he, h.email_lien_he, h.ngay_tao " +
                      "FROM ho_gia_dinh h " +
                      "JOIN can_ho c ON h.id_can_ho = c.id_can_ho " +
                      "JOIN nguoi_dung u ON h.id_chu_ho = u.id_nguoi_dung " +
                      "WHERE u.ho_ten LIKE ? OR c.so_can_ho LIKE ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Object[] row = {
                        rs.getInt("id_ho_gia_dinh"),
                        rs.getString("so_can_ho"),
                        rs.getString("ho_ten"),
                        rs.getInt("so_nhan_khau"),
                        rs.getString("so_dien_thoai_lien_he"),
                        rs.getString("email_lien_he"),
                        rs.getTimestamp("ngay_tao")
                    };
                    tableModel.addRow(row);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tìm kiếm: " + e.getMessage());
        }
    }
    
    private void addHousehold() {
        HouseholdDialog dialog = new HouseholdDialog(this, "Thêm hộ gia đình mới", null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            loadHouseholdData();
        }
    }
    
    private void editHousehold() {
        int selectedRow = householdTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hộ gia đình cần sửa!");
            return;
        }
        
        int householdId = (int) tableModel.getValueAt(selectedRow, 0);
        HouseholdDialog dialog = new HouseholdDialog(this, "Chỉnh sửa hộ gia đình", householdId);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            loadHouseholdData();
        }
    }
    
    private void deleteHousehold() {
        int selectedRow = householdTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hộ gia đình cần xóa!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn xóa hộ gia đình này?\nLưu ý: Sẽ xóa cả thông tin thành viên!", 
            "Xác nhận", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int householdId = (int) tableModel.getValueAt(selectedRow, 0);
            
            try (Connection conn = DatabaseConnection.getConnection()) {
                // Delete members first
                String deleteMembersQuery = "DELETE FROM thanh_vien_ho WHERE id_ho_gia_dinh = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(deleteMembersQuery)) {
                    pstmt.setInt(1, householdId);
                    pstmt.executeUpdate();
                }
                
                // Then delete household
                String deleteHouseholdQuery = "DELETE FROM ho_gia_dinh WHERE id_ho_gia_dinh = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(deleteHouseholdQuery)) {
                    pstmt.setInt(1, householdId);
                    pstmt.executeUpdate();
                }
                
                JOptionPane.showMessageDialog(this, "Xóa hộ gia đình thành công!");
                loadHouseholdData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
            }
        }
    }
    
    private void viewMembers() {
        int selectedRow = householdTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hộ gia đình!");
            return;
        }
        
        int householdId = (int) tableModel.getValueAt(selectedRow, 0);
        new HouseholdMemberForm(householdId).setVisible(true);
    }
}

// Dialog for Add/Edit Household
class HouseholdDialog extends JDialog {
    private JComboBox<String> cboApartment, cboHeadOfHousehold;
    private JTextField txtMembers, txtPhone, txtEmail;
    private boolean confirmed = false;
    private Integer householdId;
    
    public HouseholdDialog(JFrame parent, String title, Integer householdId) {
        super(parent, title, true);
        this.householdId = householdId;
        setSize(500, 400);
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
        
        // Head of Household
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Chủ hộ:"), gbc);
        gbc.gridx = 1;
        cboHeadOfHousehold = new JComboBox<>();
        loadResidents();
        panel.add(cboHeadOfHousehold, gbc);
        
        // Number of Members
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Số nhân khẩu:"), gbc);
        gbc.gridx = 1;
        txtMembers = new JTextField(20);
        panel.add(txtMembers, gbc);
        
        // Phone
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("SĐT liên hệ:"), gbc);
        gbc.gridx = 1;
        txtPhone = new JTextField(20);
        panel.add(txtPhone, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        txtEmail = new JTextField(20);
        panel.add(txtEmail, gbc);
        
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
        
        if (householdId != null) {
            loadHouseholdData();
            cboApartment.setEnabled(false);
        }
    }
    
    private void loadApartments() {
        String query = "SELECT id_can_ho, so_can_ho FROM can_ho WHERE trang_thai = 'trong' OR id_can_ho IN (SELECT id_can_ho FROM ho_gia_dinh WHERE id_ho_gia_dinh = ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, householdId != null ? householdId : 0);
            ResultSet rs = pstmt.executeQuery();
            
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
                cboHeadOfHousehold.addItem(rs.getInt("id_nguoi_dung") + " - " + rs.getString("ho_ten"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadHouseholdData() {
        String query = "SELECT * FROM ho_gia_dinh WHERE id_ho_gia_dinh = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, householdId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                txtMembers.setText(String.valueOf(rs.getInt("so_nhan_khau")));
                txtPhone.setText(rs.getString("so_dien_thoai_lien_he"));
                txtEmail.setText(rs.getString("email_lien_he"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void save() {
        try {
            int members = Integer.parseInt(txtMembers.getText().trim());
            String apartmentStr = (String) cboApartment.getSelectedItem();
            String headStr = (String) cboHeadOfHousehold.getSelectedItem();
            
            int apartmentId = Integer.parseInt(apartmentStr.split(" - ")[0]);
            int headId = Integer.parseInt(headStr.split(" - ")[0]);
            
            try (Connection conn = DatabaseConnection.getConnection()) {
                if (householdId == null) {
                    String query = "INSERT INTO ho_gia_dinh (id_can_ho, id_chu_ho, so_nhan_khau, so_dien_thoai_lien_he, email_lien_he) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                        pstmt.setInt(1, apartmentId);
                        pstmt.setInt(2, headId);
                        pstmt.setInt(3, members);
                        pstmt.setString(4, txtPhone.getText().trim());
                        pstmt.setString(5, txtEmail.getText().trim());
                        pstmt.executeUpdate();
                    }
                    
                    // Update apartment status
                    String updateQuery = "UPDATE can_ho SET trang_thai = 'co_nguoi_o' WHERE id_can_ho = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
                        pstmt.setInt(1, apartmentId);
                        pstmt.executeUpdate();
                    }
                } else {
                    String query = "UPDATE ho_gia_dinh SET so_nhan_khau = ?, so_dien_thoai_lien_he = ?, email_lien_he = ? WHERE id_ho_gia_dinh = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                        pstmt.setInt(1, members);
                        pstmt.setString(2, txtPhone.getText().trim());
                        pstmt.setString(3, txtEmail.getText().trim());
                        pstmt.setInt(4, householdId);
                        pstmt.executeUpdate();
                    }
                }
                
                confirmed = true;
                JOptionPane.showMessageDialog(this, "Lưu thành công!");
                dispose();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số nhân khẩu phải là số!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        }
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
}