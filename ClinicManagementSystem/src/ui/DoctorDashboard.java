package ui;

import dao.AppointmentDAO;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import model.Appointment;
import model.Appointment.Status;
import model.Doctor;

import java.util.List;

/**
 * DoctorDashboard - Programmatic JavaFX (NO Scene Builder / FXML)
 * Week 9 - OOP Architecture Submission (Partial Implementation ~40%)
 *
 * Tabs: My Appointments | Manage Availability | My Profile
 * UI matches the provided HTML mockup exactly.
 */
public class DoctorDashboard {

    private final Stage  stage;
    private final Doctor doctor;
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();

    public DoctorDashboard(Stage stage, Doctor doctor) {
        this.stage  = stage;
        this.doctor = doctor;
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f8f9fa;");
        root.setTop(buildHeader());

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab apptTab    = new Tab("My Appointments",    buildAppointmentsContent(tabPane));
        Tab availTab   = new Tab("Manage Availability", buildAvailabilityContent());
        Tab profileTab = new Tab("My Profile",         buildProfileContent());

        for (Tab t : List.of(apptTab, availTab, profileTab))
            t.setStyle("-fx-font-size: 13; -fx-font-weight: bold;");

        tabPane.getTabs().addAll(apptTab, availTab, profileTab);
        root.setCenter(tabPane);

        Scene scene = new Scene(root, 1050, 700);
        stage.setTitle("Doctor Dashboard – Clinic Management System");
        stage.setScene(scene);
        stage.show();
    }

    // Build the top header bar
    private HBox buildHeader() {
        HBox header = new HBox(14);
        header.setPadding(new Insets(18, 30, 18, 30));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: linear-gradient(to right, #16a085, #1abc9c);");

        Label icon = new Label("👨‍⚕️");
        icon.setFont(Font.font(28));
        icon.setStyle(
            "-fx-background-color: rgba(255,255,255,0.2);" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 8 12 8 12;"
        );

        VBox titleBox = new VBox(2);
        Label title    = new Label("Doctor Dashboard");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        title.setTextFill(Color.WHITE);
        Label subtitle = new Label("Welcome, " + doctor.getName());
        subtitle.setFont(Font.font("Segoe UI", 13));
        subtitle.setTextFill(Color.web("#ffffff99"));
        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button logoutBtn = new Button("🚪 Logout");
        logoutBtn.setStyle(
            "-fx-background-color: rgba(255,255,255,0.2);" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 8 18 8 18;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        logoutBtn.setOnAction(e -> new LoginScreen(stage).show());

        header.getChildren().addAll(icon, titleBox, spacer, logoutBtn);
        return header;
    }

    // My Appointments tab — view and complete appointments
    private ScrollPane buildAppointmentsContent(TabPane tabPane) {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: #f8f9fa;");

        // Stats row
        List<Appointment> allAppts = appointmentDAO.getByDoctorId(doctor.getUserId());
        long scheduled = allAppts.stream().filter(a -> a.getStatus() == Status.SCHEDULED).count();
        long completed = allAppts.stream().filter(a -> a.getStatus() == Status.COMPLETED).count();

        HBox statsRow = new HBox(16);
        statsRow.getChildren().addAll(
            statCard("📅", String.valueOf(allAppts.size()), "Total Appointments", "#667eea", "#764ba2"),
            statCard("⏳", String.valueOf(scheduled),       "Scheduled",          "#ffa751", "#ffe259"),
            statCard("✓",  String.valueOf(completed),       "Completed",          "#56ab2f", "#a8e063")
        );

        // Appointments table
        VBox tableCard = card();
        tableCard.getChildren().add(sectionTitle("My Appointments"));

        TableView<Appointment> table = new TableView<>(FXCollections.observableArrayList(allAppts));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-font-size: 13;");
        table.setPrefHeight(280);

        TableColumn<Appointment, String> colId     = col("ID",      "appointmentId");
        TableColumn<Appointment, String> colPat    = col("Patient",  "patientName");
        TableColumn<Appointment, String> colDate   = col("Date",     "appointmentDate");
        TableColumn<Appointment, String> colTime   = col("Time",     "appointmentTime");
        TableColumn<Appointment, String> colReason = col("Reason",   "reason");

        TableColumn<Appointment, Appointment.Status> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setCellFactory(c -> statusCell());

        TableColumn<Appointment, Void> colAction = new TableColumn<>("Action");
        colAction.setCellFactory(c -> new TableCell<>() {
            final Button completeBtn = actionButton("✓ Complete", "#27ae60");
            { completeBtn.setOnAction(e -> {
                Appointment a = getTableView().getItems().get(getIndex());
                if (a.getStatus() == Status.SCHEDULED) {
                    boolean ok = appointmentDAO.updateStatus(a.getAppointmentId(), Status.COMPLETED);
                    if (ok) {
                        a.setStatus(Status.COMPLETED);
                        getTableView().refresh();
                    }
                } else {
                    new Alert(Alert.AlertType.INFORMATION, "Only SCHEDULED appointments can be completed.").show();
                }
            }); }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) { setGraphic(null); return; }
                Appointment a = getTableView().getItems().get(getIndex());
                setGraphic(a.getStatus() == Status.SCHEDULED ? completeBtn : new Label("-"));
            }
        });

        table.getColumns().addAll(colId, colPat, colDate, colTime, colReason, colStatus, colAction);
        tableCard.getChildren().add(table);

        content.getChildren().addAll(statsRow, tableCard);
        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: #f8f9fa;");
        return sp;
    }

    // Availability tab — placeholder for Week 12
    private ScrollPane buildAvailabilityContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: #f8f9fa;");

