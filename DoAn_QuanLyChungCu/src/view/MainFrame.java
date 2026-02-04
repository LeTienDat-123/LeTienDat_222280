package view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import model.User;
import model.Building;
import dao.*;
import java.util.List;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
public class MainFrame extends JFrame {
    private JTabbedPane tabbedPaneBuildings;
    private JPanel panelStatistics;
    private Map<Integer, BuildingPanel> buildingPanels;
    private model.User currentUser;
    
    public MainFrame(model.User user) {
        this.currentUser = user;
        this.buildingPanels = new HashMap<>();
        setTitle("Hệ thống Quản lý Chung cư");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Tạo menu bar
        createMenuBar();
        
        // Tạo panel chính
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 242, 245));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Tạo panel header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Tạo split pane cho tòa nhà và thống kê
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(550);
        splitPane.setResizeWeight(0.6);
        splitPane.setBackground(new Color(240, 242, 245));
        
        // Panel tòa nhà
        JPanel buildingContainer = createBuildingPanel();
        splitPane.setTopComponent(buildingContainer);
        
        // Panel thống kê
        panelStatistics = createStatisticsPanel();
        splitPane.setBottomComponent(new JScrollPane(panelStatistics));
        
        mainPanel.add(splitPane, BorderLayout.CENTER);
        
        setContentPane(mainPanel);
        
        // Tải dữ liệu
        loadBuildingsData();
        
        setVisible(true);
    }
    
    // ==================== HEADER PANEL ====================

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBackground(new Color(25, 103, 210));
        panel.setBorder(new RoundedBorder(10, new Color(25, 103, 210)));
        panel.setPreferredSize(new Dimension(0, 90));
        
        // ==================== LEFT PANEL - Logo & Title ====================
        JLabel titleLabel = new JLabel("HỆ THỐNG QUẢN LÝ CHUNG CƯ");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subLabel = new JLabel("Hệ thống quản lý hiệu quả cho các tòa nhà");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLabel.setForeground(new Color(200, 220, 255));
        
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(new Color(25, 103, 210));
        leftPanel.add(titleLabel);
        leftPanel.add(subLabel);
        
        panel.add(leftPanel, BorderLayout.WEST);
        
        // ==================== CENTER PANEL - Action Buttons ====================
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        centerPanel.setBackground(new Color(25, 103, 210));
        
        // Nút Thêm mới - Dropdown
        JButton btnAdd = createDropdownButton("Thêm mới", new Color(0, 150, 136));
        JPopupMenu addMenu = new JPopupMenu();
        addMenu.setBackground(Color.WHITE);
        addMenu.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        
        JMenuItem addBuildingItem = createDropdownMenuItem("Thêm Tòa nhà", new Color(0, 150, 136));
        JMenuItem addApartmentItem = createDropdownMenuItem("Thêm Căn hộ", new Color(63, 81, 181));
        
        addBuildingItem.addActionListener(e -> openAddBuilding());
        addApartmentItem.addActionListener(e -> openAddApartment());
        
        addMenu.add(addBuildingItem);
        addMenu.addSeparator();
        addMenu.add(addApartmentItem);
        
        btnAdd.addActionListener(e -> addMenu.show(btnAdd, 0, btnAdd.getHeight()));
        
        // Nút Làm mới
        JButton btnRefresh = createToolButton("Làm mới", new Color(76, 175, 80));
        btnRefresh.setPreferredSize(new Dimension(140, 40));
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRefresh.addActionListener(e -> refreshAll());
        
        // Nút Đăng xuất
        JButton btnLogout = createToolButton("Đăng xuất", new Color(244, 67, 54));
        btnLogout.setPreferredSize(new Dimension(140, 40));
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.addActionListener(e -> logout());
        
        centerPanel.add(btnAdd);
        centerPanel.add(btnRefresh);
        centerPanel.add(btnLogout);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        
        // ==================== RIGHT PANEL - User Info ====================
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        rightPanel.setBackground(new Color(25, 103, 210));
        
        // Thông tin người dùng
        JPanel userInfoPanel = createUserInfoPanel();
        rightPanel.add(userInfoPanel);
        
        panel.add(rightPanel, BorderLayout.EAST);
        
        return panel;
    }
private JPanel createQuickActionsPanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
    panel.setBackground(new Color(25, 103, 210));
    
    // Dropdown Button - Thêm mới
    JButton btnAdd = createDropdownButton("Thêm mới", new Color(0, 150, 136));
    JPopupMenu addMenu = new JPopupMenu();
    addMenu.setBackground(Color.WHITE);
    addMenu.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
    
    JMenuItem addBuildingItem = createDropdownMenuItem("Thêm Tòa nhà", new Color(0, 150, 136));
    JMenuItem addApartmentItem = createDropdownMenuItem("Thêm Căn hộ", new Color(63, 81, 181));
    
    addBuildingItem.addActionListener(e -> openAddBuilding());
    addApartmentItem.addActionListener(e -> openAddApartment());
    
    addMenu.add(addBuildingItem);
    addMenu.addSeparator();
    addMenu.add(addApartmentItem);
    
    btnAdd.addActionListener(e -> addMenu.show(btnAdd, 0, btnAdd.getHeight()));
      
    panel.add(btnAdd);
    
    return panel;
}
private JButton createDropdownButton(String text, Color bgColor) {
    JButton btn = new JButton(text + "");
    btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
    btn.setBackground(bgColor);
    btn.setForeground(Color.WHITE);
    btn.setFocusPainted(false);
    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    btn.setPreferredSize(new Dimension(140, 40));
    btn.setBorder(new RoundedBorder(8, bgColor));
    
    btn.addMouseListener(new java.awt.event.MouseAdapter() {
        Color originalColor = bgColor;
        
        @Override
        public void mouseEntered(java.awt.event.MouseEvent evt) {
            btn.setBackground(bgColor.brighter());
        }
        
        @Override
        public void mouseExited(java.awt.event.MouseEvent evt) {
            btn.setBackground(originalColor);
        }
    });
    
    return btn;
}


