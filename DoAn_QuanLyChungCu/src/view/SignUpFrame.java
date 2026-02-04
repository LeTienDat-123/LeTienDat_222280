package view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import dao.UserDAO;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;


public class SignUpFrame extends JFrame {
    private JTextField txtFullName;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JTextField txtPhone;
    private JTextField txtEmail;
    private JButton btnSignUp;
    private JButton btnCancel;
    private JLabel lblErrorMessage;

    public SignUpFrame() {
        setTitle("ĐĂNG KÝ");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 750);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(76, 175, 80),
                    getWidth(), getHeight(), new Color(56, 142, 60)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());

        // Left Panel - Info
        JPanel leftPanel = createLeftPanel();
        mainPanel.add(leftPanel, BorderLayout.WEST);

        // Right Panel - Sign Up Form
        JPanel rightPanel = createRightPanel();
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
        setVisible(true);
    }

    // ==================== LEFT PANEL ====================
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(76, 175, 80),
                    getWidth(), getHeight(), new Color(56, 142, 60)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                g2d.setColor(new Color(255, 255, 255, 10));
                g2d.fillOval(-100, -100, 300, 300);
                g2d.fillOval(250, 400, 250, 250);
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(450, 0));

        panel.add(Box.createVerticalStrut(80));

        // Logo
        JLabel logoLabel = new JLabel();
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        try {
            String imagePath = "src/Assets/logo.jpg";
            BufferedImage image = ImageIO.read(new File(imagePath));

            int size = 120; // kích thước hình tròn

            // Resize ảnh
            Image scaledImage = image.getScaledInstance(size, size, Image.SCALE_SMOOTH);

            // Tạo ảnh tròn
            BufferedImage circleImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = circleImage.createGraphics();

            // Bật khử răng cưa
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Cắt hình tròn
            g2.setClip(new Ellipse2D.Float(0, 0, size, size));
            g2.drawImage(scaledImage, 0, 0, size, size, null);

            g2.dispose();

            logoLabel.setIcon(new ImageIcon(circleImage));

        } catch (IOException e) {
            // Fallback nếu lỗi
            logoLabel.setText("🏢");
            logoLabel.setFont(new Font("Arial", Font.PLAIN, 80));
            logoLabel.setForeground(Color.WHITE);
            System.out.println("Lỗi load logo: " + e.getMessage());
        }


        panel.add(logoLabel);

        panel.add(Box.createVerticalStrut(40));

        // Title
        JLabel titleLabel = new JLabel("ĐĂNG KÝ TÀI KHOẢN");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);

        panel.add(Box.createVerticalStrut(15));

        // Subtitle
        JLabel subtitleLabel = new JLabel("Tạo tài khoản mới để bắt đầu");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(200, 255, 200));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(subtitleLabel);

        panel.add(Box.createVerticalGlue());

        // Benefits
        JLabel benefitsLabel = new JLabel("Lợi ích của việc đăng ký:");
        benefitsLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        benefitsLabel.setForeground(Color.WHITE);
        benefitsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(benefitsLabel);

        panel.add(Box.createVerticalStrut(10));

        String[] benefits = {"Quản lý thông tin cá nhân", "Theo dõi đơn yêu cầu", "Nhận thông báo cập nhật"};
        for (String benefit : benefits) {
            JLabel benefitLabel = new JLabel(benefit);
            benefitLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            benefitLabel.setForeground(new Color(220, 255, 220));
            benefitLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(benefitLabel);
            panel.add(Box.createVerticalStrut(8));
        }

        panel.add(Box.createVerticalGlue());

        // Footer
        JLabel footerLabel = new JLabel("© 2026 - Tất cả quyền được bảo lưu");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        footerLabel.setForeground(new Color(150, 220, 150));
        footerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(footerLabel);
        panel.add(Box.createVerticalStrut(20));

        return panel;
    }

    // ==================== RIGHT PANEL ====================
    private JPanel createRightPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(240, 242, 245));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(50, 60, 50, 60));

        // Title
        JLabel titleLabel = new JLabel("Tạo tài khoản mới");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(new Color(33, 33, 33));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);

        panel.add(Box.createVerticalStrut(5));

        JLabel subLabel = new JLabel("Điền thông tin dưới đây để tạo tài khoản");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subLabel.setForeground(new Color(117, 117, 117));
        subLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(subLabel);

        panel.add(Box.createVerticalStrut(25));

        // Error message label
        lblErrorMessage = new JLabel();
        lblErrorMessage.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblErrorMessage.setForeground(new Color(244, 67, 54));
        lblErrorMessage.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblErrorMessage);
        panel.add(Box.createVerticalStrut(10));

        // Full Name field
        addLabeledTextField(panel, "Họ tên", "Nguyễn Văn A");
        panel.add(Box.createVerticalStrut(15));

        // Username field
        addLabeledTextField(panel, "Tên đăng nhập", "username");
        panel.add(Box.createVerticalStrut(15));

        // Email field
        addLabeledTextField(panel, "Email", "email@example.com");
        panel.add(Box.createVerticalStrut(15));

        // Phone field
        addLabeledTextField(panel, "Số điện thoại", "0123456789");
        panel.add(Box.createVerticalStrut(15));

        // Password field
        JPanel passwordLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        passwordLabelPanel.setBackground(new Color(240, 242, 245));
        passwordLabelPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        
        JLabel passwordLabel = new JLabel("Mật khẩu");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passwordLabel.setForeground(new Color(76, 175, 80));
        
        passwordLabelPanel.add(passwordLabel);
        panel.add(passwordLabelPanel);
        panel.add(Box.createVerticalStrut(6));

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtPassword.setBorder(new RoundedBorder(10, new Color(200, 200, 200)));
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        txtPassword.setMargin(new Insets(12, 18, 12, 18));
        panel.add(txtPassword);
        panel.add(Box.createVerticalStrut(15));

        // Confirm Password field
        JPanel confirmLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        confirmLabelPanel.setBackground(new Color(240, 242, 245));
        confirmLabelPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        
        JLabel confirmLabel = new JLabel("Xác nhận mật khẩu");
        confirmLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        confirmLabel.setForeground(new Color(76, 175, 80));
        
        confirmLabelPanel.add(confirmLabel);
        panel.add(confirmLabelPanel);
        panel.add(Box.createVerticalStrut(6));

        txtConfirmPassword = new JPasswordField();
        txtConfirmPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtConfirmPassword.setBorder(new RoundedBorder(10, new Color(200, 200, 200)));
        txtConfirmPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        txtConfirmPassword.setMargin(new Insets(12, 18, 12, 18));
        panel.add(txtConfirmPassword);

        panel.add(Box.createVerticalStrut(25));

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 18, 0));
        buttonsPanel.setBackground(new Color(240, 242, 245));
        buttonsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        btnSignUp = createStyledButton("Đăng ký", new Color(76, 175, 80));
        btnCancel = createStyledButton("Hủy", new Color(200, 200, 200));

        buttonsPanel.add(btnSignUp);
        buttonsPanel.add(btnCancel);

        panel.add(buttonsPanel);

        panel.add(Box.createVerticalStrut(15));

        // Login link
        JPanel loginPanel = new JPanel();
        loginPanel.setBackground(new Color(240, 242, 245));
        loginPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel alreadyLabel = new JLabel("Đã có tài khoản?");
        alreadyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        alreadyLabel.setForeground(new Color(117, 117, 117));

        JLabel loginLink = new JLabel("Đăng nhập");
        loginLink.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        loginLink.setForeground(new Color(76, 175, 80));
        loginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));

        loginLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new LoginFrame();
                dispose();
            }
        });

        loginPanel.add(alreadyLabel);
        loginPanel.add(loginLink);

        panel.add(loginPanel);

        panel.add(Box.createVerticalGlue());

        // Event listeners
        setupEventListeners();

        return panel;
    }

    // ==================== ADD LABELED TEXT FIELD ====================
    private void addLabeledTextField(JPanel panel, String label, String placeholder) {
        // Tạo panel cho label để căn trái
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        labelPanel.setBackground(new Color(240, 242, 245));
        labelPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(76, 175, 80));
        
        labelPanel.add(lbl);
        panel.add(labelPanel);

        panel.add(Box.createVerticalStrut(6));

        JTextField txt = new JTextField(placeholder);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt.setBorder(new RoundedBorder(10, new Color(200, 200, 200)));
        txt.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        txt.setMargin(new Insets(12, 18, 12, 18));
        txt.setForeground(new Color(150, 150, 150));

        txt.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txt.getText().equals(placeholder)) {
                    txt.setText("");
                    txt.setForeground(new Color(33, 33, 33));
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txt.getText().isEmpty()) {
                    txt.setText(placeholder);
                    txt.setForeground(new Color(150, 150, 150));
                }
            }
        });

        panel.add(txt);

        // Gán biến dựa trên label
        if (label.equals("Họ tên")) {
            txtFullName = txt;
        } else if (label.equals("Tên đăng nhập")) {
            txtUsername = txt;
        } else if (label.equals("Email")) {
            txtEmail = txt;
        } else if (label.equals("Số điện thoại")) {
            txtPhone = txt;
        }
    }

    // ==================== CREATE STYLED BUTTON ====================
    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                if (getModel().isPressed()) {
                    g.setColor(new Color(
                        Math.max(0, bgColor.getRed() - 25),
                        Math.max(0, bgColor.getGreen() - 25),
                        Math.max(0, bgColor.getBlue() - 25)
                    ));
                } else if (getModel().isRollover()) {
                    g.setColor(new Color(
                        Math.min(255, bgColor.getRed() + 25),
                        Math.min(255, bgColor.getGreen() + 25),
                        Math.min(255, bgColor.getBlue() + 25)
                    ));
                } else {
                    g.setColor(bgColor);
                }
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setBorder(null);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(false);
        return btn;
    }

    // ==================== SETUP EVENT LISTENERS ====================
    private void setupEventListeners() {
        btnSignUp.addActionListener(e -> handleSignUp());
        btnCancel.addActionListener(e -> {
            new LoginFrame();
            dispose();
        });

        txtConfirmPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleSignUp();
                }
            }
        });
    }

    // ==================== HANDLE SIGN UP ====================
    private void handleSignUp() {
        // Lấy giá trị từ các trường
        String fullName = txtFullName.getText().trim();
        String username = txtUsername.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String password = new String(txtPassword.getPassword());
        String confirmPassword = new String(txtConfirmPassword.getPassword());

        // Xóa thông báo lỗi cũ
        lblErrorMessage.setText("");

        // Validation - kiểm tra họ tên
        if (fullName.isEmpty() || fullName.equals("Nguyễn Văn A")) {
            showError("Vui lòng nhập họ tên!");
            txtFullName.requestFocus();
            return;
        }

        // Validation - kiểm tra tên đăng nhập
        if (username.isEmpty() || username.equals("username")) {
            showError("Vui lòng nhập tên đăng nhập!");
            txtUsername.requestFocus();
            return;
        }

        // Validation - kiểm tra độ dài tên đăng nhập
        if (username.length() < 4) {
            showError("Tên đăng nhập phải có ít nhất 4 ký tự!");
            txtUsername.requestFocus();
            return;
        }

        // Validation - kiểm tra email
        if (email.isEmpty() || email.equals("email@example.com")) {
            showError("Vui lòng nhập email!");
            txtEmail.requestFocus();
            return;
        }

        // Validation - kiểm tra định dạng email
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("Email không hợp lệ!");
            txtEmail.requestFocus();
            return;
        }

        // Validation - kiểm tra số điện thoại
        if (phone.isEmpty() || phone.equals("0123456789")) {
            showError("Vui lòng nhập số điện thoại!");
            txtPhone.requestFocus();
            return;
        }

        // Validation - kiểm tra định dạng số điện thoại
        if (!phone.matches("^0\\d{9}$")) {
            showError("Số điện thoại phải có 10 số và bắt đầu bằng 0!");
            txtPhone.requestFocus();
            return;
        }

        // Validation - kiểm tra mật khẩu
        if (password.isEmpty()) {
            showError("Vui lòng nhập mật khẩu!");
            txtPassword.requestFocus();
            return;
        }

        // Validation - kiểm tra độ dài mật khẩu
        if (password.length() < 6) {
            showError("Mật khẩu phải có ít nhất 6 ký tự!");
            txtPassword.requestFocus();
            return;
        }

        // Validation - kiểm tra xác nhận mật khẩu
        if (confirmPassword.isEmpty()) {
            showError("Vui lòng xác nhận mật khẩu!");
            txtConfirmPassword.requestFocus();
            return;
        }

        // Validation - kiểm tra khớp mật khẩu
        if (!password.equals(confirmPassword)) {
            showError("Mật khẩu xác nhận không khớp!");
            txtConfirmPassword.requestFocus();
            return;
        }

        // Kiểm tra tên đăng nhập đã tồn tại
        if (UserDAO.usernameExists(username)) {
            showError("Tên đăng nhập đã tồn tại! Vui lòng chọn tên khác.");
            txtUsername.requestFocus();
            return;
        }

        // Thực hiện đăng ký
        boolean success = UserDAO.registerUser(username, password, fullName, email, phone);

        if (success) {
            JOptionPane.showMessageDialog(this, 
                "Đăng ký thành công!\nVui lòng đăng nhập để tiếp tục.", 
                "Thành công", 
                JOptionPane.INFORMATION_MESSAGE);
            new LoginFrame();
            dispose();
        } else {
            showError("Đăng ký thất bại! Vui lòng thử lại sau.");
        }
    }

    // ==================== SHOW ERROR ====================
    private void showError(String message) {
        lblErrorMessage.setText("❌ " + message);
    }

    // ==================== ROUNDED BORDER ====================
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
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(5, 15, 5, 15);
        }
    }
}