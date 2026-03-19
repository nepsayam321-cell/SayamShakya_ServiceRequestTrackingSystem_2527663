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

public class StaffDashboard {

    private Scene scene;
    private Staff staff;
    private RequestService service;
    private MainApp mainApp;

    public StaffDashboard(Staff staff,
                          RequestService service,
                          MainApp mainApp) {
        this.staff   = staff;
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
            "SRTS  —  Staff: " + staff.getFullName()
            + "  |  Specialization: " + staff.getSpecialization());
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
            new Tab("Assigned Requests", buildAssignedTab()),
            new Tab("Update Request",    buildUpdateTab())
        );
        root.setCenter(tabs);

        scene = new Scene(root);
    }

    // ── Assigned Requests Tab ─────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    private VBox buildAssignedTab() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(25));

        Label heading = new Label("Your Assigned Requests");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        heading.setTextFill(Color.web("#1E3A5F"));

        TableView<ServiceRequest> table = new TableView<>();
        table.setStyle("-fx-font-size: 13px;");
        table.setPlaceholder(
            new Label("No requests assigned to you yet."));

        TableColumn<ServiceRequest, Integer> idCol =
            new TableColumn<>("ID");
        idCol.setCellValueFactory(
            new PropertyValueFactory<>("requestID"));
        idCol.setPrefWidth(60);

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

        TableColumn<ServiceRequest, String> descCol =
            new TableColumn<>("Description");
        descCol.setCellValueFactory(
            new PropertyValueFactory<>("description"));
        descCol.setPrefWidth(280);

        table.getColumns().addAll(
            idCol, typeCol, catCol, priCol, statusCol, descCol);

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setFont(Font.font("Arial", 13));
        refreshBtn.setOnAction(e ->
            table.setItems(FXCollections.observableArrayList(
                service.getStaffRequests(staff))));

        table.setItems(FXCollections.observableArrayList(
            service.getStaffRequests(staff)));

        VBox.setVgrow(table, Priority.ALWAYS);
        box.getChildren().addAll(heading, refreshBtn, table);
        return box;
    }

    // ── Update Request Tab ────────────────────────────────────────────────────
    private VBox buildUpdateTab() {
        VBox form = new VBox(14);
        form.setPadding(new Insets(30));

        Label heading = new Label("Update Request Status");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        heading.setTextFill(Color.web("#1E3A5F"));

        Label info = new Label(
            "A Completion Remark is required before marking " +
            "a request as Completed.");
        info.setFont(Font.font("Arial", 13));
        info.setTextFill(Color.web("#5B21B6"));
        info.setWrapText(true);

        TextField idField = new TextField();
        idField.setPromptText("Enter Request ID  e.g. 1");
        idField.setFont(Font.font("Arial", 13));

        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("In Progress", "Completed");
        statusBox.setPromptText("Select New Status");
        statusBox.setMaxWidth(Double.MAX_VALUE);
        statusBox.setStyle("-fx-font-size: 13px;");

        TextArea remarkArea = new TextArea();
        remarkArea.setPromptText(
            "Required when marking as Completed — " +
            "describe what was done to fix the issue.");
        remarkArea.setPrefRowCount(4);
        remarkArea.setWrapText(true);
        remarkArea.setFont(Font.font("Arial", 13));

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setFont(Font.font("Arial", 12));
        errorLabel.setWrapText(true);

        Label successLabel = new Label();
        successLabel.setTextFill(Color.GREEN);
        successLabel.setFont(Font.font("Arial", 12));
        successLabel.setWrapText(true);

        Button updateBtn = new Button("Update Request");
        updateBtn.setMaxWidth(Double.MAX_VALUE);
        updateBtn.setPrefHeight(44);
        updateBtn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        updateBtn.setStyle(
            "-fx-background-color: #2563EB;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 6;");

        updateBtn.setOnAction(e -> {
            errorLabel.setText("");
            successLabel.setText("");

            String idText = idField.getText().trim();
            if (idText.isEmpty()) {
                errorLabel.setText("Please enter a Request ID.");
                return;
            }
            if (statusBox.getValue() == null) {
                errorLabel.setText("Please select a new status.");
                return;
            }

            int id;
            try {
                id = Integer.parseInt(idText);
            } catch (NumberFormatException ex) {
                errorLabel.setText(
                    "Request ID must be a number.");
                return;
            }

            ServiceRequest target = null;
            for (ServiceRequest r :
                    service.getStaffRequests(staff)) {
                if (r.getRequestID() == id) {
                    target = r; break;
                }
            }

            if (target == null) {
                errorLabel.setText(
                    "Request #" + id +
                    " not found in your assigned list.");
                return;
            }

            if (statusBox.getValue().equals("Completed")) {
                if (remarkArea.getText().trim().isEmpty()) {
                    errorLabel.setText(
                        "A Completion Remark is required " +
                        "before marking as Completed.");
                    return;
                }
                service.completeRequest(
                    staff, target,
                    remarkArea.getText().trim());
            } else {
                service.updateStatus(
                    staff, target, statusBox.getValue());
            }

            successLabel.setText(
                "Request #" + id + " updated to: "
                + statusBox.getValue());
            idField.clear();
            statusBox.setValue(null);
            remarkArea.clear();
        });

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