private JMenuItem createDropdownMenuItem(String text, Color color) {
    JMenuItem item = new JMenuItem(text);
    item.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    item.setForeground(color);
    item.setBackground(Color.WHITE);
    item.setBorder(new EmptyBorder(8, 15, 8, 15));
    item.setCursor(new Cursor(Cursor.HAND_CURSOR));
    
    item.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseEntered(java.awt.event.MouseEvent evt) {
            item.setBackground(new Color(240, 240, 240));
        }
        
        @Override
        public void mouseExited(java.awt.event.MouseEvent evt) {
            item.setBackground(Color.WHITE);
        }
    });
    
    return item;
}
private JButton createQuickActionButton(String text, Color bgColor) {
    JButton btn = new JButton(text);
    btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
    btn.setBackground(bgColor);
    btn.setForeground(Color.WHITE);
    btn.setFocusPainted(false);
    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    btn.setPreferredSize(new Dimension(140, 40));
    btn.setBorder(new RoundedBorder(8, bgColor));
    
    // Hover effect
    btn.addMouseListener(new java.awt.event.MouseAdapter() {
        Color originalColor = bgColor;
        
        @Override
        public void mouseEntered(java.awt.event.MouseEvent evt) {
            btn.setBackground(bgColor.brighter());
        }
        
        @Override
        public void mouseExited(java.awt.event.MouseEvent evt) {
            btn.setBackground(originalColor);
        }
    });
    
    return btn;
}

