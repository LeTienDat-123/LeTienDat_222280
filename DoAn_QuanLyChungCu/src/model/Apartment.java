package model;

public class Apartment {
    private int id;
    private int buildingId;
    private String number;
    private int floor;
    private float area;
    private int bedrooms;
    private int bathrooms;
    private String status; // trong, co_nguoi_o, bao_tri
    
    public Apartment(int id, int buildingId, String number, int floor, float area, 
                    int bedrooms, int bathrooms, String status) {
        this.id = id;
        this.buildingId = buildingId;
        this.number = number;
        this.floor = floor;
        this.area = area;
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.status = status;
    }
    
    // Getters
    public int getId() {
        return id;
    }
    
    public int getBuildingId() {
        return buildingId;
    }
    
    public String getNumber() {
        return number;
    }
    
    public int getFloor() {
        return floor;
    }
    
    public float getArea() {
        return area;
    }
    
    public int getBedrooms() {
        return bedrooms;
    }
    
    public int getBathrooms() {
        return bathrooms;
    }
    
    public String getStatus() {
        return status;
    }
    
    public String getStatusDisplay() {
        switch (status) {
            case "co_nguoi_o":
                return "Có người ở";
            case "trong":
                return "Trống";
            case "bao_tri":
                return "Bảo trì";
            default:
                return status;
        }
    }
    
    public String getStatusIcon() {
        switch (status) {
            case "co_nguoi_o":
                return "●"; // ● Xanh dương
            case "trong":
                return "●"; // ● Xanh lá
            case "bao_tri":
                return "●"; // ● Cam
            default:
                return "●";
        }
    }
    
    @Override
    public String toString() {
        return "Căn " + number;
    }
}