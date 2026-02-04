package view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.io.File;
import model.User;
import dao.UserDAO;

public class UserAccountFrame extends JFrame {
    private User currentUser;
    private JTextField txtFullName, txtEmail, txtPhone;
    private JLabel lblUsername, lblRole, lblAvatar;
    
    public UserAccountFrame(User user) {
        this.currentUser = user;
        
        setTitle("Quản Lý Tài Khoản Cá Nhân");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(240, 242, 245));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Content
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        tabbedPane.addTab("Thông Tin Cá Nhân", createProfileTab());
        tabbedPane.addTab("Đổi Mật Khẩu", createPasswordTab());
        tabbedPane.addTab("Hoạt Động", createActivityTab());
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        setContentPane(mainPanel);
        setVisible(true);
    }
    
    // ==================== HEADER ====================
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBackground(new Color(25, 103, 210));
        panel.setBorder(new RoundedBorder(10, new Color(25, 103, 210)));
        panel.setPreferredSize(new Dimension(0, 100));
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        // Avatar
        lblAvatar = new JLabel();
        lblAvatar.setPreferredSize(new Dimension(70, 70));
        
        try {
            String imagePath = "src/Assets/avt.jpg";
            BufferedImage image = ImageIO.read(new File(imagePath));
            Image scaledImage = image.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
            ImageIcon icon = new ImageIcon(scaledImage);
            lblAvatar.setIcon(icon);
            lblAvatar.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        } catch (Exception e) {
            lblAvatar.setText("?");
            lblAvatar.setFont(new Font("Arial", Font.BOLD, 32));
            lblAvatar.setForeground(Color.WHITE);
        }
        
        // Info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(25, 103, 210));
        
        JLabel titleLabel = new JLabel("QUẢN LÝ TÀI KHOẢN CÁ NHÂN");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel nameLabel = new JLabel(currentUser.getFullName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(Color.WHITE);
        
        JLabel roleLabel = new JLabel(currentUser.getRoleDisplay());
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        roleLabel.setForeground(new Color(200, 220, 255));
        
        infoPanel.add(titleLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(nameLabel);
        infoPanel.add(roleLabel);
        
        panel.add(lblAvatar, BorderLayout.WEST);
        panel.add(infoPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    // ==================== PROFILE TAB ====================
    private JPanel createProfileTab() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(15, 15));
        panel.setBackground(new Color(240, 242, 245));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new RoundedBorder(10, new Color(25, 103, 210)));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 20, 15, 20);
        gbc.weightx = 1;
        
        // Username (read-only)
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblUsernameLabel = new JLabel("Tên Đăng Nhập:");
        lblUsernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUsernameLabel.setForeground(new Color(100, 100, 100));
        formPanel.add(lblUsernameLabel, gbc);
        
        gbc.gridx = 1;
        lblUsername = new JLabel(currentUser.getUsername());
        lblUsername.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblUsername.setForeground(new Color(50, 50, 50));
        formPanel.add(lblUsername, gbc);
        
        // Full Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lblFullNameLabel = new JLabel("Họ Tên:");
        lblFullNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblFullNameLabel.setForeground(new Color(100, 100, 100));
        formPanel.add(lblFullNameLabel, gbc);
        
        gbc.gridx = 1;
        txtFullName = new JTextField(currentUser.getFullName(), 30);
        txtFullName.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtFullName.setBorder(new RoundedBorder(6, new Color(200, 200, 200)));
        txtFullName.setPreferredSize(new Dimension(300, 35));
        formPanel.add(txtFullName, gbc);
        
        // Email
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lblEmailLabel = new JLabel("Email:");
        lblEmailLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblEmailLabel.setForeground(new Color(100, 100, 100));
        formPanel.add(lblEmailLabel, gbc);
        
        gbc.gridx = 1;
        txtEmail = new JTextField(currentUser.getEmail() != null ? currentUser.getEmail() : "", 30);
        txtEmail.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtEmail.setBorder(new RoundedBorder(6, new Color(200, 200, 200)));
        txtEmail.setPreferredSize(new Dimension(300, 35));
        formPanel.add(txtEmail, gbc);
        
        // Phone
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel lblPhoneLabel = new JLabel("Số Điện Thoại:");
        lblPhoneLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPhoneLabel.setForeground(new Color(100, 100, 100));
        formPanel.add(lblPhoneLabel, gbc);
        
        gbc.gridx = 1;
        txtPhone = new JTextField(currentUser.getPhone(), 30);
        txtPhone.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtPhone.setBorder(new RoundedBorder(6, new Color(200, 200, 200)));
        txtPhone.setPreferredSize(new Dimension(300, 35));
        formPanel.add(txtPhone, gbc);
        
        // Role (read-only)
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel lblRoleLabel = new JLabel("Vai Trò:");
        lblRoleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblRoleLabel.setForeground(new Color(100, 100, 100));
        formPanel.add(lblRoleLabel, gbc);
        
        gbc.gridx = 1;
        lblRole = new JLabel(currentUser.getRoleDisplay());
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRole.setForeground(new Color(25, 103, 210));
        formPanel.add(lblRole, gbc);
        
        panel.add(formPanel, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttonPanel.setBackground(new Color(240, 242, 245));
        
        JButton btnSave = new JButton("Lưu Thay Đổi");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSave.setBackground(new Color(76, 175, 80));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.setPreferredSize(new Dimension(150, 40));
        btnSave.setBorder(new RoundedBorder(8, new Color(76, 175, 80)));
        btnSave.addActionListener(e -> saveProfile());
        
        JButton btnCancel = new JButton("Hủy");
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCancel.setBackground(new Color(244, 67, 54));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFocusPainted(false);
        btnCancel.setPreferredSize(new Dimension(120, 40));
        btnCancel.setBorder(new RoundedBorder(8, new Color(244, 67, 54)));
        btnCancel.addActionListener(e -> dispose());
        
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // ==================== PASSWORD TAB ====================
    private JPanel createPasswordTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(240, 242, 245));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new RoundedBorder(10, new Color(25, 103, 210)));
        formPanel.setPreferredSize(new Dimension(500, 300));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 20, 15, 20);
        gbc.weightx = 1;
        
        // Warning message
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel warningLabel = new JLabel("Vui lòng nhập mật khẩu hiện tại để đổi mật khẩu mới");
        warningLabel.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        warningLabel.setForeground(new Color(244, 67, 54));
        formPanel.add(warningLabel, gbc);
        gbc.gridwidth = 1;
        
        // Old password
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lblOldLabel = new JLabel("Mật Khẩu Hiện Tại:");
        lblOldLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblOldLabel.setForeground(new Color(100, 100, 100));
        formPanel.add(lblOldLabel, gbc);
        
        gbc.gridx = 1;
        JPasswordField txtOldPassword = new JPasswordField(20);
        txtOldPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtOldPassword.setBorder(new RoundedBorder(6, new Color(200, 200, 200)));
        txtOldPassword.setPreferredSize(new Dimension(300, 35));
        formPanel.add(txtOldPassword, gbc);
        
        // New password
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lblNewLabel = new JLabel("Mật Khẩu Mới:");
        lblNewLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblNewLabel.setForeground(new Color(100, 100, 100));
        formPanel.add(lblNewLabel, gbc);
        
        gbc.gridx = 1;
        JPasswordField txtNewPassword = new JPasswordField(20);
        txtNewPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtNewPassword.setBorder(new RoundedBorder(6, new Color(200, 200, 200)));
        txtNewPassword.setPreferredSize(new Dimension(300, 35));
        formPanel.add(txtNewPassword, gbc);
        
        // Confirm password
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel lblConfirmLabel = new JLabel("Xác Nhận Mật Khẩu:");
        lblConfirmLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblConfirmLabel.setForeground(new Color(100, 100, 100));
        formPanel.add(lblConfirmLabel, gbc);
        
        gbc.gridx = 1;
        JPasswordField txtConfirmPassword = new JPasswordField(20);
        txtConfirmPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtConfirmPassword.setBorder(new RoundedBorder(6, new Color(200, 200, 200)));
        txtConfirmPassword.setPreferredSize(new Dimension(300, 35));
        formPanel.add(txtConfirmPassword, gbc);
        
        panel.add(formPanel, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttonPanel.setBackground(new Color(240, 242, 245));
        
        JButton btnChangePassword = new JButton("Đổi Mật Khẩu");
        btnChangePassword.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnChangePassword.setBackground(new Color(25, 103, 210));
        btnChangePassword.setForeground(Color.WHITE);
        btnChangePassword.setFocusPainted(false);
        btnChangePassword.setPreferredSize(new Dimension(150, 40));
        btnChangePassword.setBorder(new RoundedBorder(8, new Color(25, 103, 210)));
        btnChangePassword.addActionListener(e -> {
            String oldPass = new String(txtOldPassword.getPassword());
            String newPass = new String(txtNewPassword.getPassword());
            String confirmPass = new String(txtConfirmPassword.getPassword());
            
            if (newPass.equals(confirmPass) && newPass.length() >= 6) {
                if (UserDAO.updatePassword(currentUser.getId(), newPass)) {
                    JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    txtOldPassword.setText("");
                    txtNewPassword.setText("");
                    txtConfirmPassword.setText("");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Mật khẩu không khớp hoặc quá ngắn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        buttonPanel.add(btnChangePassword);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // ==================== ACTIVITY TAB ====================
    private JPanel createActivityTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(240, 242, 245));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        textArea.setEditable(false);
        textArea.setText("LỊCH SỬ HOẠT ĐỘNG TÀI KHOẢN\n\n" +
            "Đăng nhập lần cuối: 02/02/2026 14:30\n" +
            "Địa chỉ IP: 192.168.1.100\n" +
            "Trình duyệt: Java Swing\n\n" +
            "Lịch sử hoạt động:\n" +
            "• 02/02/2026 14:30 - Đăng nhập\n" +
            "• 02/02/2026 14:25 - Xem báo cáo\n" +
            "• 02/02/2026 14:20 - Cập nhật hóa đơn\n" +
            "• 02/02/2026 14:15 - Thêm căn hộ mới\n");
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(new LineBorder(new Color(220, 220, 220)));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    // ==================== SAVE PROFILE ====================
    private void saveProfile() {
        String fullName = txtFullName.getText();
        String email = txtEmail.getText();
        String phone = txtPhone.getText();
        
        if (fullName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập họ tên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (UserDAO.updateUserInfo(currentUser.getId(), fullName, email, phone)) {
            JOptionPane.showMessageDialog(this, "Cập nhật thông tin thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            // Update current user object
            currentUser = UserDAO.getUserById(currentUser.getId());
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thông tin thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // ==================== BORDER CLASS ====================
    static class RoundedBorder extends AbstractBorder {
        private int radius;
        private Color color;
        
        public RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(5, 5, 5, 5);
        }
    }
}