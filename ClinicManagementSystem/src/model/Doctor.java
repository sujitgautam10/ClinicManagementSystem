package model;

/**
 * Doctor model - extends User (Inheritance)
 * Week 9 - OOP Architecture Submission (Partial Implementation ~40%)
 */
public class Doctor extends User {

    private String specialization;
    private String qualifications;
    private int experienceYears;

    // Constructor
    public Doctor(String userId, String name, String email, String phone, String password,
                  String specialization, String qualifications, int experienceYears) {
        super(userId, name, email, phone, password);
        this.specialization = specialization;
        this.qualifications = qualifications;
        this.experienceYears = experienceYears;
    }

    @Override
    public String getRole() {
        return "DOCTOR";
    }

    // Getters and Setters
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getQualifications() { return qualifications; }
    public void setQualifications(String qualifications) { this.qualifications = qualifications; }

    public int getExperienceYears() { return experienceYears; }
    public void setExperienceYears(int experienceYears) { this.experienceYears = experienceYears; }

    // TODO: Add availability schedule management in final version
    // TODO: Add working hours / slots in final version
}
