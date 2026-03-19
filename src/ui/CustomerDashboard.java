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

public class CustomerDashboard {

    private Scene scene;
    private Customer customer;
    private RequestService service;
    private MainApp mainApp;

    public CustomerDashboard(Customer customer,
                             RequestService service,
                             MainApp mainApp) {
        this.customer = customer;
        this.service  = service;
        this.mainApp  = mainApp;
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
            "SRTS  —  Customer: " + customer.getFullName());
        topLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        topLabel.setTextFill(Color.WHITE);
        Button logoutBtn = new Button("Logout");
        logoutBtn.setFont(Font.font("Arial", 13));
        logoutBtn.setOnAction(e -> mainApp.showLoginScreen());
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topBar.getChildren().addAll(topLabel, spacer, logoutBtn);
        root.setTop(topBar);

        // Tabs fill the whole remaining space
        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(
            TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.setStyle("-fx-font-size: 13px;");
        tabs.getTabs().addAll(
            new Tab("Submit Request", buildSubmitForm()),
            new Tab("My Requests",    buildMyRequestsTable()),
            new Tab("Help",           buildHelpTab())
        );
        root.setCenter(tabs);

        // Scene fills the whole window
        scene = new Scene(root);
    }

    // Submit Form
    private VBox buildSubmitForm() {
        VBox form = new VBox(14);
        form.setPadding(new Insets(30));

        Label heading = new Label("Submit a New Service Request");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        heading.setTextFill(Color.web("#1E3A5F"));

        // Service type from database
        Label typeLabel = new Label("Service Type:");
        typeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll(service.getServiceTypes());
        typeBox.setPromptText("Select Service Type");
        typeBox.setMaxWidth(Double.MAX_VALUE);
        typeBox.setStyle("-fx-font-size: 13px;");

        // Category from database
        Label catLabel = new Label("Category:");
        catLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        ComboBox<String> categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll(service.getCategories());
        categoryBox.setPromptText("Select Category");
        categoryBox.setMaxWidth(Double.MAX_VALUE);
        categoryBox.setStyle("-fx-font-size: 13px;");

        // Priority from database with colour coding
        Label priLabel = new Label("Priority:");
        priLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        ComboBox<String> priorityBox = new ComboBox<>();
        priorityBox.getItems().addAll(service.getPriorities());
        priorityBox.setPromptText("Select Priority (Rush = Urgent)");
        priorityBox.setMaxWidth(Double.MAX_VALUE);
        priorityBox.setStyle("-fx-font-size: 13px;");

        priorityBox.setOnAction(e -> {
            if ("Rush".equals(priorityBox.getValue())) {
                priorityBox.setStyle(
                    "-fx-background-color: #FEE2E2;" +
                    "-fx-border-color: #DC2626;" +
                    "-fx-font-size: 13px;");
            } else if ("Standard".equals(priorityBox.getValue())) {
                priorityBox.setStyle(
                    "-fx-background-color: #DBEAFE;" +
                    "-fx-border-color: #2563EB;" +
                    "-fx-font-size: 13px;");
            }
        });

        // Description with character counter
        Label descLabel = new Label("Problem Description:");
        descLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        TextArea descArea = new TextArea();
        descArea.setPromptText(
            "Describe your issue in detail " +
            "(minimum 10 characters)...");
        descArea.setPrefRowCount(5);
        descArea.setWrapText(true);
        descArea.setFont(Font.font("Arial", 13));
        descArea.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-border-color: #CBD5E1;" +
            "-fx-border-radius: 5;");

        Label charCount = new Label("0 characters");
        charCount.setFont(Font.font("Arial", 12));
        charCount.setTextFill(Color.RED);

        descArea.textProperty().addListener(
                (obs, oldVal, newVal) -> {
            int count = newVal.trim().length();
            if (count < 10) {
                charCount.setText(count + " characters");
                charCount.setTextFill(Color.RED);
            } else {
                charCount.setText(count + " characters  ✓");
                charCount.setTextFill(Color.GREEN);
            }
        });

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setFont(Font.font("Arial", 12));
        errorLabel.setWrapText(true);

        Button submitBtn = new Button("Submit Request");
        submitBtn.setMaxWidth(Double.MAX_VALUE);
        submitBtn.setPrefHeight(44);
        submitBtn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        submitBtn.setStyle(
            "-fx-background-color: #2563EB;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 6;");

        submitBtn.setOnAction(e -> {
            if (typeBox.getValue() == null) {
                errorLabel.setText(
                    "Please select a service type.");
                return;
            }
            if (categoryBox.getValue() == null) {
                errorLabel.setText("Please select a category.");
                return;
            }
            if (priorityBox.getValue() == null) {
                errorLabel.setText(
                    "Please select a priority level.");
                return;
            }
            if (!service.validateDescription(descArea.getText())) {
                errorLabel.setText(
                    "Description must be at least 10 characters.");
                return;
            }

            ServiceRequest req = service.submitRequest(
                customer,
                categoryBox.getValue(),
                typeBox.getValue(),
                priorityBox.getValue(),
                descArea.getText().trim()
            );

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Request Submitted");
            alert.setHeaderText("Your request has been submitted!");
            alert.setContentText(
                "Request ID:   #" + req.getRequestID() + "\n" +
                "Service Type: "  + req.getServiceType()  + "\n" +
                "Category:     "  + req.getCategory()     + "\n" +
                "Priority:     "  + req.getPriority()     + "\n" +
                "Status:       "  + req.getStatus()       + "\n\n" +
                "Go to My Requests tab to track your request.");
            alert.showAndWait();

            typeBox.setValue(null);
            categoryBox.setValue(null);
            priorityBox.setValue(null);
            priorityBox.setStyle("-fx-font-size: 13px;");
            descArea.clear();
            errorLabel.setText("");
        });

