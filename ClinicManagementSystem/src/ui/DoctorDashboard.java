package ui;

import dao.AppointmentDAO;
import dao.DoctorDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.util.stream.Collectors;

/**
 * DoctorDashboard - Programmatic JavaFX (NO Scene Builder / FXML)
 * Week 12 - Final Implementation
 *
 * Tabs: My Appointments | My Profile
 */
public class DoctorDashboard {

    private final Stage          stage;
    private       Doctor         doctor;
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final DoctorDAO      doctorDAO      = new DoctorDAO();

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

        Tab apptTab    = new Tab("My Appointments", buildAppointmentsContent(tabPane));
        Tab profileTab = new Tab("My Profile",      buildProfileContent());

        for (Tab t : List.of(apptTab, profileTab))
            t.setStyle("-fx-font-size: 13; -fx-font-weight: bold;");

        tabPane.getTabs().addAll(apptTab, profileTab);
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

    // My Appointments tab with stats and status filter
    private ScrollPane buildAppointmentsContent(TabPane tabPane) {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: #f8f9fa;");

        List<Appointment> allAppts = appointmentDAO.getByDoctorId(doctor.getUserId());
        long scheduled = allAppts.stream().filter(a -> a.getStatus() == Status.SCHEDULED).count();
        long completed = allAppts.stream().filter(a -> a.getStatus() == Status.COMPLETED).count();
        long cancelled = allAppts.stream().filter(a -> a.getStatus() == Status.CANCELLED).count();

        // Stats row
        HBox statsRow = new HBox(16);
        statsRow.getChildren().addAll(
                statCard("📅", String.valueOf(allAppts.size()), "Total Appointments", "#667eea", "#764ba2"),
                statCard("⏳", String.valueOf(scheduled),       "Scheduled",          "#ffa751", "#ffe259"),
                statCard("✓",  String.valueOf(completed),       "Completed",          "#56ab2f", "#a8e063"),
                statCard("✖",  String.valueOf(cancelled),       "Cancelled",          "#e74c3c", "#c0392b")
        );

        // Filter row
        VBox tableCard = card();
        tableCard.getChildren().add(sectionTitle("My Appointments"));

        HBox filterRow = new HBox(12);
        filterRow.setAlignment(Pos.CENTER_LEFT);
        Label filterLabel = formLabel("Filter by Status:");
        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All", "SCHEDULED", "COMPLETED", "CANCELLED");
        statusFilter.getSelectionModel().select("All");
        statusFilter.setStyle("-fx-font-size: 13;");
        filterRow.getChildren().addAll(filterLabel, statusFilter);

        ObservableList<Appointment> apptList = FXCollections.observableArrayList(allAppts);
        TableView<Appointment> table = new TableView<>(apptList);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-font-size: 13;");
        table.setPrefHeight(300);

        TableColumn<Appointment, String> colId     = col("ID",      "appointmentId");
        TableColumn<Appointment, String> colPat    = col("Patient",  "patientName");
        TableColumn<Appointment, String> colDate   = col("Date",     "appointmentDate");
        TableColumn<Appointment, String> colTime   = col("Time",     "appointmentTime");
        TableColumn<Appointment, String> colReason = col("Reason",   "reason");

        TableColumn<Appointment, Status> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setCellFactory(c -> new TableCell<>() {
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
        });

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

        // Status filter logic
        statusFilter.setOnAction(e -> {
            String selected = statusFilter.getValue();
            apptList.clear();
            if ("All".equals(selected)) {
                apptList.addAll(allAppts);
            } else {
                apptList.addAll(allAppts.stream()
                        .filter(a -> a.getStatus().name().equals(selected))
                        .collect(Collectors.toList()));
            }
        });

        tableCard.getChildren().addAll(filterRow, table);
        content.getChildren().addAll(statsRow, tableCard);

        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: #f8f9fa;");
        return sp;
    }

    // Profile tab with edit functionality
    private ScrollPane buildProfileContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: #f8f9fa;");

        VBox profileCard = card();
        profileCard.setMaxWidth(720);

        Label title = sectionTitle("My Profile");
        title.setPadding(new Insets(0, 0, 16, 0));

        // View mode
        GridPane viewGrid = new GridPane();
        viewGrid.setHgap(30);
        viewGrid.addRow(0, profileItem("Doctor ID",      doctor.getUserId()),
                profileItem("Name",           doctor.getName()));
        viewGrid.addRow(1, profileItem("Specialization", doctor.getSpecialization()),
                profileItem("Qualifications", doctor.getQualifications()));
        viewGrid.addRow(2, profileItem("Experience",     doctor.getExperienceYears() + " years"),
                profileItem("Email",          doctor.getEmail()));
        VBox phoneItem = profileItem("Phone", doctor.getPhone());
        GridPane.setColumnSpan(phoneItem, 2);
        viewGrid.add(phoneItem, 0, 3);

        // Edit mode
        GridPane editGrid = new GridPane();
        editGrid.setHgap(16); editGrid.setVgap(12);
        editGrid.setVisible(false); editGrid.setManaged(false);

