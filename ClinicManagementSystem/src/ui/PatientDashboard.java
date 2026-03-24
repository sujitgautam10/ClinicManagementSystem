package ui;

import dao.AppointmentDAO;
import dao.DoctorDAO;
import dao.PatientDAO;
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
import model.Patient;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
public class PatientDashboard {

    private final Stage          stage;
    private       Patient        patient;
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final DoctorDAO      doctorDAO      = new DoctorDAO();
    private final PatientDAO     patientDAO     = new PatientDAO();

    public PatientDashboard(Stage stage, Patient patient) {
        this.stage   = stage;
        this.patient = patient;
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f8f9fa;");
        root.setTop(buildHeader());

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab bookTab    = new Tab("Book Appointment", buildBookContent(tabPane));
        Tab myApptTab  = new Tab("My Appointments",  buildMyAppointmentsContent(tabPane));
        Tab profileTab = new Tab("My Profile",        buildProfileContent());

        for (Tab t : List.of(bookTab, myApptTab, profileTab))
            t.setStyle("-fx-font-size: 13; -fx-font-weight: bold;");

        tabPane.getTabs().addAll(bookTab, myApptTab, profileTab);
        root.setCenter(tabPane);

        Scene scene = new Scene(root, 1050, 720);
        stage.setTitle("Patient Dashboard – Clinic Management System");
        stage.setScene(scene);
        stage.show();
    }