        form.getChildren().addAll(
            heading,    new Separator(),
            typeLabel,  typeBox,
            catLabel,   categoryBox,
            priLabel,   priorityBox,
            descLabel,  descArea,
            charCount,  errorLabel,
            submitBtn
        );
        return form;
    }

    // My Requests Table
    @SuppressWarnings("unchecked")
    private VBox buildMyRequestsTable() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(25));

        Label heading = new Label("My Request History");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        heading.setTextFill(Color.web("#1E3A5F"));

        Label sub = new Label(
            "All your submitted requests are shown below. " +
            "Hover over the Description column to read full text.");
        sub.setFont(Font.font("Arial", 13));
        sub.setTextFill(Color.GRAY);
        sub.setWrapText(true);

        TableView<ServiceRequest> table = new TableView<>();
        table.setStyle("-fx-font-size: 13px;");
        table.setPlaceholder(
            new Label("No requests submitted yet."));

        // Taller rows so wrapped text fits
        table.setRowFactory(tv -> {
            TableRow<ServiceRequest> row = new TableRow<>();
            row.setPrefHeight(50);
            return row;
        });

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

        // Priority with colour coding
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

        // Status with colour coding
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

        TableColumn<ServiceRequest, String> dateCol =
            new TableColumn<>("Date");
        dateCol.setCellValueFactory(
            new PropertyValueFactory<>("submissionDate"));
        dateCol.setPrefWidth(110);

        // Description column with wrapped text and tooltip
        TableColumn<ServiceRequest, String> descCol =
            new TableColumn<>("Description");
        descCol.setCellValueFactory(
            new PropertyValueFactory<>("description"));
        descCol.setPrefWidth(300);
        descCol.setCellFactory(
                col -> new TableCell<ServiceRequest, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setTooltip(null);
                    setGraphic(null);
                } else {
                    Label label = new Label(item);
                    label.setWrapText(true);
                    label.setMaxWidth(290);
                    label.setFont(Font.font("Arial", 12));
                    Tooltip tooltip = new Tooltip(item);
                    tooltip.setWrapText(true);
                    tooltip.setMaxWidth(400);
                    setTooltip(tooltip);
                    setText(null);
                    setGraphic(label);
                }
            }
        });

        table.getColumns().addAll(
            idCol, typeCol, catCol,
            priCol, statusCol, dateCol, descCol);

        // Make table grow to fill available space
        VBox.setVgrow(table, Priority.ALWAYS);

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setFont(Font.font("Arial", 13));
        refreshBtn.setOnAction(e -> {
            List<ServiceRequest> list =
                service.getCustomerRequests(customer);
            table.setItems(
                FXCollections.observableArrayList(list));
        });

        table.setItems(FXCollections.observableArrayList(
            service.getCustomerRequests(customer)));

        box.getChildren().addAll(heading, sub, refreshBtn, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        return box;
    }

    // Help Tab
    private VBox buildHelpTab() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(30));

        Label heading = new Label("Help & Guide");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        heading.setTextFill(Color.web("#1E3A5F"));

        Label step1 = new Label(
            "How to Submit a Service Request:");
        step1.setFont(Font.font("Arial", FontWeight.BOLD, 15));

        Label steps = new Label(
            "1.  Click the Submit Request tab above.\n\n" +
            "2.  Select your Service Type:\n" +
            "       IT Support — software, network or hardware.\n" +
            "       Laptop Servicing — physical laptop repairs.\n\n" +
            "3.  Select a Category:\n" +
            "       Hardware — screen, keyboard, components.\n" +
            "       Software — programs, OS issues.\n" +
            "       Maintenance — general servicing.\n\n" +
            "4.  Select a Priority:\n" +
            "       Rush — urgent (shown in RED).\n" +
            "       Standard — normal priority.\n\n" +
            "5.  Write a description of your problem.\n" +
            "       Minimum 10 characters required.\n" +
            "       Counter turns GREEN when enough.\n\n" +
            "6.  Click Submit Request.\n\n" +
            "7.  Go to My Requests tab to track status.\n\n" +
            "Status Colour Guide:\n" +
            "       Yellow = Pending\n" +
            "       Blue   = Assigned\n" +
            "       Purple = In Progress\n" +
            "       Green  = Completed"
        );
        steps.setWrapText(true);
        steps.setFont(Font.font("Arial", 14));

        box.getChildren().addAll(
            heading, new Separator(), step1, steps);
        return box;
    }

    public Scene getScene() { return scene; }
}