        TextField tfName  = styledField("Name");         tfName.setText(doctor.getName());
        TextField tfEmail = styledField("Email");         tfEmail.setText(doctor.getEmail());
        TextField tfPhone = styledField("Phone");         tfPhone.setText(doctor.getPhone());
        TextField tfSpec  = styledField("Specialization"); tfSpec.setText(doctor.getSpecialization());
        TextField tfQual  = styledField("Qualifications"); tfQual.setText(doctor.getQualifications());
        TextField tfExp   = styledField("Experience");    tfExp.setText(String.valueOf(doctor.getExperienceYears()));

        editGrid.addRow(0, formLabel("Name"),           tfName,  formLabel("Email"),          tfEmail);
        editGrid.addRow(1, formLabel("Phone"),          tfPhone, formLabel("Specialization"), tfSpec);
        editGrid.addRow(2, formLabel("Qualifications"), tfQual,  formLabel("Experience (yrs)"), tfExp);

        Label feedbackLbl = new Label("");
        feedbackLbl.setFont(Font.font("Segoe UI", 13));

        Button editBtn      = actionButton("✏ Edit Profile", "#3498db");
        Button saveBtn      = actionButton("💾 Save Changes", "#27ae60");
        Button cancelEditBtn = actionButton("✖ Cancel", "#95a5a6");
        saveBtn.setVisible(false);      saveBtn.setManaged(false);
        cancelEditBtn.setVisible(false); cancelEditBtn.setManaged(false);

        editBtn.setOnAction(e -> {
            viewGrid.setVisible(false); viewGrid.setManaged(false);
            editGrid.setVisible(true);  editGrid.setManaged(true);
            editBtn.setVisible(false);  editBtn.setManaged(false);
            saveBtn.setVisible(true);   saveBtn.setManaged(true);
            cancelEditBtn.setVisible(true); cancelEditBtn.setManaged(true);
            feedbackLbl.setText("");
        });

        cancelEditBtn.setOnAction(e -> {
            viewGrid.setVisible(true);  viewGrid.setManaged(true);
            editGrid.setVisible(false); editGrid.setManaged(false);
            editBtn.setVisible(true);   editBtn.setManaged(true);
            saveBtn.setVisible(false);  saveBtn.setManaged(false);
            cancelEditBtn.setVisible(false); cancelEditBtn.setManaged(false);
            feedbackLbl.setText("");
        });

        saveBtn.setOnAction(e -> {
            if (tfName.getText().isBlank()) {
                feedbackLbl.setTextFill(Color.RED);
                feedbackLbl.setText("Name cannot be empty.");
                return;
            }
            int exp = 0;
            try { exp = Integer.parseInt(tfExp.getText().trim()); } catch (NumberFormatException ignored) {}

            doctor.setName(tfName.getText().trim());
            doctor.setEmail(tfEmail.getText().trim());
            doctor.setPhone(tfPhone.getText().trim());
            doctor.setSpecialization(tfSpec.getText().trim());
            doctor.setQualifications(tfQual.getText().trim());
            doctor.setExperienceYears(exp);

            boolean ok = doctorDAO.updateDoctor(doctor);
            if (ok) {
                feedbackLbl.setTextFill(Color.web("#27ae60"));
                feedbackLbl.setText("Profile updated successfully.");

                viewGrid.getChildren().clear();
                viewGrid.addRow(0, profileItem("Doctor ID",      doctor.getUserId()),
                        profileItem("Name",           doctor.getName()));
                viewGrid.addRow(1, profileItem("Specialization", doctor.getSpecialization()),
                        profileItem("Qualifications", doctor.getQualifications()));
                viewGrid.addRow(2, profileItem("Experience",     doctor.getExperienceYears() + " years"),
                        profileItem("Email",          doctor.getEmail()));
                VBox ph = profileItem("Phone", doctor.getPhone());
                GridPane.setColumnSpan(ph, 2);
                viewGrid.add(ph, 0, 3);

                viewGrid.setVisible(true);  viewGrid.setManaged(true);
                editGrid.setVisible(false); editGrid.setManaged(false);
                editBtn.setVisible(true);   editBtn.setManaged(true);
                saveBtn.setVisible(false);  saveBtn.setManaged(false);
                cancelEditBtn.setVisible(false); cancelEditBtn.setManaged(false);
            } else {
                feedbackLbl.setTextFill(Color.RED);
                feedbackLbl.setText("Failed to update profile. Please try again.");
            }
        });

        HBox btnRow = new HBox(10, editBtn, saveBtn, cancelEditBtn);
        profileCard.getChildren().addAll(title, viewGrid, editGrid, btnRow, feedbackLbl);
        content.getChildren().add(profileCard);

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

    private Label formLabel(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        lbl.setTextFill(Color.web("#2c3e50"));
        return lbl;
    }

    private TextField styledField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(
                "-fx-padding: 9 12 9 12;" +
                        "-fx-border-color: #dfe6e9;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 7;" +
                        "-fx-background-radius: 7;" +
                        "-fx-font-size: 13;"
        );
        return tf;
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
}