    // Build the top header bar
    private HBox buildHeader() {
        HBox header = new HBox(14);
        header.setPadding(new Insets(18, 30, 18, 30));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: linear-gradient(to right, #2c3e50, #3498db);");

        Label icon = new Label("👤");
        icon.setFont(Font.font(28));
        icon.setStyle(
                "-fx-background-color: rgba(255,255,255,0.2);" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 8 12 8 12;"
        );

        VBox titleBox = new VBox(2);
        Label title    = new Label("Patient Dashboard");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        title.setTextFill(Color.WHITE);
        Label subtitle = new Label("Welcome, " + patient.getName());
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

    // Book Appointment tab with doctor search
    private ScrollPane buildBookContent(TabPane tabPane) {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: #f8f9fa;");

        VBox formCard = card();
        formCard.setMaxWidth(800);

        Label formTitle = sectionTitle("📅 Book New Appointment");

        // Doctor search field
        Label searchLabel = formLabel("Search Doctor");
        TextField searchField = styledField("Search by name or specialization");

        // Doctor ComboBox
        Label docLabel = formLabel("Select Doctor");
        List<Doctor> allDoctors = doctorDAO.getAllDoctors();
        ObservableList<String> doctorItems = FXCollections.observableArrayList();
        doctorItems.add("Choose a doctor");
        for (Doctor d : allDoctors)
            doctorItems.add(d.getUserId() + " – " + d.getName() + " – " + d.getSpecialization());

        ComboBox<String> doctorCombo = new ComboBox<>(doctorItems);
        doctorCombo.setMaxWidth(Double.MAX_VALUE);
        doctorCombo.setStyle("-fx-font-size: 13; -fx-padding: 4;");
        doctorCombo.getSelectionModel().selectFirst();

        // Live search — filters doctor list as user types
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            doctorCombo.getItems().clear();
            doctorCombo.getItems().add("Choose a doctor");
            String filter = newVal.toLowerCase().trim();
            for (Doctor d : allDoctors) {
                if (filter.isEmpty()
                        || d.getName().toLowerCase().contains(filter)
                        || d.getSpecialization().toLowerCase().contains(filter)) {
                    doctorCombo.getItems().add(d.getUserId() + " – " + d.getName() + " – " + d.getSpecialization());
                }
            }
            doctorCombo.getSelectionModel().selectFirst();
        });

        // Doctor info box
        VBox infoBox = new VBox(6);
        infoBox.setPadding(new Insets(14));
        infoBox.setStyle(
                "-fx-background-color: #e3f2fd;" +
                        "-fx-border-color: #90caf9;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;"
        );
        Label infoSpec = new Label("Specialization: –");
        Label infoQual = new Label("Qualifications: –");
        Label infoExp  = new Label("Experience: –");
        for (Label l : List.of(infoSpec, infoQual, infoExp)) {
            l.setFont(Font.font("Segoe UI", 13));
            l.setTextFill(Color.web("#1565c0"));
        }
        infoBox.getChildren().addAll(infoSpec, infoQual, infoExp);

        doctorCombo.setOnAction(e -> {
            int idx = doctorCombo.getSelectionModel().getSelectedIndex();
            if (idx > 0) {
                String selected = doctorCombo.getItems().get(idx);
                String uid = selected.split(" – ")[0];
                allDoctors.stream().filter(d -> d.getUserId().equals(uid)).findFirst().ifPresent(d -> {
                    infoSpec.setText("Specialization: " + d.getSpecialization());
                    infoQual.setText("Qualifications: " + d.getQualifications());
                    infoExp.setText("Experience: " + d.getExperienceYears() + " years");
                });
            } else {
                infoSpec.setText("Specialization: –");
                infoQual.setText("Qualifications: –");
                infoExp.setText("Experience: –");
            }
        });

        // Date and Time row
        GridPane dateRow = new GridPane();
        dateRow.setHgap(20);

        Label dateLabel = formLabel("Select Date");
        DatePicker datePicker = new DatePicker(LocalDate.now().plusDays(1));
        datePicker.setMaxWidth(Double.MAX_VALUE);
        datePicker.setStyle("-fx-font-size: 13;");
        // Block past dates
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now().plusDays(1)));
            }
        });

        Label timeLabel = formLabel("Select Time");
        ComboBox<String> timeCombo = new ComboBox<>();
        timeCombo.setMaxWidth(Double.MAX_VALUE);
        timeCombo.setStyle("-fx-font-size: 13;");
        timeCombo.getItems().addAll("09:00","09:30","10:00","10:30","11:00","11:30",
                "14:00","14:30","15:00","15:30","16:00");
        timeCombo.getSelectionModel().select("10:00");

        dateRow.addRow(0, dateLabel, timeLabel);
        dateRow.addRow(1, datePicker, timeCombo);
        ColumnConstraints cc = new ColumnConstraints(); cc.setPercentWidth(50);
        dateRow.getColumnConstraints().addAll(cc, new ColumnConstraints(){{ setPercentWidth(50); }});

        // Reason
        Label reasonLabel = formLabel("Reason for Visit");
        TextArea reasonArea = new TextArea();
        reasonArea.setPromptText("Describe your symptoms or reason for visit");
        reasonArea.setPrefRowCount(3);
        reasonArea.setStyle(
                "-fx-border-color: #dfe6e9;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-font-size: 13;"
        );

        Label feedbackLbl = new Label("");
        feedbackLbl.setFont(Font.font("Segoe UI", 13));
        feedbackLbl.setWrapText(true);

        Button bookBtn = new Button("📅 Book Appointment");
        bookBtn.setMaxWidth(Double.MAX_VALUE);
        bookBtn.setStyle(
                "-fx-background-color: #27ae60;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 12;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
        );

        bookBtn.setOnAction(e -> {
            int docIdx = doctorCombo.getSelectionModel().getSelectedIndex();
            if (docIdx <= 0) {
                feedbackLbl.setTextFill(Color.RED);
                feedbackLbl.setText("Please select a doctor.");
                return;
            }
            if (datePicker.getValue() == null) {
                feedbackLbl.setTextFill(Color.RED);
                feedbackLbl.setText("Please select a date.");
                return;
            }
            if (datePicker.getValue().isBefore(LocalDate.now().plusDays(1))) {
                feedbackLbl.setTextFill(Color.RED);
                feedbackLbl.setText("Please select a future date.");
                return;
            }
            if (timeCombo.getValue() == null) {
                feedbackLbl.setTextFill(Color.RED);
                feedbackLbl.setText("Please select a time slot.");
                return;
            }
            if (reasonArea.getText().isBlank()) {
                feedbackLbl.setTextFill(Color.RED);
                feedbackLbl.setText("Please describe the reason for your visit.");
                return;
            }

            String selected = doctorCombo.getItems().get(docIdx);
            String uid = selected.split(" – ")[0];
            Doctor selectedDoc = allDoctors.stream()
                    .filter(d -> d.getUserId().equals(uid))
                    .findFirst().orElse(null);
            if (selectedDoc == null) return;

            String apptId = "APT" + (1000 + (int)(Math.random() * 8999));
            Appointment appt = new Appointment(
                    apptId,
                    patient.getUserId(), patient.getName(),
                    selectedDoc.getUserId(), selectedDoc.getName(),
                    datePicker.getValue().toString(),
                    timeCombo.getValue() + ":00",
                    reasonArea.getText().trim(),
                    Status.SCHEDULED, ""
            );

            boolean ok = appointmentDAO.bookAppointment(appt);
            if (ok) {
                feedbackLbl.setTextFill(Color.web("#27ae60"));
                feedbackLbl.setText("Appointment booked successfully! ID: " + apptId);
                reasonArea.clear();
                searchField.clear();
                doctorCombo.getSelectionModel().selectFirst();
                tabPane.getTabs().get(1).setContent(buildMyAppointmentsContent(tabPane));
            } else {
                feedbackLbl.setTextFill(Color.RED);
                feedbackLbl.setText("Failed to book appointment. Please try again.");
            }
        });

        formCard.getChildren().addAll(
                formTitle,
                searchLabel, searchField,
                docLabel, doctorCombo,
                infoBox,
                dateRow,
                reasonLabel, reasonArea,
                bookBtn, feedbackLbl
        );

        content.getChildren().add(formCard);
        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: #f8f9fa;");
        return sp;
    }

    // My Appointments tab with status filter
    private ScrollPane buildMyAppointmentsContent(TabPane tabPane) {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: #f8f9fa;");

        VBox tableCard = card();
        tableCard.getChildren().add(sectionTitle("My Appointments"));

        // Status filter
        HBox filterRow = new HBox(12);
        filterRow.setAlignment(Pos.CENTER_LEFT);
        Label filterLabel = formLabel("Filter by Status:");
        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All", "SCHEDULED", "COMPLETED", "CANCELLED");
        statusFilter.getSelectionModel().select("All");
        statusFilter.setStyle("-fx-font-size: 13;");
        filterRow.getChildren().addAll(filterLabel, statusFilter);

        List<Appointment> allAppts = appointmentDAO.getByPatientId(patient.getUserId());
        ObservableList<Appointment> apptList = FXCollections.observableArrayList(allAppts);

        TableView<Appointment> table = new TableView<>(apptList);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-font-size: 13;");
        table.setPrefHeight(300);

        TableColumn<Appointment, String> colId     = col("Appointment ID", "appointmentId");
        TableColumn<Appointment, String> colDoc    = col("Doctor",          "doctorName");
        TableColumn<Appointment, String> colDate   = col("Date",            "appointmentDate");
        TableColumn<Appointment, String> colTime   = col("Time",            "appointmentTime");
        TableColumn<Appointment, String> colReason = col("Reason",          "reason");

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
            final Button cancelBtn = actionButton("✖ Cancel", "#e74c3c");
            { cancelBtn.setOnAction(e -> {
                Appointment a = getTableView().getItems().get(getIndex());
                if (a.getStatus() == Status.SCHEDULED) {
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                            "Cancel appointment " + a.getAppointmentId() + "?", ButtonType.YES, ButtonType.NO);
                    confirm.showAndWait().ifPresent(res -> {
                        if (res == ButtonType.YES) {
                            appointmentDAO.updateStatus(a.getAppointmentId(), Status.CANCELLED);
                            a.setStatus(Status.CANCELLED);
                            getTableView().refresh();
                        }
                    });
                } else {
                    new Alert(Alert.AlertType.INFORMATION, "Only SCHEDULED appointments can be cancelled.").show();
                }
            }); }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) { setGraphic(null); return; }
                Appointment a = getTableView().getItems().get(getIndex());
                setGraphic(a.getStatus() == Status.SCHEDULED ? cancelBtn : new Label("-"));
            }
        });

        table.getColumns().addAll(colId, colDoc, colDate, colTime, colReason, colStatus, colAction);

        // Filter logic
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
        content.getChildren().add(tableCard);
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

        // View mode fields
        GridPane viewGrid = new GridPane();
        viewGrid.setHgap(30);
        viewGrid.addRow(0, profileItem("Patient ID",   patient.getUserId()),
                profileItem("Name",         patient.getName()));
        viewGrid.addRow(1, profileItem("Email",        patient.getEmail()),
                profileItem("Phone",        patient.getPhone()));
        viewGrid.addRow(2, profileItem("Date of Birth",patient.getDateOfBirth()),
                profileItem("Age",          patient.getAge() + " years"));
        VBox addrItem = profileItem("Address", patient.getAddress());
        GridPane.setColumnSpan(addrItem, 2);
        viewGrid.add(addrItem, 0, 3);

        // Edit mode form
        GridPane editGrid = new GridPane();
        editGrid.setHgap(16); editGrid.setVgap(12);
        editGrid.setVisible(false); editGrid.setManaged(false);

        TextField tfName  = styledField(patient.getName());   tfName.setText(patient.getName());
        TextField tfEmail = styledField(patient.getEmail());   tfEmail.setText(patient.getEmail());
        TextField tfPhone = styledField(patient.getPhone());   tfPhone.setText(patient.getPhone());
        TextField tfAddr  = styledField(patient.getAddress()); tfAddr.setText(patient.getAddress());

        editGrid.addRow(0, formLabel("Name"),    tfName,  formLabel("Email"),   tfEmail);
        editGrid.addRow(1, formLabel("Phone"),   tfPhone, formLabel("Address"), tfAddr);

        Label feedbackLbl = new Label("");
        feedbackLbl.setFont(Font.font("Segoe UI", 13));

        Button editBtn = actionButton("✏ Edit Profile", "#3498db");
        Button saveBtn = actionButton("💾 Save Changes", "#27ae60");
        Button cancelEditBtn = actionButton("✖ Cancel", "#95a5a6");
        saveBtn.setVisible(false); saveBtn.setManaged(false);
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
            patient.setName(tfName.getText().trim());
            patient.setEmail(tfEmail.getText().trim());
            patient.setPhone(tfPhone.getText().trim());
            patient.setAddress(tfAddr.getText().trim());

            boolean ok = patientDAO.updatePatient(patient);
            if (ok) {
                feedbackLbl.setTextFill(Color.web("#27ae60"));
                feedbackLbl.setText("Profile updated successfully.");
                viewGrid.getChildren().clear();
                viewGrid.addRow(0, profileItem("Patient ID",   patient.getUserId()),
                        profileItem("Name",         patient.getName()));
                viewGrid.addRow(1, profileItem("Email",        patient.getEmail()),
                        profileItem("Phone",        patient.getPhone()));
                viewGrid.addRow(2, profileItem("Date of Birth",patient.getDateOfBirth()),
                        profileItem("Age",          patient.getAge() + " years"));
                VBox addr = profileItem("Address", patient.getAddress());
                GridPane.setColumnSpan(addr, 2);
                viewGrid.add(addr, 0, 3);

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