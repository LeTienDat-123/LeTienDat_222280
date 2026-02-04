package view;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import config.DatabaseConnection;

public class UserManagementForm extends JFrame {
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JComboBox<String> cboRoleFilter;
    
    public UserManagementForm() {
        setTitle("Quản lý Người dùng");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 242, 245));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Header
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Table
        mainPanel.add(createTablePanel(), BorderLayout.CENTER);
        
        // Buttons
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        loadUserData();
        setVisible(true);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(240, 242, 245));
        
        JLabel titleLabel = new JLabel("QUẢN LÝ NGƯỜI DÙNG");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(25, 103, 210));
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setBackground(new Color(240, 242, 245));
        
        txtSearch = new JTextField(20);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnSearch.addActionListener(e -> searchUsers());
        
        cboRoleFilter = new JComboBox<>(new String[]{
            "Tất cả", "Quản lý hệ thống", "Quản lý tòa nhà", "Cư dân"
        });
        cboRoleFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cboRoleFilter.addActionListener(e -> filterByRole());
        
        searchPanel.add(new JLabel("Vai trò:"));
        searchPanel.add(cboRoleFilter);
        searchPanel.add(new JLabel("Tìm kiếm:"));
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
        
        String[] columns = {"ID", "Tên đăng nhập", "Họ tên", "Email", "SĐT", "Vai trò", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        userTable = new JTable(tableModel);
        userTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userTable.setRowHeight(30);
        userTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        userTable.getTableHeader().setBackground(new Color(25, 103, 210));
        userTable.getTableHeader().setForeground(Color.WHITE);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(userTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(new Color(240, 242, 245));
        
        JButton btnAdd = createButton("Thêm mới", new Color(76, 175, 80));
        JButton btnEdit = createButton("Sửa", new Color(255, 152, 0));
        JButton btnDelete = createButton("Xóa", new Color(244, 67, 54));
        JButton btnResetPassword = createButton("Đặt lại mật khẩu", new Color(103, 58, 183));
        JButton btnRefresh = createButton("Làm mới", new Color(33, 150, 243));
        
        btnAdd.addActionListener(e -> addUser());
        btnEdit.addActionListener(e -> editUser());
        btnDelete.addActionListener(e -> deleteUser());
        btnResetPassword.addActionListener(e -> resetPassword());
        btnRefresh.addActionListener(e -> loadUserData());
        
        panel.add(btnAdd);
        panel.add(btnEdit);
        panel.add(btnDelete);
        panel.add(btnResetPassword);
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
        btn.setPreferredSize(new Dimension(150, 35));
        return btn;
    }
    
    private void loadUserData() {
        tableModel.setRowCount(0);
        String query = "SELECT id_nguoi_dung, ten_dang_nhap, ho_ten, email, so_dien_thoai, vai_tro, trang_thai_hoat_dong FROM nguoi_dung ORDER BY id_nguoi_dung DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id_nguoi_dung"),
                    rs.getString("ten_dang_nhap"),
                    rs.getString("ho_ten"),
                    rs.getString("email"),
                    rs.getString("so_dien_thoai"),
                    getRoleDisplay(rs.getString("vai_tro")),
                    rs.getBoolean("trang_thai_hoat_dong") ? "Hoạt động" : "Khóa"
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void searchUsers() {
        String keyword = txtSearch.getText().trim();
        tableModel.setRowCount(0);
        
        String query = "SELECT id_nguoi_dung, ten_dang_nhap, ho_ten, email, so_dien_thoai, vai_tro, trang_thai_hoat_dong " +
                      "FROM nguoi_dung WHERE ho_ten LIKE ? OR ten_dang_nhap LIKE ? OR so_dien_thoai LIKE ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Object[] row = {
                        rs.getInt("id_nguoi_dung"),
                        rs.getString("ten_dang_nhap"),
                        rs.getString("ho_ten"),
                        rs.getString("email"),
                        rs.getString("so_dien_thoai"),
                        getRoleDisplay(rs.getString("vai_tro")),
                        rs.getBoolean("trang_thai_hoat_dong") ? "Hoạt động" : "Khóa"
                    };
                    tableModel.addRow(row);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tìm kiếm: " + e.getMessage());
        }
    }
    
    private void filterByRole() {
        String selectedRole = (String) cboRoleFilter.getSelectedItem();
        if ("Tất cả".equals(selectedRole)) {
            loadUserData();
            return;
        }
        
        String roleValue = getRoleValue(selectedRole);
        tableModel.setRowCount(0);
        
        String query = "SELECT id_nguoi_dung, ten_dang_nhap, ho_ten, email, so_dien_thoai, vai_tro, trang_thai_hoat_dong " +
                      "FROM nguoi_dung WHERE vai_tro = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, roleValue);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Object[] row = {
                        rs.getInt("id_nguoi_dung"),
                        rs.getString("ten_dang_nhap"),
                        rs.getString("ho_ten"),
                        rs.getString("email"),
                        rs.getString("so_dien_thoai"),
                        getRoleDisplay(rs.getString("vai_tro")),
                        rs.getBoolean("trang_thai_hoat_dong") ? "Hoạt động" : "Khóa"
                    };
                    tableModel.addRow(row);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi lọc dữ liệu: " + e.getMessage());
        }
    }
    
    private void addUser() {
        UserDialog dialog = new UserDialog(this, "Thêm người dùng mới", null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            loadUserData();
        }
    }
    
    private void editUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn người dùng cần sửa!");
            return;
        }
        
        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        UserDialog dialog = new UserDialog(this, "Chỉnh sửa thông tin", userId);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            loadUserData();
        }
    }
    
    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn người dùng cần xóa!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn xóa người dùng này?", 
            "Xác nhận", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int userId = (int) tableModel.getValueAt(selectedRow, 0);
            String query = "UPDATE nguoi_dung SET trang_thai_hoat_dong = FALSE WHERE id_nguoi_dung = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                
                pstmt.setInt(1, userId);
                pstmt.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Đã khóa tài khoản thành công!");
                loadUserData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
            }
        }
    }
    
    private void resetPassword() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn người dùng!");
            return;
        }
        
        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String newPassword = JOptionPane.showInputDialog(this, "Nhập mật khẩu mới:");
        
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            String query = "UPDATE nguoi_dung SET mat_khau = ? WHERE id_nguoi_dung = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                
                pstmt.setString(1, newPassword);
                pstmt.setInt(2, userId);
                pstmt.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Đặt lại mật khẩu thành công!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
            }
        }
    }
    
    private String getRoleDisplay(String role) {
        switch (role) {
            case "quan_ly_he_thong": return "Quản lý hệ thống";
            case "quan_ly_toa_nha": return "Quản lý tòa nhà";
            case "cu_dan": return "Cư dân";
            default: return role;
        }
    }
    
    private String getRoleValue(String display) {
        switch (display) {
            case "Quản lý hệ thống": return "quan_ly_he_thong";
            case "Quản lý tòa nhà": return "quan_ly_toa_nha";
            case "Cư dân": return "cu_dan";
            default: return "";
        }
    }
}