private void openAddBuilding() {
    BuildingDialog dialog = new BuildingDialog(this, "Thêm tòa nhà mới", null);
    dialog.setVisible(true);
    if (dialog.isConfirmed()) {
        loadBuildingsData();
        JOptionPane.showMessageDialog(this, 
            "✓ Đã thêm tòa nhà mới thành công!", 
            "Thành công", 
            JOptionPane.INFORMATION_MESSAGE);
    }
}

    private void openFloorManagement() {
        // Show building selection dialog
        java.util.List<model.Building> buildings = dao.BuildingDAO.getAllBuildings();
        if (buildings.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chưa có tòa nhà nào!\nVui lòng thêm tòa nhà trước.");
            return;
        }
        
        String[] buildingNames = buildings.stream()
            .map(b -> b.getId() + " - " + b.getName())
            .toArray(String[]::new);
        
        String selected = (String) JOptionPane.showInputDialog(
            this,
            "Chọn tòa nhà để quản lý tầng/căn hộ:",
            "Chọn tòa nhà",
            JOptionPane.QUESTION_MESSAGE,
            null,
            buildingNames,
            buildingNames[0]
        );
        
        if (selected != null) {
            int buildingId = Integer.parseInt(selected.split(" - ")[0]);
            new ApartmentManagementForm(buildingId);
        }
    }

    private void openAddApartment() {
        // Tạo dialog mới với đầy đủ thông tin
        new EnhancedAddApartmentDialog(this).setVisible(true);
    }
    private void openApartmentManagementSelection() {
        // Show building selection dialog
        java.util.List<model.Building> buildings = dao.BuildingDAO.getAllBuildings();
        if (buildings.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Chưa có tòa nhà nào!\nVui lòng thêm tòa nhà trước.", 
                "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String[] buildingNames = buildings.stream()
            .map(b -> b.getId() + " - " + b.getName())
            .toArray(String[]::new);
        
        String selected = (String) JOptionPane.showInputDialog(
            this,
            "Chọn tòa nhà để quản lý căn hộ:",
            "Chọn tòa nhà",
            JOptionPane.QUESTION_MESSAGE,
            null,
            buildingNames,
            buildingNames[0]
        );
        
        if (selected != null) {
            int buildingId = Integer.parseInt(selected.split(" - ")[0]);
            new ApartmentManagementForm(buildingId);
        }
    }
   
    // ==================== USER INFO PANEL ====================
    private JPanel createUserInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(new Color(25, 103, 210));
        panel.setBorder(BorderFactory.createLineBorder(new Color(80, 150, 220), 2));
        panel.setPreferredSize(new Dimension(280, 70));

        // __Avatar__
        JLabel avatarLabel = new JLabel();
        avatarLabel.setPreferredSize(new Dimension(50, 50));
        
        try {
           
            String imagePath = "src/Assets/avt.jpg"; 
            BufferedImage image = ImageIO.read(new File(imagePath));
            
            // Resize ảnh thành 50x50
            Image scaledImage = image.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            ImageIcon icon = new ImageIcon(scaledImage);
            avatarLabel.setIcon(icon);
            avatarLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        } catch (IOException e) {
            avatarLabel.setText("?");
            avatarLabel.setFont(new Font("Arial", Font.BOLD, 28));
            avatarLabel.setForeground(Color.WHITE);
            System.out.println("Lỗi: " + e.getMessage());
        }
        
        panel.add(avatarLabel);

        // Info Panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(25, 103, 210));
        infoPanel.setBorder(new EmptyBorder(2, 5, 2, 5));

        JLabel nameLabel = new JLabel(currentUser.getFullName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nameLabel.setForeground(Color.WHITE);

        JLabel roleLabel = new JLabel("" + currentUser.getRoleDisplay());
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        roleLabel.setForeground(new Color(200, 220, 255));

        JLabel phoneLabel = new JLabel("" + currentUser.getPhone());
        phoneLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        phoneLabel.setForeground(new Color(200, 220, 255));

        infoPanel.add(nameLabel);
        infoPanel.add(roleLabel);
        infoPanel.add(phoneLabel);

        panel.add(infoPanel);

        return panel;
    }
    
    // ==================== REFRESH ALL ====================
    private void refreshAll() {
        // Refresh tất cả building panels
        for (BuildingPanel panel : buildingPanels.values()) {
            panel.refreshPanel();
        }
        
        // Refresh statistics
        refreshStatistics();
        
        JOptionPane.showMessageDialog(this, 
            "✓ Đã làm mới dữ liệu!", 
            "Thông báo", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    // ==================== LOGOUT ====================
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc chắn muốn đăng xuất?",
            "Xác nhận đăng xuất",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame());
        }
    }
    
    private JButton createToolButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setBorder(new RoundedBorder(8, bgColor));
        btn.setPreferredSize(new Dimension(140, 40));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalColor = bgColor;
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgColor.brighter());
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(originalColor);
            }
        });
        
        return btn;
    }
    // ==================== BUILDING PANEL ====================
    private JPanel createBuildingPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(new Color(240, 242, 245));
        
        JLabel titleLabel = new JLabel("DANH SÁCH TÒA NHÀ VÀ CĂN HỘ");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(33, 33, 33));
        titleLabel.setBorder(new EmptyBorder(5, 10, 5, 10));
        
        container.add(titleLabel, BorderLayout.NORTH);
        
        tabbedPaneBuildings = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPaneBuildings.setTabPlacement(JTabbedPane.TOP);
        tabbedPaneBuildings.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabbedPaneBuildings.setBackground(Color.WHITE);
        
        container.add(tabbedPaneBuildings, BorderLayout.CENTER);
        
        return container;
    }
    
    // ==================== STATISTICS PANEL ====================
    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 15, 15));
        panel.setBackground(new Color(240, 242, 245));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("THỐNG KÊ & BÁO CÁO");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(33, 33, 33));
        
        panel.add(createChartCard("Trạng thái căn hộ", createApartmentStatusChart()));
        panel.add(createChartCard("Tình trạng thanh toán", createPaymentStatusChart()));
        panel.add(createChartCard("Thu nhập theo tòa nhà", createRevenueChart()));
        panel.add(createChartCard("Hợp đồng theo trạng thái", createContractStatusChart()));
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(240, 242, 245));
        wrapper.add(titleLabel, BorderLayout.NORTH);
        wrapper.add(panel, BorderLayout.CENTER);
        
        return wrapper;
    }
    
    private void refreshStatistics() {
        panelStatistics.removeAll();
        
        JPanel panel = new JPanel(new GridLayout(2, 2, 15, 15));
        panel.setBackground(new Color(240, 242, 245));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("THỐNG KÊ & BÁO CÁO");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(33, 33, 33));
        
        panel.add(createChartCard("Trạng thái căn hộ", createApartmentStatusChart()));
        panel.add(createChartCard("Tình trạng thanh toán", createPaymentStatusChart()));
        panel.add(createChartCard("Thu nhập theo tòa nhà", createRevenueChart()));
        panel.add(createChartCard("Hợp đồng theo trạng thái", createContractStatusChart()));
        
        panelStatistics.add(titleLabel, BorderLayout.NORTH);
        panelStatistics.add(panel, BorderLayout.CENTER);
        
        panelStatistics.revalidate();
        panelStatistics.repaint();
    }
    
    private JPanel createChartCard(String title, JPanel chart) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new RoundedBorder(10, new Color(220, 220, 220)));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLabel.setForeground(new Color(66, 133, 244));
        titleLabel.setBorder(new EmptyBorder(10, 10, 5, 10));
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(chart, BorderLayout.CENTER);
        
        return card;
    }
    
    // ==================== CHARTS FROM DATABASE ====================
    private JPanel createApartmentStatusChart() {
        Map<String, Integer> stats = BuildingDAO.getAllApartmentStats();
        
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                int centerX = width / 2;
                int centerY = height / 2 - 10;
                int radius = Math.min(width, height) / 3;
                
                int available = stats.get("available");
                int occupied = stats.get("occupied");
                int maintenance = stats.get("maintenance");
                int total = stats.get("total");
                
                if (total == 0) return;
                
                int angle = 0;
                
                g2d.setColor(new Color(76, 175, 80));
                int availSlice = (int) (360 * available / total);
                g2d.fillArc(centerX - radius, centerY - radius, radius * 2, radius * 2, angle, availSlice);
                angle += availSlice;
                
                g2d.setColor(new Color(25, 103, 210));
                int occupSlice = (int) (360 * occupied / total);
                g2d.fillArc(centerX - radius, centerY - radius, radius * 2, radius * 2, angle, occupSlice);
                angle += occupSlice;
                
                g2d.setColor(new Color(255, 152, 0));
                g2d.fillArc(centerX - radius, centerY - radius, radius * 2, radius * 2, angle, 360 - angle);
                
                int legY = centerY + radius + 25;
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                
                g2d.setColor(new Color(76, 175, 80));
                g2d.fillRect(10, legY, 10, 10);
                g2d.setColor(Color.BLACK);
                g2d.drawString("Trống: " + available, 25, legY + 10);
                
                g2d.setColor(new Color(25, 103, 210));
                g2d.fillRect(10, legY + 15, 10, 10);
                g2d.setColor(Color.BLACK);
                g2d.drawString("Đang ở: " + occupied, 25, legY + 25);
                
                g2d.setColor(new Color(255, 152, 0));
                g2d.fillRect(10, legY + 30, 10, 10);
                g2d.setColor(Color.BLACK);
                g2d.drawString("Bảo trì: " + maintenance, 25, legY + 40);
                
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 11));
                g2d.drawString("Tổng: " + total + " căn", 10, legY + 60);
            }
        };
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(0, 200));
        return panel;
    }
    
    private JPanel createPaymentStatusChart() {
        // Đếm hóa đơn từ DB
        int[] counts = getInvoiceStats();
        
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                int barWidth = 60;
                int maxHeight = height - 60;
                
                int paid = counts[0];
                int unpaid = counts[1];
                int overdue = counts[2];
                int total = paid + unpaid + overdue;
                
                if (total == 0) {
                    g2d.setFont(new Font("Segoe UI", Font.ITALIC, 11));
                    g2d.drawString("Chưa có dữ liệu", width/2 - 50, height/2);
                    return;
                }
                
                int x = 30;
                drawBar(g2d, x, height - 40, barWidth, paid * maxHeight / total, new Color(76, 175, 80), "Đã TT\n" + paid);
                x += barWidth + 30;
                drawBar(g2d, x, height - 40, barWidth, unpaid * maxHeight / total, new Color(255, 152, 0), "Chưa TT\n" + unpaid);
                x += barWidth + 30;
                drawBar(g2d, x, height - 40, barWidth, overdue * maxHeight / total, new Color(244, 67, 54), "Quá hạn\n" + overdue);
            }
            
            private void drawBar(Graphics2D g2d, int x, int y, int width, int height, Color color, String label) {
                g2d.setColor(color);
                g2d.fillRect(x, y - height, width, height);
                g2d.setColor(Color.BLACK);
                g2d.drawRect(x, y - height, width, height);
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 9));
                g2d.drawString(label, x - 5, y + 20);
            }
        };
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(0, 200));
        return panel;
    }
    
    private JPanel createRevenueChart() {
        Map<Integer, Double> revenue = BuildingDAO.getRevenueByBuilding();
        List<Building> buildings = BuildingDAO.getAllBuildings();
        
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (buildings.isEmpty()) return;
                
                int width = getWidth();
                int height = getHeight();
                int barWidth = Math.min(70, width / buildings.size() - 30);
                int maxHeight = height - 80;
                
                double maxRevenue = revenue.values().stream().max(Double::compare).orElse(1.0);
                
                int x = 20;
                Color[] colors = {new Color(25, 103, 210), new Color(56, 142, 60), new Color(255, 111, 0)};
                
                for (int i = 0; i < buildings.size(); i++) {
                    Building b = buildings.get(i);
                    double rev = revenue.getOrDefault(b.getId(), 0.0);
                    int barHeight = (int)(rev * maxHeight / maxRevenue);
                    
                    g2d.setColor(colors[i % colors.length]);
                    g2d.fillRect(x, height - 40 - barHeight, barWidth, barHeight);
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(x, height - 40 - barHeight, barWidth, barHeight);
                    
                    g2d.setFont(new Font("Segoe UI", Font.BOLD, 9));
                    g2d.drawString(b.getName(), x, height - 10);
                    g2d.setFont(new Font("Segoe UI", Font.PLAIN, 8));
                    g2d.drawString(String.format("%.1fM", rev / 1000000), x, height - 45 - barHeight);
                    
                    x += barWidth + 30;
                }
            }
        };
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(0, 200));
        return panel;
    }
    
    private JPanel createContractStatusChart() {
        int[] contractStats = getContractStats();
        
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                
                int active = contractStats[0];
                int expired = contractStats[1];
                int cancelled = contractStats[2];
                
                int y = 30;
                drawStatusRow(g2d, 10, y, width - 20, active, active + expired + cancelled + 1, "Đang hoạt động", new Color(76, 175, 80));
                y += 40;
                drawStatusRow(g2d, 10, y, width - 20, expired, active + expired + cancelled + 1, "Hết hạn", new Color(255, 152, 0));
                y += 40;
                drawStatusRow(g2d, 10, y, width - 20, cancelled, active + expired + cancelled + 1, "Đã hủy", new Color(244, 67, 54));
            }
            
            private void drawStatusRow(Graphics2D g2d, int x, int y, int width, int value, int maxValue, String label, Color color) {
                int barWidth = maxValue > 0 ? (int)(width * value / maxValue) : 0;
                g2d.setColor(color);
                g2d.fillRect(x, y, barWidth, 20);
                g2d.setColor(Color.lightGray);
                g2d.drawRect(x, y, width, 20);
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                g2d.drawString(label + ": " + value, x + 5, y + 15);
            }
        };
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(0, 200));
        return panel;
    }
    
    // ==================== GET STATS FROM DB ====================
    private int[] getInvoiceStats() {
        int[] stats = new int[3]; // paid, unpaid, overdue
        
        try (java.sql.Connection conn = config.DatabaseConnection.getConnection()) {
            // Điện
            String query = "SELECT trang_thai, COUNT(*) as count FROM hoa_don_dien GROUP BY trang_thai";
            try (java.sql.Statement stmt = conn.createStatement();
                 java.sql.ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    String status = rs.getString("trang_thai");
                    int count = rs.getInt("count");
                    if ("da_thanh_toan".equals(status)) stats[0] += count;
                    else if ("chua_thanh_toan".equals(status)) stats[1] += count;
                    else if ("qua_han".equals(status)) stats[2] += count;
                }
            }
            
            // Nước
            query = "SELECT trang_thai, COUNT(*) as count FROM hoa_don_nuoc GROUP BY trang_thai";
            try (java.sql.Statement stmt = conn.createStatement();
                 java.sql.ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    String status = rs.getString("trang_thai");
                    int count = rs.getInt("count");
                    if ("da_thanh_toan".equals(status)) stats[0] += count;
                    else if ("chua_thanh_toan".equals(status)) stats[1] += count;
                    else if ("qua_han".equals(status)) stats[2] += count;
                }
            }
            
            // Quản lý
            query = "SELECT trang_thai, COUNT(*) as count FROM hoa_don_quan_ly_chung GROUP BY trang_thai";
            try (java.sql.Statement stmt = conn.createStatement();
                 java.sql.ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    String status = rs.getString("trang_thai");
                    int count = rs.getInt("count");
                    if ("da_thanh_toan".equals(status)) stats[0] += count;
                    else if ("chua_thanh_toan".equals(status)) stats[1] += count;
                    else if ("qua_han".equals(status)) stats[2] += count;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return stats;
    }
    
    private int[] getContractStats() {
        int[] stats = new int[3]; // active, expired, cancelled
        
        String query = "SELECT trang_thai, COUNT(*) as count FROM hop_dong_thue GROUP BY trang_thai";
        
        try (java.sql.Connection conn = config.DatabaseConnection.getConnection();
             java.sql.Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                String status = rs.getString("trang_thai");
                int count = rs.getInt("count");
                if ("hoat_dong".equals(status)) stats[0] = count;
                else if ("het_han".equals(status)) stats[1] = count;
                else if ("huy".equals(status)) stats[2] = count;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return stats;
    }
    
    // ==================== LOAD DATA ====================
    public void loadBuildingsData() {
        // Đảm bảo buildingPanels không null
        if (buildingPanels == null) {
            buildingPanels = new HashMap<>();
        }
        
        buildingPanels.clear();
        tabbedPaneBuildings.removeAll();
        
        try {
            java.util.List<model.Building> buildings = dao.BuildingDAO.getAllBuildings();
            
            for (model.Building building : buildings) {
                BuildingPanel buildingPanel = new BuildingPanel(
                    building.getId(), 
                    building.getName(), 
                    building.getFloors()
                );
                buildingPanels.put(building.getId(), buildingPanel);
                tabbedPaneBuildings.addTab(building.getName(), buildingPanel);
            }
            
            tabbedPaneBuildings.revalidate();
            tabbedPaneBuildings.repaint();
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi tải dữ liệu tòa nhà: " + e.getMessage());
            e.printStackTrace();
            
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tải dữ liệu tòa nhà:\n" + e.getMessage(), 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    // ==================== MENU BAR ====================
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.WHITE);
        
       
        
        // Quản lý Menu
        JMenu manageMenu = new JMenu("Thông tin quản lý");
        manageMenu.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        
        JMenuItem buildingItem = new JMenuItem("Quản lý Tòa nhà");
        buildingItem.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        buildingItem.addActionListener(e -> openBuildingManagement());
        
        JMenuItem apartmentItem = new JMenuItem("Quản lý Căn hộ");
        apartmentItem.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        apartmentItem.addActionListener(e -> openApartmentManagementSelection());
       
        
        JMenuItem userItem = new JMenuItem("Quản lý Người dùng");
        userItem.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        userItem.addActionListener(e -> openUserManagement());
        
        JMenuItem householdItem = new JMenuItem("Quản lý Hộ gia đình");
        householdItem.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        householdItem.addActionListener(e -> openHouseholdManagement());
        
        JMenuItem contractItem = new JMenuItem("Quản lý Hợp đồng thuê");
        contractItem.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        contractItem.addActionListener(e -> openContractManagement());
        
        JMenuItem securityItem = new JMenuItem("Quản lý Thẻ an ninh");
        securityItem.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        securityItem.addActionListener(e -> openSecurityCardManagement());
        
        JMenuItem maintenanceItem = new JMenuItem("Quản lý Yêu cầu bảo trì");
        maintenanceItem.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        maintenanceItem.addActionListener(e -> openMaintenanceManagement());
        
        JMenuItem refreshItem = new JMenuItem("Làm mới dữ liệu");
        refreshItem.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        refreshItem.addActionListener(e -> refreshAll());
        
      
        manageMenu.add(buildingItem);
        manageMenu.add(apartmentItem);
        manageMenu.addSeparator();
        manageMenu.add(userItem);
        manageMenu.add(householdItem);
        manageMenu.add(contractItem);
        manageMenu.add(securityItem);
        manageMenu.add(maintenanceItem);
        manageMenu.addSeparator();
        manageMenu.add(refreshItem);
        
        // ==================== BÁO CÁO MENU ====================
        JMenu reportMenu = new JMenu("Báo cáo");
        reportMenu.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        
        JMenuItem apartmentReportItem = new JMenuItem("Báo cáo Căn hộ");
        JMenuItem invoiceReportItem = new JMenuItem("Báo cáo Hóa đơn");
        JMenuItem contractReportItem = new JMenuItem("Báo cáo Hợp đồng");
       
        
        apartmentReportItem.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        invoiceReportItem.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        contractReportItem.setFont(new Font("Segoe UI", Font.PLAIN, 18));
       
        
        apartmentReportItem.addActionListener(e -> showApartmentReport());
        invoiceReportItem.addActionListener(e -> showInvoiceReport());
        contractReportItem.addActionListener(e -> showContractReport());
       
        
        reportMenu.add(apartmentReportItem);
        reportMenu.add(invoiceReportItem);
        reportMenu.add(contractReportItem);
       
        
        // ==================== TÀI KHOẢN MENU ====================
        JMenu accountMenu = new JMenu("Tài khoản");
        accountMenu.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        
        JMenuItem profileItem = new JMenuItem("Thông tin cá nhân");
       
        JMenuItem logoutItem = new JMenuItem("Đăng xuất");
        
        profileItem.setFont(new Font("Segoe UI", Font.PLAIN, 18));
       
        logoutItem.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        
        profileItem.addActionListener(e -> showUserProfile());
      
        logoutItem.addActionListener(e -> logout());
        
        accountMenu.add(profileItem);
    
        accountMenu.addSeparator();
        accountMenu.add(logoutItem);
        
        // ==================== HELP MENU ====================
        JMenu helpMenu = new JMenu("Trợ giúp");
        helpMenu.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        
        JMenuItem guideItem = new JMenuItem("Hướng dẫn sử dụng");
        JMenuItem aboutItem = new JMenuItem("Giới thiệu");
        
        guideItem.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        aboutItem.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        
        guideItem.addActionListener(e -> showGuide());
        aboutItem.addActionListener(e -> showAbout());
        

        // ==================== THỐNG KÊ MENU ====================
        JMenu statsMenu = new JMenu("Thống Kê");
        statsMenu.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        
        JMenuItem overviewItem = new JMenuItem("Tổng Quan Hệ Thống");
        overviewItem.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        overviewItem.addActionListener(e -> openStatistics());
        
        statsMenu.add(overviewItem);
        
        helpMenu.add(guideItem);
        helpMenu.add(aboutItem);
        
        // Add all menus to menubar
        menuBar.add(statsMenu);
        menuBar.add(manageMenu);
        menuBar.add(reportMenu);
        menuBar.add(accountMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
  
 // ==================== MANAGEMENT FORM OPENERS ====================
    private void openBuildingManagement() {
        new BuildingManagementForm();
    }
    private void openUserManagement() {
        new UserManagementForm();
    }

    private void openHouseholdManagement() {
        new HouseholdManagementForm();
    }
    
    private void openContractManagement() {
        new ContractManagementForm();
    }

    private void openSecurityCardManagement() {
        new SecurityCardManagementForm();
    }

    private void openMaintenanceManagement() {
        new MaintenanceRequestForm();
    }
    private void openDoiMatKhau() {
        new ForgotPasswordFrame();
    }

    // ==================== REPORT METHODS ====================
    private void showApartmentReport() {
        new ApartmentDetailReportFrame();
    }

    private void showInvoiceReport() {
        new InvoiceDetailReportFrame();
    }

    private void showContractReport() {
        new ContractDetailReportFrame();
    }
    private void openStatistics() {
        new StatisticsFrame();
    }
  

    // ==================== ACCOUNT METHODS ====================

    private void showUserProfile() {
        new UserAccountFrame(currentUser);
    }

   

    // ==================== HELP METHODS ====================
    private void showGuide() {
        String guide = "HƯỚNG DẪN SỬ DỤNG\n\n" +
            "1. QUẢN LÝ NGƯỜI DÙNG\n" +
            "   - Thêm, sửa, xóa người dùng\n" +
            "   - Đặt lại mật khẩu\n" +
            "   - Tìm kiếm và lọc theo vai trò\n\n" +
            "2. QUẢN LÝ HỘ GIA ĐÌNH\n" +
            "   - Tạo hộ gia đình mới\n" +
            "   - Quản lý thành viên trong hộ\n" +
            "   - Cập nhật thông tin liên hệ\n\n" +
            "3. QUẢN LÝ HỢP ĐỒNG\n" +
            "   - Tạo hợp đồng thuê\n" +
            "   - Gia hạn, chấm dứt hợp đồng\n" +
            "   - Theo dõi trạng thái\n\n" +
            "4. QUẢN LÝ THẺ AN NINH\n" +
            "   - Cấp thẻ mới\n" +
            "   - Khóa/mở thẻ\n" +
            "   - Báo mất thẻ\n\n" +
            "5. QUẢN LÝ BẢO TRÌ\n" +
            "   - Tiếp nhận yêu cầu\n" +
            "   - Xử lý và hoàn thành\n" +
            "   - Theo dõi chi phí";
        
        JTextArea textArea = new JTextArea(guide);
        textArea.setEditable(false);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Hướng dẫn sử dụng", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAbout() {
        String about = "HỆ THỐNG QUẢN LÝ CHUNG CƯ\n\n" +
            "Phiên bản: 1.0.0\n" +
            "Ngày phát hành: Tháng 1/2026\n\n" +
            "Phát triển bởi: Lê Tiến Đạt" +
            "© 2026 All Rights Reserved\n\n" +
            "Liên hệ hỗ trợ:\n" +
            "Email: support@apartment.com\n" +
            "Hotline: 1900-2222";
        
        JOptionPane.showMessageDialog(this, about, "Giới thiệu", JOptionPane.INFORMATION_MESSAGE);
    }
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
            return new Insets(5, 5, 5, 5);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}
class EnhancedAddApartmentDialog extends JDialog {
    private JComboBox<String> cboBuilding, cboFloor;
    private JTextField txtNumber, txtArea, txtBedrooms, txtBathrooms;
    private JComboBox<String> cboStatus;
    private JButton btnSave, btnCancel;
    private JLabel lblFloorInfo; // Thêm label hiển thị thông tin tòa nhà
    
    public EnhancedAddApartmentDialog(JFrame parent) {
        super(parent, "Thêm căn hộ mới", true);
        setSize(650, 550);
        setLocationRelativeTo(parent);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(25, 103, 210));
        JLabel headerLabel = new JLabel("THÊM CĂN HỘ MỚI");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setBorder(new EmptyBorder(10, 15, 10, 15));
        headerPanel.add(headerLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        
        // ==================== BUILDING SELECTION ====================
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblBuilding = new JLabel("Tòa nhà:");
        lblBuilding.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(lblBuilding, gbc);
        
        gbc.gridx = 1;
        cboBuilding = new JComboBox<>();
        cboBuilding.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cboBuilding.setPreferredSize(new Dimension(300, 30));
        loadBuildings();
        cboBuilding.addActionListener(e -> onBuildingSelected());
        formPanel.add(cboBuilding, gbc);
        
        // ==================== BUILDING INFO LABEL ====================
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        lblFloorInfo = new JLabel(" ");
        lblFloorInfo.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblFloorInfo.setForeground(new Color(100, 100, 100));
        lblFloorInfo.setBorder(new EmptyBorder(0, 5, 5, 5));
        formPanel.add(lblFloorInfo, gbc);
        gbc.gridwidth = 1;
        
        // ==================== FLOOR SELECTION ====================
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblFloor = new JLabel("Chọn tầng:");
        lblFloor.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(lblFloor, gbc);
        
        gbc.gridx = 1;
        cboFloor = new JComboBox<>();
        cboFloor.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cboFloor.setPreferredSize(new Dimension(300, 30));
        formPanel.add(cboFloor, gbc);
        
        // ==================== APARTMENT NUMBER ====================
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lblNumber = new JLabel("Số căn hộ:");
        lblNumber.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(lblNumber, gbc);
        
        gbc.gridx = 1;
        txtNumber = new JTextField(20);
        txtNumber.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtNumber.setPreferredSize(new Dimension(300, 30));
        
        // Thêm placeholder hint
        txtNumber.setToolTipText("VD: 101, 201A, 305B, ...");
        formPanel.add(txtNumber, gbc);
        
        // ==================== AREA ====================
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel lblArea = new JLabel("Diện tích (m²):");
        lblArea.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(lblArea, gbc);
        
        gbc.gridx = 1;
        txtArea = new JTextField(20);
        txtArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtArea.setPreferredSize(new Dimension(300, 30));
        txtArea.setToolTipText("VD: 50, 75.5, 120, ...");
        formPanel.add(txtArea, gbc);
        
        // ==================== BEDROOMS ====================
        gbc.gridx = 0; gbc.gridy = 5;
        JLabel lblBedrooms = new JLabel("Số phòng ngủ:");
        lblBedrooms.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(lblBedrooms, gbc);
        
        gbc.gridx = 1;
        JPanel bedroomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        bedroomPanel.setBackground(Color.WHITE);
        
        txtBedrooms = new JTextField(10);
        txtBedrooms.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBedrooms.setPreferredSize(new Dimension(100, 30));
        txtBedrooms.setToolTipText("VD: 1, 2, 3, ...");
        
        JLabel lblBedroomHint = new JLabel("phòng");
        lblBedroomHint.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblBedroomHint.setForeground(Color.GRAY);
        
        bedroomPanel.add(txtBedrooms);
        bedroomPanel.add(lblBedroomHint);
        formPanel.add(bedroomPanel, gbc);
        
        // ==================== BATHROOMS ====================
        gbc.gridx = 0; gbc.gridy = 6;
        JLabel lblBathrooms = new JLabel("Số phòng tắm:");
        lblBathrooms.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(lblBathrooms, gbc);
        
        gbc.gridx = 1;
        JPanel bathroomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        bathroomPanel.setBackground(Color.WHITE);
        
        txtBathrooms = new JTextField(10);
        txtBathrooms.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBathrooms.setPreferredSize(new Dimension(100, 30));
        txtBathrooms.setToolTipText("VD: 1, 2, 3, ...");
        
        JLabel lblBathroomHint = new JLabel("phòng");
        lblBathroomHint.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblBathroomHint.setForeground(Color.GRAY);
        
        bathroomPanel.add(txtBathrooms);
        bathroomPanel.add(lblBathroomHint);
        formPanel.add(bathroomPanel, gbc);
        
        // ==================== STATUS ====================
        gbc.gridx = 0; gbc.gridy = 7;
        JLabel lblStatus = new JLabel("Trạng thái:");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(lblStatus, gbc);
        
        gbc.gridx = 1;
        cboStatus = new JComboBox<>(new String[]{"Trống", "Có người ở", "Bảo trì"});
        cboStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cboStatus.setPreferredSize(new Dimension(300, 30));
        formPanel.add(cboStatus, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // ==================== BUTTON PANEL ====================
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(5, 0, 5, 0));
        
        btnSave = new JButton("Lưu");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setBackground(new Color(76, 175, 80));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.setPreferredSize(new Dimension(130, 45));
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.addActionListener(e -> save());
        
        btnCancel = new JButton("Hủy");
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCancel.setBackground(new Color(244, 67, 54));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFocusPainted(false);
        btnCancel.setPreferredSize(new Dimension(130, 45));
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.addActionListener(e -> dispose());
        
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        
        // Trigger initial load
        onBuildingSelected();
    }
    
    // ==================== LOAD BUILDINGS ====================
    private void loadBuildings() {
        cboBuilding.removeAllItems();
        java.util.List<model.Building> buildings = dao.BuildingDAO.getAllBuildings();
        
        if (buildings.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Chưa có tòa nhà nào!\nVui lòng thêm tòa nhà trước.", 
                "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            dispose();
            return;
        }
        
        for (model.Building building : buildings) {
            cboBuilding.addItem(building.getId() + " - " + building.getName());
        }
    }
    
    // ==================== ON BUILDING SELECTED ====================
    private void onBuildingSelected() {
        cboFloor.removeAllItems();
        lblFloorInfo.setText(" ");
        
        if (cboBuilding.getSelectedItem() == null) {
            return;
        }
        
        try {
            String selected = (String) cboBuilding.getSelectedItem();
            int buildingId = Integer.parseInt(selected.split(" - ")[0]);
            
            // Get building info
            model.Building building = dao.BuildingDAO.getBuildingById(buildingId);
            
            if (building != null) {
                int totalFloors = building.getFloors();
                
                // Update info label
                lblFloorInfo.setText(String.format(
                    "Tòa nhà có %d tầng | Địa chỉ: %s", 
                    totalFloors, 
                    building.getAddress()
                ));
                
                // Load floors into dropdown
                for (int i = 1; i <= totalFloors; i++) {
                    cboFloor.addItem("Tầng " + i);
                }
                
                // Auto-select first floor
                if (cboFloor.getItemCount() > 0) {
                    cboFloor.setSelectedIndex(0);
                }
                
                // Auto-focus to apartment number field
                SwingUtilities.invokeLater(() -> txtNumber.requestFocus());
            } else {
                lblFloorInfo.setText(" Không tìm thấy thông tin tòa nhà");
            }
            
        } catch (Exception e) {
            System.err.println("Lỗi khi load thông tin tòa nhà: " + e.getMessage());
            lblFloorInfo.setText(" Lỗi khi tải thông tin tòa nhà");
        }
    }
    
    // ==================== SAVE ====================
    private void save() {
        try {
            // ========== VALIDATION ==========
            if (cboBuilding.getSelectedItem() == null) {
                showError("Vui lòng chọn tòa nhà!");
                cboBuilding.requestFocus();
                return;
            }
            
            if (cboFloor.getSelectedItem() == null) {
                showError("Vui lòng chọn tầng!");
                cboFloor.requestFocus();
                return;
            }
            
            if (txtNumber.getText().trim().isEmpty()) {
                showError("Vui lòng nhập số căn hộ!");
                txtNumber.requestFocus();
                return;
            }
            
            if (txtArea.getText().trim().isEmpty()) {
                showError("Vui lòng nhập diện tích!");
                txtArea.requestFocus();
                return;
            }
            
            if (txtBedrooms.getText().trim().isEmpty()) {
                showError("Vui lòng nhập số phòng ngủ!");
                txtBedrooms.requestFocus();
                return;
            }
            
            if (txtBathrooms.getText().trim().isEmpty()) {
                showError("Vui lòng nhập số phòng tắm!");
                txtBathrooms.requestFocus();
                return;
            }
            
            // ========== GET VALUES ==========
            String buildingStr = (String) cboBuilding.getSelectedItem();
            int buildingId = Integer.parseInt(buildingStr.split(" - ")[0]);
            
            String floorStr = (String) cboFloor.getSelectedItem();
            int floor = Integer.parseInt(floorStr.replace("Tầng ", ""));
            
            String number = txtNumber.getText().trim();
            float area = Float.parseFloat(txtArea.getText().trim());
            int bedrooms = Integer.parseInt(txtBedrooms.getText().trim());
            int bathrooms = Integer.parseInt(txtBathrooms.getText().trim());
            
            // ========== VALIDATE RANGES ==========
            if (area <= 0) {
                showError("Diện tích phải lớn hơn 0!");
                txtArea.requestFocus();
                return;
            }
            
            if (bedrooms < 0) {
                showError("Số phòng ngủ không được âm!");
                txtBedrooms.requestFocus();
                return;
            }
            
            if (bathrooms < 0) {
                showError("Số phòng tắm không được âm!");
                txtBathrooms.requestFocus();
                return;
            }
            
            // ========== SAVE TO DATABASE ==========
            if (dao.ApartmentDAO.addApartment(buildingId, number, floor, area, bedrooms, bathrooms)) {
                JOptionPane.showMessageDialog(this, 
                    "✓ Thêm căn hộ thành công!\n\n" +
                    "Tòa nhà: " + buildingStr + "\n" +
                    "Tầng: " + floor + "\n" +
                    "Số căn hộ: " + number, 
                    "Thành công", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Refresh parent frame
                if (getOwner() instanceof MainFrame) {
                    ((MainFrame) getOwner()).loadBuildingsData();
                }
                
                // Ask if want to add more
                int choice = JOptionPane.showConfirmDialog(this,
                    "Bạn có muốn thêm căn hộ khác không?",
                    "Thêm tiếp",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
                
                if (choice == JOptionPane.YES_OPTION) {
                    // Clear form for next entry
                    clearForm();
                } else {
                    dispose();
                }
            } else {
                showError("Thêm căn hộ thất bại!\nCó thể căn hộ đã tồn tại.");
            }
            
        } catch (NumberFormatException ex) {
            showError("Vui lòng nhập số hợp lệ cho các trường số!");
        } catch (Exception ex) {
            showError("Lỗi: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    // ==================== HELPER METHODS ====================
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, 
            message, 
            "Lỗi", 
            JOptionPane.ERROR_MESSAGE);
    }
    
    private void clearForm() {
        txtNumber.setText("");
        txtArea.setText("");
        txtBedrooms.setText("");
        txtBathrooms.setText("");
        cboStatus.setSelectedIndex(0);
        txtNumber.requestFocus();
    }
}