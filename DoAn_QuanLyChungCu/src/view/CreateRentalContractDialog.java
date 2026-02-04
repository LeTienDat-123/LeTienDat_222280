package view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Date;
import java.time.LocalDate;
import model.*;
import dao.*;

public class CreateRentalContractDialog extends JDialog {
    private Apartment apartment;
    private JTextField txtFullName, txtPhone, txtEmail, txtUsername;
    private JPasswordField txtPassword;
    private JTextField txtMonthlyRent, txtDeposit, txtContractDuration;
    private JTextArea txtNotes;
    private JButton btnCreate, btnCancel;
    private boolean success = false;

    public CreateRentalContractDialog(Frame parent, Apartment apartment) {
        super(parent, "Tạo hợp đồng thuê - Căn " + apartment.getNumber(), true);
        this.apartment = apartment;
        
        setSize(600, 700);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        
        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        // Title
        JLabel titleLabel = new JLabel("TẠO HỢP ĐỒNG THUÊ");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(25, 103, 210));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(10));
        
        // Apartment Info
        JPanel aptInfoPanel = createApartmentInfoPanel();
        mainPanel.add(aptInfoPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Tenant Info Section
        JLabel tenantLabel = new JLabel("THÔNG TIN NGƯỜI THUÊ");
        tenantLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tenantLabel.setForeground(new Color(33, 33, 33));
        mainPanel.add(tenantLabel);
        mainPanel.add(Box.createVerticalStrut(10));
        
        mainPanel.add(createFieldPanel("Họ tên:", txtFullName = new JTextField()));
        mainPanel.add(createFieldPanel("Số điện thoại:", txtPhone = new JTextField()));
        mainPanel.add(createFieldPanel("Email:", txtEmail = new JTextField()));
        mainPanel.add(createFieldPanel("Tên đăng nhập:", txtUsername = new JTextField()));
        mainPanel.add(createFieldPanel("Mật khẩu:", txtPassword = new JPasswordField()));
        
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Contract Info Section
        JLabel contractLabel = new JLabel("THÔNG TIN HỢP ĐỒNG");
        contractLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        contractLabel.setForeground(new Color(33, 33, 33));
        mainPanel.add(contractLabel);
        mainPanel.add(Box.createVerticalStrut(10));
        
        mainPanel.add(createFieldPanel("Tiền thuê/tháng (VNĐ):", txtMonthlyRent = new JTextField()));
        mainPanel.add(createFieldPanel("Tiền cọc/đặt cọc (VNĐ):", txtDeposit = new JTextField()));
        mainPanel.add(createFieldPanel("Thời hạn (tháng):", txtContractDuration = new JTextField()));
        
        JPanel notesPanel = new JPanel(new BorderLayout(5, 5));
        notesPanel.setBackground(Color.WHITE);
        notesPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        JLabel notesLabel = new JLabel("Ghi chú:");
        notesLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtNotes = new JTextArea(3, 20);
        txtNotes.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtNotes.setBorder(new LineBorder(new Color(200, 200, 200)));
        txtNotes.setLineWrap(true);
        txtNotes.setWrapStyleWord(true);
        JScrollPane notesScroll = new JScrollPane(txtNotes);
        notesPanel.add(notesLabel, BorderLayout.NORTH);
        notesPanel.add(notesScroll, BorderLayout.CENTER);
        mainPanel.add(notesPanel);
        
        // Scroll pane for main panel
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
        
        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(new Color(240, 242, 245));
        
        btnCreate = createStyledButton("Tạo hợp đồng", new Color(76, 175, 80));
        btnCancel = createStyledButton("Hủy", new Color(158, 158, 158));
        
        btnCreate.addActionListener(e -> handleCreateContract());
        btnCancel.addActionListener(e -> dispose());
        
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnCreate);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Default values
        txtMonthlyRent.setText("5000000");
        txtDeposit.setText("10000000");
        txtContractDuration.setText("12");
    }
    
    private JPanel createApartmentInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 5));
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(new CompoundBorder(
            new LineBorder(new Color(25, 103, 210)),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        panel.add(createInfoLabel("Căn hộ:", "Căn " + apartment.getNumber()));
        panel.add(createInfoLabel("Diện tích:", apartment.getArea() + " m²"));
        panel.add(createInfoLabel("Phòng ngủ:", apartment.getBedrooms() + " phòng"));
        panel.add(createInfoLabel("Phòng tắm:", apartment.getBathrooms() + " phòng"));
        
        return panel;
    }
    
    private JPanel createInfoLabel(String label, String value) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(240, 248, 255));
        
        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblValue.setForeground(new Color(25, 103, 210));
        
        panel.add(lblLabel);
        panel.add(lblValue);
        
        return panel;
    }
    
    private JPanel createFieldPanel(String label, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setPreferredSize(new Dimension(150, 25));
        
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        field.setBorder(new CompoundBorder(
            new LineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 10, 5, 10)
        ));
        
        panel.add(lbl, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setPreferredSize(new Dimension(150, 35));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
    
    private void handleCreateContract() {
        // Validation
        if (!validateInputs()) {
            return;
        }
        
        try {
            // 1. Kiểm tra username đã tồn tại chưa
            String username = txtUsername.getText().trim();
            User existingUser = findUserByUsername(username);
            
            int residentId;
            if (existingUser != null) {
                // Người dùng đã tồn tại
                int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Tên đăng nhập đã tồn tại cho: " + existingUser.getFullName() + 
                    "\nBạn có muốn sử dụng người dùng này không?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION
                );
                
                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }
                residentId = existingUser.getId();
            } else {
                // 2. Tạo người dùng mới
                residentId = createNewUser();
                if (residentId == -1) {
                    JOptionPane.showMessageDialog(this, 
                        "Lỗi khi tạo người dùng!", 
                        "Lỗi", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            // 3. Tạo hợp đồng thuê
            double monthlyRent = Double.parseDouble(txtMonthlyRent.getText().trim());
            double deposit = Double.parseDouble(txtDeposit.getText().trim());
            int duration = Integer.parseInt(txtContractDuration.getText().trim());
            
            Date startDate = Date.valueOf(LocalDate.now());
            Date endDate = Date.valueOf(LocalDate.now().plusMonths(duration));
            
            int contractId = RentalContractDAO.createRentalContract(
                apartment.getId(),
                residentId,
                startDate,
                endDate,
                monthlyRent,
                deposit,
                txtNotes.getText().trim()
            );
            
            if (contractId == -1) {
                JOptionPane.showMessageDialog(this, 
                    "Lỗi khi tạo hợp đồng!", 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // 4. Tạo hộ gia đình (nếu chưa có)
            if (!HouseholdDAO.hasHousehold(apartment.getId())) {
                int householdId = HouseholdDAO.createHousehold(
                    apartment.getId(),
                    residentId,
                    1, // Số nhân khẩu ban đầu
                    txtPhone.getText().trim(),
                    txtEmail.getText().trim()
                );
                
                if (householdId == -1) {
                    System.out.println("⚠ Cảnh báo: Không thể tạo hộ gia đình");
                }
            }
            
            // 5. Cập nhật trạng thái căn hộ
            if (ApartmentDAO.updateApartmentStatus(apartment.getId(), "co_nguoi_o")) {
                // 6. Tạo hóa đơn định kỳ cho tháng hiện tại
                LocalDate now = LocalDate.now();
                InvoiceDAO.createMonthlyInvoicesForApartment(
                    apartment.getId(),
                    now.getMonthValue(),
                    now.getYear()
                );
                
                success = true;
                
                JOptionPane.showMessageDialog(this,
                    "✓ Tạo hợp đồng thành công!\n\n" +
                    "Mã hợp đồng: #" + contractId + "\n" +
                    "Người thuê: " + txtFullName.getText() + "\n" +
                    "Bắt đầu: " + startDate + "\n" +
                    "Kết thúc: " + endDate,
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
                
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Lỗi khi cập nhật trạng thái căn hộ!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng nhập đúng định dạng số!",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Lỗi: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private boolean validateInputs() {
        if (txtFullName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập họ tên!");
            txtFullName.requestFocus();
            return false;
        }
        
        if (txtPhone.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại!");
            txtPhone.requestFocus();
            return false;
        }
        
        if (txtUsername.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên đăng nhập!");
            txtUsername.requestFocus();
            return false;
        }
        
        if (txtPassword.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mật khẩu!");
            txtPassword.requestFocus();
            return false;
        }
        
        if (txtMonthlyRent.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tiền thuê!");
            txtMonthlyRent.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private User findUserByUsername(String username) {
        // Tìm user bằng cách query database
        // Giả sử đã có method trong UserDAO
        // return UserDAO.getUserByUsername(username);
        return null; // Placeholder
    }
    
    private int createNewUser() {
        String query = "INSERT INTO nguoi_dung (ten_dang_nhap, mat_khau, ho_ten, email, " +
                      "so_dien_thoai, vai_tro) VALUES (?, ?, ?, ?, ?, 'cu_dan')";
        
        try (java.sql.Connection conn = config.DatabaseConnection.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(query, 
                 java.sql.Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, txtUsername.getText().trim());
            pstmt.setString(2, new String(txtPassword.getPassword()));
            pstmt.setString(3, txtFullName.getText().trim());
            pstmt.setString(4, txtEmail.getText().trim());
            pstmt.setString(5, txtPhone.getText().trim());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (java.sql.ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    public boolean isSuccess() {
        return success;
    }
}