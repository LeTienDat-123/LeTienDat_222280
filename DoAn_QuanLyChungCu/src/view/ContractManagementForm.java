package view;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.util.Date;
import config.DatabaseConnection;

public class ContractManagementForm extends JFrame {
    private JTable contractTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> cboStatusFilter;
    
    public ContractManagementForm() {
        setTitle("Quản lý Hợp đồng thuê");
        setSize(1300, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 242, 245));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createTablePanel(), BorderLayout.CENTER);
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        loadContractData();
        setVisible(true);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(240, 242, 245));
        
        JLabel titleLabel = new JLabel("QUẢN LÝ HỢP ĐỒNG THUÊ");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(25, 103, 210));
        
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterPanel.setBackground(new Color(240, 242, 245));
        
        cboStatusFilter = new JComboBox<>(new String[]{"Tất cả", "Đang hoạt động", "Hết hạn", "Đã hủy"});
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
        
        String[] columns = {"ID", "Căn hộ", "Cư dân", "Ngày bắt đầu", "Ngày kết thúc", "Tiền thuê/tháng", "Tiền cọc", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        contractTable = new JTable(tableModel);
        contractTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        contractTable.setRowHeight(30);
        contractTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        contractTable.getTableHeader().setBackground(new Color(25, 103, 210));
        contractTable.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(contractTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(new Color(240, 242, 245));
        
        JButton btnAdd = createButton("Tạo hợp đồng", new Color(76, 175, 80));
        JButton btnEdit = createButton("Sửa", new Color(255, 152, 0));
        JButton btnTerminate = createButton("Chấm dứt", new Color(244, 67, 54));
        JButton btnRenew = createButton("Gia hạn", new Color(103, 58, 183));
        JButton btnRefresh = createButton("Làm mới", new Color(33, 150, 243));
        
        btnAdd.addActionListener(e -> addContract());
        btnEdit.addActionListener(e -> editContract());
        btnTerminate.addActionListener(e -> terminateContract());
        btnRenew.addActionListener(e -> renewContract());
        btnRefresh.addActionListener(e -> loadContractData());
        
        panel.add(btnAdd);
        panel.add(btnEdit);
        panel.add(btnTerminate);
        panel.add(btnRenew);
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
    
    private void loadContractData() {
        tableModel.setRowCount(0);
        String query = "SELECT h.id_hop_dong, c.so_can_ho, u.ho_ten, h.ngay_bat_dau, h.ngay_ket_thuc, " +
                      "h.tien_thue_hang_thang, h.tien_canh_bao_dao, h.trang_thai " +
                      "FROM hop_dong_thue h " +
                      "JOIN can_ho c ON h.id_can_ho = c.id_can_ho " +
                      "JOIN nguoi_dung u ON h.id_cu_dan = u.id_nguoi_dung " +
                      "ORDER BY h.id_hop_dong DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id_hop_dong"),
                    rs.getString("so_can_ho"),
                    rs.getString("ho_ten"),
                    rs.getDate("ngay_bat_dau"),
                    rs.getDate("ngay_ket_thuc"),
                    String.format("%,.0f đ", rs.getDouble("tien_thue_hang_thang")),
                    String.format("%,.0f đ", rs.getDouble("tien_canh_bao_dao")),
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
            loadContractData();
            return;
        }
        
        String statusValue = getStatusValue(selectedStatus);
        tableModel.setRowCount(0);
        
        String query = "SELECT h.id_hop_dong, c.so_can_ho, u.ho_ten, h.ngay_bat_dau, h.ngay_ket_thuc, " +
                      "h.tien_thue_hang_thang, h.tien_canh_bao_dao, h.trang_thai " +
                      "FROM hop_dong_thue h " +
                      "JOIN can_ho c ON h.id_can_ho = c.id_can_ho " +
                      "JOIN nguoi_dung u ON h.id_cu_dan = u.id_nguoi_dung " +
                      "WHERE h.trang_thai = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, statusValue);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id_hop_dong"),
                    rs.getString("so_can_ho"),
                    rs.getString("ho_ten"),
                    rs.getDate("ngay_bat_dau"),
                    rs.getDate("ngay_ket_thuc"),
                    String.format("%,.0f đ", rs.getDouble("tien_thue_hang_thang")),
                    String.format("%,.0f đ", rs.getDouble("tien_canh_bao_dao")),
                    getStatusDisplay(rs.getString("trang_thai"))
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi lọc dữ liệu: " + e.getMessage());
        }
    }
    
    private void addContract() {
        ContractDialog dialog = new ContractDialog(this, "Tạo hợp đồng thuê mới", null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            loadContractData();
        }
    }
    
    private void editContract() {
        int selectedRow = contractTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hợp đồng cần sửa!");
            return;
        }
        
        int contractId = (int) tableModel.getValueAt(selectedRow, 0);
        ContractDialog dialog = new ContractDialog(this, "Chỉnh sửa hợp đồng", contractId);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            loadContractData();
        }
    }
    
    private void terminateContract() {
        int selectedRow = contractTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hợp đồng!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn chấm dứt hợp đồng này?", 
            "Xác nhận", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int contractId = (int) tableModel.getValueAt(selectedRow, 0);
            String query = "UPDATE hop_dong_thue SET trang_thai = 'het_han' WHERE id_hop_dong = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                
                pstmt.setInt(1, contractId);
                pstmt.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Chấm dứt hợp đồng thành công!");
                loadContractData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
            }
        }
    }
    
    private void renewContract() {
        int selectedRow = contractTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hợp đồng!");
            return;
        }
        
        String endDateStr = JOptionPane.showInputDialog(this, "Nhập ngày kết thúc mới (yyyy-MM-dd):");
        if (endDateStr != null && !endDateStr.trim().isEmpty()) {
            try {
                int contractId = (int) tableModel.getValueAt(selectedRow, 0);
                String query = "UPDATE hop_dong_thue SET ngay_ket_thuc = ?, trang_thai = 'hoat_dong' WHERE id_hop_dong = ?";
                
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(query)) {
                    
                    pstmt.setDate(1, java.sql.Date.valueOf(endDateStr));
                    pstmt.setInt(2, contractId);
                    pstmt.executeUpdate();
                    
                    JOptionPane.showMessageDialog(this, "Gia hạn hợp đồng thành công!");
                    loadContractData();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
            }
        }
    }
    
    private String getStatusDisplay(String status) {
        switch (status) {
            case "hoat_dong": return "Đang hoạt động";
            case "het_han": return "Hết hạn";
            case "huy": return "Đã hủy";
            default: return status;
        }
    }
    
    private String getStatusValue(String display) {
        switch (display) {
            case "Đang hoạt động": return "hoat_dong";
            case "Hết hạn": return "het_han";
            case "Đã hủy": return "huy";
            default: return "";
        }
    }
}

