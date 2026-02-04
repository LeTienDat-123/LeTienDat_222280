package model;

import java.sql.Timestamp;

// Base Invoice class
public abstract class Invoice {
    protected int id;
    protected int apartmentId;
    protected int month;
    protected int year;
    protected double totalAmount;
    protected Timestamp paymentDate;
    protected String status; // chua_thanh_toan, da_thanh_toan, qua_han

    public Invoice(int id, int apartmentId, int month, int year, 
                  double totalAmount, Timestamp paymentDate, String status) {
        this.id = id;
        this.apartmentId = apartmentId;
        this.month = month;
        this.year = year;
        this.totalAmount = totalAmount;
        this.paymentDate = paymentDate;
        this.status = status;
    }

    // Common getters
    public int getId() { return id; }
    public int getApartmentId() { return apartmentId; }
    public int getMonth() { return month; }
    public int getYear() { return year; }
    public double getTotalAmount() { return totalAmount; }
    public Timestamp getPaymentDate() { return paymentDate; }
    public String getStatus() { return status; }
    
    public void setPaymentDate(Timestamp paymentDate) {
        this.paymentDate = paymentDate;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDisplay() {
        switch (status) {
            case "chua_thanh_toan":
                return "Chưa thanh toán";
            case "da_thanh_toan":
                return "Đã thanh toán";
            case "qua_han":
                return "Quá hạn";
            default:
                return status;
        }
    }

    public abstract String getInvoiceType();
}

// Electricity Invoice
class ElectricityInvoice extends Invoice {
    private int oldReading;
    private int newReading;
    private int consumption;
    private double pricePerUnit;

    public ElectricityInvoice(int id, int apartmentId, int month, int year,
                             int oldReading, int newReading, int consumption,
                             double pricePerUnit, double totalAmount,
                             Timestamp paymentDate, String status) {
        super(id, apartmentId, month, year, totalAmount, paymentDate, status);
        this.oldReading = oldReading;
        this.newReading = newReading;
        this.consumption = consumption;
        this.pricePerUnit = pricePerUnit;
    }

    public int getOldReading() { return oldReading; }
    public int getNewReading() { return newReading; }
    public int getConsumption() { return consumption; }
    public double getPricePerUnit() { return pricePerUnit; }

    @Override
    public String getInvoiceType() {
        return "Hóa đơn tiền điện";
    }
}

// Water Invoice
class WaterInvoice extends Invoice {
    private int oldReading;
    private int newReading;
    private int consumption;
    private double pricePerUnit;

    public WaterInvoice(int id, int apartmentId, int month, int year,
                       int oldReading, int newReading, int consumption,
                       double pricePerUnit, double totalAmount,
                       Timestamp paymentDate, String status) {
        super(id, apartmentId, month, year, totalAmount, paymentDate, status);
        this.oldReading = oldReading;
        this.newReading = newReading;
        this.consumption = consumption;
        this.pricePerUnit = pricePerUnit;
    }

    public int getOldReading() { return oldReading; }
    public int getNewReading() { return newReading; }
    public int getConsumption() { return consumption; }
    public double getPricePerUnit() { return pricePerUnit; }

    @Override
    public String getInvoiceType() {
        return "Hóa đơn tiền nước";
    }
}

// Management Fee Invoice
class ManagementFeeInvoice extends Invoice {
    public ManagementFeeInvoice(int id, int apartmentId, int month, int year,
                               double amount, Timestamp paymentDate, String status) {
        super(id, apartmentId, month, year, amount, paymentDate, status);
    }

    @Override
    public String getInvoiceType() {
        return "Phí quản lý chung";
    }
}

// Other Service Invoice
class OtherServiceInvoice extends Invoice {
    private String serviceName;

    public OtherServiceInvoice(int id, int apartmentId, String serviceName,
                              double amount, int month, int year,
                              Timestamp paymentDate, String status) {
        super(id, apartmentId, month, year, amount, paymentDate, status);
        this.serviceName = serviceName;
    }

    public String getServiceName() { return serviceName; }

    @Override
    public String getInvoiceType() {
        return "Dịch vụ: " + serviceName;
    }
}