package view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.Map;
import model.Apartment;
import dao.InvoiceDAO;

public class CreateInvoiceDialog extends JDialog {
    private Apartment apartment;
    private String invoiceType;
    private Integer invoiceId;  // null = tạo mới, có giá trị = cập nhật
    private boolean success = false;
    
    private JTextField txtMonth, txtYear;
    private JTextField txtOldReading, txtNewReading, txtPrice;
    private JTextField txtAmount, txtServiceName;
    
    /**
     * Constructor cho cả tạo mới và cập nhật
     * @param invoiceId null = tạo mới, có giá trị = cập nhật
     */
    public CreateInvoiceDialog(Frame parent, Apartment apartment, String type, Integer invoiceId) {
        super(parent, invoiceId == null ? "Tạo hóa đơn mới" : "Cập nhật hóa đơn", true);
        this.apartment = apartment;
        this.invoiceType = type;
        this.invoiceId = invoiceId;
        
        setSize(500, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        
        // Header
        JLabel titleLabel = new JLabel(getTitle(type, invoiceId));
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(25, 103, 210));
        titleLabel.setBorder(new EmptyBorder(15, 15, 10, 15));
        add(titleLabel, BorderLayout.NORTH);
        
        // Form
        JPanel formPanel = createFormPanel(type);
        add(formPanel, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = createButtonPanel(invoiceId);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Load data nếu đang cập nhật
        if (invoiceId != null) {
            loadInvoiceData();
        } else {
            // Set default values cho tạo mới
            LocalDate now = LocalDate.now();
            txtMonth.setText(String.valueOf(now.getMonthValue()));
            txtYear.setText(String.valueOf(now.getYear()));
        }
    }
    
    private String getTitle(String type, Integer invoiceId) {
        String prefix = invoiceId == null ? "Tạo hóa đơn" : "Cập nhật hóa đơn";
        switch (type) {
            case "electricity": return prefix + " tiền điện";
            case "water": return prefix + " tiền nước";
            case "management": return prefix + " phí quản lý";
            case "other": return prefix + " dịch vụ khác";
            default: return prefix;
        }
    }
    
    private void loadInvoiceData() {
        Map<String, Object> data = null;
        
        switch (invoiceType) {
            case "electricity":
                data = InvoiceDAO.getElectricityInvoiceDetail(invoiceId);
                break;
            case "water":
                data = InvoiceDAO.getWaterInvoiceDetail(invoiceId);
                break;
            case "management":
                data = InvoiceDAO.getManagementFeeInvoiceDetail(invoiceId);
                break;
            case "other":
                data = InvoiceDAO.getOtherServiceInvoiceDetail(invoiceId);
                break;
        }
        
        if (data != null) {
            txtMonth.setText(String.valueOf(data.get("month")));
            txtYear.setText(String.valueOf(data.get("year")));
            
            if ("electricity".equals(invoiceType) || "water".equals(invoiceType)) {
                txtOldReading.setText(String.valueOf(data.get("oldReading")));
                txtNewReading.setText(String.valueOf(data.get("newReading")));
                txtPrice.setText(String.valueOf(data.get("pricePerUnit")));
            } else if ("management".equals(invoiceType)) {
                txtAmount.setText(String.valueOf(data.get("amount")));
            } else if ("other".equals(invoiceType)) {
                txtServiceName.setText((String) data.get("serviceName"));
                txtAmount.setText(String.valueOf(data.get("amount")));
            }
        }
    }
    
    private JPanel createFormPanel(String type) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));
        panel.setBackground(Color.WHITE);
        
        // Common fields
        panel.add(createFieldPanel("Tháng:", txtMonth = new JTextField()));
        panel.add(createFieldPanel("Năm:", txtYear = new JTextField()));
        
        if ("electricity".equals(type) || "water".equals(type)) {
            panel.add(createFieldPanel("Chỉ số cũ:", txtOldReading = new JTextField()));
            panel.add(createFieldPanel("Chỉ số mới:", txtNewReading = new JTextField()));
            panel.add(createFieldPanel("Đơn giá (VNĐ):", txtPrice = new JTextField()));
            
            if (invoiceId == null) { // Chỉ set giá trị mặc định khi tạo mới
                txtPrice.setText(type.equals("electricity") ? "3000" : "20000");
            }
        } else if ("management".equals(type)) {
            panel.add(createFieldPanel("Số tiền (VNĐ):", txtAmount = new JTextField()));
            if (invoiceId == null) {
                txtAmount.setText(String.valueOf((int)(apartment.getArea() * 50000)));
            }
        } else {
            panel.add(createFieldPanel("Tên dịch vụ:", txtServiceName = new JTextField()));
            panel.add(createFieldPanel("Số tiền (VNĐ):", txtAmount = new JTextField()));
        }
        
        return panel;
    }
    
    private JPanel createFieldPanel(String label, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setPreferredSize(new Dimension(120, 25));
        
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        field.setBorder(new CompoundBorder(
            new LineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 10, 5, 10)
        ));
        
        panel.add(lbl, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createButtonPanel(Integer invoiceId) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setBackground(new Color(240, 242, 245));
        
        String btnText = invoiceId == null ? "Tạo hóa đơn" : "Cập nhật";
        JButton btnAction = new JButton(btnText);
        btnAction.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAction.setBackground(invoiceId == null ? new Color(76, 175, 80) : new Color(33, 150, 243));
        btnAction.setForeground(Color.WHITE);
        btnAction.setFocusPainted(false);
        btnAction.addActionListener(e -> handleAction());
        
        JButton btnCancel = new JButton("✗ Hủy");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCancel.addActionListener(e -> dispose());
        
        panel.add(btnCancel);
        panel.add(btnAction);
        
        return panel;
    }

    private void handleAction() {
        try {
            int month = Integer.parseInt(txtMonth.getText().trim());
            int year = Integer.parseInt(txtYear.getText().trim());
            
            // Validate month and year
            if (month < 1 || month > 12) {
                JOptionPane.showMessageDialog(this,
                    "Tháng phải từ 1 đến 12!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (year < 2000 || year > 2100) {
                JOptionPane.showMessageDialog(this,
                    "Năm không hợp lệ!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            boolean result = false;
            
            if (invoiceId == null) {
                // Tạo mới
                result = createInvoice(month, year);
            } else {
                // Cập nhật
                result = updateInvoice(month, year);
            }
            
            if (result) {
                success = true;
                String message = invoiceId == null ? "✓ Tạo hóa đơn thành công!" : "✓ Cập nhật hóa đơn thành công!";
                JOptionPane.showMessageDialog(this,
                    message,
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                String message = invoiceId == null ? "✗ Tạo hóa đơn thất bại!" : "✗ Cập nhật hóa đơn thất bại!";
                JOptionPane.showMessageDialog(this,
                    message,
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng nhập đúng định dạng số!",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean createInvoice(int month, int year) {
        switch (invoiceType) {
            case "electricity":
                int oldElec = Integer.parseInt(txtOldReading.getText().trim());
                int newElec = Integer.parseInt(txtNewReading.getText().trim());
                double priceElec = Double.parseDouble(txtPrice.getText().trim());
                
                if (newElec < oldElec) {
                    JOptionPane.showMessageDialog(this,
                        "Chỉ số mới phải lớn hơn hoặc bằng chỉ số cũ!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                
                return InvoiceDAO.createElectricityInvoice(
                    apartment.getId(), month, year, oldElec, newElec, priceElec
                );
                
            case "water":
                int oldWater = Integer.parseInt(txtOldReading.getText().trim());
                int newWater = Integer.parseInt(txtNewReading.getText().trim());
                double priceWater = Double.parseDouble(txtPrice.getText().trim());
                
                if (newWater < oldWater) {
                    JOptionPane.showMessageDialog(this,
                        "Chỉ số mới phải lớn hơn hoặc bằng chỉ số cũ!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                
                return InvoiceDAO.createWaterInvoice(
                    apartment.getId(), month, year, oldWater, newWater, priceWater
                );
                
            case "management":
                double amount = Double.parseDouble(txtAmount.getText().trim());
                return InvoiceDAO.createManagementFeeInvoice(
                    apartment.getId(), month, year, amount
                );
                
            case "other":
                String serviceName = txtServiceName.getText().trim();
                if (serviceName.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                        "Vui lòng nhập tên dịch vụ!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                double serviceAmount = Double.parseDouble(txtAmount.getText().trim());
                return createOtherServiceInvoice(apartment.getId(), serviceName, 
                    serviceAmount, month, year);
        }
        return false;
    }
    
    private boolean updateInvoice(int month, int year) {
        switch (invoiceType) {
            case "electricity":
                int oldElec = Integer.parseInt(txtOldReading.getText().trim());
                int newElec = Integer.parseInt(txtNewReading.getText().trim());
                double priceElec = Double.parseDouble(txtPrice.getText().trim());
                
                if (newElec < oldElec) {
                    JOptionPane.showMessageDialog(this,
                        "Chỉ số mới phải lớn hơn hoặc bằng chỉ số cũ!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                
                return InvoiceDAO.updateElectricityInvoice(
                    invoiceId, month, year, oldElec, newElec, priceElec
                );
                
            case "water":
                int oldWater = Integer.parseInt(txtOldReading.getText().trim());
                int newWater = Integer.parseInt(txtNewReading.getText().trim());
                double priceWater = Double.parseDouble(txtPrice.getText().trim());
                
                if (newWater < oldWater) {
                    JOptionPane.showMessageDialog(this,
                        "Chỉ số mới phải lớn hơn hoặc bằng chỉ số cũ!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                
                return InvoiceDAO.updateWaterInvoice(
                    invoiceId, month, year, oldWater, newWater, priceWater
                );
                
            case "management":
                double amount = Double.parseDouble(txtAmount.getText().trim());
                return InvoiceDAO.updateManagementFeeInvoice(
                    invoiceId, month, year, amount
                );
                
            case "other":
                String serviceName = txtServiceName.getText().trim();
                if (serviceName.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                        "Vui lòng nhập tên dịch vụ!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                double serviceAmount = Double.parseDouble(txtAmount.getText().trim());
                return InvoiceDAO.updateOtherServiceInvoice(
                    invoiceId, serviceName, month, year, serviceAmount
                );
        }
        return false;
    }

    private boolean createOtherServiceInvoice(int apartmentId, String serviceName,
            double amount, int month, int year) {
        String query = "INSERT INTO hoa_don_dich_vu_khac (id_can_ho, ten_dich_vu, so_tien, " +
                      "thang, nam, trang_thai) VALUES (?, ?, ?, ?, ?, 'chua_thanh_toan')";
        
        try (java.sql.Connection conn = config.DatabaseConnection.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, apartmentId);
            pstmt.setString(2, serviceName);
            pstmt.setDouble(3, amount);
            pstmt.setInt(4, month);
            pstmt.setInt(5, year);
            
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isSuccess() {
        return success;
    }
}