// Dialog for Add/Edit Contract
class ContractDialog extends JDialog {
    private JComboBox<String> cboApartment, cboResident;
    private JSpinner dateStartSpinner, dateEndSpinner;
    private JTextField txtRent, txtDeposit, txtNotes;
    private boolean confirmed = false;
    private Integer contractId;
    
    public ContractDialog(JFrame parent, String title, Integer contractId) {
        super(parent, title, true);
        this.contractId = contractId;
        setSize(550, 500);
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
        
        // Start Date - Using JSpinner
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Ngày bắt đầu:"), gbc);
        gbc.gridx = 1;
        SpinnerDateModel startDateModel = new SpinnerDateModel();
        dateStartSpinner = new JSpinner(startDateModel);
        JSpinner.DateEditor startEditor = new JSpinner.DateEditor(dateStartSpinner, "dd/MM/yyyy");
        dateStartSpinner.setEditor(startEditor);
        panel.add(dateStartSpinner, gbc);
        
        // End Date - Using JSpinner
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Ngày kết thúc:"), gbc);
        gbc.gridx = 1;
        SpinnerDateModel endDateModel = new SpinnerDateModel();
        dateEndSpinner = new JSpinner(endDateModel);
        JSpinner.DateEditor endEditor = new JSpinner.DateEditor(dateEndSpinner, "dd/MM/yyyy");
        dateEndSpinner.setEditor(endEditor);
        panel.add(dateEndSpinner, gbc);
        
        // Rent
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Tiền thuê/tháng:"), gbc);
        gbc.gridx = 1;
        txtRent = new JTextField(20);
        panel.add(txtRent, gbc);
        
        // Deposit
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Tiền cọc:"), gbc);
        gbc.gridx = 1;
        txtDeposit = new JTextField(20);
        panel.add(txtDeposit, gbc);
        
        // Notes
        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(new JLabel("Ghi chú:"), gbc);
        gbc.gridx = 1;
        txtNotes = new JTextField(20);
        panel.add(txtNotes, gbc);
        
        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnSave = new JButton("💾 Lưu");
        JButton btnCancel = new JButton("❌ Hủy");
        
        btnSave.addActionListener(e -> save());
        btnCancel.addActionListener(e -> dispose());
        
        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);
        
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        panel.add(btnPanel, gbc);
        
