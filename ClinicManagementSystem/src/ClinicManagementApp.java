import javafx.application.Application;
import javafx.stage.Stage;
import ui.LoginScreen;
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
