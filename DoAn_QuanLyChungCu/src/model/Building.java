package model;

// ==================== BUILDING MODEL ====================
public class Building {
    private int id;
    private String name;
    private String address;
    private int floors;
    private int yearBuilt;
    private int managerId;
    
    public Building(int id, String name, String address, int floors, int yearBuilt, int managerId) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.floors = floors;
        this.yearBuilt = yearBuilt;
        this.managerId = managerId;
    }
    
    // Getters
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getAddress() {
        return address;
    }
    
    public int getFloors() {
        return floors;
    }
    
    public int getYearBuilt() {
        return yearBuilt;
    }
    
    public int getManagerId() {
        return managerId;
    }
    
    @Override
    public String toString() {
        return name;
    }
}