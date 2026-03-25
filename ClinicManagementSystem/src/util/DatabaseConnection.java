package util;

import java.sql.*;

/**
 * DatabaseConnection - Singleton Pattern
 * Week 9 - OOP Architecture Submission (Partial Implementation ~40%)
 *
 * Uses MySQL Connector/J.
 * Place mysql-connector-j-x.x.x.jar in the lib/ folder and add to classpath.
 * Download from: https://dev.mysql.com/downloads/connector/j/
 *
 * Setup:
 *   1. Install MySQL Server
 *   2. Run: CREATE DATABASE clinic_management;
 *   3. Update DB_USER and DB_PASSWORD below if needed
 *   4. Run the application - tables and sample data are created automatically
 */
public class DatabaseConnection {

    // Database connection settings
    private static final String DB_HOST     = "localhost";
    private static final String DB_PORT     = "3306";
    private static final String DB_NAME     = "clinic_management";
    private static final String DB_USER     = "root";       // Change if needed
    private static final String DB_PASSWORD = "9999";           // Change if needed
    private static final String DB_URL      =
            "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME +
            "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    

    // Singleton instance
    private static DatabaseConnection instance;
    private Connection connection;

    // Private constructor - prevents external instantiation (Singleton)
    private DatabaseConnection() {
        try {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("[DB] Connected to MySQL successfully.");
            initializeTables();
            insertSampleData();
        } catch (ClassNotFoundException e) {
            System.err.println("[DB ERROR] MySQL Connector/J not found. Add mysql-connector-j JAR to lib/ folder.");
            System.err.println("           Download: https://dev.mysql.com/downloads/connector/j/");
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Cannot connect to MySQL: " + e.getMessage());
            System.err.println("           Make sure MySQL is running and DB_USER/DB_PASSWORD are correct.");
        }
    }

    // Singleton getInstance()
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    // Returns the active connection
    public Connection getConnection() {
        return connection;
    }

    // Create tables if they don't exist
    private void initializeTables() throws SQLException {
        Statement stmt = connection.createStatement();

        // Doctors table
        stmt.executeUpdate(
            "CREATE TABLE IF NOT EXISTS doctors (" +
            "  user_id        VARCHAR(20)  PRIMARY KEY," +
            "  name           VARCHAR(100) NOT NULL," +
            "  email          VARCHAR(100)," +
            "  phone          VARCHAR(20)," +
            "  password       VARCHAR(100) NOT NULL," +
            "  specialization VARCHAR(100)," +
            "  qualifications VARCHAR(100)," +
            "  experience_years INT DEFAULT 0" +
            ") ENGINE=InnoDB"
        );

        // Patients table
        stmt.executeUpdate(
            "CREATE TABLE IF NOT EXISTS patients (" +
            "  user_id       VARCHAR(20)  PRIMARY KEY," +
            "  name          VARCHAR(100) NOT NULL," +
            "  email         VARCHAR(100)," +
            "  phone         VARCHAR(20)," +
            "  password      VARCHAR(100) NOT NULL," +
            "  date_of_birth DATE," +
            "  address       VARCHAR(200)," +
            "  age           INT DEFAULT 0" +
            ") ENGINE=InnoDB"
        );

        // Appointments table
        stmt.executeUpdate(
            "CREATE TABLE IF NOT EXISTS appointments (" +
            "  appointment_id   VARCHAR(20)  PRIMARY KEY," +
            "  patient_id       VARCHAR(20)  NOT NULL," +
            "  patient_name     VARCHAR(100)," +
            "  doctor_id        VARCHAR(20)  NOT NULL," +
            "  doctor_name      VARCHAR(100)," +
            "  appointment_date DATE," +
            "  appointment_time TIME," +
            "  reason           VARCHAR(300)," +
            "  status           VARCHAR(20)  DEFAULT 'SCHEDULED'," +
            "  notes            VARCHAR(300)," +
            "  FOREIGN KEY (patient_id) REFERENCES patients(user_id) ON DELETE CASCADE," +
            "  FOREIGN KEY (doctor_id)  REFERENCES doctors(user_id)  ON DELETE CASCADE" +
            ") ENGINE=InnoDB"
        );

        stmt.close();
        System.out.println("[DB] Tables initialized.");
    }

    // Insert sample data on first run
    private void insertSampleData() throws SQLException {
        Statement stmt = connection.createStatement();

        // Doctors - use INSERT IGNORE to avoid duplicate key errors on re-run
        stmt.executeUpdate(
            "INSERT IGNORE INTO doctors VALUES " +
            "('D001','Dr. Sarah Johnson','sarah@clinic.com','9876543210','doc123','General Physician','MBBS, MD',15)," +
            "('D002','Dr. Michael Chen','michael@clinic.com','9876543211','doc123','Pediatrician','MBBS, DCH',10)," +
            "('D003','Dr. Emily Williams','emily@clinic.com','9876543212','doc123','Dermatologist','MBBS, MD',8)"
        );

        // Patients
        stmt.executeUpdate(
            "INSERT IGNORE INTO patients VALUES " +
            "('P001','John Smith','john@email.com','5551234567','pat123','1990-05-15','123 Main St, Springfield',34)," +
            "('P002','Emma Davis','emma@email.com','5559876543','pat123','1985-08-22','456 Oak Ave, Springfield',38)"
        );

        // Appointments
        stmt.executeUpdate(
            "INSERT IGNORE INTO appointments VALUES " +
            "('APT1001','P001','John Smith','D001','Dr. Sarah Johnson','2025-02-18','10:00:00','Regular checkup','SCHEDULED','')," +
            "('APT1002','P002','Emma Davis','D002','Dr. Michael Chen','2025-02-19','11:00:00','Child vaccination','COMPLETED','Vaccination done')," +
            "('APT1003','P001','John Smith','D003','Dr. Emily Williams','2025-02-20','14:00:00','Skin rash consultation','SCHEDULED','')," +
            "('APT1004','P002','Emma Davis','D001','Dr. Sarah Johnson','2025-02-18','14:30:00','Follow-up consultation','SCHEDULED','')," +
            "('APT1005','P001','John Smith','D003','Dr. Emily Williams','2025-02-20','14:00:00','Skin rash','SCHEDULED','')," +
            "('APT1007','P001','John Smith','D001','Dr. Sarah Johnson','2025-02-17','09:00:00','Fever and cold','COMPLETED','Prescribed medication')," +
            "('APT1008','P001','John Smith','D001','Dr. Sarah Johnson','2025-02-15','09:00:00','Fever and cold','COMPLETED','Recovered')," +
            "('APT1009','P001','John Smith','D002','Dr. Michael Chen','2025-02-12','11:00:00','Vaccination','CANCELLED','')"
        );

        stmt.close();
        System.out.println("[DB] Sample data loaded.");
    }
}
