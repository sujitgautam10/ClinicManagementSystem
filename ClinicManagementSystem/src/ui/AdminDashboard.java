package ui;

import dao.AppointmentDAO;
import dao.DoctorDAO;
import dao.PatientDAO;
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
import model.Admin;
import model.Appointment;
import model.Doctor;
import model.Patient;

import java.util.List;

/**
 * AdminDashboard - Programmatic JavaFX (NO Scene Builder / FXML)
 * Week 9 - OOP Architecture Submission (Partial Implementation ~40%)
 *
 * Tabs: Dashboard | Manage Doctors | Manage Patients | View Appointments
 * UI matches the provided HTML mockup exactly.
 */
public class AdminDashboard {

    private final Stage stage;
    private final Admin admin;

    private final DoctorDAO      doctorDAO      = new DoctorDAO();
    private final PatientDAO     patientDAO     = new PatientDAO();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();

    public AdminDashboard(Stage stage, Admin admin) {
        this.stage = stage;
        this.admin = admin;
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f8f9fa;");

        // ---- Header ----
        root.setTop(buildHeader());

        // ---- Tab Pane ----
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-background-color: white;");

        Tab dashTab    = new Tab("Dashboard",         buildDashboardContent());
        Tab doctorTab  = new Tab("Manage Doctors",    buildManageDoctorsContent(tabPane));
        Tab patientTab = new Tab("Manage Patients",   buildManagePatientsContent());
        Tab apptTab    = new Tab("View Appointments", buildAppointmentsContent());

        styleTabs(dashTab, doctorTab, patientTab, apptTab);
        tabPane.getTabs().addAll(dashTab, doctorTab, patientTab, apptTab);
        root.setCenter(tabPane);

        Scene scene = new Scene(root, 1100, 720);
        stage.setTitle("Admin Dashboard – Clinic Management System");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }

