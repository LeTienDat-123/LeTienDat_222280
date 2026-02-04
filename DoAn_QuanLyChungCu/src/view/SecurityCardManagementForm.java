package view;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.util.Date;
import config.DatabaseConnection;

public class SecurityCardManagementForm extends JFrame {
    private JTable cardTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> cboStatusFilter;
    
    public SecurityCardManagementForm() {
        setTitle("Quản lý Thẻ an ninh");
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
        loadCardData();
        setVisible(true);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(240, 242, 245));
        
        JLabel titleLabel = new JLabel("QUẢN LÝ THẺ AN NINH");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(25, 103, 210));
        
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterPanel.setBackground(new Color(240, 242, 245));
        
        cboStatusFilter = new JComboBox<>(new String[]{"Tất cả", "Hoạt động", "Mất", "Hết hạn", "Vô hiệu hóa"});
        cboStatusFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cboStatusFilter.addActionListener(e -> filterByStatus());
        
        filterPanel.add(new JLabel("Trạng thái:"));
        filterPanel.add(cboStatusFilter);
        
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(filterPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        
        String[] columns = {"ID Thẻ", "Số thẻ", "Căn hộ", "Chủ thẻ", "Ngày cấp", "Ngày hết hạn", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        cardTable = new JTable(tableModel);
        cardTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cardTable.setRowHeight(30);
        cardTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        cardTable.getTableHeader().setBackground(new Color(25, 103, 210));
        cardTable.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(cardTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(new Color(240, 242, 245));
        
        JButton btnAdd = createButton("Cấp thẻ mới", new Color(76, 175, 80));
        JButton btnEdit = createButton("Sửa", new Color(255, 152, 0));
        JButton btnBlock = createButton("Khóa thẻ", new Color(244, 67, 54));
        JButton btnReportLost = createButton("Báo mất", new Color(156, 39, 176));
        JButton btnRefresh = createButton("Làm mới", new Color(33, 150, 243));
        
        btnAdd.addActionListener(e -> addCard());
        btnEdit.addActionListener(e -> editCard());
        btnBlock.addActionListener(e -> blockCard());
        btnReportLost.addActionListener(e -> reportLost());
        btnRefresh.addActionListener(e -> loadCardData());
        
        panel.add(btnAdd);
        panel.add(btnEdit);
        panel.add(btnBlock);
        panel.add(btnReportLost);
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
    
    private void loadCardData() {
        tableModel.setRowCount(0);
        String query = "SELECT t.id_the, t.so_the, c.so_can_ho, u.ho_ten, t.ngay_cap, t.ngay_het_han, t.trang_thai " +
                      "FROM the_an_ninh t " +
                      "JOIN can_ho c ON t.id_can_ho = c.id_can_ho " +
                      "JOIN nguoi_dung u ON t.id_cu_dan = u.id_nguoi_dung " +
                      "ORDER BY t.id_the DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id_the"),
                    rs.getString("so_the"),
                    rs.getString("so_can_ho"),
                    rs.getString("ho_ten"),
                    rs.getDate("ngay_cap"),
                    rs.getDate("ngay_het_han"),
                    getStatusDisplay(rs.getString("trang_thai"))
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
        }
    }
    
    private void filterByStatus() {
        String selectedStatus = (String) cboStatusFilter.getSelectedItem();
        if ("Tất cả".equals(selectedStatus)) {
            loadCardData();
            return;
        }
        
        String statusValue = getStatusValue(selectedStatus);
        tableModel.setRowCount(0);
        
        String query = "SELECT t.id_the, t.so_the, c.so_can_ho, u.ho_ten, t.ngay_cap, t.ngay_het_han, t.trang_thai " +
                      "FROM the_an_ninh t " +
                      "JOIN can_ho c ON t.id_can_ho = c.id_can_ho " +
                      "JOIN nguoi_dung u ON t.id_cu_dan = u.id_nguoi_dung " +
                      "WHERE t.trang_thai = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, statusValue);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id_the"),
                    rs.getString("so_the"),
                    rs.getString("so_can_ho"),
                    rs.getString("ho_ten"),
                    rs.getDate("ngay_cap"),
                    rs.getDate("ngay_het_han"),
                    getStatusDisplay(rs.getString("trang_thai"))
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi lọc dữ liệu: " + e.getMessage());
        }
    }
    
    private void addCard() {
        SecurityCardDialog dialog = new SecurityCardDialog(this, "Cấp thẻ an ninh mới", null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            loadCardData();
        }
    }
    
    private void editCard() {
        int selectedRow = cardTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thẻ cần sửa!");
            return;
        }
        
        int cardId = (int) tableModel.getValueAt(selectedRow, 0);
        SecurityCardDialog dialog = new SecurityCardDialog(this, "Chỉnh sửa thẻ", cardId);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            loadCardData();
        }
    }
    
    private void blockCard() {
        int selectedRow = cardTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thẻ!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn vô hiệu hóa thẻ này?", 
            "Xác nhận", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int cardId = (int) tableModel.getValueAt(selectedRow, 0);
            String query = "UPDATE the_an_ninh SET trang_thai = 'vo_hieu_hoa' WHERE id_the = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                
                pstmt.setInt(1, cardId);
                pstmt.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Vô hiệu hóa thẻ thành công!");
                loadCardData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
            }
        }
    }
    
    private void reportLost() {
        int selectedRow = cardTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thẻ!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Báo thẻ bị mất?", 
            "Xác nhận", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int cardId = (int) tableModel.getValueAt(selectedRow, 0);
            String query = "UPDATE the_an_ninh SET trang_thai = 'mat' WHERE id_the = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                
                pstmt.setInt(1, cardId);
                pstmt.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Đã báo mất thẻ!");
                loadCardData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
            }
        }
    }
    
    private String getStatusDisplay(String status) {
        switch (status) {
            case "hoat_dong": return "Hoạt động";
            case "mat": return "Mất";
            case "het_han": return "Hết hạn";
            case "vo_hieu_hoa": return "Vô hiệu hóa";
            default: return status;
        }
    }
    
    private String getStatusValue(String display) {
        switch (display) {
            case "Hoạt động": return "hoat_dong";
            case "Mất": return "mat";
            case "Hết hạn": return "het_han";
            case "Vô hiệu hóa": return "vo_hieu_hoa";
            default: return "";
        }
    }
}

