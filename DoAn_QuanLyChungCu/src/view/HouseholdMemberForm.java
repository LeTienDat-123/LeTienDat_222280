package view;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import config.DatabaseConnection;

public class HouseholdMemberForm extends JFrame {
    private JTable memberTable;
    private DefaultTableModel tableModel;
    private int householdId;
    
    public HouseholdMemberForm(int householdId) {
        this.householdId = householdId;
        setTitle("Quản lý Thành viên Hộ gia đình #" + householdId);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 242, 245));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createTablePanel(), BorderLayout.CENTER);
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        loadMemberData();
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 242, 245));
        
        JLabel titleLabel = new JLabel("THÀNH VIÊN HỘ GIA ĐÌNH");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(25, 103, 210));
        
        panel.add(titleLabel, BorderLayout.WEST);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        
        String[] columns = {"ID", "Tên thành viên", "Quan hệ", "Ngày sinh", "CMND/CCCD", "SĐT"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        memberTable = new JTable(tableModel);
        memberTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        memberTable.setRowHeight(30);
        memberTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        memberTable.getTableHeader().setBackground(new Color(25, 103, 210));
        memberTable.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(memberTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(new Color(240, 242, 245));
        
        JButton btnAdd = createButton("Thêm thành viên", new Color(76, 175, 80));
        JButton btnEdit = createButton("Sửa", new Color(255, 152, 0));
        JButton btnDelete = createButton("Xóa", new Color(244, 67, 54));
        JButton btnRefresh = createButton(" Làm mới", new Color(33, 150, 243));
        
        btnAdd.addActionListener(e -> addMember());
        btnEdit.addActionListener(e -> editMember());
        btnDelete.addActionListener(e -> deleteMember());
        btnRefresh.addActionListener(e -> loadMemberData());
        
        panel.add(btnAdd);
        panel.add(btnEdit);
        panel.add(btnDelete);
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
    
    private void loadMemberData() {
        tableModel.setRowCount(0);
        String query = "SELECT * FROM thanh_vien_ho WHERE id_ho_gia_dinh = ? ORDER BY id_thanh_vien DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, householdId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id_thanh_vien"),
                    rs.getString("ten_thanh_vien"),
                    rs.getString("quan_he"),
                    rs.getDate("ngay_sinh"),
                    rs.getString("so_chung_minh_nhan_dan"),
                    rs.getString("so_dien_thoai")
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
        }
    }
    
    private void addMember() {
        MemberDialog dialog = new MemberDialog(this, "Thêm thành viên", householdId, null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            loadMemberData();
        }
    }
    
    private void editMember() {
        int selectedRow = memberTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thành viên cần sửa!");
            return;
        }
        
        int memberId = (int) tableModel.getValueAt(selectedRow, 0);
        MemberDialog dialog = new MemberDialog(this, "Chỉnh sửa thành viên", householdId, memberId);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            loadMemberData();
        }
    }
    
    private void deleteMember() {
        int selectedRow = memberTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thành viên cần xóa!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn xóa thành viên này?", 
            "Xác nhận", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int memberId = (int) tableModel.getValueAt(selectedRow, 0);
            String query = "DELETE FROM thanh_vien_ho WHERE id_thanh_vien = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                
                pstmt.setInt(1, memberId);
                pstmt.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
                loadMemberData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
            }
        }
    }
}

// Dialog for Add/Edit Member
class MemberDialog extends JDialog {
    private JTextField txtName, txtRelation, txtIDCard, txtPhone;
    private JSpinner dateSpinner;
    private boolean confirmed = false;
    private int householdId;
    private Integer memberId;
    
    public MemberDialog(JFrame parent, String title, int householdId, Integer memberId) {
        super(parent, title, true);
        this.householdId = householdId;
        this.memberId = memberId;
        setSize(450, 400);
        setLocationRelativeTo(parent);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Name
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Tên thành viên:"), gbc);
        gbc.gridx = 1;
        txtName = new JTextField(20);
        panel.add(txtName, gbc);
        
        // Relation
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Quan hệ:"), gbc);
        gbc.gridx = 1;
        txtRelation = new JTextField(20);
        panel.add(txtRelation, gbc);
        
        // Date of Birth - Using JSpinner instead of JDateChooser
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Ngày sinh:"), gbc);
        gbc.gridx = 1;
        
        SpinnerDateModel dateModel = new SpinnerDateModel();
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);
        panel.add(dateSpinner, gbc);
        
        // ID Card
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("CMND/CCCD:"), gbc);
        gbc.gridx = 1;
        txtIDCard = new JTextField(20);
        panel.add(txtIDCard, gbc);
        
        // Phone
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Số điện thoại:"), gbc);
        gbc.gridx = 1;
        txtPhone = new JTextField(20);
        panel.add(txtPhone, gbc);
        
        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnSave = new JButton("Lưu");
        JButton btnCancel = new JButton("Hủy");
        
        btnSave.addActionListener(e -> save());
        btnCancel.addActionListener(e -> dispose());
        
        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        panel.add(btnPanel, gbc);
        
        add(panel);
        
        if (memberId != null) {
            loadMemberData();
        }
    }
    
    private void loadMemberData() {
        String query = "SELECT * FROM thanh_vien_ho WHERE id_thanh_vien = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, memberId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                txtName.setText(rs.getString("ten_thanh_vien"));
                txtRelation.setText(rs.getString("quan_he"));
                if (rs.getDate("ngay_sinh") != null) {
                    dateSpinner.setValue(rs.getDate("ngay_sinh"));
                }
                txtIDCard.setText(rs.getString("so_chung_minh_nhan_dan"));
                txtPhone.setText(rs.getString("so_dien_thoai"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void save() {
        if (txtName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên thành viên!");
            return;
        }
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            Date selectedDate = (Date) dateSpinner.getValue();
            java.sql.Date sqlDate = selectedDate != null ? new java.sql.Date(selectedDate.getTime()) : null;
            
            if (memberId == null) {
                String query = "INSERT INTO thanh_vien_ho (id_ho_gia_dinh, ten_thanh_vien, quan_he, ngay_sinh, so_chung_minh_nhan_dan, so_dien_thoai) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setInt(1, householdId);
                    pstmt.setString(2, txtName.getText().trim());
                    pstmt.setString(3, txtRelation.getText().trim());
                    pstmt.setDate(4, sqlDate);
                    pstmt.setString(5, txtIDCard.getText().trim());
                    pstmt.setString(6, txtPhone.getText().trim());
                    pstmt.executeUpdate();
                }
            } else {
                String query = "UPDATE thanh_vien_ho SET ten_thanh_vien = ?, quan_he = ?, ngay_sinh = ?, so_chung_minh_nhan_dan = ?, so_dien_thoai = ? WHERE id_thanh_vien = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setString(1, txtName.getText().trim());
                    pstmt.setString(2, txtRelation.getText().trim());
                    pstmt.setDate(3, sqlDate);
                    pstmt.setString(4, txtIDCard.getText().trim());
                    pstmt.setString(5, txtPhone.getText().trim());
                    pstmt.setInt(6, memberId);
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