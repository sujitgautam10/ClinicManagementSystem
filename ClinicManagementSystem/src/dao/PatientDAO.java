package dao;

import model.Patient;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class PatientDAO {

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    // Authenticate user by ID and password
    public Patient authenticate(String userId, String password) {
        String sql = "SELECT * FROM patients WHERE user_id = ? AND password = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapPatient(rs);
        } catch (SQLException e) {
            System.err.println("[PatientDAO] authenticate error: " + e.getMessage());
        }
        return null;
    }

    // Get all records
    public List<Patient> getAllPatients() {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT * FROM patients";
        try (Statement stmt = getConn().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapPatient(rs));
            }
        } catch (SQLException e) {
            System.err.println("[PatientDAO] getAllPatients error: " + e.getMessage());
        }
        return list;
    }

    // Get a single record by ID
    public Patient getPatientById(String userId) {
        String sql = "SELECT * FROM patients WHERE user_id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapPatient(rs);
        } catch (SQLException e) {
            System.err.println("[PatientDAO] getPatientById error: " + e.getMessage());
        }
        return null;
    }

    // Delete a record by ID
    public boolean deletePatient(String userId) {
        String sql = "DELETE FROM patients WHERE user_id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[PatientDAO] deletePatient error: " + e.getMessage());
        }
        return false;
    }

    // Count total and scheduled appointments
    public int getPatientCount() {
        String sql = "SELECT COUNT(*) FROM patients";
        try (Statement stmt = getConn().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[PatientDAO] count error: " + e.getMessage());
        }
        return 0;
    }

    // Map ResultSet row to model object
    private Patient mapPatient(ResultSet rs) throws SQLException {
        return new Patient(
                rs.getString("user_id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("password"),
                rs.getDate("date_of_birth") != null ? rs.getDate("date_of_birth").toString() : "",
                rs.getString("address"),
                rs.getInt("age")
        );
    }

    // Update patient profile details
    public boolean updatePatient(Patient patient) {
        String sql = "UPDATE patients SET name = ?, email = ?, phone = ?, address = ? WHERE user_id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, patient.getName());
            ps.setString(2, patient.getEmail());
            ps.setString(3, patient.getPhone());
            ps.setString(4, patient.getAddress());
            ps.setString(5, patient.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[PatientDAO] updatePatient error: " + e.getMessage());
        }
        return false;
    }
}