// Dialog for Add/Edit User
class UserDialog extends JDialog {
    private JTextField txtUsername, txtFullName, txtEmail, txtPhone, txtPassword;
    private JComboBox<String> cboRole;
    private JCheckBox chkActive;
    private boolean confirmed = false;
    private Integer userId;
    
    public UserDialog(JFrame parent, String title, Integer userId) {
        super(parent, title, true);
        this.userId = userId;
        setSize(500, 450);
        setLocationRelativeTo(parent);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Tên đăng nhập:"), gbc);
        gbc.gridx = 1;
        txtUsername = new JTextField(20);
        panel.add(txtUsername, gbc);
        
        // Password
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Mật khẩu:"), gbc);
        gbc.gridx = 1;
        txtPassword = new JTextField(20);
        panel.add(txtPassword, gbc);
        
        // Full Name
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Họ tên:"), gbc);
        gbc.gridx = 1;
        txtFullName = new JTextField(20);
        panel.add(txtFullName, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        txtEmail = new JTextField(20);
        panel.add(txtEmail, gbc);
        
        // Phone
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Số điện thoại:"), gbc);
        gbc.gridx = 1;
        txtPhone = new JTextField(20);
        panel.add(txtPhone, gbc);
        
        // Role
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Vai trò:"), gbc);
        gbc.gridx = 1;
        cboRole = new JComboBox<>(new String[]{"Cư dân", "Quản lý tòa nhà", "Quản lý hệ thống"});
        panel.add(cboRole, gbc);
        
        // Active
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        chkActive = new JCheckBox("Hoạt động");
        chkActive.setSelected(true);
        panel.add(chkActive, gbc);
        
        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnSave = new JButton("💾 Lưu");
        JButton btnCancel = new JButton("❌ Hủy");
        
        btnSave.addActionListener(e -> save());
        btnCancel.addActionListener(e -> dispose());
        
        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);
        
        gbc.gridy = 7;
        panel.add(btnPanel, gbc);
        
        add(panel);
        
        if (userId != null) {
            loadUserData();
            txtUsername.setEnabled(false);
            txtPassword.setEnabled(false);
        }
    }
    
