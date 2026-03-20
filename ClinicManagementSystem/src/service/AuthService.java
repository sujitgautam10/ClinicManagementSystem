package service;

import dao.DoctorDAO;
import dao.PatientDAO;
import model.Admin;
import model.Doctor;
import model.Patient;
import model.User;

/**
 * AuthService - Handles authentication for all user roles
 * Week 9 - OOP Architecture Submission (Partial Implementation ~40%)
 * Business Logic Layer: validates credentials and returns appropriate User objects
 */
public class AuthService {

    private final DoctorDAO doctorDAO;
    private final PatientDAO patientDAO;

    // Hardcoded admin credentials for Week 9 demo
    // TODO: Move admin credentials to database in final version
    private static final String ADMIN_ID       = "admin";
    private static final String ADMIN_PASSWORD = "password";

    public AuthService() {
        this.doctorDAO  = new DoctorDAO();
        this.patientDAO = new PatientDAO();
    }

    /**
     * Main authentication method.
     * Detects role automatically from userId prefix or admin check.
     * Returns a User object (Admin/Doctor/Patient) on success, null on failure.
     */
    public User authenticate(String userId, String password) {
        if (userId == null || password == null || userId.isBlank() || password.isBlank()) {
            return null;
        }

        // 1. Check Admin
        if (userId.equalsIgnoreCase(ADMIN_ID) && password.equals(ADMIN_PASSWORD)) {
            return new Admin("admin", "System Administrator", "admin@clinic.com", "N/A", ADMIN_PASSWORD);
        }

        // 2. Check Doctor (userId starts with "D")
        if (userId.toUpperCase().startsWith("D")) {
            Doctor doc = doctorDAO.authenticate(userId, password);
            if (doc != null) return doc;
        }

        // 3. Check Patient (userId starts with "P")
        if (userId.toUpperCase().startsWith("P")) {
            Patient pat = patientDAO.authenticate(userId, password);
            if (pat != null) return pat;
        }

        return null; // Authentication failed
    }

    /**
     * Validate non-empty input fields before sending to DB.
     */
    public boolean isInputValid(String userId, String password) {
        return userId != null && !userId.isBlank()
            && password != null && !password.isBlank();
    }

    // TODO: Add session timeout management in final version
    // TODO: Add login attempt limiting / lockout in final version
    // TODO: Add password hashing (BCrypt) in final version
    // TODO: Add "Forgot Password" flow in final version
}
