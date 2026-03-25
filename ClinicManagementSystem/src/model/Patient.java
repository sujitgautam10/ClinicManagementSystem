package model;

/**
 * Patient model - extends User (Inheritance)
 * Week 9 - OOP Architecture Submission (Partial Implementation ~40%)
 */
public class Patient extends User {

    private String dateOfBirth;
    private String address;
    private int age;

    // Constructor
    public Patient(String userId, String name, String email, String phone, String password,
                   String dateOfBirth, String address, int age) {
        super(userId, name, email, phone, password);
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.age = age;
    }

    @Override
    public String getRole() {
        return "PATIENT";
    }

    // Getters and Setters
    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
}
