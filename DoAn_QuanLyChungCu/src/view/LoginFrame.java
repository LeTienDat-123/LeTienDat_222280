package view;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.*;
import model.User;
import dao.UserDAO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.geom.Ellipse2D;
public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnReset;
    private JCheckBox chkRememberMe;
    private JLabel lblErrorMessage;
    private JLabel lblSignUpLink;
    private JLabel lblForgotLink;	

    public LoginFrame() {
        setTitle("Đăng nhập - Hệ thống Quản lý Chung cư");
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
                    0, 0, new Color(25, 103, 210),
                    getWidth(), getHeight(), new Color(13, 71, 161)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());

        // Left Panel - Logo & Title
        JPanel leftPanel = createLeftPanel();
        mainPanel.add(leftPanel, BorderLayout.WEST);

        // Right Panel - Login Form
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
                    0, 0, new Color(25, 103, 210),
                    getWidth(), getHeight(), new Color(13, 71, 161)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Decorative circles
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
        JLabel titleLabel = new JLabel("QUẢN LÝ CHUNG CƯ");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);

        panel.add(Box.createVerticalStrut(15));

        // Subtitle
        JLabel subtitleLabel = new JLabel("Hệ thống quản lý hiệu quả");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(200, 220, 255));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(subtitleLabel);

        panel.add(Box.createVerticalGlue());

        // Footer
        JLabel footerLabel = new JLabel("© 2026 - Tất cả quyền được bảo lưu");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        footerLabel.setForeground(new Color(150, 180, 220));
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

        // Welcome message
        JLabel welcomeLabel = new JLabel("ĐĂNG NHẬP");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        welcomeLabel.setForeground(new Color(33, 33, 33));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(welcomeLabel);

        panel.add(Box.createVerticalStrut(6));

        JLabel subLabel = new JLabel("Vui lòng nhập thông tin đăng nhập");
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
        usernameLabel.setForeground(new Color(66, 133, 244));
        
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

        // Password field - ĐÃ ĐIỀU CHỈNH
        JPanel passwordLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        passwordLabelPanel.setBackground(new Color(240, 242, 245));
        passwordLabelPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        
        JLabel passwordLabel = new JLabel("Mật khẩu");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passwordLabel.setForeground(new Color(66, 133, 244));
        
        passwordLabelPanel.add(passwordLabel);
        panel.add(passwordLabelPanel);

        panel.add(Box.createVerticalStrut(6));

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtPassword.setBorder(new RoundedBorder(10, new Color(200, 200, 200)));
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        txtPassword.setMargin(new Insets(12, 18, 12, 18));
        panel.add(txtPassword);

        panel.add(Box.createVerticalStrut(16));

        // Remember me checkbox - ĐÃ ĐIỀU CHỈNH
        JPanel checkboxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        checkboxPanel.setBackground(new Color(240, 242, 245));
        checkboxPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        
        chkRememberMe = new JCheckBox("Ghi nhớ tôi");
        chkRememberMe.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkRememberMe.setForeground(new Color(117, 117, 117));
        chkRememberMe.setBackground(new Color(240, 242, 245));
        chkRememberMe.setFocusPainted(false);
        
        checkboxPanel.add(chkRememberMe);
        panel.add(checkboxPanel);

        panel.add(Box.createVerticalStrut(28));

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 18, 0));
        buttonsPanel.setBackground(new Color(240, 242, 245));
        buttonsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        btnLogin = createStyledButton("Đăng nhập", new Color(25, 103, 210));
        btnReset = createStyledButton("Xóa", new Color(200, 200, 200));

        buttonsPanel.add(btnLogin);
        buttonsPanel.add(btnReset);

        panel.add(buttonsPanel);

        panel.add(Box.createVerticalStrut(20));

        // Forgot password and Sign up links
        JPanel linksPanel = new JPanel();
        linksPanel.setBackground(new Color(240, 242, 245));
        linksPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 0));
        linksPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        lblForgotLink = createLinkLabel("Quên mật khẩu?");
        lblSignUpLink = createLinkLabel("Đăng ký");

        linksPanel.add(lblForgotLink);
        linksPanel.add(new JLabel("|"));
        linksPanel.add(lblSignUpLink);

        panel.add(linksPanel);

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

    // ==================== CREATE LINK LABEL ====================
    private JLabel createLinkLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(new Color(25, 103, 210));
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                label.setForeground(new Color(13, 71, 161));
                label.setText("<html><u>" + text + "</u></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                label.setForeground(new Color(25, 103, 210));
                label.setText(text);
            }
        });

        return label;
    }

    // ==================== SETUP EVENT LISTENERS ====================
    private void setupEventListeners() {
        btnLogin.addActionListener(e -> handleLogin());
        btnReset.addActionListener(e -> handleReset());

        txtPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLogin();
                }
            }
        });

        lblForgotLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new ForgotPasswordFrame();
                dispose();
            }
        });

        lblSignUpLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new SignUpFrame();
                dispose();
            }
        });
    }

    // ==================== HANDLE LOGIN ====================
    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty()) {
            showError("Vui lòng nhập tên đăng nhập!");
            txtUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            showError("Vui lòng nhập mật khẩu!");
            txtPassword.requestFocus();
            return;
        }

        User user = authenticateUser(username, password);

        if (user != null) {
            dispose();
            new MainFrame(user);
        } else {
            showError("Tên đăng nhập hoặc mật khẩu không chính xác!");
            txtPassword.setText("");
            txtPassword.requestFocus();
        }
    }

    // ==================== AUTHENTICATE USER ====================
    private User authenticateUser(String username, String password) {
        return UserDAO.authenticateUser(username, password);
    }

    // ==================== HANDLE RESET ====================
    private void handleReset() {
        txtUsername.setText("");
        txtPassword.setText("");
        lblErrorMessage.setText("");
        txtUsername.requestFocus();
    }

    // ==================== SHOW ERROR ====================
    private void showError(String message) {
        lblErrorMessage.setText(message);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}