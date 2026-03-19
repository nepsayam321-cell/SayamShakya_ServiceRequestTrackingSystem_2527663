package ui;

import model.*;
import service.RequestService;
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

// StaffDashboard is the main screen for Staff users
// It has two tabs:
// 1. Assigned Requests - shows all requests assigned to this staff
// 2. Update Request    - allows staff to update the status of a request
// Staff must add a completion remark before marking a request as Completed
public class StaffDashboard {

    private Scene scene;
    private Staff staff;
    private RequestService service;
    private MainApp mainApp;

    // Constructor receives the logged in staff member
    // the shared RequestService and MainApp for navigation
    public StaffDashboard(Staff staff,
                          RequestService service,
                          MainApp mainApp) {
        this.staff   = staff;
        this.service = service;
        this.mainApp = mainApp;
        buildUI();
    }

    // Builds the main layout of the staff dashboard
    // Uses BorderPane so it fills the whole window
    private void buildUI() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f0f4ff;");

        // Top navigation bar showing staff name and specialization
        // so the staff member knows they are logged in correctly
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(14, 24, 14, 24));
        topBar.setStyle("-fx-background-color: #1E3A5F;");
        Label topLabel = new Label(
            "SRTS  —  Staff: " + staff.getFullName()
            + "  |  Specialization: " + staff.getSpecialization());
        topLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        topLabel.setTextFill(Color.WHITE);

        // Logout button returns the user to the login screen
        Button logoutBtn = new Button("Logout");
        logoutBtn.setFont(Font.font("Arial", 13));
        logoutBtn.setOnAction(e -> mainApp.showLoginScreen());

        // Spacer pushes the logout button to the right side
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topBar.getChildren().addAll(topLabel, spacer, logoutBtn);
        root.setTop(topBar);

        // Two tabs for different staff functions
        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(
            TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.setStyle("-fx-font-size: 13px;");
        tabs.getTabs().addAll(
            new Tab("Assigned Requests", buildAssignedTab()),
            new Tab("Update Request",    buildUpdateTab())
        );
        root.setCenter(tabs);

        // Scene fills the whole window - no fixed size
        scene = new Scene(root);
    }

    // ── Assigned Requests Tab ─────────────────────────────────────────────────

    // This tab shows all requests that have been assigned
    // to the currently logged in staff member
    // Priority and Status columns are colour coded for easy reading
    // Rush = Red, Standard = Blue
    // Pending = Yellow, Assigned = Blue,
    // In Progress = Purple, Completed = Green
    @SuppressWarnings("unchecked")
    private VBox buildAssignedTab() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(25));

        // Heading at the top of the tab
        Label heading = new Label("Your Assigned Requests");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        heading.setTextFill(Color.web("#1E3A5F"));

        // Table shows all requests assigned to this staff member
        TableView<ServiceRequest> table = new TableView<>();
        table.setStyle("-fx-font-size: 13px;");
        table.setPlaceholder(
            new Label("No requests assigned to you yet."));

        // ID column shows the unique request ID
        TableColumn<ServiceRequest, Integer> idCol =
            new TableColumn<>("ID");
        idCol.setCellValueFactory(
            new PropertyValueFactory<>("requestID"));
        idCol.setPrefWidth(60);

        // Service type column e.g. IT Support or Laptop Servicing
        TableColumn<ServiceRequest, String> typeCol =
            new TableColumn<>("Service Type");
        typeCol.setCellValueFactory(
            new PropertyValueFactory<>("serviceType"));
        typeCol.setPrefWidth(140);

        // Category column e.g. Hardware, Software, Maintenance
        TableColumn<ServiceRequest, String> catCol =
            new TableColumn<>("Category");
        catCol.setCellValueFactory(
            new PropertyValueFactory<>("category"));
        catCol.setPrefWidth(120);

        // Priority column with colour coding
        // Rush requests are shown in red to highlight urgency
        // Standard requests are shown in blue
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
                    // Red for Rush, Blue for Standard
                    setStyle(item.equals("Rush")
                        ? "-fx-background-color: #FEE2E2;" +
                          "-fx-text-fill: #DC2626;" +
                          "-fx-font-weight: bold;"
                        : "-fx-background-color: #DBEAFE;" +
                          "-fx-text-fill: #2563EB;");
                }
            }
        });

        // Status column with colour coding
        // Each status has its own colour so staff can
        // quickly identify the stage of each request
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
                        // Yellow for Pending
                        setStyle(
                            "-fx-background-color: #FEF9C3;" +
                            "-fx-text-fill: #854D0E;"); break;
                    case "Assigned":
                        // Blue for Assigned
                        setStyle(
                            "-fx-background-color: #DBEAFE;" +
                            "-fx-text-fill: #1E40AF;"); break;
                    case "In Progress":
                        // Purple for In Progress
                        setStyle(
                            "-fx-background-color: #EDE9FE;" +
                            "-fx-text-fill: #5B21B6;"); break;
                    case "Completed":
                        // Green for Completed
                        setStyle(
                            "-fx-background-color: #DCFCE7;" +
                            "-fx-text-fill: #166534;" +
                            "-fx-font-weight: bold;"); break;
                    default: setStyle("");
                }
            }
        });

        // Description column shows the problem description
        TableColumn<ServiceRequest, String> descCol =
            new TableColumn<>("Description");
        descCol.setCellValueFactory(
            new PropertyValueFactory<>("description"));
        descCol.setPrefWidth(280);

        // Add all columns to the table
        table.getColumns().addAll(
            idCol, typeCol, catCol, priCol, statusCol, descCol);

        // Refresh button reloads the assigned requests from MySQL
        // in case new requests have been assigned since login
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setFont(Font.font("Arial", 13));
        refreshBtn.setOnAction(e ->
            table.setItems(FXCollections.observableArrayList(
                service.getStaffRequests(staff))));

        // Load the staff member's assigned requests when tab opens
        table.setItems(FXCollections.observableArrayList(
            service.getStaffRequests(staff)));

        // Table grows to fill all available space
        VBox.setVgrow(table, Priority.ALWAYS);
        box.getChildren().addAll(heading, refreshBtn, table);
        return box;
    }

    // ── Update Request Tab ────────────────────────────────────────────────────

    // This tab allows staff to update the status of a request
    // Staff enters the request ID and selects a new status
    // If marking as Completed a completion remark is required
    // This enforces the business rule from the requirements
    private VBox buildUpdateTab() {
        VBox form = new VBox(14);
        form.setPadding(new Insets(30));

        // Heading at the top of the tab
        Label heading = new Label("Update Request Status");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        heading.setTextFill(Color.web("#1E3A5F"));

        // Info message reminding staff that a remark is required
        // before they can mark a request as Completed
        Label info = new Label(
            "A Completion Remark is required before marking " +
            "a request as Completed.");
        info.setFont(Font.font("Arial", 13));
        info.setTextFill(Color.web("#5B21B6"));
        info.setWrapText(true);

        // Request ID input - staff types the ID of the request
        // they want to update
        TextField idField = new TextField();
        idField.setPromptText("Enter Request ID  e.g. 1");
        idField.setFont(Font.font("Arial", 13));

        // Status dropdown - only two options available
        // In Progress means work has started
        // Completed means the issue has been fully resolved
        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("In Progress", "Completed");
        statusBox.setPromptText("Select New Status");
        statusBox.setMaxWidth(Double.MAX_VALUE);
        statusBox.setStyle("-fx-font-size: 13px;");

        // Completion remark text area
        // This is required when marking a request as Completed
        // Staff must describe what they did to fix the issue
        TextArea remarkArea = new TextArea();
        remarkArea.setPromptText(
            "Required when marking as Completed — " +
            "describe what was done to fix the issue.");
        remarkArea.setPrefRowCount(4);
        remarkArea.setWrapText(true);
        remarkArea.setFont(Font.font("Arial", 13));

        // Error label shown in red when validation fails
        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setFont(Font.font("Arial", 12));
        errorLabel.setWrapText(true);

        // Success label shown in green when update is successful
        Label successLabel = new Label();
        successLabel.setTextFill(Color.GREEN);
        successLabel.setFont(Font.font("Arial", 12));
        successLabel.setWrapText(true);

        // Update button spans the full width of the form
        Button updateBtn = new Button("Update Request");
        updateBtn.setMaxWidth(Double.MAX_VALUE);
        updateBtn.setPrefHeight(44);
        updateBtn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        updateBtn.setStyle(
            "-fx-background-color: #2563EB;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 6;");

        // Update button action performs these steps:
        // 1. Validate all fields are filled
        // 2. Parse the request ID as a number
        // 3. Find the request in this staff member's assigned list
        // 4. If Completed check that a remark has been entered
        // 5. Update the status and save to MySQL database
        updateBtn.setOnAction(e -> {
            errorLabel.setText("");
            successLabel.setText("");

            String idText = idField.getText().trim();

            // Check request ID field is not empty
            if (idText.isEmpty()) {
                errorLabel.setText("Please enter a Request ID.");
                return;
            }

            // Check a status has been selected
            if (statusBox.getValue() == null) {
                errorLabel.setText("Please select a new status.");
                return;
            }

            // Try to parse the ID as an integer
            // Show error if the user typed letters instead of numbers
            int id;
            try {
                id = Integer.parseInt(idText);
            } catch (NumberFormatException ex) {
                errorLabel.setText(
                    "Request ID must be a number.");
                return;
            }

            // Search for the request in this staff member's
            // assigned request list
            ServiceRequest target = null;
            for (ServiceRequest r :
                    service.getStaffRequests(staff)) {
                if (r.getRequestID() == id) {
                    target = r; break;
                }
            }

            // If request not found show an error
            if (target == null) {
                errorLabel.setText(
                    "Request #" + id +
                    " not found in your assigned list.");
                return;
            }

            // If marking as Completed check remark is not empty
            // This enforces the business rule that staff must
            // describe what they did before completing a request
            if (statusBox.getValue().equals("Completed")) {
                if (remarkArea.getText().trim().isEmpty()) {
                    errorLabel.setText(
                        "A Completion Remark is required " +
                        "before marking as Completed.");
                    return;
                }
                // Complete the request with the remark
                // This saves status and remark to MySQL
                service.completeRequest(
                    staff, target,
                    remarkArea.getText().trim());
            } else {
                // Just update the status to In Progress
                // and save to MySQL
                service.updateStatus(
                    staff, target, statusBox.getValue());
            }

            // Show success message and reset all fields
            successLabel.setText(
                "Request #" + id + " updated to: "
                + statusBox.getValue());
            idField.clear();
            statusBox.setValue(null);
            remarkArea.clear();
        });

        // Add all elements to the form in order
        form.getChildren().addAll(
            heading, new Separator(), info,
            new Label("Request ID:"),        idField,
            new Label("New Status:"),        statusBox,
            new Label("Completion Remark:"), remarkArea,
            errorLabel, successLabel,        updateBtn
        );
        return form;
    }

    public Scene getScene() { return scene; }
}