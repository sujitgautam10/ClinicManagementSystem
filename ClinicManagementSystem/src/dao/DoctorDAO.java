package dao;

import model.Doctor;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DoctorDAO - Data Access Object for Doctor operations
 * Week 12 - Final Implementation
 */
public class DoctorDAO {

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    // Authenticate doctor by ID and password
    public Doctor authenticate(String userId, String password) {
        String sql = "SELECT * FROM doctors WHERE user_id = ? AND password = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapDoctor(rs);
        } catch (SQLException e) {
            System.err.println("[DoctorDAO] authenticate error: " + e.getMessage());
        }
        return null;
    }

    // Get all doctors ordered by name
    public List<Doctor> getAllDoctors() {
        List<Doctor> list = new ArrayList<>();
        String sql = "SELECT * FROM doctors ORDER BY name";
        try (Statement stmt = getConn().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapDoctor(rs));
        } catch (SQLException e) {
            System.err.println("[DoctorDAO] getAllDoctors error: " + e.getMessage());
        }
        return list;
    }

    // Get a single doctor by ID
    public Doctor getDoctorById(String userId) {
        String sql = "SELECT * FROM doctors WHERE user_id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapDoctor(rs);
        } catch (SQLException e) {
            System.err.println("[DoctorDAO] getDoctorById error: " + e.getMessage());
        }
        return null;
    }

    // Add a new doctor
    public boolean addDoctor(Doctor doctor) {
        String sql = "INSERT INTO doctors (user_id, name, email, phone, password, specialization, qualifications, experience_years) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, doctor.getUserId());
            ps.setString(2, doctor.getName());
            ps.setString(3, doctor.getEmail());
            ps.setString(4, doctor.getPhone());
            ps.setString(5, doctor.getPassword());
            ps.setString(6, doctor.getSpecialization());
            ps.setString(7, doctor.getQualifications());
            ps.setInt(8, doctor.getExperienceYears());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DoctorDAO] addDoctor error: " + e.getMessage());
        }
        return false;
    }

    // Update doctor profile details
    public boolean updateDoctor(Doctor doctor) {
        String sql = "UPDATE doctors SET name = ?, email = ?, phone = ?, " +
                "specialization = ?, qualifications = ?, experience_years = ? " +
                "WHERE user_id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, doctor.getName());
            ps.setString(2, doctor.getEmail());
            ps.setString(3, doctor.getPhone());
            ps.setString(4, doctor.getSpecialization());
            ps.setString(5, doctor.getQualifications());
            ps.setInt(6, doctor.getExperienceYears());
            ps.setString(7, doctor.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DoctorDAO] updateDoctor error: " + e.getMessage());
        }
        return false;
    }

    // Delete a doctor by ID
    public boolean deleteDoctor(String userId) {
        String sql = "DELETE FROM doctors WHERE user_id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DoctorDAO] deleteDoctor error: " + e.getMessage());
        }
        return false;
    }

    // Search doctors by name or specialization
    public List<Doctor> searchDoctors(String keyword) {
        List<Doctor> list = new ArrayList<>();
        String sql = "SELECT * FROM doctors WHERE LOWER(name) LIKE ? OR LOWER(specialization) LIKE ? ORDER BY name";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            String pattern = "%" + keyword.toLowerCase() + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapDoctor(rs));
        } catch (SQLException e) {
            System.err.println("[DoctorDAO] searchDoctors error: " + e.getMessage());
        }
        return list;
    }

    // Count total doctors
    public int getDoctorCount() {
        String sql = "SELECT COUNT(*) FROM doctors";
        try (Statement stmt = getConn().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[DoctorDAO] count error: " + e.getMessage());
        }
        return 0;
    }

    // Map ResultSet row to Doctor object
    private Doctor mapDoctor(ResultSet rs) throws SQLException {
        return new Doctor(
                rs.getString("user_id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("password"),
                rs.getString("specialization"),
                rs.getString("qualifications"),
                rs.getInt("experience_years")
        );
    }
}