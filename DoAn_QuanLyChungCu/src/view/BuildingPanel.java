package view;

import javax.swing.JPanel;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;
import java.awt.geom.*;
import java.util.*;
import model.Apartment;
import model.Household;
import model.RentalContract;
import model.User;
import dao.BuildingDAO;
import dao.*;


public class BuildingPanel extends JPanel {

    private int buildingId;
    private String buildingName;
    private int totalFloors;
    private JPanel floorContainer;
    
    public BuildingPanel(int buildingId, String buildingName, int totalFloors) {
        this.buildingId = buildingId;
        this.buildingName = buildingName;
        this.totalFloors = totalFloors;
        
        setLayout(new BorderLayout());
        setBackground(new Color(240, 242, 245));
        
        // Panel header
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Panel chứa các tầng với scroll NGANG và DỌC
        JScrollPane scrollPane = new JScrollPane(createFloorsPanel());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    // ==================== HEADER PANEL ====================
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(new CompoundBorder(
            new LineBorder(new Color(220, 220, 220)),
            new EmptyBorder(5, 10, 5, 10)
        ));
        
        JLabel titleLabel = new JLabel("🏢 " + buildingName);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(33, 33, 33));
        
        JLabel infoLabel = new JLabel(totalFloors + " tầng");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoLabel.setForeground(new Color(117, 117, 117));
        
        panel.add(titleLabel);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(infoLabel);
        
