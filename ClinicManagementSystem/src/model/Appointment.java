package model;

/**
 * Appointment model class
 * Week 9 - OOP Architecture Submission (Partial Implementation ~40%)
 */
public class Appointment {

    // Enum for appointment status (OOP concept)
    public enum Status {
        SCHEDULED, COMPLETED, CANCELLED, NO_SHOW
    }

    private String appointmentId;
    private String patientId;
    private String patientName;
    private String doctorId;
    private String doctorName;
    private String appointmentDate;
    private String appointmentTime;
    private String reason;
    private Status status;
    private String notes;

    // Constructor
    public Appointment(String appointmentId, String patientId, String patientName,
                       String doctorId, String doctorName, String appointmentDate,
                       String appointmentTime, String reason, Status status, String notes) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.patientName = patientName;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.reason = reason;
        this.status = status;
        this.notes = notes;
    }

    // Getters and Setters (Encapsulation)
    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public String getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(String appointmentDate) { this.appointmentDate = appointmentDate; }

    public String getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(String appointmentTime) { this.appointmentTime = appointmentTime; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    // Update notes method
    public void updateNotes(String newNotes) {
        this.notes = newNotes;
    }

    @Override
    public String toString() {
        return "Appointment{id='" + appointmentId + "', patient='" + patientName +
               "', doctor='" + doctorName + "', date='" + appointmentDate + "', status=" + status + "}";
    }

}