        add(panel);
        
        if (contractId != null) {
            loadContractData();
            cboApartment.setEnabled(false);
            cboResident.setEnabled(false);
        }
    }
    
    private void loadApartments() {
        String query = "SELECT id_can_ho, so_can_ho FROM can_ho WHERE trang_thai = 'trong'";
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
    
    private void loadContractData() {
        String query = "SELECT * FROM hop_dong_thue WHERE id_hop_dong = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, contractId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                if (rs.getDate("ngay_bat_dau") != null) {
                    dateStartSpinner.setValue(rs.getDate("ngay_bat_dau"));
                }
                if (rs.getDate("ngay_ket_thuc") != null) {
                    dateEndSpinner.setValue(rs.getDate("ngay_ket_thuc"));
                }
                txtRent.setText(String.valueOf(rs.getDouble("tien_thue_hang_thang")));
                txtDeposit.setText(String.valueOf(rs.getDouble("tien_canh_bao_dao")));
                txtNotes.setText(rs.getString("ghi_chu"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void save() {
        try {
            double rent = Double.parseDouble(txtRent.getText().trim());
            double deposit = Double.parseDouble(txtDeposit.getText().trim());
            
            Date startDate = (Date) dateStartSpinner.getValue();
            Date endDate = (Date) dateEndSpinner.getValue();
            
            if (startDate == null || endDate == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày!");
                return;
            }
            
            try (Connection conn = DatabaseConnection.getConnection()) {
                if (contractId == null) {
                    String apartmentStr = (String) cboApartment.getSelectedItem();
                    String residentStr = (String) cboResident.getSelectedItem();
                    
                    int apartmentId = Integer.parseInt(apartmentStr.split(" - ")[0]);
                    int residentId = Integer.parseInt(residentStr.split(" - ")[0]);
                    
                    String query = "INSERT INTO hop_dong_thue (id_can_ho, id_cu_dan, ngay_bat_dau, ngay_ket_thuc, tien_thue_hang_thang, tien_canh_bao_dao, trang_thai, ghi_chu) VALUES (?, ?, ?, ?, ?, ?, 'hoat_dong', ?)";
                    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                        pstmt.setInt(1, apartmentId);
                        pstmt.setInt(2, residentId);
                        pstmt.setDate(3, new java.sql.Date(startDate.getTime()));
                        pstmt.setDate(4, new java.sql.Date(endDate.getTime()));
                        pstmt.setDouble(5, rent);
                        pstmt.setDouble(6, deposit);
                        pstmt.setString(7, txtNotes.getText().trim());
                        pstmt.executeUpdate();
                    }
                    
                    // Update apartment status
                    String updateQuery = "UPDATE can_ho SET trang_thai = 'co_nguoi_o' WHERE id_can_ho = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
                        pstmt.setInt(1, apartmentId);
                        pstmt.executeUpdate();
                    }
                } else {
                    String query = "UPDATE hop_dong_thue SET ngay_ket_thuc = ?, tien_thue_hang_thang = ?, ghi_chu = ? WHERE id_hop_dong = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                        pstmt.setDate(1, new java.sql.Date(endDate.getTime()));
                        pstmt.setDouble(2, rent);
                        pstmt.setString(3, txtNotes.getText().trim());
                        pstmt.setInt(4, contractId);
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