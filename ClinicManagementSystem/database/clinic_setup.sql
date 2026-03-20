-- ============================================================
--  Clinic Management System - MySQL Setup Script
--  Week 9 OOP Architecture Submission
-- ============================================================
--  Run this script ONLY if you prefer to create tables manually.
--  The application also auto-creates tables on first run.
-- ============================================================

CREATE DATABASE IF NOT EXISTS clinic_management;
USE clinic_management;

-- Doctors table
CREATE TABLE IF NOT EXISTS doctors (
    user_id          VARCHAR(20)  PRIMARY KEY,
    name             VARCHAR(100) NOT NULL,
    email            VARCHAR(100),
    phone            VARCHAR(20),
    password         VARCHAR(100) NOT NULL,
    specialization   VARCHAR(100),
    qualifications   VARCHAR(100),
    experience_years INT DEFAULT 0
) ENGINE=InnoDB;

-- Patients table
CREATE TABLE IF NOT EXISTS patients (
    user_id       VARCHAR(20)  PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    email         VARCHAR(100),
    phone         VARCHAR(20),
    password      VARCHAR(100) NOT NULL,
    date_of_birth DATE,
    address       VARCHAR(200),
    age           INT DEFAULT 0
) ENGINE=InnoDB;

-- Appointments table
CREATE TABLE IF NOT EXISTS appointments (
    appointment_id   VARCHAR(20)  PRIMARY KEY,
    patient_id       VARCHAR(20)  NOT NULL,
    patient_name     VARCHAR(100),
    doctor_id        VARCHAR(20)  NOT NULL,
    doctor_name      VARCHAR(100),
    appointment_date DATE         NOT NULL,
    appointment_time TIME         NOT NULL,
    reason           VARCHAR(300),
    status           VARCHAR(20)  DEFAULT 'SCHEDULED',
    notes            VARCHAR(300),
    FOREIGN KEY (patient_id) REFERENCES patients(user_id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id)  REFERENCES doctors(user_id)  ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- Sample Data
-- ============================================================

INSERT IGNORE INTO doctors VALUES
('D001','Dr. Sarah Johnson','sarah@clinic.com','9876543210','doc123','General Physician','MBBS, MD',15),
('D002','Dr. Michael Chen','michael@clinic.com','9876543211','doc123','Pediatrician','MBBS, DCH',10),
('D003','Dr. Emily Williams','emily@clinic.com','9876543212','doc123','Dermatologist','MBBS, MD',8);

INSERT IGNORE INTO patients VALUES
('P001','John Smith','john@email.com','5551234567','pat123','1990-05-15','123 Main St, Springfield',34),
('P002','Emma Davis','emma@email.com','5559876543','pat123','1985-08-22','456 Oak Ave, Springfield',38);

INSERT IGNORE INTO appointments VALUES
('APT1001','P001','John Smith','D001','Dr. Sarah Johnson','2025-02-18','10:00:00','Regular checkup','SCHEDULED',''),
('APT1002','P002','Emma Davis','D002','Dr. Michael Chen','2025-02-19','11:00:00','Child vaccination','COMPLETED','Vaccination done'),
('APT1003','P001','John Smith','D003','Dr. Emily Williams','2025-02-20','14:00:00','Skin rash consultation','SCHEDULED',''),
('APT1004','P002','Emma Davis','D001','Dr. Sarah Johnson','2025-02-18','14:30:00','Follow-up consultation','SCHEDULED',''),
('APT1005','P001','John Smith','D003','Dr. Emily Williams','2025-02-20','14:00:00','Skin rash','SCHEDULED',''),
('APT1007','P001','John Smith','D001','Dr. Sarah Johnson','2025-02-17','09:00:00','Fever and cold','COMPLETED','Prescribed medication'),
('APT1008','P001','John Smith','D001','Dr. Sarah Johnson','2025-02-15','09:00:00','Fever and cold','COMPLETED','Recovered'),
('APT1009','P001','John Smith','D002','Dr. Michael Chen','2025-02-12','11:00:00','Vaccination','CANCELLED','');
