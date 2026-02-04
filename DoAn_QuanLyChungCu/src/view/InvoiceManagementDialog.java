package view;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import model.Apartment;
import dao.InvoiceDAO;

public class InvoiceManagementDialog extends JDialog {
    private Apartment apartment;
    private JTabbedPane tabbedPane;
    private boolean hasChanges = false;
    
    public InvoiceManagementDialog(Frame parent, Apartment apartment) {
        super(parent, "Quản lý hóa đơn - Căn " + apartment.getNumber(), true);
        this.apartment = apartment;
        
        setSize(1000, 650);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Main content
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        loadInvoiceData();
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(25, 103, 210));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel("QUẢN LÝ HÓA ĐƠN");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel infoLabel = new JLabel(String.format("Căn hộ: %s | Diện tích: %.1f m²", 
            apartment.getNumber(), apartment.getArea()));
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoLabel.setForeground(new Color(200, 220, 255));
        
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(new Color(25, 103, 210));
        leftPanel.add(titleLabel);
        leftPanel.add(infoLabel);
        
        panel.add(leftPanel, BorderLayout.WEST);
        
        return panel;
    }
    
    private void loadInvoiceData() {
        Map<String, java.util.List<Map<String, Object>>> invoices = InvoiceDAO.getAllInvoices(apartment.getId());
        
        // Tab tiền điện
        JPanel electricityPanel = createInvoicePanel("electricity", invoices.get("electricity"));
        tabbedPane.addTab("Tiền điện", electricityPanel);
        
        // Tab tiền nước
        JPanel waterPanel = createInvoicePanel("water", invoices.get("water"));
        tabbedPane.addTab("Tiền nước", waterPanel);
        
        // Tab phí quản lý
        JPanel managementPanel = createInvoicePanel("management", invoices.get("management"));
        tabbedPane.addTab("Phí quản lý", managementPanel);
        
        // Tab dịch vụ khác
        JPanel otherPanel = createInvoicePanel("other", invoices.get("other"));
        tabbedPane.addTab("Dịch vụ khác", otherPanel);
    }
    
    private JPanel createInvoicePanel(String type, java.util.List<Map<String, Object>> invoiceList) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        if (invoiceList.isEmpty()) {
            JLabel emptyLabel = new JLabel("Chưa phát sinh phí");
            emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(emptyLabel, BorderLayout.CENTER);
            
            // Thêm nút tạo hóa đơn mới
            JButton btnCreate = new JButton("+ Tạo hóa đơn mới");
            btnCreate.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnCreate.setBackground(new Color(76, 175, 80));
            btnCreate.setForeground(Color.WHITE);
            btnCreate.setFocusPainted(false);
            btnCreate.addActionListener(e -> createNewInvoice(type));
            
            JPanel btnPanel = new JPanel();
            btnPanel.setBackground(Color.WHITE);
            btnPanel.add(btnCreate);
            panel.add(btnPanel, BorderLayout.SOUTH);
            
            return panel;
        }
        
        // Tạo table
        String[] columns;
        if ("electricity".equals(type) || "water".equals(type)) {
            columns = new String[]{"Tháng/Năm", "Chỉ số cũ", "Chỉ số mới", "Tiêu thụ", "Thành tiền", "Trạng thái", "Ngày TT", "Thao tác"};
        } else if ("management".equals(type)) {
            columns = new String[]{"Tháng/Năm", "Số tiền", "Trạng thái", "Ngày TT", "Thao tác"};
        } else {
            columns = new String[]{"Dịch vụ", "Tháng/Năm", "Số tiền", "Trạng thái", "Ngày TT", "Thao tác"};
        }
        
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == columns.length - 1; // Chỉ cột thao tác
            }
        };
        
        // Thêm dữ liệu
        for (Map<String, Object> invoice : invoiceList) {
            Object[] row;
            if ("electricity".equals(type) || "water".equals(type)) {
                row = new Object[]{
                    invoice.get("month") + "/" + invoice.get("year"),
                    invoice.get("oldReading"),
                    invoice.get("newReading"),
                    invoice.get("consumption"),
                    String.format("%,.0f VNĐ", (Double) invoice.get("amount")),
                    getStatusDisplay((String) invoice.get("status")),
                    invoice.get("paymentDate") != null ? invoice.get("paymentDate").toString() : "Chưa TT",
                    invoice.get("id")
                };
            } else if ("management".equals(type)) {
                row = new Object[]{
                    invoice.get("month") + "/" + invoice.get("year"),
                    String.format("%,.0f VNĐ", (Double) invoice.get("amount")),
                    getStatusDisplay((String) invoice.get("status")),
                    invoice.get("paymentDate") != null ? invoice.get("paymentDate").toString() : "Chưa TT",
                    invoice.get("id")
                };
            } else {
                row = new Object[]{
                    invoice.get("serviceName"),
                    invoice.get("month") + "/" + invoice.get("year"),
                    String.format("%,.0f VNĐ", (Double) invoice.get("amount")),
                    getStatusDisplay((String) invoice.get("status")),
                    invoice.get("paymentDate") != null ? invoice.get("paymentDate").toString() : "Chưa TT",
                    invoice.get("id")
                };
            }
            model.addRow(row);
        }
        
        JTable table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        
        // Thêm button renderer và editor cho cột thao tác
        table.getColumn("Thao tác").setCellRenderer(new ActionButtonRenderer());
        table.getColumn("Thao tác").setCellEditor(new ActionButtonEditor(new JCheckBox(), type));
        table.getColumn("Thao tác").setPreferredWidth(250);
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Bottom panel chứa summary và nút thêm
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 0));
        bottomPanel.setBackground(Color.WHITE);
        
        // Summary panel
        JPanel summaryPanel = createSummaryPanel(invoiceList);
        bottomPanel.add(summaryPanel, BorderLayout.CENTER);
        
        // Nút thêm hóa đơn mới
        JButton btnAddInvoice = new JButton("+ Tạo hóa đơn mới");
        btnAddInvoice.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAddInvoice.setBackground(new Color(76, 175, 80));
        btnAddInvoice.setForeground(Color.WHITE);
        btnAddInvoice.setFocusPainted(false);
        btnAddInvoice.setPreferredSize(new Dimension(150, 35));
        btnAddInvoice.addActionListener(e -> createNewInvoice(type));
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(btnAddInvoice);
        
        bottomPanel.add(btnPanel, BorderLayout.EAST);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createSummaryPanel(java.util.List<Map<String, Object>> invoiceList) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(new LineBorder(new Color(25, 103, 210)));
        
        double totalUnpaid = 0;
        double totalPaid = 0;
        
        for (Map<String, Object> invoice : invoiceList) {
            String status = (String) invoice.get("status");
            double amount = (Double) invoice.get("amount");
            
            if ("da_thanh_toan".equals(status)) {
                totalPaid += amount;
            } else {
                totalUnpaid += amount;
            }
        }
        
        JLabel lblPaid = new JLabel(String.format("Đã thanh toán: %,.0f VNĐ", totalPaid));
        lblPaid.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPaid.setForeground(new Color(76, 175, 80));
        
        JLabel lblUnpaid = new JLabel(String.format("Chưa thanh toán: %,.0f VNĐ", totalUnpaid));
        lblUnpaid.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUnpaid.setForeground(new Color(244, 67, 54));
        
        panel.add(lblPaid);
        panel.add(new JLabel("|"));
        panel.add(lblUnpaid);
        
        return panel;
    }
    
    private void createNewInvoice(String type) {
        CreateInvoiceDialog dialog = new CreateInvoiceDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            apartment,
            type,
            null  // null = tạo mới
        );
        dialog.setVisible(true);
        
        if (dialog.isSuccess()) {
            hasChanges = true;
            refreshData();
        }
    }
    
    private void updateInvoice(String type, int invoiceId) {
        CreateInvoiceDialog dialog = new CreateInvoiceDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            apartment,
            type,
            invoiceId  // truyền ID để cập nhật
        );
        dialog.setVisible(true);
        
        if (dialog.isSuccess()) {
            hasChanges = true;
            refreshData();
        }
    }
    
    private void deleteInvoice(String type, int invoiceId) {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc chắn muốn xóa hóa đơn này?\nHành động này không thể hoàn tác!",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = false;
            
            switch (type) {
                case "electricity":
                    success = InvoiceDAO.deleteElectricityInvoice(invoiceId);
                    break;
                case "water":
                    success = InvoiceDAO.deleteWaterInvoice(invoiceId);
                    break;
                case "management":
                    success = InvoiceDAO.deleteManagementFeeInvoice(invoiceId);
                    break;
                case "other":
                    success = InvoiceDAO.deleteOtherServiceInvoice(invoiceId);
                    break;
            }
            
            if (success) {
                hasChanges = true;
                JOptionPane.showMessageDialog(
                    this,
                    "✓ Xóa hóa đơn thành công!",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE
                );
                refreshData();
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "✗ Xóa hóa đơn thất bại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
    
    private void refreshData() {
        tabbedPane.removeAll();
        loadInvoiceData();
        tabbedPane.revalidate();
        tabbedPane.repaint();
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setBackground(new Color(240, 242, 245));
        
        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnRefresh.addActionListener(e -> refreshData());
        
        JButton btnClose = new JButton("Đóng");
        btnClose.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnClose.addActionListener(e -> dispose());
        
        panel.add(btnRefresh);
        panel.add(btnClose);
        
        return panel;
    }
    
    private String getStatusDisplay(String status) {
        switch (status) {
            case "da_thanh_toan": return "Đã thanh toán";
            case "chua_thanh_toan": return "Chưa thanh toán";
            case "qua_han": return "Quá hạn";
            default: return status;
        }
    }
    
    public boolean hasChanges() {
        return hasChanges;
    }
    
    // Action Button Renderer - Hiển thị các nút hành động
    class ActionButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton btnPay, btnEdit, btnDelete;
        
        public ActionButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 2));
            
            btnPay = new JButton("Thanh toán");
            btnPay.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            btnPay.setBackground(new Color(76, 175, 80));
            btnPay.setForeground(Color.WHITE);
            btnPay.setFocusPainted(false);
            btnPay.setPreferredSize(new Dimension(75, 25));
            
            btnEdit = new JButton("Sửa");
            btnEdit.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            btnEdit.setBackground(new Color(33, 150, 243));
            btnEdit.setForeground(Color.WHITE);
            btnEdit.setFocusPainted(false);
            btnEdit.setPreferredSize(new Dimension(60, 25));
            
            btnDelete = new JButton("Xóa");
            btnDelete.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            btnDelete.setBackground(new Color(244, 67, 54));
            btnDelete.setForeground(Color.WHITE);
            btnDelete.setFocusPainted(false);
            btnDelete.setPreferredSize(new Dimension(60, 25));
            
            add(btnPay);
            add(btnEdit);
            add(btnDelete);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            String status = (String) table.getValueAt(row, table.getColumnCount() - 3);
            
            if (status.contains("Đã thanh toán")) {
                btnPay.setText("Đã TT");
                btnPay.setBackground(new Color(200, 230, 201));
                btnPay.setEnabled(false);
                btnEdit.setEnabled(false);
            } else {
                btnPay.setText("Thanh toán");
                btnPay.setBackground(new Color(76, 175, 80));
                btnPay.setEnabled(true);
                btnEdit.setEnabled(true);
            }
            
            return this;
        }
    }
    
    // Action Button Editor - Xử lý sự kiện khi click nút
    class ActionButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton btnPay, btnEdit, btnDelete;
        private String type;
        private int invoiceId;
        private String currentAction;
        
        public ActionButtonEditor(JCheckBox checkBox, String type) {
            super(checkBox);
            this.type = type;
            
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 2));
            
            btnPay = new JButton("Thanh toán");
            btnPay.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            btnPay.setBackground(new Color(76, 175, 80));
            btnPay.setForeground(Color.WHITE);
            btnPay.setFocusPainted(false);
            btnPay.setPreferredSize(new Dimension(75, 25));
            btnPay.addActionListener(e -> {
                currentAction = "pay";
                fireEditingStopped();
            });
            
            btnEdit = new JButton("Sửa");
            btnEdit.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            btnEdit.setBackground(new Color(33, 150, 243));
            btnEdit.setForeground(Color.WHITE);
            btnEdit.setFocusPainted(false);
            btnEdit.setPreferredSize(new Dimension(60, 25));
            btnEdit.addActionListener(e -> {
                currentAction = "edit";
                fireEditingStopped();
            });
            
            btnDelete = new JButton("Xóa");
            btnDelete.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            btnDelete.setBackground(new Color(244, 67, 54));
            btnDelete.setForeground(Color.WHITE);
            btnDelete.setFocusPainted(false);
            btnDelete.setPreferredSize(new Dimension(60, 25));
            btnDelete.addActionListener(e -> {
                currentAction = "delete";
                fireEditingStopped();
            });
            
            panel.add(btnPay);
            panel.add(btnEdit);
            panel.add(btnDelete);
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            
            invoiceId = (Integer) value;
            String status = (String) table.getValueAt(row, table.getColumnCount() - 3);
            
            if (status.contains("Đã thanh toán")) {
                btnPay.setText("Đã TT");
                btnPay.setBackground(new Color(200, 230, 201));
                btnPay.setEnabled(false);
                btnEdit.setEnabled(false);
            } else {
                btnPay.setText("Thanh toán");
                btnPay.setBackground(new Color(76, 175, 80));
                btnPay.setEnabled(true);
                btnEdit.setEnabled(true);
            }
            
            currentAction = "";
            return panel;
        }
        
        @Override
        public Object getCellEditorValue() {
            if ("pay".equals(currentAction)) {
                processPayment();
            } else if ("edit".equals(currentAction)) {
                updateInvoice(type, invoiceId);
            } else if ("delete".equals(currentAction)) {
                deleteInvoice(type, invoiceId);
            }
            return invoiceId;
        }
        
        private void processPayment() {
            int confirm = JOptionPane.showConfirmDialog(
                InvoiceManagementDialog.this,
                "Xác nhận thanh toán hóa đơn này?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = false;
                
                switch (type) {
                    case "electricity":
                        success = InvoiceDAO.payElectricityInvoice(invoiceId);
                        break;
                    case "water":
                        success = InvoiceDAO.payWaterInvoice(invoiceId);
                        break;
                    case "management":
                        success = InvoiceDAO.payManagementFeeInvoice(invoiceId);
                        break;
                    case "other":
                        success = InvoiceDAO.payOtherServiceInvoice(invoiceId);
                        break;
                }
                
                if (success) {
                    hasChanges = true;
                    JOptionPane.showMessageDialog(
                        InvoiceManagementDialog.this,
                        "✓ Thanh toán thành công!",
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(
                        InvoiceManagementDialog.this,
                        "✗ Thanh toán thất bại!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }
    }
}