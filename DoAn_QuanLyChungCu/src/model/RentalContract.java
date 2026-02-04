package model;

import java.sql.Date;

public class RentalContract {
    private int id;
    private int apartmentId;
    private int residentId;
    private Date startDate;
    private Date endDate;
    private double monthlyRent;
    private double deposit;
    private String status; // hoat_dong, het_han, huy
    private String notes;

    public RentalContract(int id, int apartmentId, int residentId, Date startDate, 
                         Date endDate, double monthlyRent, double deposit, 
                         String status, String notes) {
        this.id = id;
        this.apartmentId = apartmentId;
        this.residentId = residentId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.monthlyRent = monthlyRent;
        this.deposit = deposit;
        this.status = status;
        this.notes = notes;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getApartmentId() {
        return apartmentId;
    }

    public void setApartmentId(int apartmentId) {
        this.apartmentId = apartmentId;
    }

    public int getResidentId() {
        return residentId;
    }

    public void setResidentId(int residentId) {
        this.residentId = residentId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public double getMonthlyRent() {
        return monthlyRent;
    }

    public void setMonthlyRent(double monthlyRent) {
        this.monthlyRent = monthlyRent;
    }

    public double getDeposit() {
        return deposit;
    }

    public void setDeposit(double deposit) {
        this.deposit = deposit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStatusDisplay() {
        switch (status) {
            case "hoat_dong":
                return "Đang hoạt động";
            case "het_han":
                return "Hết hạn";
            case "huy":
                return "Đã hủy";
            default:
                return status;
        }
    }

    @Override
    public String toString() {
        return "Hợp đồng #" + id + " - " + getStatusDisplay();
    }
}