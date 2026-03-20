# 🏥 Clinic Management System
### Week 9 – OOP Architecture Submission (Partial Implementation ~40%)

---

## 📌 Project Overview

This is a **partial implementation** of a Java-based Online Clinic Appointment Booking System,
submitted for the **Week 9 OOP Architecture milestone**.

The system supports three user roles: **Admin**, **Doctor**, and **Patient**, each with their
own dashboard. The UI is built using **pure programmatic JavaFX** (no Scene Builder / no FXML).

---

## ✅ Working Features (~40% Complete)

| Feature | Status |
|---|---|
| Admin Login | ✅ Working |
| Doctor Login | ✅ Working |
| Patient Login | ✅ Working |
| Admin – View Stats (doctor/patient/appointment counts) | ✅ Working |
| Admin – Add Doctor | ✅ Working |
| Admin – Delete Doctor | ✅ Working |
| Admin – View All Patients | ✅ Working |
| Admin – Delete Patient | ✅ Working |
| Admin – View All Appointments | ✅ Working |
| Doctor – View My Appointments | ✅ Working |
| Doctor – Mark Appointment as Complete | ✅ Working |
| Doctor – View Profile | ✅ Working |
| Patient – Book Appointment | ✅ Working |
| Patient – View My Appointments | ✅ Working |
| Patient – Cancel Appointment | ✅ Working |
| Patient – View Profile | ✅ Working |
| MySQL Database Connection (Singleton) | ✅ Working |
| Auto Table Creation + Sample Data | ✅ Working |

---

## ❌ Features Pending (Week 12 Final Submission)

- Doctor availability / schedule management
- Advanced appointment filtering by date
- Patient self-registration
- Admin – Update doctor/patient records
- Doctor / Patient – Edit profile
- Appointment time conflict checking
- Notifications (email / SMS)
- Reports and analytics dashboard
- Password hashing (BCrypt)
- Session timeout and login attempt limiting
- Advanced search and filtering

---

## 🗂️ Project Structure

```
ClinicManagementSystem/
├── src/
│   ├── ClinicManagementApp.java         ← Main entry point (JavaFX Application)
│   ├── model/
│   │   ├── User.java                    ← Abstract base class (Abstraction)
│   │   ├── Admin.java                   ← Admin model (extends User)
│   │   ├── Doctor.java                  ← Doctor model (extends User)
│   │   ├── Patient.java                 ← Patient model (extends User)
│   │   └── Appointment.java             ← Appointment + Status enum
│   ├── dao/
│   │   ├── DoctorDAO.java               ← Doctor CRUD operations
│   │   ├── PatientDAO.java              ← Patient CRUD operations
│   │   └── AppointmentDAO.java          ← Appointment CRUD operations
│   ├── service/
│   │   └── AuthService.java             ← Authentication business logic
│   ├── ui/
│   │   ├── LoginScreen.java             ← Login UI (programmatic JavaFX)
│   │   ├── AdminDashboard.java          ← Admin UI – 4 tabs
│   │   ├── DoctorDashboard.java         ← Doctor UI – 3 tabs
│   │   └── PatientDashboard.java        ← Patient UI – 3 tabs
│   └── util/
│       └── DatabaseConnection.java      ← Singleton DB connection (MySQL)
├── lib/
│   └── mysql-connector-j-x.x.x.jar     ← Place MySQL connector JAR here
├── clinic_setup.sql                     ← Optional manual SQL setup script
└── README.md
```

---

## 🏛️ OOP Design Patterns Used

| Pattern | Location | Purpose |
|---|---|---|
| **Singleton** | `DatabaseConnection` | Single DB connection instance |
| **DAO Pattern** | `DoctorDAO`, `PatientDAO`, `AppointmentDAO` | Separate data access from business logic |
| **Inheritance** | `User → Admin, Doctor, Patient` | Code reuse and polymorphism |
| **Abstraction** | `User` (abstract class) | Hide implementation details |
| **Encapsulation** | All model classes | Private fields with getters/setters |
| **Polymorphism** | `getRole()` method | Different return for each subclass |
| **Enum** | `Appointment.Status` | Type-safe appointment states |

