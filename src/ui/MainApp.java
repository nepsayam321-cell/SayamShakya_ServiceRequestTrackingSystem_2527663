package ui;

import model.*;
import service.RequestService;
import javafx.application.Application;
import javafx.stage.Stage;

// MainApp is the starting point of the entire application
// It controls which screen is shown at any given time
// It holds one shared RequestService instance so all screens
// use the same database connection throughout the app
public class MainApp extends Application {

    // primaryStage is the main window of the application
    private Stage primaryStage;

    // Static so only one instance is ever created
    // This is important because DatabaseManager uses Singleton
    // If we created a new RequestService each time it could
    // break the Singleton and open multiple connections
    private static RequestService service = new RequestService();

    // start() is called automatically by JavaFX when the app launches
    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("Service Request Tracking System");

        // Show login screen first
        showLoginScreen();

        // Set a default window size so it does not start too small
        // or fill the whole screen - user can resize freely
        // setMinWidth and setMinHeight prevent the window from
        // becoming too small to use the UI properly
        // centerOnScreen places the window in the middle of the display
        stage.setWidth(900);
        stage.setHeight(650);
        stage.setMinWidth(600);
        stage.setMinHeight(500);
        stage.centerOnScreen();

        // show() makes the window visible on screen
        stage.show();
    }

    // Show the login screen
    // Also called when any user clicks Logout
    public void showLoginScreen() {
        LoginScreen login = new LoginScreen(service, this);
        primaryStage.setScene(login.getScene());
    }

    // Show the correct dashboard based on the user role
    public void showDashboard(User user) {
        if (user instanceof Customer) {
            CustomerDashboard dash = new CustomerDashboard(
                    (Customer) user, service, this);
            primaryStage.setScene(dash.getScene());

        } else if (user instanceof Staff) {
            StaffDashboard dash = new StaffDashboard(
                    (Staff) user, service, this);
            primaryStage.setScene(dash.getScene());

        } else if (user instanceof Admin) {
            AdminDashboard dash = new AdminDashboard(
                    (Admin) user, service, this);
            primaryStage.setScene(dash.getScene());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}