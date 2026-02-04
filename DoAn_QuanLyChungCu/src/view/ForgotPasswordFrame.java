package view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import dao.UserDAO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class ForgotPasswordFrame extends JFrame {
    private JTextField txtUsername;
    private JTextField txtEmail;
    private JPasswordField txtNewPassword;
    private JPasswordField txtConfirmPassword;
    private JButton btnReset;
    private JButton btnCancel;
    private JLabel lblErrorMessage;
    private int currentStep = 1; // 1: Enter username, 2: Enter new password

    public ForgotPasswordFrame() {
        setTitle("KHÔI PHỤC MẬT KHẨU");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
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
                    0, 0, new Color(255, 152, 0),
                    getWidth(), getHeight(), new Color(245, 127, 23)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());

        // Left Panel - Info
        JPanel leftPanel = createLeftPanel();
        mainPanel.add(leftPanel, BorderLayout.WEST);

        // Right Panel - Reset Form
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
                    0, 0, new Color(255, 152, 0),
                    getWidth(), getHeight(), new Color(245, 127, 23)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                g2d.setColor(new Color(255, 255, 255, 10));
                g2d.fillOval(-100, -100, 300, 300);
                g2d.fillOval(250, 350, 250, 250);
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
        JLabel titleLabel = new JLabel("ĐẶT LẠI MẬT KHẨU");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);

        panel.add(Box.createVerticalStrut(15));

        // Subtitle
        JLabel subtitleLabel = new JLabel("Khôi phục quyền truy cập của bạn");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(255, 220, 180));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(subtitleLabel);

        panel.add(Box.createVerticalGlue());

        // Process steps
        JLabel stepsLabel = new JLabel("Quy trình:");
        stepsLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        stepsLabel.setForeground(Color.WHITE);
        stepsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(stepsLabel);

        panel.add(Box.createVerticalStrut(10));

        String[] steps = {"1️⃣ Nhập tên đăng nhập", "2️⃣ Xác minh thông tin", "3️⃣ Tạo mật khẩu mới"};
        for (String step : steps) {
            JLabel stepLabel = new JLabel(step);
            stepLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            stepLabel.setForeground(new Color(255, 230, 200));
            stepLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(stepLabel);
            panel.add(Box.createVerticalStrut(8));
        }

        panel.add(Box.createVerticalGlue());

        // Footer
        JLabel footerLabel = new JLabel("© 2026 - Tất cả quyền được bảo lưu");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        footerLabel.setForeground(new Color(220, 170, 130));
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
        panel.setBorder(new EmptyBorder(80, 60, 80, 60));

        // Title
        JLabel titleLabel = new JLabel("ĐẶT LẠI MẬT KHẨU");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(new Color(33, 33, 33));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);

        panel.add(Box.createVerticalStrut(8));

        JLabel subLabel = new JLabel("Nhập thông tin để xác minh và đặt lại mật khẩu");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subLabel.setForeground(new Color(117, 117, 117));
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(subLabel);

        panel.add(Box.createVerticalStrut(35));

        // Error message label
        lblErrorMessage = new JLabel();
        lblErrorMessage.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblErrorMessage.setForeground(new Color(244, 67, 54));
        lblErrorMessage.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblErrorMessage);
        panel.add(Box.createVerticalStrut(12));

        // Username field - ĐÃ ĐIỀU CHỈNH
        JPanel usernameLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        usernameLabelPanel.setBackground(new Color(240, 242, 245));
        usernameLabelPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        
        JLabel usernameLabel = new JLabel("Tên đăng nhập");
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        usernameLabel.setForeground(new Color(255, 152, 0));
        
        usernameLabelPanel.add(usernameLabel);
        panel.add(usernameLabelPanel);

        panel.add(Box.createVerticalStrut(6));

        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtUsername.setBorder(new RoundedBorder(10, new Color(200, 200, 200)));
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        txtUsername.setMargin(new Insets(12, 18, 12, 18));
        panel.add(txtUsername);

        panel.add(Box.createVerticalStrut(20));

        // Email field - ĐÃ ĐIỀU CHỈNH
        JPanel emailLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        emailLabelPanel.setBackground(new Color(240, 242, 245));
        emailLabelPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        emailLabel.setForeground(new Color(255, 152, 0));
        
        emailLabelPanel.add(emailLabel);
        panel.add(emailLabelPanel);

        panel.add(Box.createVerticalStrut(6));

        txtEmail = new JTextField();
        txtEmail.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtEmail.setBorder(new RoundedBorder(10, new Color(200, 200, 200)));
        txtEmail.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        txtEmail.setMargin(new Insets(12, 18, 12, 18));
        panel.add(txtEmail);

        panel.add(Box.createVerticalStrut(20));

        // New password field - ĐÃ ĐIỀU CHỈNH
        JPanel newPasswordLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        newPasswordLabelPanel.setBackground(new Color(240, 242, 245));
        newPasswordLabelPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        
        JLabel newPasswordLabel = new JLabel("Mật khẩu mới");
        newPasswordLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        newPasswordLabel.setForeground(new Color(255, 152, 0));
        
        newPasswordLabelPanel.add(newPasswordLabel);
        panel.add(newPasswordLabelPanel);

        panel.add(Box.createVerticalStrut(6));

        txtNewPassword = new JPasswordField();
        txtNewPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtNewPassword.setBorder(new RoundedBorder(10, new Color(200, 200, 200)));
        txtNewPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        txtNewPassword.setMargin(new Insets(12, 18, 12, 18));
        panel.add(txtNewPassword);

        panel.add(Box.createVerticalStrut(20));

        // Confirm password field - ĐÃ ĐIỀU CHỈNH
        JPanel confirmLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        confirmLabelPanel.setBackground(new Color(240, 242, 245));
        confirmLabelPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        
        JLabel confirmLabel = new JLabel("Xác nhận mật khẩu");
        confirmLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        confirmLabel.setForeground(new Color(255, 152, 0));
        
        confirmLabelPanel.add(confirmLabel);
        panel.add(confirmLabelPanel);

        panel.add(Box.createVerticalStrut(6));

        txtConfirmPassword = new JPasswordField();
        txtConfirmPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtConfirmPassword.setBorder(new RoundedBorder(10, new Color(200, 200, 200)));
        txtConfirmPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        txtConfirmPassword.setMargin(new Insets(12, 18, 12, 18));
        panel.add(txtConfirmPassword);

        panel.add(Box.createVerticalStrut(30));

        // Buttons panel - ĐÃ ĐIỀU CHỈNH
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 18, 0));
        buttonsPanel.setBackground(new Color(240, 242, 245));
        buttonsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        btnReset = createStyledButton("Đặt lại mật khẩu", new Color(255, 152, 0));
        btnCancel = createStyledButton("Hủy", new Color(200, 200, 200));

        buttonsPanel.add(btnReset);
        buttonsPanel.add(btnCancel);

        panel.add(buttonsPanel);

        panel.add(Box.createVerticalStrut(15));

        // Login link
        JPanel loginPanel = new JPanel();
        loginPanel.setBackground(new Color(240, 242, 245));
        loginPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel backLabel = new JLabel("Nhớ mật khẩu?");
        backLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        backLabel.setForeground(new Color(117, 117, 117));

        JLabel loginLink = new JLabel("Đăng nhập");
        loginLink.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        loginLink.setForeground(new Color(255, 152, 0));
        loginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));

        loginLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new LoginFrame();
                dispose();
            }
        });

        loginPanel.add(backLabel);
        loginPanel.add(loginLink);

        panel.add(loginPanel);

        panel.add(Box.createVerticalGlue());

        // Event listeners
        setupEventListeners();

        return panel;
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
        btnReset.addActionListener(e -> handleReset());
        btnCancel.addActionListener(e -> {
            new LoginFrame();
            dispose();
        });

        txtConfirmPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleReset();
                }
            }
        });
    }

    // ==================== HANDLE RESET ====================
    private void handleReset() {
        String username = txtUsername.getText().trim();
        String email = txtEmail.getText().trim();
        String newPassword = new String(txtNewPassword.getPassword());
        String confirmPassword = new String(txtConfirmPassword.getPassword());

        // Validation
        if (username.isEmpty()) {
            showError("Vui lòng nhập tên đăng nhập!");
            return;
        }

        if (email.isEmpty()) {
            showError("Vui lòng nhập email!");
            return;
        }

        if (!email.contains("@")) {
            showError("Email không hợp lệ!");
            return;
        }

        if (newPassword.isEmpty()) {
            showError("Vui lòng nhập mật khẩu mới!");
            return;
        }

        if (newPassword.length() < 6) {
            showError("Mật khẩu phải có ít nhất 6 ký tự!");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showError("Mật khẩu xác nhận không khớp!");
            return;
        }

        // TODO: Verify username and email from database
        // For now, just show success message
        boolean updateSuccess = UserDAO.updatePassword(1, newPassword); // Replace 1 with actual userId

        if (updateSuccess) {
            JOptionPane.showMessageDialog(this, "Mật khẩu đã được đặt lại thành công!\nVui lòng đăng nhập.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            new LoginFrame();
            dispose();
        } else {
            showError("Không thể cập nhật mật khẩu. Vui lòng thử lại!");
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