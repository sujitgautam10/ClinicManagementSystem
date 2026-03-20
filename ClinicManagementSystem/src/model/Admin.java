package model;

/**
 * Admin model - extends User (Inheritance)
 * Week 9 - OOP Architecture Submission (Partial Implementation ~40%)
 */
public class Admin extends User {

    // Constructor
    public Admin(String userId, String name, String email, String phone, String password) {
        super(userId, name, email, phone, password);
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }

    // TODO: Add admin-specific permissions and audit log in final version
}
