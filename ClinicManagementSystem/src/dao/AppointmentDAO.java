package dao;

import model.Appointment;
import model.Appointment.Status;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * AppointmentDAO - Data Access Object for Appointment operations
 * Week 9 - OOP Architecture Submission (Partial Implementation ~40%)
 */
public class AppointmentDAO {

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    // Get all appointments
    public List<Appointment> getAllAppointments() {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT * FROM appointments ORDER BY appointment_date DESC, appointment_time ASC";
        try (Statement stmt = getConn().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapAppointment(rs));
        } catch (SQLException e) {
            System.err.println("[AppointmentDAO] getAll error: " + e.getMessage());
        }
        return list;
    }

    // Get appointments for a specific doctor
    public List<Appointment> getByDoctorId(String doctorId) {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE doctor_id = ? ORDER BY appointment_date DESC";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, doctorId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapAppointment(rs));
        } catch (SQLException e) {
            System.err.println("[AppointmentDAO] getByDoctorId error: " + e.getMessage());
        }
        return list;
    }

    // Get appointments for a specific patient
    public List<Appointment> getByPatientId(String patientId) {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE patient_id = ? ORDER BY appointment_date DESC";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, patientId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapAppointment(rs));
        } catch (SQLException e) {
            System.err.println("[AppointmentDAO] getByPatientId error: " + e.getMessage());
        }
        return list;
    }

    // Update appointment status
    /**
     * Update appointment status (working for Week 9 submission).
     */
    public boolean updateStatus(String appointmentId, Status status) {
        String sql = "UPDATE appointments SET status = ? WHERE appointment_id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setString(2, appointmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[AppointmentDAO] updateStatus error: " + e.getMessage());
        }
        return false;
    }

    // Book a new appointment
    /**
     * Book a new appointment (basic implementation for Week 9 submission).
     * Full conflict checking will be added in final version.
     */
    public boolean bookAppointment(Appointment appt) {
        String sql = "INSERT INTO appointments (appointment_id, patient_id, patient_name, " +
                     "doctor_id, doctor_name, appointment_date, appointment_time, reason, status, notes) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, appt.getAppointmentId());
            ps.setString(2, appt.getPatientId());
            ps.setString(3, appt.getPatientName());
            ps.setString(4, appt.getDoctorId());
            ps.setString(5, appt.getDoctorName());
            ps.setDate(6, java.sql.Date.valueOf(appt.getAppointmentDate()));
            ps.setTime(7, java.sql.Time.valueOf(appt.getAppointmentTime()));
            ps.setString(8, appt.getReason());
            ps.setString(9, appt.getStatus().name());
            ps.setString(10, appt.getNotes() != null ? appt.getNotes() : "");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[AppointmentDAO] bookAppointment error: " + e.getMessage());
        }
        return false;
    }

    // Count total and scheduled appointments
    public int getAppointmentCount() {
        String sql = "SELECT COUNT(*) FROM appointments";
        try (Statement stmt = getConn().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[AppointmentDAO] count error: " + e.getMessage());
        }
        return 0;
    }

    public int getScheduledCount() {
        String sql = "SELECT COUNT(*) FROM appointments WHERE status = 'SCHEDULED'";
        try (Statement stmt = getConn().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[AppointmentDAO] scheduledCount error: " + e.getMessage());
        }
        return 0;
    }

    // Map ResultSet row to model object
    private Appointment mapAppointment(ResultSet rs) throws SQLException {
        return new Appointment(
            rs.getString("appointment_id"),
            rs.getString("patient_id"),
            rs.getString("patient_name"),
            rs.getString("doctor_id"),
            rs.getString("doctor_name"),
            rs.getDate("appointment_date").toString(),
            rs.getTime("appointment_time").toString(),
            rs.getString("reason"),
            Status.valueOf(rs.getString("status")),
            rs.getString("notes")
        );
    }

    // TODO: cancelAppointment() with proper business rules - final version
    // TODO: filterByDate() - implement in final version
    // TODO: checkTimeConflict() - implement in final version
    // TODO: generateAppointmentReport() - implement in final version
}