        return panel;
    }
    
    // ==================== FLOORS PANEL ====================
    private JPanel createFloorsPanel() {
        floorContainer = new JPanel();
        floorContainer.setLayout(new BoxLayout(floorContainer, BoxLayout.Y_AXIS));
        floorContainer.setBackground(new Color(240, 242, 245));
        floorContainer.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Lấy danh sách căn hộ từ CSDL
        List<Apartment> apartments = BuildingDAO.getApartmentsByBuilding(buildingId);
        
        // Nhóm căn hộ theo tầng
        Map<Integer, List<Apartment>> apartmentsByFloor = new TreeMap<>(Collections.reverseOrder());
        for (Apartment apt : apartments) {
            apartmentsByFloor.computeIfAbsent(apt.getFloor(), k -> new ArrayList<>()).add(apt);
        }
        
        // Hiển thị từ tầng cao nhất xuống tầng 1
        for (int floor : apartmentsByFloor.keySet()) {
            floorContainer.add(createFloorPanel(floor, apartmentsByFloor.get(floor)));
            floorContainer.add(Box.createVerticalStrut(15));
        }
        
        floorContainer.add(Box.createVerticalGlue());
        
        // Wrap trong JPanel với preferred width để kích hoạt horizontal scroll
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(240, 242, 245));
        wrapper.add(floorContainer, BorderLayout.WEST);
        
        return wrapper;
    }
    
    // ==================== FLOOR PANEL ====================
    private JPanel createFloorPanel(int floor, List<Apartment> apartments) {
        JPanel floorPanel = new JPanel();
        floorPanel.setLayout(new BorderLayout());
        floorPanel.setBackground(new Color(240, 242, 245));
        floorPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        
        // Floor label
        JLabel floorLabel = new JLabel("Tầng " + floor + " (" + apartments.size() + " căn)");
        floorLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        floorLabel.setForeground(new Color(25, 103, 210));
        floorLabel.setBorder(new EmptyBorder(5, 10, 5, 10));
        
        floorPanel.add(floorLabel, BorderLayout.NORTH);
        
        // Panel chứa các căn hộ với scroll ngang
        JPanel apartmentsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        apartmentsPanel.setBackground(new Color(240, 242, 245));
        apartmentsPanel.setBorder(new EmptyBorder(5, 10, 10, 10));
        
        // Thêm các căn hộ từ CSDL
        for (Apartment apt : apartments) {
            apartmentsPanel.add(createApartmentCard(apt));
        }
        
        floorPanel.add(apartmentsPanel, BorderLayout.CENTER);
        
        return floorPanel;
    }
    
    // ==================== APARTMENT CARD ====================
    private JPanel createApartmentCard(Apartment apartment) {
        String apartmentNumber = apartment.getNumber();
        String status = apartment.getStatus();
        
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                int radius = 12;
                
                // Vẽ nền với bo góc
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, width - 1, height - 1, radius, radius);
                
                // Vẽ border màu theo trạng thái
                Color borderColor = getStatusColor(status);
                g2d.setColor(borderColor);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(0, 0, width - 1, height - 1, radius, radius);
                
                // Vẽ indicator tròn (dấu chấm màu)
                g2d.setColor(borderColor);
                g2d.fillOval(width - 20, 10, 10, 10);
            }
        };
        
        card.setLayout(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(140, 110));
        card.setMaximumSize(new Dimension(140, 110));
        card.setBorder(new EmptyBorder(10, 10, 10, 10));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Thêm thông tin căn hộ
        JLabel aptNumLabel = new JLabel("Căn " + apartmentNumber);
        aptNumLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        aptNumLabel.setForeground(new Color(33, 33, 33));
        
        JLabel sizeLabel = new JLabel(String.format("%.1f m²", apartment.getArea()));
        sizeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        sizeLabel.setForeground(new Color(117, 117, 117));
        
        JLabel statusLabel = new JLabel(apartment.getStatusDisplay());
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        statusLabel.setForeground(getStatusColor(status));
        statusLabel.setOpaque(true);
        statusLabel.setBackground(new Color(240, 240, 240));
        statusLabel.setBorder(new EmptyBorder(2, 5, 2, 5));
        
        JLabel bedroomLabel = new JLabel(apartment.getBedrooms() + " PN/" + apartment.getBathrooms() + " PT");
        bedroomLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        bedroomLabel.setForeground(new Color(117, 117, 117));
        
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.add(aptNumLabel);
        infoPanel.add(Box.createVerticalStrut(2));
        infoPanel.add(sizeLabel);
        infoPanel.add(Box.createVerticalStrut(2));
        infoPanel.add(statusLabel);
        infoPanel.add(Box.createVerticalStrut(2));
        infoPanel.add(bedroomLabel);
        
        card.add(infoPanel, BorderLayout.CENTER);
        
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBorder(new CompoundBorder(
                    new LineBorder(getStatusColor(status), 2),
                    new EmptyBorder(8, 8, 8, 8)
                ));
                card.setBackground(new Color(248, 248, 248));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBorder(new EmptyBorder(10, 10, 10, 10));
                card.setBackground(Color.WHITE);
            }
            
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showApartmentMenu(apartment, card, evt);
            }
        });
        
        return card;
    }
    
    // ==================== SHOW APARTMENT MENU ====================
    private void showApartmentMenu(Apartment apartment, Component invoker, java.awt.event.MouseEvent evt) {
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setBackground(Color.WHITE);
        popupMenu.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        
        // Menu item: Xem chi tiết
        JMenuItem viewDetailsItem = createMenuItem("Xem chi tiết đầy đủ", new Color(25, 103, 210));
        viewDetailsItem.addActionListener(e -> showModernApartmentDetails(apartment));
        popupMenu.add(viewDetailsItem);
        
        popupMenu.addSeparator();
        
        // Menu items theo trạng thái
        if ("trong".equals(apartment.getStatus())) {
            JMenuItem createContractItem = createMenuItem("Tạo hợp đồng thuê", new Color(76, 175, 80));
            createContractItem.addActionListener(e -> createRentalContract(apartment));
            popupMenu.add(createContractItem);
            
            JMenuItem maintenanceItem = createMenuItem("Chuyển sang bảo trì", new Color(255, 152, 0));
            maintenanceItem.addActionListener(e -> updateApartmentStatus(apartment, "bao_tri"));
            popupMenu.add(maintenanceItem);
            
        } else if ("co_nguoi_o".equals(apartment.getStatus())) {
            JMenuItem viewContractItem = createMenuItem("Xem hợp đồng", new Color(25, 103, 210));
            viewContractItem.addActionListener(e -> showModernContractDetails(apartment));
            popupMenu.add(viewContractItem);
            
            JMenuItem viewInvoicesItem = createMenuItem("Xem hóa đơn", new Color(255, 152, 0));
            viewInvoicesItem.addActionListener(e -> showModernInvoiceDetails(apartment));
            popupMenu.add(viewInvoicesItem);
            
            JMenuItem manageInvoicesItem = createMenuItem("Quản lý hóa đơn", new Color(156, 39, 176));
            manageInvoicesItem.addActionListener(e -> manageInvoices(apartment));
            popupMenu.add(manageInvoicesItem);
            
            popupMenu.addSeparator();
            
            JMenuItem terminateItem = createMenuItem("Chấm dứt hợp đồng", new Color(244, 67, 54));
            terminateItem.addActionListener(e -> terminateContract(apartment));
            popupMenu.add(terminateItem);
            
        } else if ("bao_tri".equals(apartment.getStatus())) {
            JMenuItem completeItem = createMenuItem("Hoàn thành bảo trì", new Color(76, 175, 80));
            completeItem.addActionListener(e -> updateApartmentStatus(apartment, "trong"));
            popupMenu.add(completeItem);
        }
        
        popupMenu.show(invoker, evt.getX(), evt.getY());
    }
    
    // ==================== MODERN APARTMENT DETAILS DIALOG ====================
    private void showModernApartmentDetails(Apartment apartment) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Chi tiết căn hộ " + apartment.getNumber(), true);
        dialog.setSize(600, 700);
        dialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(Color.WHITE);
        
        // HEADER
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(25, 103, 210));
        headerPanel.setPreferredSize(new Dimension(0, 80));
        
        JLabel titleLabel = new JLabel("CĂN HỘ " + apartment.getNumber());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(20, 30, 10, 30));
        
       
        
        headerPanel.add(titleLabel, BorderLayout.NORTH);
       
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // CONTENT
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(25, 30, 25, 30));
        
        // Basic Info
        contentPanel.add(createSectionLabel("THÔNG TIN CƠ BẢN"));
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(createInfoRow("Diện tích", String.format("%.1f m²", apartment.getArea())));
        contentPanel.add(createInfoRow("Số phòng ngủ", apartment.getBedrooms() + " phòng"));
        contentPanel.add(createInfoRow("Số phòng tắm", apartment.getBathrooms() + " phòng"));
        contentPanel.add(createInfoRow("Trạng thái", apartment.getStatusDisplay()));
        
        contentPanel.add(Box.createVerticalStrut(25));
        
        // Contract Info
        RentalContract contract = dao.RentalContractDAO.getActiveContractByApartment(apartment.getId());
        if (contract != null) {
            User resident = dao.UserDAO.getUserById(contract.getResidentId());
            
            contentPanel.add(createSectionLabel("HỢP ĐỒNG HIỆN TẠI"));
            contentPanel.add(Box.createVerticalStrut(15));
            contentPanel.add(createInfoRow("Người thuê", resident != null ? resident.getFullName() : "Chưa có"));
            contentPanel.add(createInfoRow("Số điện thoại", resident != null ? resident.getPhone() : "Chưa có"));
            contentPanel.add(createInfoRow("Ngày bắt đầu", contract.getStartDate().toString()));
            contentPanel.add(createInfoRow("Ngày kết thúc", contract.getEndDate() != null ? contract.getEndDate().toString() : "Không xác định"));
            contentPanel.add(createInfoRow("Tiền thuê/tháng", String.format("%,.0f VNĐ", contract.getMonthlyRent())));
            contentPanel.add(createInfoRow("Tiền cọc", String.format("%,.0f VNĐ", contract.getDeposit())));
            
            contentPanel.add(Box.createVerticalStrut(25));
        }
        
        // Household Info
        Household household = dao.HouseholdDAO.getHouseholdByApartment(apartment.getId());
        if (household != null) {
            contentPanel.add(createSectionLabel("HỘ GIA ĐÌNH"));
            contentPanel.add(Box.createVerticalStrut(15));
            contentPanel.add(createInfoRow("Số nhân khẩu", household.getNumberOfMembers() + " người"));
            contentPanel.add(createInfoRow("Điện thoại", household.getContactPhone()));
            contentPanel.add(createInfoRow("Email", household.getContactEmail() != null ? household.getContactEmail() : "Chưa có"));
        }
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // FOOTER
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        footerPanel.setBackground(new Color(250, 250, 250));
        footerPanel.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        JButton btnClose = new JButton("Đóng");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnClose.setBackground(new Color(100, 100, 100));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.setPreferredSize(new Dimension(120, 40));
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dialog.dispose());
        
        footerPanel.add(btnClose);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        dialog.setContentPane(mainPanel);
        dialog.setVisible(true);
    }
    
    // ==================== MODERN CONTRACT DETAILS DIALOG ====================
    private void showModernContractDetails(Apartment apartment) {
        RentalContract contract = dao.RentalContractDAO.getActiveContractByApartment(apartment.getId());
        if (contract == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy hợp đồng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        User resident = dao.UserDAO.getUserById(contract.getResidentId());
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Hợp đồng căn hộ " + apartment.getNumber(), true);
        dialog.setSize(600, 600);
        dialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(Color.WHITE);
        
        // HEADER
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(76, 175, 80));
        headerPanel.setPreferredSize(new Dimension(0, 80));
        
        JLabel titleLabel = new JLabel("HỢP ĐỒNG THUÊ CĂN HỘ");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(20, 30, 10, 30));
        
        
        
        headerPanel.add(titleLabel, BorderLayout.NORTH);
      
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // CONTENT
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(25, 30, 25, 30));
        
        contentPanel.add(createSectionLabel("THÔNG TIN BÊN THUÊ"));
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(createInfoRow("Họ tên", resident != null ? resident.getFullName() : "Chưa có"));
        contentPanel.add(createInfoRow("Số điện thoại", resident != null ? resident.getPhone() : "Chưa có"));
        contentPanel.add(createInfoRow("Email", resident != null ? resident.getEmail() : "Chưa có"));
        
        contentPanel.add(Box.createVerticalStrut(25));
        
        contentPanel.add(createSectionLabel("THÔNG TIN HỢP ĐỒNG"));
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(createInfoRow("Căn hộ", apartment.getNumber()));
        contentPanel.add(createInfoRow("Ngày bắt đầu", contract.getStartDate().toString()));
        contentPanel.add(createInfoRow("Ngày kết thúc", contract.getEndDate() != null ? contract.getEndDate().toString() : "Không xác định"));
        contentPanel.add(createInfoRow("Trạng thái", contract.getStatusDisplay()));
        
        contentPanel.add(Box.createVerticalStrut(25));
        
        contentPanel.add(createSectionLabel("THÔNG TIN TÀI CHÍNH"));
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(createInfoRow("Tiền thuê/tháng", String.format("%,.0f VNĐ", contract.getMonthlyRent())));
        contentPanel.add(createInfoRow("Tiền cọc", String.format("%,.0f VNĐ", contract.getDeposit())));
        
        if (contract.getNotes() != null && !contract.getNotes().isEmpty()) {
            contentPanel.add(Box.createVerticalStrut(25));
            contentPanel.add(createSectionLabel("GHI CHÚ"));
            contentPanel.add(Box.createVerticalStrut(15));
            
            JTextArea notesArea = new JTextArea(contract.getNotes());
            notesArea.setEditable(false);
            notesArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            notesArea.setBackground(new Color(245, 245, 245));
            notesArea.setBorder(new EmptyBorder(10, 10, 10, 10));
            notesArea.setLineWrap(true);
            notesArea.setWrapStyleWord(true);
            contentPanel.add(notesArea);
        }
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // FOOTER
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        footerPanel.setBackground(new Color(250, 250, 250));
        footerPanel.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        JButton btnClose = new JButton("Đóng");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnClose.setBackground(new Color(100, 100, 100));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.setPreferredSize(new Dimension(120, 40));
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dialog.dispose());
        
        footerPanel.add(btnClose);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        dialog.setContentPane(mainPanel);
        dialog.setVisible(true);
    }
    
    // ==================== MODERN INVOICE DETAILS DIALOG ====================
    private void showModernInvoiceDetails(Apartment apartment) {
        java.util.Map<String, java.util.List<java.util.Map<String, Object>>> invoices = 
            dao.InvoiceDAO.getUnpaidInvoices(apartment.getId());
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Hóa đơn căn hộ " + apartment.getNumber(), true);
        dialog.setSize(650, 700);
        dialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(Color.WHITE);
        
        // HEADER
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(255, 152, 0));
        headerPanel.setPreferredSize(new Dimension(0, 80));
        
        JLabel titleLabel = new JLabel("HÓA ĐƠN CHƯA THANH TOÁN");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(20, 30, 10, 30));
        
        JLabel subtitleLabel = new JLabel("Căn hộ " + apartment.getNumber());
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(255, 230, 200));
        subtitleLabel.setBorder(new EmptyBorder(0, 30, 20, 30));
        
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.CENTER);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // CONTENT
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(25, 30, 25, 30));
        
        double totalUnpaid = 0;
        
        // Hóa đơn điện
        if (!invoices.get("electricity").isEmpty()) {
            contentPanel.add(createSectionLabel("TIỀN ĐIỆN"));
            contentPanel.add(Box.createVerticalStrut(15));
            
            for (java.util.Map<String, Object> inv : invoices.get("electricity")) {
                double amount = (double) inv.get("amount");
                totalUnpaid += amount;
                contentPanel.add(createInvoiceRow(
                    "Tháng " + inv.get("month") + "/" + inv.get("year"),
                    String.format("%,.0f VNĐ", amount)
                ));
            }
            contentPanel.add(Box.createVerticalStrut(20));
        }
        
        // Hóa đơn nước
        if (!invoices.get("water").isEmpty()) {
            contentPanel.add(createSectionLabel("TIỀN NƯỚC"));
            contentPanel.add(Box.createVerticalStrut(15));
            
            for (java.util.Map<String, Object> inv : invoices.get("water")) {
                double amount = (double) inv.get("amount");
                totalUnpaid += amount;
                contentPanel.add(createInvoiceRow(
                    "Tháng " + inv.get("month") + "/" + inv.get("year"),
                    String.format("%,.0f VNĐ", amount)
                ));
            }
            contentPanel.add(Box.createVerticalStrut(20));
        }
        
        // Hóa đơn quản lý
        if (!invoices.get("management").isEmpty()) {
            contentPanel.add(createSectionLabel("PHÍ QUẢN LÝ"));
            contentPanel.add(Box.createVerticalStrut(15));
            
            for (java.util.Map<String, Object> inv : invoices.get("management")) {
                double amount = (double) inv.get("amount");
                totalUnpaid += amount;
                contentPanel.add(createInvoiceRow(
                    "Tháng " + inv.get("month") + "/" + inv.get("year"),
                    String.format("%,.0f VNĐ", amount)
                ));
            }
            contentPanel.add(Box.createVerticalStrut(20));
        }
        
        // Separator
        contentPanel.add(new JSeparator());
        contentPanel.add(Box.createVerticalStrut(20));
        
        // Total
        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setBackground(new Color(255, 245, 230));
        totalPanel.setBorder(new CompoundBorder(
            new LineBorder(new Color(255, 152, 0), 2),
            new EmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel totalLabel = new JLabel("TỔNG CỘNG");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalLabel.setForeground(new Color(230, 81, 0));
        
        JLabel totalAmount = new JLabel(String.format("%,.0f VNĐ", totalUnpaid));
        totalAmount.setFont(new Font("Segoe UI", Font.BOLD, 20));
        totalAmount.setForeground(new Color(230, 81, 0));
        
        totalPanel.add(totalLabel, BorderLayout.WEST);
        totalPanel.add(totalAmount, BorderLayout.EAST);
        
        contentPanel.add(totalPanel);
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // FOOTER
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        footerPanel.setBackground(new Color(250, 250, 250));
        footerPanel.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        JButton btnClose = new JButton("Đóng");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnClose.setBackground(new Color(100, 100, 100));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.setPreferredSize(new Dimension(120, 40));
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dialog.dispose());
        
        footerPanel.add(btnClose);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        dialog.setContentPane(mainPanel);
        dialog.setVisible(true);
    }
    
    // ==================== HELPER METHODS FOR MODERN DIALOGS ====================
    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(60, 60, 60));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
    
    private JPanel createInfoRow(String label, String value) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(8, 0, 8, 0));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        
        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblLabel.setForeground(new Color(100, 100, 100));
        lblLabel.setPreferredSize(new Dimension(150, 25));
        
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblValue.setForeground(new Color(33, 33, 33));
        
        panel.add(lblLabel, BorderLayout.WEST);
        panel.add(lblValue, BorderLayout.CENTER);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        return panel;
    }
    
    private JPanel createInvoiceRow(String period, String amount) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(new Color(250, 250, 250));
        panel.setBorder(new CompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(12, 15, 12, 15)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        JLabel lblPeriod = new JLabel(period);
        lblPeriod.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblPeriod.setForeground(new Color(80, 80, 80));
        
        JLabel lblAmount = new JLabel(amount);
        lblAmount.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblAmount.setForeground(new Color(230, 81, 0));
        
        panel.add(lblPeriod, BorderLayout.WEST);
        panel.add(lblAmount, BorderLayout.EAST);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        return panel;
    }
    
    // ==================== MANAGE INVOICES ====================
    private void manageInvoices(Apartment apartment) {
        InvoiceManagementDialog dialog = new InvoiceManagementDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            apartment
        );
        dialog.setVisible(true);
        
        if (dialog.hasChanges()) {
            refreshPanel();
        }
    }
    
    // ==================== CREATE RENTAL CONTRACT ====================
    private void createRentalContract(Apartment apartment) {
        CreateRentalContractDialog dialog = new CreateRentalContractDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            apartment
        );
        dialog.setVisible(true);
        
        if (dialog.isSuccess()) {
            refreshPanel();
        }
    }
    
    // ==================== TERMINATE CONTRACT ====================
    private void terminateContract(Apartment apartment) {
        RentalContract contract = dao.RentalContractDAO.getActiveContractByApartment(apartment.getId());
        
        if (contract == null) {
            JOptionPane.showMessageDialog(
                this,
                "Không tìm thấy hợp đồng đang hoạt động!",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "BẠN CÓ CHẮC CHẮN MUỐN CHẤM DỨT HỢP ĐỒNG?\n\n" +
            "Hành động này sẽ:\n" +
            "• Chấm dứt hợp đồng thuê\n" +
            "• Chuyển trạng thái căn hộ về TRỐNG\n" +
            "• Xóa hộ gia đình\n\n" +
            "Lưu ý: Vẫn còn hóa đơn chưa thanh toán!",
            "Xác nhận chấm dứt hợp đồng",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.RentalContractDAO.terminateContract(contract.getId())) {
                dao.HouseholdDAO.deleteHousehold(apartment.getId());
                
                if (dao.ApartmentDAO.updateApartmentStatus(apartment.getId(), "trong")) {
                    JOptionPane.showMessageDialog(
                        this,
                        "✓ Đã chấm dứt hợp đồng thành công!",
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    refreshPanel();
                } else {
                    JOptionPane.showMessageDialog(
                        this,
                        "Lỗi khi cập nhật trạng thái căn hộ!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Lỗi khi chấm dứt hợp đồng!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
    
    // ==================== CREATE MENU ITEM ====================
    private JMenuItem createMenuItem(String text, Color color) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        item.setForeground(color);
        item.setBackground(Color.WHITE);
        item.setBorder(new EmptyBorder(5, 10, 5, 10));
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
    
    // ==================== UPDATE APARTMENT STATUS ====================
    private void updateApartmentStatus(Apartment apartment, String newStatus) {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc chắn muốn cập nhật trạng thái căn hộ " + apartment.getNumber() + 
            " thành \"" + getStatusText(newStatus) + "\"?",
            "Xác nhận cập nhật",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = dao.ApartmentDAO.updateApartmentStatus(apartment.getId(), newStatus);
            
            if (success) {
                JOptionPane.showMessageDialog(
                    this,
                    "Cập nhật trạng thái thành công!",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE
                );
                refreshPanel();
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Cập nhật trạng thái thất bại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
    
    // ==================== REFRESH PANEL ====================
    public void refreshPanel() {
        floorContainer.removeAll();
        
        List<Apartment> apartments = BuildingDAO.getApartmentsByBuilding(buildingId);
        Map<Integer, List<Apartment>> apartmentsByFloor = new TreeMap<>(Collections.reverseOrder());
        for (Apartment apt : apartments) {
            apartmentsByFloor.computeIfAbsent(apt.getFloor(), k -> new ArrayList<>()).add(apt);
        }
        
        for (int floor : apartmentsByFloor.keySet()) {
            floorContainer.add(createFloorPanel(floor, apartmentsByFloor.get(floor)));
            floorContainer.add(Box.createVerticalStrut(15));
        }
        
        floorContainer.add(Box.createVerticalGlue());
        floorContainer.revalidate();
        floorContainer.repaint();
    }
    
    // ==================== HELPER METHODS ====================
    private Color getStatusColor(String status) {
        switch (status) {
            case "co_nguoi_o":
                return new Color(25, 103, 210);
            case "trong":
                return new Color(76, 175, 80);
            case "bao_tri":
                return new Color(255, 152, 0);
            default:
                return Color.GRAY;
        }
    }
    
    private String getStatusText(String status) {
        switch (status) {
            case "co_nguoi_o":
                return "Có người ở";
            case "trong":
                return "Trống";
            case "bao_tri":
                return "Bảo trì";
            default:
                return "Không xác định";
        }
    }
}