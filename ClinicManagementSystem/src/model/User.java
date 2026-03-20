package model;

/**
 * Abstract base class for all users in the Clinic Management System.
 * Week 9 - OOP Architecture Submission (Partial Implementation ~40%)
 * Demonstrates: Abstraction, Encapsulation, Inheritance
 */
public abstract class User {

    // Encapsulated fields
    private String userId;
    private String name;
    private String email;
    private String phone;
    private String password;

    // Constructor
    public User(String userId, String name, String email, String phone, String password) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    // Abstract method - must be implemented by subclasses (Polymorphism)
    public abstract String getRole();

    // Password validation
    public boolean validatePassword(String inputPassword) {
        return this.password != null && this.password.equals(inputPassword);
        // TODO: Implement bcrypt password hashing in final version
    }

    // Getters and Setters (Encapsulation)
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public String toString() {
        return "User{userId='" + userId + "', name='" + name + "', role='" + getRole() + "'}";
    }
}
