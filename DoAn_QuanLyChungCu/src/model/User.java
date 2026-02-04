package model;

public class User {
    private int id;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String role; // quan_ly_he_thong, quan_ly_toa_nha, cu_dan

    public User(int id, String username, String fullName, String phone, String role) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.phone = phone;
        this.role = role;
        this.email = "";
    }
    
    public User(int id, String username, String fullName, String email, String phone, String role) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }
    
    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getRole() {
        return role;
    }

    public String getRoleDisplay() {
        switch (role) {
            case "quan_ly_he_thong":
                return "Quản lý hệ thống";
            case "quan_ly_toa_nha":
                return "Quản lý tòa nhà";
            case "cu_dan":
                return "Cư dân";
            default:
                return role;
        }
    }

    @Override
    public String toString() {
        return fullName + " (" + getRoleDisplay() + ")";
    }
}