// Dialog for Add/Edit Security Card
class SecurityCardDialog extends JDialog {
    private JTextField txtCardNumber;
    private JComboBox<String> cboApartment, cboResident;
    private JSpinner dateIssueSpinner, dateExpirySpinner;
    private boolean confirmed = false;
    private Integer cardId;
    
    public SecurityCardDialog(JFrame parent, String title, Integer cardId) {
        super(parent, title, true);
        this.cardId = cardId;
        setSize(500, 400);
        setLocationRelativeTo(parent);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Card Number
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Số thẻ:"), gbc);
        gbc.gridx = 1;
        txtCardNumber = new JTextField(20);
        panel.add(txtCardNumber, gbc);
        
        // Apartment
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Căn hộ:"), gbc);
        gbc.gridx = 1;
        cboApartment = new JComboBox<>();
        loadApartments();
        panel.add(cboApartment, gbc);
        
        // Resident
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Cư dân:"), gbc);
        gbc.gridx = 1;
        cboResident = new JComboBox<>();
        loadResidents();
        panel.add(cboResident, gbc);
        
        // Issue Date - Using JSpinner
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Ngày cấp:"), gbc);
        gbc.gridx = 1;
        SpinnerDateModel issueDateModel = new SpinnerDateModel();
        dateIssueSpinner = new JSpinner(issueDateModel);
        JSpinner.DateEditor issueEditor = new JSpinner.DateEditor(dateIssueSpinner, "dd/MM/yyyy");
        dateIssueSpinner.setEditor(issueEditor);
        dateIssueSpinner.setValue(new Date());
        panel.add(dateIssueSpinner, gbc);
        
        // Expiry Date - Using JSpinner
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Ngày hết hạn:"), gbc);
        gbc.gridx = 1;
        SpinnerDateModel expiryDateModel = new SpinnerDateModel();
        dateExpirySpinner = new JSpinner(expiryDateModel);
        JSpinner.DateEditor expiryEditor = new JSpinner.DateEditor(dateExpirySpinner, "dd/MM/yyyy");
        dateExpirySpinner.setEditor(expiryEditor);
        panel.add(dateExpirySpinner, gbc);
        
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
        
        if (cardId != null) {
            loadCardData();
        }
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
    
    private void loadCardData() {
        String query = "SELECT * FROM the_an_ninh WHERE id_the = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, cardId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                txtCardNumber.setText(rs.getString("so_the"));
                if (rs.getDate("ngay_cap") != null) {
                    dateIssueSpinner.setValue(rs.getDate("ngay_cap"));
                }
                if (rs.getDate("ngay_het_han") != null) {
                    dateExpirySpinner.setValue(rs.getDate("ngay_het_han"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void save() {
        if (txtCardNumber.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số thẻ!");
            return;
        }
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            Date issueDate = (Date) dateIssueSpinner.getValue();
            Date expiryDate = (Date) dateExpirySpinner.getValue();
            
            if (cardId == null) {
                String apartmentStr = (String) cboApartment.getSelectedItem();
                String residentStr = (String) cboResident.getSelectedItem();
                
                int apartmentId = Integer.parseInt(apartmentStr.split(" - ")[0]);
                int residentId = Integer.parseInt(residentStr.split(" - ")[0]);
                
                String query = "INSERT INTO the_an_ninh (so_the, id_cu_dan, id_can_ho, ngay_cap, ngay_het_han, trang_thai) VALUES (?, ?, ?, ?, ?, 'hoat_dong')";
                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setString(1, txtCardNumber.getText().trim());
                    pstmt.setInt(2, residentId);
                    pstmt.setInt(3, apartmentId);
                    pstmt.setDate(4, new java.sql.Date(issueDate.getTime()));
                    pstmt.setDate(5, expiryDate != null ? new java.sql.Date(expiryDate.getTime()) : null);
                    pstmt.executeUpdate();
                }
            } else {
                String query = "UPDATE the_an_ninh SET ngay_het_han = ? WHERE id_the = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setDate(1, expiryDate != null ? new java.sql.Date(expiryDate.getTime()) : null);
                    pstmt.setInt(2, cardId);
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