    // Build the top header bar
    private HBox buildHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(18, 30, 18, 30));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: linear-gradient(to right, #8e44ad, #9b59b6);");

        Label icon = new Label("👨‍💼");
        icon.setFont(Font.font(28));
        icon.setStyle(
            "-fx-background-color: rgba(255,255,255,0.2);" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 8 12 8 12;"
        );

        VBox titleBox = new VBox(2);
        Label title    = new Label("Admin Dashboard");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        title.setTextFill(Color.WHITE);
        Label subtitle = new Label("Welcome, System Administrator");
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

        header.setSpacing(14);
        header.getChildren().addAll(icon, titleBox, spacer, logoutBtn);
        return header;
    }

    // Dashboard tab — stats and recent appointments
    private ScrollPane buildDashboardContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: #f8f9fa;");

        // Stats cards
        int doctorCount    = doctorDAO.getDoctorCount();
        int patientCount   = patientDAO.getPatientCount();
        int totalAppts     = appointmentDAO.getAppointmentCount();
        int scheduledCount = appointmentDAO.getScheduledCount();

        HBox statsRow = new HBox(16);
        statsRow.getChildren().addAll(
            statCard("👨‍⚕️", String.valueOf(doctorCount),  "Total Doctors",      "#667eea", "#764ba2"),
            statCard("👥",   String.valueOf(patientCount),  "Total Patients",     "#f093fb", "#f5576c"),
            statCard("📅",   String.valueOf(totalAppts),    "Total Appointments", "#ffa751", "#ffe259"),
            statCard("✓",    String.valueOf(scheduledCount),"Scheduled",          "#56ab2f", "#a8e063")
        );

        // Recent appointments table
        Label tableTitle = new Label("Recent Appointments");
        tableTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        tableTitle.setTextFill(Color.web("#2c3e50"));

        TableView<Appointment> table = buildAppointmentTable(appointmentDAO.getAllAppointments().stream().limit(5).toList());

        content.getChildren().addAll(statsRow, tableTitle, table);

        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: #f8f9fa;");
        return sp;
    }

    // Manage Doctors tab — add and view doctors
    private ScrollPane buildManageDoctorsContent(TabPane tabPane) {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: #f8f9fa;");

        // Add Doctor Form
        VBox formCard = card();
        Label formTitle = sectionTitle("➕ Add New Doctor");

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(14);

        TextField tfId     = styledField("e.g., D004");
        TextField tfName   = styledField("Dr. John Doe");
        TextField tfEmail  = styledField("doctor@clinic.com");
        TextField tfPhone  = styledField("9876543210");
        TextField tfSpec   = styledField("e.g., Cardiologist");
        TextField tfQual   = styledField("MBBS, MD");
        TextField tfExp    = styledField("10");
        TextField tfPass   = styledField("Password");

        grid.addRow(0, formLabel("Doctor ID"), tfId,   formLabel("Full Name"), tfName);
        grid.addRow(1, formLabel("Email"),     tfEmail, formLabel("Phone"),    tfPhone);
        grid.addRow(2, formLabel("Specialization"), tfSpec, formLabel("Qualifications"), tfQual);
        grid.addRow(3, formLabel("Experience (years)"), tfExp, formLabel("Password"), tfPass);

        Label feedbackLbl = new Label("");
        feedbackLbl.setFont(Font.font("Segoe UI", 13));

        Button addBtn = actionButton("Add Doctor", "#27ae60");
        addBtn.setMaxWidth(Double.MAX_VALUE);
        addBtn.setOnAction(e -> {
            if (tfId.getText().isBlank() || tfName.getText().isBlank() || tfPass.getText().isBlank()) {
                feedbackLbl.setTextFill(Color.RED);
                feedbackLbl.setText("Doctor ID, Name and Password are required.");
                return;
            }
            int exp = 0;
            try { exp = Integer.parseInt(tfExp.getText().trim()); } catch (NumberFormatException ignored) {}

            Doctor doc = new Doctor(
                tfId.getText().trim(), tfName.getText().trim(),
                tfEmail.getText().trim(), tfPhone.getText().trim(),
                tfPass.getText().trim(), tfSpec.getText().trim(),
                tfQual.getText().trim(), exp
            );
            boolean ok = doctorDAO.addDoctor(doc);
            if (ok) {
                feedbackLbl.setTextFill(Color.web("#27ae60"));
                feedbackLbl.setText("Doctor added successfully.");
                tfId.clear(); tfName.clear(); tfEmail.clear(); tfPhone.clear();
                tfSpec.clear(); tfQual.clear(); tfExp.clear(); tfPass.clear();
                // Refresh doctor tab
                tabPane.getTabs().get(1).setContent(buildManageDoctorsContent(tabPane));
            } else {
                feedbackLbl.setTextFill(Color.RED);
                feedbackLbl.setText("Failed to add doctor. ID may already exist.");
            }
        });

        formCard.getChildren().addAll(formTitle, grid, addBtn, feedbackLbl);

        // Doctors list table
        Label listTitle = sectionTitle("All Doctors");
        VBox tableCard = card();

        List<Doctor> doctors = doctorDAO.getAllDoctors();
        TableView<Doctor> docTable = new TableView<>(FXCollections.observableArrayList(doctors));
        docTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        docTable.setStyle("-fx-font-size: 13;");
        docTable.setPrefHeight(220);

        TableColumn<Doctor, String> colId   = new TableColumn<>("ID");           colId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        TableColumn<Doctor, String> colName = new TableColumn<>("Name");         colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Doctor, String> colSpec = new TableColumn<>("Specialization"); colSpec.setCellValueFactory(new PropertyValueFactory<>("specialization"));
        TableColumn<Doctor, String> colQual = new TableColumn<>("Qualifications"); colQual.setCellValueFactory(new PropertyValueFactory<>("qualifications"));
        TableColumn<Doctor, Integer> colExp = new TableColumn<>("Exp (yrs)");    colExp.setCellValueFactory(new PropertyValueFactory<>("experienceYears"));

        TableColumn<Doctor, Void> colAction = new TableColumn<>("Actions");
        colAction.setCellFactory(col -> new TableCell<>() {
            final Button delBtn = actionButton("✖ Delete", "#e74c3c");
            { delBtn.setOnAction(e -> {
                Doctor d = getTableView().getItems().get(getIndex());
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Delete doctor " + d.getName() + "?", ButtonType.YES, ButtonType.NO);
                confirm.showAndWait().ifPresent(res -> {
                    if (res == ButtonType.YES) {
                        doctorDAO.deleteDoctor(d.getUserId());
                        getTableView().getItems().remove(d);
                    }
                });
            }); }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : delBtn);
            }
        });

        docTable.getColumns().addAll(colId, colName, colSpec, colQual, colExp, colAction);
        tableCard.getChildren().addAll(listTitle, docTable);

        content.getChildren().addAll(formCard, tableCard);
        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: #f8f9fa;");
        return sp;
    }

    // Manage Patients tab — view all patients
    private ScrollPane buildManagePatientsContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: #f8f9fa;");

        VBox tableCard = card();
        tableCard.getChildren().add(sectionTitle("All Patients"));

        List<Patient> patients = patientDAO.getAllPatients();
        TableView<Patient> table = new TableView<>(FXCollections.observableArrayList(patients));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-font-size: 13;");
        table.setPrefHeight(280);

        TableColumn<Patient, String> colId   = new TableColumn<>("ID");       colId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        TableColumn<Patient, String> colName = new TableColumn<>("Name");     colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Patient, String> colEmail= new TableColumn<>("Email");    colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        TableColumn<Patient, String> colPhone= new TableColumn<>("Phone");    colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        TableColumn<Patient, String> colDob  = new TableColumn<>("DOB");      colDob.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
        TableColumn<Patient, Integer> colAge = new TableColumn<>("Age");      colAge.setCellValueFactory(new PropertyValueFactory<>("age"));

        TableColumn<Patient, Void> colAction = new TableColumn<>("Actions");
        colAction.setCellFactory(col -> new TableCell<>() {
            final Button delBtn = actionButton("✖ Delete", "#e74c3c");
            { delBtn.setOnAction(e -> {
                Patient p = getTableView().getItems().get(getIndex());
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Delete patient " + p.getName() + "?", ButtonType.YES, ButtonType.NO);
                confirm.showAndWait().ifPresent(res -> {
                    if (res == ButtonType.YES) {
                        patientDAO.deletePatient(p.getUserId());
                        getTableView().getItems().remove(p);
                    }
                });
            }); }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : delBtn);
            }
        });

        table.getColumns().addAll(colId, colName, colEmail, colPhone, colDob, colAge, colAction);
        tableCard.getChildren().add(table);

        // TODO: Add patient registration form in final version
        Label todo = new Label("ℹ️  Patient self-registration will be available in the final version.");
        todo.setStyle(
            "-fx-background-color: #fff3cd;" +
            "-fx-border-color: #ffc107;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 12;" +
            "-fx-font-size: 13;"
        );
        todo.setWrapText(true);

        content.getChildren().addAll(tableCard, todo);
        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: #f8f9fa;");
        return sp;
    }

    // View Appointments tab — all appointments
    private ScrollPane buildAppointmentsContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: #f8f9fa;");

        VBox tableCard = card();
        tableCard.getChildren().add(sectionTitle("All Appointments"));

        List<Appointment> appts = appointmentDAO.getAllAppointments();
        tableCard.getChildren().add(buildAppointmentTable(appts));

        // TODO: Add date filtering in final version
        Label todo = new Label("ℹ️  Date filter and appointment reports will be available in the final version.");
        todo.setStyle(
            "-fx-background-color: #fff3cd;" +
            "-fx-border-color: #ffc107;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 12;" +
            "-fx-font-size: 13;"
        );
        todo.setWrapText(true);

        content.getChildren().addAll(tableCard, todo);
        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: #f8f9fa;");
        return sp;
    }

    // Reusable appointment table builder
    private TableView<Appointment> buildAppointmentTable(List<Appointment> appts) {
        TableView<Appointment> table = new TableView<>(FXCollections.observableArrayList(appts));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-font-size: 13;");
        table.setPrefHeight(230);

        TableColumn<Appointment, String> colId     = new TableColumn<>("ID");       colId.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        TableColumn<Appointment, String> colPat    = new TableColumn<>("Patient");   colPat.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        TableColumn<Appointment, String> colDoc    = new TableColumn<>("Doctor");    colDoc.setCellValueFactory(new PropertyValueFactory<>("doctorName"));
        TableColumn<Appointment, String> colDate   = new TableColumn<>("Date");      colDate.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
        TableColumn<Appointment, String> colTime   = new TableColumn<>("Time");      colTime.setCellValueFactory(new PropertyValueFactory<>("appointmentTime"));
        TableColumn<Appointment, Appointment.Status> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Appointment.Status s, boolean empty) {
                super.updateItem(s, empty);
                setText(empty || s == null ? null : s.name());
                if (!empty && s != null) {
                    setStyle(switch (s) {
                        case SCHEDULED  -> "-fx-background-color:#fff3cd; -fx-text-fill:#856404; -fx-background-radius:12; -fx-font-weight:bold;";
                        case COMPLETED  -> "-fx-background-color:#d4edda; -fx-text-fill:#155724; -fx-background-radius:12; -fx-font-weight:bold;";
                        case CANCELLED  -> "-fx-background-color:#f8d7da; -fx-text-fill:#721c24; -fx-background-radius:12; -fx-font-weight:bold;";
                        default         -> "";
                    });
                }
            }
        });

        table.getColumns().addAll(colId, colPat, colDoc, colDate, colTime, colStatus);
        return table;
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
        tf.setPrefWidth(200);
        return tf;
    }

    private Button actionButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 7 14 7 14;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;" +
            "-fx-font-size: 12;"
        );
        return btn;
    }

    private VBox statCard(String emoji, String value, String label, String c1, String c2) {
        VBox box = new VBox(6);
        box.setPadding(new Insets(22));
        box.setStyle(String.format(
            "-fx-background-color: linear-gradient(to bottom right, %s, %s);" +
            "-fx-background-radius: 12;", c1, c2));
        box.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(box, Priority.ALWAYS);

        Label emojiLbl = new Label(emoji);
        emojiLbl.setFont(Font.font(36));

        Label valLbl = new Label(value);
        valLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 34));
        valLbl.setTextFill(Color.WHITE);

        Label nameLbl = new Label(label);
        nameLbl.setFont(Font.font("Segoe UI", 13));
        nameLbl.setTextFill(Color.web("#ffffffcc"));

        box.getChildren().addAll(emojiLbl, valLbl, nameLbl);
        return box;
    }

    private void styleTabs(Tab... tabs) {
        for (Tab t : tabs) {
            t.setStyle("-fx-font-size: 13; -fx-font-weight: bold;");
        }
    }
}