        // TODO: Full availability/schedule management in final version
        VBox card = card();
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(60));

        Label icon = new Label("🗓️");
        icon.setFont(Font.font(56));

        Label msg = new Label("Availability Management");
        msg.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        msg.setTextFill(Color.web("#2c3e50"));

        Label sub = new Label("This feature is under development and will be fully implemented in the Week 12 final submission.\n" +
                              "Planned features: set working days, define time slots, block unavailable dates.");
        sub.setFont(Font.font("Segoe UI", 14));
        sub.setTextFill(Color.web("#7f8c8d"));
        sub.setWrapText(true);
        sub.setTextAlignment(TextAlignment.CENTER);
        sub.setMaxWidth(500);

        Label todo = new Label("// TODO: Implement availability scheduling in final version");
        todo.setFont(Font.font("Courier New", 12));
        todo.setTextFill(Color.web("#856404"));
        todo.setStyle("-fx-background-color:#fff3cd; -fx-padding: 10 16 10 16; -fx-background-radius:6;");

        card.getChildren().addAll(icon, msg, sub, todo);
        content.getChildren().add(card);

        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: #f8f9fa;");
        return sp;
    }

    // Profile tab — show patient information
    private ScrollPane buildProfileContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: #f8f9fa;");

        VBox profileCard = card();
        profileCard.setMaxWidth(680);

        Label title = sectionTitle("My Profile");
        title.setPadding(new Insets(0, 0, 16, 0));

        GridPane grid = new GridPane();
        grid.setHgap(30);
        grid.setVgap(0);

        int row = 0;
        grid.addRow(row++, profileItem("Doctor ID",       doctor.getUserId()),
                           profileItem("Name",            doctor.getName()));
        grid.addRow(row++, profileItem("Specialization",  doctor.getSpecialization()),
                           profileItem("Qualifications",  doctor.getQualifications()));
        grid.addRow(row++, profileItem("Experience",      doctor.getExperienceYears() + " years"),
                           profileItem("Email",           doctor.getEmail()));

        VBox phoneRow = profileItem("Phone", doctor.getPhone());
        GridPane.setColumnSpan(phoneRow, 2);
        grid.add(phoneRow, 0, row);

        profileCard.getChildren().addAll(title, grid);

        // TODO: Edit profile form in final version
        Label todo = new Label("ℹ️  Profile editing will be available in the final version.");
        todo.setStyle(
            "-fx-background-color: #fff3cd;" +
            "-fx-border-color: #ffc107;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 12;" +
            "-fx-font-size: 13;"
        );

        content.getChildren().addAll(profileCard, todo);
        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: #f8f9fa;");
        return sp;
    }

    // UI helper methods
    private VBox card() {
        VBox box = new VBox(14);
        box.setPadding(new Insets(22));
        box.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.07), 10, 0, 0, 3);"
        );
        return box;
    }

    private Label sectionTitle(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        lbl.setTextFill(Color.web("#2c3e50"));
        return lbl;
    }

    private VBox statCard(String emoji, String value, String label, String c1, String c2) {
        VBox box = new VBox(5);
        box.setPadding(new Insets(22));
        box.setStyle(String.format(
            "-fx-background-color: linear-gradient(to bottom right, %s, %s);" +
            "-fx-background-radius: 12;", c1, c2));
        HBox.setHgrow(box, Priority.ALWAYS);

        Label emojiLbl = new Label(emoji); emojiLbl.setFont(Font.font(32));
        Label valLbl   = new Label(value); valLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 30)); valLbl.setTextFill(Color.WHITE);
        Label nameLbl  = new Label(label); nameLbl.setFont(Font.font("Segoe UI", 12)); nameLbl.setTextFill(Color.web("#ffffffcc"));

        box.getChildren().addAll(emojiLbl, valLbl, nameLbl);
        return box;
    }

    private VBox profileItem(String labelText, String value) {
        VBox box = new VBox(4);
        box.setPadding(new Insets(12, 0, 12, 0));
        box.setStyle("-fx-border-color: transparent transparent #ecf0f1 transparent; -fx-border-width: 0 0 1 0;");

        Label lbl = new Label(labelText.toUpperCase());
        lbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        lbl.setTextFill(Color.web("#7f8c8d"));

        Label val = new Label(value != null ? value : "-");
        val.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 15));
        val.setTextFill(Color.web("#2c3e50"));

        box.getChildren().addAll(lbl, val);
        return box;
    }

    private <T> TableColumn<T, String> col(String header, String prop) {
        TableColumn<T, String> c = new TableColumn<>(header);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        return c;
    }

    private Button actionButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 6 12 6 12;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;" +
            "-fx-font-size: 12;"
        );
        return btn;
    }

    private TableCell<Appointment, Appointment.Status> statusCell() {
        return new TableCell<>() {
            @Override protected void updateItem(Status s, boolean empty) {
                super.updateItem(s, empty);
                setText(empty || s == null ? null : s.name());
                if (!empty && s != null) {
                    setStyle(switch (s) {
                        case SCHEDULED -> "-fx-background-color:#fff3cd;-fx-text-fill:#856404;-fx-background-radius:12;-fx-font-weight:bold;";
                        case COMPLETED -> "-fx-background-color:#d4edda;-fx-text-fill:#155724;-fx-background-radius:12;-fx-font-weight:bold;";
                        case CANCELLED -> "-fx-background-color:#f8d7da;-fx-text-fill:#721c24;-fx-background-radius:12;-fx-font-weight:bold;";
                        default        -> "";
                    });
                }
            }
        };
    }
}