    private void loadUserData() {
        String query = "SELECT * FROM nguoi_dung WHERE id_nguoi_dung = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                txtUsername.setText(rs.getString("ten_dang_nhap"));
                txtFullName.setText(rs.getString("ho_ten"));
                txtEmail.setText(rs.getString("email"));
                txtPhone.setText(rs.getString("so_dien_thoai"));
                
                String role = rs.getString("vai_tro");
                switch (role) {
                    case "quan_ly_he_thong": cboRole.setSelectedIndex(2); break;
                    case "quan_ly_toa_nha": cboRole.setSelectedIndex(1); break;
                    default: cboRole.setSelectedIndex(0);
                }
                
                chkActive.setSelected(rs.getBoolean("trang_thai_hoat_dong"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void save() {
        if (txtFullName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập họ tên!");
            return;
        }
        
        String roleValue = "";
        switch (cboRole.getSelectedIndex()) {
            case 0: roleValue = "cu_dan"; break;
            case 1: roleValue = "quan_ly_toa_nha"; break;
            case 2: roleValue = "quan_ly_he_thong"; break;
        }
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (userId == null) {
                // Add new
                if (txtUsername.getText().trim().isEmpty() || txtPassword.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập tên đăng nhập và mật khẩu!");
                    return;
                }
                
                String query = "INSERT INTO nguoi_dung (ten_dang_nhap, mat_khau, ho_ten, email, so_dien_thoai, vai_tro, trang_thai_hoat_dong) " +
                              "VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setString(1, txtUsername.getText().trim());
                    pstmt.setString(2, txtPassword.getText().trim());
                    pstmt.setString(3, txtFullName.getText().trim());
                    pstmt.setString(4, txtEmail.getText().trim());
                    pstmt.setString(5, txtPhone.getText().trim());
                    pstmt.setString(6, roleValue);
                    pstmt.setBoolean(7, chkActive.isSelected());
                    pstmt.executeUpdate();
                }
            } else {
                // Update
                String query = "UPDATE nguoi_dung SET ho_ten = ?, email = ?, so_dien_thoai = ?, vai_tro = ?, trang_thai_hoat_dong = ? WHERE id_nguoi_dung = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setString(1, txtFullName.getText().trim());
                    pstmt.setString(2, txtEmail.getText().trim());
                    pstmt.setString(3, txtPhone.getText().trim());
                    pstmt.setString(4, roleValue);
                    pstmt.setBoolean(5, chkActive.isSelected());
                    pstmt.setInt(6, userId);
                    pstmt.executeUpdate();
                }
            }
            
            confirmed = true;
            JOptionPane.showMessageDialog(this, "Lưu thành công!");
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        }
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
}