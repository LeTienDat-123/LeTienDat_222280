package model;

public class Household {
    private int id;
    private int apartmentId;
    private int headOfHouseholdId;
    private int numberOfMembers;
    private String contactPhone;
    private String contactEmail;

    public Household(int id, int apartmentId, int headOfHouseholdId, 
                    int numberOfMembers, String contactPhone, String contactEmail) {
        this.id = id;
        this.apartmentId = apartmentId;
        this.headOfHouseholdId = headOfHouseholdId;
        this.numberOfMembers = numberOfMembers;
        this.contactPhone = contactPhone;
        this.contactEmail = contactEmail;
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

    public int getHeadOfHouseholdId() {
        return headOfHouseholdId;
    }

    public void setHeadOfHouseholdId(int headOfHouseholdId) {
        this.headOfHouseholdId = headOfHouseholdId;
    }

    public int getNumberOfMembers() {
        return numberOfMembers;
    }

    public void setNumberOfMembers(int numberOfMembers) {
        this.numberOfMembers = numberOfMembers;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    @Override
    public String toString() {
        return "Hộ gia đình #" + id + " - " + numberOfMembers + " thành viên";
    }
}