---

## 🔧 Setup Instructions

### Prerequisites

| Requirement | Version |
|---|---|
| Java JDK | 17 or higher |
| JavaFX SDK | 17 or higher |
| MySQL Server | 8.0 or higher |
| MySQL Connector/J | 8.x or 9.x |
| IDE | IntelliJ IDEA / Eclipse / NetBeans |

---

### Step 1 – Install MySQL and Create Database

```sql
-- Open MySQL CLI or MySQL Workbench, then run:
CREATE DATABASE clinic_management;
```

Or import the full setup script:
```bash
mysql -u root -p < clinic_setup.sql
```

The application will **auto-create tables and sample data** on first run.

---

### Step 2 – Download MySQL Connector/J

1. Go to: https://dev.mysql.com/downloads/connector/j/
2. Download the **Platform Independent** ZIP
3. Extract and copy `mysql-connector-j-x.x.x.jar` into the `lib/` folder

---

### Step 3 – Configure Database Credentials

Open `src/util/DatabaseConnection.java` and update if needed:

```java
private static final String DB_USER     = "root";   // your MySQL username
private static final String DB_PASSWORD = "";        // your MySQL password
```

---

### Step 4 – Setup in IntelliJ IDEA

1. Open IntelliJ → **File → Open** → select `ClinicManagementSystem/`
2. **Add JavaFX SDK**:
   - File → Project Structure → Libraries → `+` → Java
   - Select the `lib/` folder of your JavaFX SDK
3. **Add MySQL Connector JAR**:
   - File → Project Structure → Libraries → `+` → Java
   - Select `lib/mysql-connector-j-x.x.x.jar`
4. **Set VM Options** (Run → Edit Configurations):
   ```
   --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml
   ```
5. Set **Main class** to `ClinicManagementApp`
6. Click **Run** ▶

---

### Step 5 – Compile and Run via Terminal

```bash
# Set your paths
JAVAFX_PATH=/path/to/javafx-sdk/lib
MYSQL_JAR=lib/mysql-connector-j-x.x.x.jar

# Compile
javac --module-path $JAVAFX_PATH --add-modules javafx.controls,javafx.fxml \
  -cp $MYSQL_JAR -d out \
  src/model/*.java src/dao/*.java src/service/*.java src/util/*.java \
  src/ui/*.java src/ClinicManagementApp.java

# Run
java --module-path $JAVAFX_PATH --add-modules javafx.controls,javafx.fxml \
  -cp out:$MYSQL_JAR ClinicManagementApp
```

---

## 🔑 Sample Login Credentials

| Role | User ID | Password |
|---|---|---|
| Admin | `admin` | `password` |
| Doctor | `D001` | `doc123` |
| Doctor | `D002` | `doc123` |
| Doctor | `D003` | `doc123` |
| Patient | `P001` | `pat123` |
| Patient | `P002` | `pat123` |

---

## 📝 Development Notes

- This submission represents **~40% completion** of the full system.
- All unfinished features are clearly marked with `// TODO:` comments in the source code.
- Placeholder messages ("Feature under development") are shown in the UI for incomplete tabs.
- The UI matches the original mockup screens submitted in previous reports.
- No Scene Builder or FXML is used — all UI is built with **pure programmatic JavaFX**.

---

## 👨‍💻 Week 12 Final Submission Plan

The following will be completed for the final submission:

1. Doctor availability and time slot management
2. Appointment conflict detection
3. Patient self-registration
4. Full profile editing for all roles
5. Date-based filtering for appointments
6. Analytics / reporting dashboard
7. Email/SMS notification stubs
8. Password hashing with BCrypt
9. Login attempt limiting and session management
