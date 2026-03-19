package ui;

import model.*;
import service.RequestService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

// LoginScreen is the first screen shown when the app starts
// It has two tabs - Login and Register Customer
// Staff accounts cannot be created here
// Only Admin can create Staff accounts from Admin Dashboard
public class LoginScreen {

    private Scene scene;
    private RequestService service;
    private MainApp mainApp;

    public LoginScreen(RequestService service, MainApp mainApp) {
        this.service = service;
        this.mainApp = mainApp;
        buildUI();
    }

    // Builds the full login screen layout
    // Uses a simple VBox centred on screen
    private void buildUI() {

        // Root is a VBox centred on the screen
        // It fills the whole window
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #f0f4ff;");

        // App title
        Label title = new Label(
            "Service Request Tracking System");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#1E3A5F"));

        // Subtitle instruction
        Label subtitle = new Label(
            "Please select your role and login or register");
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setTextFill(Color.GRAY);

        // TabPane holds Login and Register Customer tabs
        // setMaxWidth limits how wide it grows on large screens
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(
            TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setMaxWidth(480);
        tabPane.setMinWidth(380);
        tabPane.setMinHeight(420);
        tabPane.getTabs().addAll(
            new Tab("Login",             buildLoginForm()),
            new Tab("Register Customer", buildRegisterForm())
        );

        // Hint at the bottom
        Label hint = new Label(
            "Staff accounts are created by the Admin " +
            "inside the Admin Dashboard.");
        hint.setFont(Font.font("Arial", 11));
        hint.setTextFill(Color.GRAY);
        hint.setWrapText(true);
        hint.setMaxWidth(480);

        root.getChildren().addAll(
            title, subtitle, tabPane, hint);

        // Scene fills the whole window
        // No fixed size - fills whatever size stage is
        scene = new Scene(root);
    }

    // Login Form

    // Creates the login form inside a ScrollPane
    // Has role dropdown, username, password with show/hide
    // and a login button that validates all fields
    private ScrollPane buildLoginForm() {
        VBox form = new VBox(12);
        form.setPadding(new Insets(20));

        // Role dropdown - user must select role before logging in
        // Colour changes based on selected role
        Label roleLabel = new Label("Select Your Role:");
        roleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("Customer", "Staff", "Admin");
        roleBox.setPromptText("-- Select Role --");
        roleBox.setMaxWidth(Double.MAX_VALUE);

        // Blue for Customer, Green for Staff, Yellow for Admin
        roleBox.setOnAction(e -> {
            if (roleBox.getValue() == null) return;
            switch (roleBox.getValue()) {
                case "Customer":
                    roleBox.setStyle(
                        "-fx-background-color: #DBEAFE;" +
                        "-fx-border-color: #2563EB;" +
                        "-fx-font-size: 13px;" +
                        "-fx-background-radius: 5;" +
                        "-fx-border-radius: 5;");
                    break;
                case "Staff":
                    roleBox.setStyle(
                        "-fx-background-color: #D1FAE5;" +
                        "-fx-border-color: #059669;" +
                        "-fx-font-size: 13px;" +
                        "-fx-background-radius: 5;" +
                        "-fx-border-radius: 5;");
                    break;
                case "Admin":
                    roleBox.setStyle(
                        "-fx-background-color: #FEF3C7;" +
                        "-fx-border-color: #D97706;" +
                        "-fx-font-size: 13px;" +
                        "-fx-background-radius: 5;" +
                        "-fx-border-radius: 5;");
                    break;
            }
        });

        // Username input field
        Label userLabel = new Label("Username:");
        userLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        usernameField.setFont(Font.font("Arial", 13));

        // Password section with show/hide toggle
        // Two fields used - only one visible at a time
        Label passLabel = new Label("Password:");
        passLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        // Default field shows dots
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setFont(Font.font("Arial", 13));

        // Plain text field shown when checkbox is ticked
        // Hidden by default - takes no space when hidden
        TextField passwordVisible = new TextField();
        passwordVisible.setPromptText("Enter your password");
        passwordVisible.setFont(Font.font("Arial", 13));
        passwordVisible.setVisible(false);
        passwordVisible.setManaged(false);

        // Keep both fields in sync so text is never lost
        passwordVisible.textProperty().bindBidirectional(
            passwordField.textProperty());

        // Checkbox toggles between the two password fields
        CheckBox showPassCheck = new CheckBox("Show Password");
        showPassCheck.setFont(Font.font("Arial", 12));
        showPassCheck.setTextFill(Color.GRAY);

        showPassCheck.setOnAction(e -> {
            if (showPassCheck.isSelected()) {
                passwordField.setVisible(false);
                passwordField.setManaged(false);
                passwordVisible.setVisible(true);
                passwordVisible.setManaged(true);
            } else {
                passwordVisible.setVisible(false);
                passwordVisible.setManaged(false);
                passwordField.setVisible(true);
                passwordField.setManaged(true);
            }
        });

        // Error label shown in red when something goes wrong
        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setFont(Font.font("Arial", 12));
        errorLabel.setWrapText(true);

        // Login button - spans full width
        Button loginBtn = new Button("Login");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setPrefHeight(42);
        loginBtn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        loginBtn.setStyle(
            "-fx-background-color: #2563EB;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 5;");

        // Login button performs three checks:
        // 1. All fields must be filled
        // 2. Credentials must match the database
        // 3. Selected role must match actual role in database
        loginBtn.setOnAction(e -> {
            String role     = roleBox.getValue();
            String username = usernameField.getText().trim();
            String password = showPassCheck.isSelected()
                    ? passwordVisible.getText().trim()
                    : passwordField.getText().trim();

            // Check all fields are filled
            if (role == null) {
                errorLabel.setText(
                    "Please select your role first.");
                return;
            }
            if (username.isEmpty()) {
                errorLabel.setText("Please enter your username.");
                return;
            }
            if (password.isEmpty()) {
                errorLabel.setText("Please enter your password.");
                return;
            }

            // Check credentials against the database
            User user = service.login(username, password);
            if (user == null) {
                errorLabel.setText(
                    "Invalid username or password. " +
                    "Please try again.");
                return;
            }

            // Check selected role matches actual role in database
            // Prevents customer logging in as admin etc
            if (!user.getRole().equals(role)) {
                errorLabel.setText(
                    "Wrong role selected. " +
                    "Your account role is: " + user.getRole());
                return;
            }

            // All checks passed - go to correct dashboard
            mainApp.showDashboard(user);
        });

        // Role colour legend at the bottom
        Label legendTitle = new Label("Role Colour Guide:");
        legendTitle.setFont(
            Font.font("Arial", FontWeight.BOLD, 11));
        legendTitle.setTextFill(Color.GRAY);

        HBox legend = new HBox(10);

        Label custLegend = new Label("  Customer  ");
        custLegend.setStyle(
            "-fx-background-color: #DBEAFE;" +
            "-fx-text-fill: #1E40AF;" +
            "-fx-padding: 2 8 2 8;" +
            "-fx-background-radius: 4;" +
            "-fx-font-size: 11px;");

        Label staffLegend = new Label("  Staff  ");
        staffLegend.setStyle(
            "-fx-background-color: #D1FAE5;" +
            "-fx-text-fill: #065F46;" +
            "-fx-padding: 2 8 2 8;" +
            "-fx-background-radius: 4;" +
            "-fx-font-size: 11px;");

        Label adminLegend = new Label("  Admin  ");
        adminLegend.setStyle(
            "-fx-background-color: #FEF3C7;" +
            "-fx-text-fill: #92400E;" +
            "-fx-padding: 2 8 2 8;" +
            "-fx-background-radius: 4;" +
            "-fx-font-size: 11px;");

        legend.getChildren().addAll(
            custLegend, staffLegend, adminLegend);

        form.getChildren().addAll(
            roleLabel,    roleBox,
            userLabel,    usernameField,
            passLabel,    passwordField,
            passwordVisible,
            showPassCheck,
            errorLabel,
            loginBtn,
            new Separator(),
            legendTitle,  legend
        );

        // Wrap in ScrollPane so nothing is cut off
        ScrollPane scroll = new ScrollPane(form);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");
        return scroll;
    }

    // Register Customer Form

    // Creates the customer registration form
    // Only customers can register themselves here
    // Staff accounts are created by the Admin
    private ScrollPane buildRegisterForm() {
        VBox form = new VBox(12);
        form.setPadding(new Insets(20));

        // Heading at top of register tab
        Label heading = new Label("Create a Customer Account");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        heading.setTextFill(Color.web("#1E3A5F"));

        // Yellow info box - explains this is for customers only
        Label infoBox = new Label(
            "This form is for Customer registration only.\n" +
            "If you are a Staff member your account will be\n" +
            "created by the Admin inside the Admin Dashboard.");
        infoBox.setWrapText(true);
        infoBox.setFont(Font.font("Arial", 12));
        infoBox.setTextFill(Color.web("#92400E"));
        infoBox.setStyle(
            "-fx-background-color: #FEF3C7;" +
            "-fx-padding: 10;" +
            "-fx-background-radius: 6;");

        // Account type locked to Customer - display only
        Label roleDisplay = new Label("Account Type:  Customer");
        roleDisplay.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        roleDisplay.setStyle(
            "-fx-background-color: #DBEAFE;" +
            "-fx-text-fill: #1E40AF;" +
            "-fx-padding: 5 12 5 12;" +
            "-fx-background-radius: 5;");

        // Registration input fields
        TextField nameField = new TextField();
        nameField.setPromptText("Enter your full name");
        nameField.setFont(Font.font("Arial", 13));

        // Username must be unique - checked against database
        TextField userField = new TextField();
        userField.setPromptText("Choose a username");
        userField.setFont(Font.font("Arial", 13));

        // Password with show/hide toggle
        Label passLabel = new Label("Password:");
        passLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        // Default password field shows dots
        PasswordField passField = new PasswordField();
        passField.setPromptText(
            "Choose a password (min 6 characters)");
        passField.setFont(Font.font("Arial", 13));

        // Plain text shown when checkbox is ticked
        TextField passVisible = new TextField();
        passVisible.setPromptText(
            "Choose a password (min 6 characters)");
        passVisible.setFont(Font.font("Arial", 13));
        passVisible.setVisible(false);
        passVisible.setManaged(false);

        // Keep both password fields in sync
        passVisible.textProperty().bindBidirectional(
            passField.textProperty());

        CheckBox showRegPassCheck = new CheckBox("Show Password");
        showRegPassCheck.setFont(Font.font("Arial", 12));
        showRegPassCheck.setTextFill(Color.GRAY);

        showRegPassCheck.setOnAction(e -> {
            if (showRegPassCheck.isSelected()) {
                passField.setVisible(false);
                passField.setManaged(false);
                passVisible.setVisible(true);
                passVisible.setManaged(true);
            } else {
                passVisible.setVisible(false);
                passVisible.setManaged(false);
                passField.setVisible(true);
                passField.setManaged(true);
            }
        });

        // Contact number input
        TextField contactField = new TextField();
        contactField.setPromptText("Enter your contact number");
        contactField.setFont(Font.font("Arial", 13));

        // Address input
        TextField addressField = new TextField();
        addressField.setPromptText("Enter your address");
        addressField.setFont(Font.font("Arial", 13));

        // Error label shown in red
        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setFont(Font.font("Arial", 12));
        errorLabel.setWrapText(true);

        // Register button spans full width
        Button registerBtn = new Button(
            "Create Customer Account");
        registerBtn.setMaxWidth(Double.MAX_VALUE);
        registerBtn.setPrefHeight(42);
        registerBtn.setFont(
            Font.font("Arial", FontWeight.BOLD, 14));
        registerBtn.setStyle(
            "-fx-background-color: #2563EB;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 5;");

        // Validates all fields then saves to MySQL
        // Returns null if username is already taken
        registerBtn.setOnAction(e -> {
            String name    = nameField.getText().trim();
            String uname   = userField.getText().trim();
            String contact = contactField.getText().trim();
            String address = addressField.getText().trim();
            String pass    = showRegPassCheck.isSelected()
                    ? passVisible.getText().trim()
                    : passField.getText().trim();

            // Validate all required fields
            if (name.isEmpty()) {
                errorLabel.setText(
                    "Please enter your full name.");
                return;
            }
            if (uname.isEmpty()) {
                errorLabel.setText("Please choose a username.");
                return;
            }
            if (pass.isEmpty()) {
                errorLabel.setText("Please choose a password.");
                return;
            }
            if (pass.length() < 6) {
                errorLabel.setText(
                    "Password must be at least 6 characters.");
                return;
            }
            if (contact.isEmpty()) {
                errorLabel.setText(
                    "Please enter your contact number.");
                return;
            }

            // Try to register in MySQL
            // Returns null if username is already taken
            Customer newCustomer = service.registerCustomer(
                    uname, pass, name, contact, address);

            if (newCustomer == null) {
                errorLabel.setText(
                    "Username already taken. " +
                    "Please choose a different username.");
            } else {
                // Registration worked - show popup and go to dashboard
                Alert alert = new Alert(
                    Alert.AlertType.INFORMATION);
                alert.setTitle("Registration Successful");
                alert.setHeaderText("Welcome " + name + "!");
                alert.setContentText(
                    "Your account has been created.\n" +
                    "You can now login with your credentials.");
                alert.showAndWait();
                mainApp.showDashboard(newCustomer);
            }
        });

        form.getChildren().addAll(
            heading,
            infoBox,
            roleDisplay,
            new Separator(),
            new Label("Full Name:"),      nameField,
            new Label("Username:"),       userField,
            passLabel,
            passField,
            passVisible,
            showRegPassCheck,
            new Label("Contact Number:"), contactField,
            new Label("Address:"),        addressField,
            errorLabel,
            registerBtn
        );

        // Wrap in ScrollPane so all fields accessible
        ScrollPane scroll = new ScrollPane(form);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");
        return scroll;
    }

    public Scene getScene() { return scene; }
}