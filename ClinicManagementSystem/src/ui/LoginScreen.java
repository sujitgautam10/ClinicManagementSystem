package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import javafx.stage.Stage;
import model.Admin;
import model.Doctor;
import model.Patient;
import model.User;
import service.AuthService;

/**
 * LoginScreen - Programmatic JavaFX UI (NO Scene Builder / FXML)
 * Week 9 - OOP Architecture Submission (Partial Implementation ~40%)
 *
 * UI matches the provided mockup:
 *   - Purple gradient background
 *   - White login card with hospital icon
 *   - User ID + Password fields
 *   - Login button
 *   - Sample credentials hint box
 */
public class LoginScreen {

    private final Stage primaryStage;
    private final AuthService authService;

    public LoginScreen(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.authService  = new AuthService();
    }

    public void show() {
        // ---- Root: gradient background ----
        StackPane root = new StackPane();
        root.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #667eea, #764ba2);"
        );
        root.setPrefSize(900, 650);

        // ---- White login card ----
        VBox card = new VBox(16);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(40));
        card.setPrefWidth(400);
        card.setMaxWidth(400);
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 16;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 30, 0, 0, 10);"
        );

        // ---- Hospital icon ----
        Label icon = new Label("🏥");
        icon.setFont(Font.font(40));
        icon.setStyle(
            "-fx-background-color: #3498db;" +
            "-fx-background-radius: 50;" +
            "-fx-padding: 16 20 16 20;"
        );

        // ---- Title ----
        Label title = new Label("Clinic Management System");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        title.setTextFill(Color.web("#2c3e50"));
        title.setWrapText(true);
        title.setTextAlignment(TextAlignment.CENTER);

        Label subtitle = new Label("Login to continue");
        subtitle.setFont(Font.font("Segoe UI", 13));
        subtitle.setTextFill(Color.web("#7f8c8d"));

        // ---- User ID field ----
        Label idLabel = new Label("User ID");
        idLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        idLabel.setTextFill(Color.web("#2c3e50"));
        idLabel.setMaxWidth(Double.MAX_VALUE);

        TextField userIdField = new TextField();
        userIdField.setPromptText("Enter your user ID");
        userIdField.setStyle(
            "-fx-padding: 10 14 10 14;" +
            "-fx-border-color: #dfe6e9;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-font-size: 14;"
        );

        // ---- Password field ----
        Label passLabel = new Label("Password");
        passLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        passLabel.setTextFill(Color.web("#2c3e50"));
        passLabel.setMaxWidth(Double.MAX_VALUE);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setStyle(
            "-fx-padding: 10 14 10 14;" +
            "-fx-border-color: #dfe6e9;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-font-size: 14;"
        );

        // ---- Login button ----
        Button loginBtn = new Button("Login");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setStyle(
            "-fx-background-color: #3498db;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 15;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 12;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        loginBtn.setOnMouseEntered(e ->
            loginBtn.setStyle(loginBtn.getStyle() + "-fx-background-color: #2980b9;"));
        loginBtn.setOnMouseExited(e ->
            loginBtn.setStyle(loginBtn.getStyle().replace("-fx-background-color: #2980b9;", "") ));

        // ---- Error label ----
        Label errorLabel = new Label("");
        errorLabel.setTextFill(Color.RED);
        errorLabel.setFont(Font.font("Segoe UI", 12));
        errorLabel.setWrapText(true);

        // ---- Login action ----
        loginBtn.setOnAction(e -> handleLogin(userIdField.getText().trim(),
                                              passwordField.getText(), errorLabel));
        passwordField.setOnAction(e -> handleLogin(userIdField.getText().trim(),
                                                   passwordField.getText(), errorLabel));

        // ---- Credentials hint box ----
        VBox credBox = new VBox(6);
        credBox.setPadding(new Insets(16, 0, 0, 0));
        credBox.setStyle("-fx-border-color: #ecf0f1; -fx-border-width: 1 0 0 0;");

        Label credTitle = new Label("Sample Credentials:");
        credTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        credTitle.setTextFill(Color.web("#34495e"));

        Label c1 = credItem("Admin:   admin / password");
        Label c2 = credItem("Doctor:  D001 / doc123");
        Label c3 = credItem("Patient: P001 / pat123");

        credBox.getChildren().addAll(credTitle, c1, c2, c3);

        // ---- Assemble card ----
        card.getChildren().addAll(
            icon, title, subtitle,
            idLabel, userIdField,
            passLabel, passwordField,
            loginBtn, errorLabel,
            credBox
        );

        root.getChildren().add(card);

        // ---- Scene ----
        Scene scene = new Scene(root, 900, 650);
        primaryStage.setTitle("Clinic Management System");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    // ---- Helper: credential row style ----
    private Label credItem(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Courier New", 11));
        lbl.setTextFill(Color.web("#7f8c8d"));
        lbl.setStyle(
            "-fx-background-color: #f8f9fa;" +
            "-fx-padding: 5 8 5 8;" +
            "-fx-background-radius: 4;"
        );
        lbl.setMaxWidth(Double.MAX_VALUE);
        return lbl;
    }

    // ---- Handle login logic ----
    private void handleLogin(String userId, String password, Label errorLabel) {
        if (userId.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter your User ID and Password.");
            return;
        }

        User user = authService.authenticate(userId, password);

        if (user == null) {
            errorLabel.setText("Invalid credentials. Please try again.");
            return;
        }

        errorLabel.setText("");

        // Route to correct dashboard based on role
        if (user instanceof Admin admin) {
            new AdminDashboard(primaryStage, admin).show();
        } else if (user instanceof Doctor doctor) {
            new DoctorDashboard(primaryStage, doctor).show();
        } else if (user instanceof Patient patient) {
            new PatientDashboard(primaryStage, patient).show();
        }
    }
}
