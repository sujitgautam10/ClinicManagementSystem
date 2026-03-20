import javafx.application.Application;
import javafx.stage.Stage;
import ui.LoginScreen;

/**
 * ClinicManagementApp - Main Entry Point
 * Week 9 - OOP Architecture Submission (Partial Implementation ~40%)
 *
 * This is a partial implementation prepared for the Week 9 OOP Architecture demonstration.
 * The remaining features will be fully implemented in the Week 12 final submission.
 *
 * WORKING FEATURES (~40%):
 *   - Authentication: Admin / Doctor / Patient login
 *   - Admin Dashboard: view stats, add/delete doctors, view patients, view appointments
 *   - Doctor Dashboard: view & complete appointments, view profile
 *   - Patient Dashboard: book appointments, cancel appointments, view profile
 *
 * TODO (Week 12 final submission):
 *   - Doctor availability management
 *   - Advanced appointment filtering by date
 *   - Full patient registration by admin
 *   - Notifications (email/SMS)
 *   - Reports and analytics
 *   - Password hashing (BCrypt)
 *   - Session timeout management
 *   - Profile editing for Doctor and Patient
 *   - Time conflict checking when booking
 *
 * Run configuration:
 *   Add JavaFX SDK to VM options:
 *   --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml
 *   Also add mysql-connector-j-x.x.x.jar to classpath (place in lib/ folder).
 */
public class ClinicManagementApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        System.out.println("==========================================================");
        System.out.println("  Clinic Management System - Week 9 OOP Submission");
        System.out.println("  Partial Implementation (~40%)");
        System.out.println("-------------------------");
        System.out.println("  Sample credentials:");
        System.out.println("    Admin:   admin / password");
        System.out.println("    Doctor:  D001  / doc123");
        System.out.println("    Patient: P001  / pat123");
        System.out.println("==========================================================");

        new LoginScreen(primaryStage).show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
