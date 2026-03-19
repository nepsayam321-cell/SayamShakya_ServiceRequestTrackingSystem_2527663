package ui;

import model.*;
import service.RequestService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.List;
import java.util.stream.Collectors;

public class AdminDashboard {

    private Scene scene;
    private Admin admin;
    private RequestService service;
    private MainApp mainApp;

    public AdminDashboard(Admin admin,
                          RequestService service,
                          MainApp mainApp) {
        this.admin   = admin;
        this.service = service;
        this.mainApp = mainApp;
        buildUI();
    }

    private void buildUI() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f0f4ff;");

        // Top bar
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(14, 24, 14, 24));
        topBar.setStyle("-fx-background-color: #1E3A5F;");
        Label topLabel = new Label(
            "SRTS  —  Admin: " + admin.getFullName());
        topLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        topLabel.setTextFill(Color.WHITE);
        Button logoutBtn = new Button("Logout");
        logoutBtn.setFont(Font.font("Arial", 13));
        logoutBtn.setOnAction(e -> mainApp.showLoginScreen());
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topBar.getChildren().addAll(topLabel, spacer, logoutBtn);
        root.setTop(topBar);

        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(
            TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.setStyle("-fx-font-size: 13px;");
        tabs.getTabs().addAll(
            new Tab("All Requests",
                buildAllRequestsTab()),
            new Tab("Assign Staff",
                buildAssignTab()),
            new Tab("Create Staff Account",
                buildCreateStaffTab()),
            new Tab("View All Staff",
                buildViewStaffTab())
        );
        root.setCenter(tabs);

        scene = new Scene(root);
    }

    // All Requests Tab
    @SuppressWarnings("unchecked")
    private VBox buildAllRequestsTab() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(25));

        Label heading = new Label("All Service Requests");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        heading.setTextFill(Color.web("#1E3A5F"));

        HBox filterRow = new HBox(12);
        Label filterLabel = new Label("Filter by Status:");
        filterLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        ComboBox<String> filterBox = new ComboBox<>();
        filterBox.getItems().addAll(
            "All", "Pending", "Assigned",
            "In Progress", "Completed");
        filterBox.setValue("All");
        filterBox.setPrefWidth(180);
        filterBox.setStyle("-fx-font-size: 13px;");

        TableView<ServiceRequest> table = new TableView<>();
        table.setStyle("-fx-font-size: 13px;");
        table.setPlaceholder(new Label("No requests found."));

        TableColumn<ServiceRequest, Integer> idCol =
            new TableColumn<>("ID");
        idCol.setCellValueFactory(
            new PropertyValueFactory<>("requestID"));
        idCol.setPrefWidth(55);

        TableColumn<ServiceRequest, String> custCol =
            new TableColumn<>("Customer");
        custCol.setCellValueFactory(data ->
            new SimpleStringProperty(
                data.getValue().getCustomer().getFullName()));
        custCol.setPrefWidth(140);

        TableColumn<ServiceRequest, String> typeCol =
            new TableColumn<>("Service Type");
        typeCol.setCellValueFactory(
            new PropertyValueFactory<>("serviceType"));
        typeCol.setPrefWidth(140);

        TableColumn<ServiceRequest, String> catCol =
            new TableColumn<>("Category");
        catCol.setCellValueFactory(
            new PropertyValueFactory<>("category"));
        catCol.setPrefWidth(120);

        // Priority with colour
        TableColumn<ServiceRequest, String> priCol =
            new TableColumn<>("Priority");
        priCol.setCellValueFactory(
            new PropertyValueFactory<>("priority"));
        priCol.setPrefWidth(100);
        priCol.setCellFactory(
                col -> new TableCell<ServiceRequest, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null); setStyle("");
                } else {
                    setText(item);
                    setStyle(item.equals("Rush")
                        ? "-fx-background-color: #FEE2E2;" +
                          "-fx-text-fill: #DC2626;" +
                          "-fx-font-weight: bold;"
                        : "-fx-background-color: #DBEAFE;" +
                          "-fx-text-fill: #2563EB;");
                }
            }
        });

        // Status with colour
        TableColumn<ServiceRequest, String> statusCol =
            new TableColumn<>("Status");
        statusCol.setCellValueFactory(
            new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(120);
        statusCol.setCellFactory(
                col -> new TableCell<ServiceRequest, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null); setStyle(""); return;
                }
                setText(item);
                switch (item) {
                    case "Pending":
                        setStyle(
                            "-fx-background-color: #FEF9C3;" +
                            "-fx-text-fill: #854D0E;"); break;
                    case "Assigned":
                        setStyle(
                            "-fx-background-color: #DBEAFE;" +
                            "-fx-text-fill: #1E40AF;"); break;
                    case "In Progress":
                        setStyle(
                            "-fx-background-color: #EDE9FE;" +
                            "-fx-text-fill: #5B21B6;"); break;
                    case "Completed":
                        setStyle(
                            "-fx-background-color: #DCFCE7;" +
                            "-fx-text-fill: #166534;" +
                            "-fx-font-weight: bold;"); break;
                    default: setStyle("");
                }
            }
        });

        TableColumn<ServiceRequest, String> staffCol =
            new TableColumn<>("Assigned Staff");
        staffCol.setCellValueFactory(data -> {
            Staff s = data.getValue().getAssignedStaff();
            return new SimpleStringProperty(
                s != null ? s.getFullName() : "Unassigned");
        });
        staffCol.setPrefWidth(140);

        table.getColumns().addAll(
            idCol, custCol, typeCol,
            catCol, priCol, statusCol, staffCol);

        table.setItems(FXCollections.observableArrayList(
            service.getAllRequests()));

        filterBox.setOnAction(e -> {
            String selected = filterBox.getValue();
            List<ServiceRequest> all = service.getAllRequests();
            if (selected.equals("All")) {
                table.setItems(
                    FXCollections.observableArrayList(all));
            } else {
                List<ServiceRequest> filtered = all.stream()
                    .filter(r -> r.getStatus().equals(selected))
                    .collect(Collectors.toList());
                table.setItems(
                    FXCollections.observableArrayList(filtered));
            }
        });

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setFont(Font.font("Arial", 13));
        refreshBtn.setOnAction(e -> {
            filterBox.setValue("All");
            table.setItems(FXCollections.observableArrayList(
                service.getAllRequests()));
        });

        filterRow.getChildren().addAll(
            filterLabel, filterBox, refreshBtn);
        VBox.setVgrow(table, Priority.ALWAYS);
        box.getChildren().addAll(heading, filterRow, table);
        return box;
    }

    // Assign Staff Tab
    private VBox buildAssignTab() {
        VBox form = new VBox(14);
        form.setPadding(new Insets(30));

        Label heading = new Label("Assign Staff to a Request");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        heading.setTextFill(Color.web("#1E3A5F"));

        TextField requestIdField = new TextField();
        requestIdField.setPromptText("Enter Request ID  e.g. 1");
        requestIdField.setFont(Font.font("Arial", 13));

        ComboBox<String> staffBox = new ComboBox<>();
        for (Staff s : service.getAllStaff()) {
            staffBox.getItems().add(
                s.getUserID() + " - " + s.getFullName()
                + " (" + s.getSpecialization() + ")");
        }
        staffBox.setPromptText(
            "Select Staff Member by Specialization");
        staffBox.setMaxWidth(Double.MAX_VALUE);
        staffBox.setStyle("-fx-font-size: 13px;");

        Label errorLabel   = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setFont(Font.font("Arial", 12));
        errorLabel.setWrapText(true);

        Label successLabel = new Label();
        successLabel.setTextFill(Color.GREEN);
        successLabel.setFont(Font.font("Arial", 12));
        successLabel.setWrapText(true);

        Button assignBtn = new Button("Assign Staff");
        assignBtn.setMaxWidth(Double.MAX_VALUE);
        assignBtn.setPrefHeight(44);
        assignBtn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        assignBtn.setStyle(
            "-fx-background-color: #2563EB;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 6;");

        assignBtn.setOnAction(e -> {
            errorLabel.setText("");
            successLabel.setText("");

            String idText = requestIdField.getText().trim();
            if (idText.isEmpty()) {
                errorLabel.setText("Please enter a Request ID.");
                return;
            }
            if (staffBox.getValue() == null) {
                errorLabel.setText(
                    "Please select a staff member.");
                return;
            }

            int reqId;
            try {
                reqId = Integer.parseInt(idText);
            } catch (NumberFormatException ex) {
                errorLabel.setText(
                    "Request ID must be a number.");
                return;
            }

            ServiceRequest targetRequest = null;
            for (ServiceRequest r : service.getAllRequests()) {
                if (r.getRequestID() == reqId) {
                    targetRequest = r; break;
                }
            }

            if (targetRequest == null) {
                errorLabel.setText(
                    "Request #" + reqId + " not found.");
                return;
            }

            int staffId = Integer.parseInt(
                staffBox.getValue().split(" - ")[0]);
            Staff selectedStaff = null;
            for (Staff s : service.getAllStaff()) {
                if (s.getUserID() == staffId) {
                    selectedStaff = s; break;
                }
            }

            if (selectedStaff == null) {
                errorLabel.setText("Staff member not found.");
                return;
            }

            service.assignStaff(admin, selectedStaff,
                targetRequest);
            successLabel.setText(
                "Successfully assigned "
                + selectedStaff.getFullName()
                + " to Request #" + reqId);
            requestIdField.clear();
            staffBox.setValue(null);
        });

        form.getChildren().addAll(
            heading, new Separator(),
            new Label("Request ID:"),   requestIdField,
            new Label("Staff Member:"), staffBox,
            errorLabel, successLabel,   assignBtn
        );
        return form;
    }

    // Create Staff Account Tab
    private VBox buildCreateStaffTab() {
        VBox form = new VBox(14);
        form.setPadding(new Insets(30));

        Label heading = new Label("Create a New Staff Account");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        heading.setTextFill(Color.web("#1E3A5F"));

        Label infoBox = new Label(
            "Only the Admin can create Staff accounts.\n" +
            "Staff members will use these credentials to login.");
        infoBox.setWrapText(true);
        infoBox.setFont(Font.font("Arial", 13));
        infoBox.setTextFill(Color.web("#065F46"));
        infoBox.setStyle(
            "-fx-background-color: #D1FAE5;" +
            "-fx-padding: 12;" +
            "-fx-background-radius: 6;");

        Label roleDisplay = new Label("Account Type:  Staff");
        roleDisplay.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        roleDisplay.setStyle(
            "-fx-background-color: #D1FAE5;" +
            "-fx-text-fill: #065F46;" +
            "-fx-padding: 6 14 6 14;" +
            "-fx-background-radius: 5;");

        TextField nameField = new TextField();
        nameField.setPromptText("Enter staff full name");
        nameField.setFont(Font.font("Arial", 13));

        TextField userField = new TextField();
        userField.setPromptText("Choose a username");
        userField.setFont(Font.font("Arial", 13));

        PasswordField passField = new PasswordField();
        passField.setPromptText("Set a password (min 6 chars)");
        passField.setFont(Font.font("Arial", 13));

        Label specLabel = new Label("Specialization:");
        specLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        ComboBox<String> specBox = new ComboBox<>();
        specBox.getItems().addAll(
            "Hardware", "Software", "Network", "Maintenance");
        specBox.setPromptText("Select Specialization");
        specBox.setMaxWidth(Double.MAX_VALUE);
        specBox.setStyle("-fx-font-size: 13px;");

        Label errorLabel   = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setFont(Font.font("Arial", 12));
        errorLabel.setWrapText(true);

        Label successLabel = new Label();
        successLabel.setTextFill(Color.GREEN);
        successLabel.setFont(Font.font("Arial", 12));
        successLabel.setWrapText(true);

        Button createBtn = new Button("Create Staff Account");
        createBtn.setMaxWidth(Double.MAX_VALUE);
        createBtn.setPrefHeight(44);
        createBtn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        createBtn.setStyle(
            "-fx-background-color: #059669;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 6;");

        createBtn.setOnAction(e -> {
            errorLabel.setText("");
            successLabel.setText("");

            String name  = nameField.getText().trim();
            String uname = userField.getText().trim();
            String pass  = passField.getText().trim();
            String spec  = specBox.getValue();

            if (name.isEmpty()) {
                errorLabel.setText(
                    "Please enter the staff member's full name.");
                return;
            }
            if (uname.isEmpty()) {
                errorLabel.setText("Please choose a username.");
                return;
            }
            if (pass.isEmpty()) {
                errorLabel.setText("Please set a password.");
                return;
            }
            if (pass.length() < 6) {
                errorLabel.setText(
                    "Password must be at least 6 characters.");
                return;
            }
            if (spec == null) {
                errorLabel.setText(
                    "Please select a specialization.");
                return;
            }

            if (service.usernameExists(uname)) {
                errorLabel.setText(
                    "Username '" + uname + "' is already taken.");
                return;
            }

            System.out.println("Creating staff: "
                + name + " | " + uname + " | " + spec);

            Staff newStaff = service.registerStaff(
                uname, pass, name, spec);

            if (newStaff == null) {
                errorLabel.setText(
                    "Failed to create account. Please try again.");
            } else {
                successLabel.setText(
                    "Staff account created!\n" +
                    "Name: "           + name  + "\n" +
                    "Username: "       + uname + "\n" +
                    "Specialization: " + spec);
                nameField.clear();
                userField.clear();
                passField.clear();
                specBox.setValue(null);
            }
        });

        form.getChildren().addAll(
            heading, infoBox, roleDisplay, new Separator(),
            new Label("Full Name:"), nameField,
            new Label("Username:"), userField,
            new Label("Password:"), passField,
            specLabel, specBox,
            errorLabel, successLabel, createBtn
        );
        return form;
    }

    // View All Staff Tab
    @SuppressWarnings("unchecked")
    private VBox buildViewStaffTab() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(25));

        Label heading = new Label("All Staff Members");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        heading.setTextFill(Color.web("#1E3A5F"));

        TableView<Staff> table = new TableView<>();
        table.setStyle("-fx-font-size: 13px;");
        table.setPlaceholder(
            new Label("No staff members found."));

        TableColumn<Staff, Integer> idCol =
            new TableColumn<>("ID");
        idCol.setCellValueFactory(
            new PropertyValueFactory<>("userID"));
        idCol.setPrefWidth(60);

        TableColumn<Staff, String> nameCol =
            new TableColumn<>("Full Name");
        nameCol.setCellValueFactory(
            new PropertyValueFactory<>("fullName"));
        nameCol.setPrefWidth(200);

        TableColumn<Staff, String> userCol =
            new TableColumn<>("Username");
        userCol.setCellValueFactory(
            new PropertyValueFactory<>("username"));
        userCol.setPrefWidth(150);

        TableColumn<Staff, String> specCol =
            new TableColumn<>("Specialization");
        specCol.setCellValueFactory(
            new PropertyValueFactory<>("specialization"));
        specCol.setPrefWidth(150);

        // Availability with colour coding
        TableColumn<Staff, String> availCol =
            new TableColumn<>("Availability");
        availCol.setCellValueFactory(data ->
            new SimpleStringProperty(
                data.getValue().isAvailabilityStatus()
                ? "Available" : "Occupied"));
        availCol.setPrefWidth(120);
        availCol.setCellFactory(
                col -> new TableCell<Staff, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null); setStyle(""); return;
                }
                setText(item);
                setStyle(item.equals("Available")
                    ? "-fx-background-color: #DCFCE7;" +
                      "-fx-text-fill: #166534;" +
                      "-fx-font-weight: bold;"
                    : "-fx-background-color: #FEE2E2;" +
                      "-fx-text-fill: #DC2626;");
            }
        });

        table.getColumns().addAll(
            idCol, nameCol, userCol, specCol, availCol);

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setFont(Font.font("Arial", 13));
        refreshBtn.setOnAction(e ->
            table.setItems(FXCollections.observableArrayList(
                service.getAllStaff())));

        table.setItems(FXCollections.observableArrayList(
            service.getAllStaff()));

        VBox.setVgrow(table, Priority.ALWAYS);
        box.getChildren().addAll(heading, refreshBtn, table);
        return box;
    }

    public Scene getScene() { return scene; }
}