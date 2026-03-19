package service;

import database.DatabaseManager;
import model.*;
import java.time.LocalDate;
import java.util.List;

// RequestService is the business logic layer of the system
// All UI screens call this class instead of the database directly
// This keeps the UI clean and puts all the rules in one place
public class RequestService {

    // We get the single DatabaseManager instance using getInstance()
    // because DatabaseManager uses the Singleton pattern
    private DatabaseManager db = DatabaseManager.getInstance();

    // Checks the username and password against the database
    // Returns the User object if correct, null if wrong
    public User login(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) return null;
        return db.findUser(username, password);
    }

    // Registers a new customer account
    // Returns null if the username is already taken
    // MySQL AUTO_INCREMENT assigns the ID so we pass 0
    public Customer registerCustomer(String username, String password,
                                     String fullName, String contact,
                                     String address) {
        if (db.usernameExists(username)) return null;
        Customer c = new Customer(0, username, password, fullName, contact, address);
        db.addCustomer(c);
        return c;
    }

    // Creates a new staff account - only called by the Admin
    // Returns null if the username is already taken
    public Staff registerStaff(String username, String password,
                                String fullName, String specialization) {
        if (db.usernameExists(username)) return null;
        Staff s = new Staff(0, username, password, fullName, specialization, true);
        db.addStaff(s);
        return s;
    }

    // Checks if a username already exists in the database
    public boolean usernameExists(String username) {
        return db.usernameExists(username);
    }

    // Creates a new service request and saves it to the database
    // The submission date is set automatically to today's date
    public ServiceRequest submitRequest(Customer customer, String category,
                                        String serviceType, String priority,
                                        String description) {
        String today = LocalDate.now().toString();
        ServiceRequest req = new ServiceRequest(
                category, serviceType, priority,
                description, customer, today);
        db.saveRequest(req);
        customer.submitRequest(req);
        return req;
    }

    // Returns all service requests in the system
    public List<ServiceRequest> getAllRequests() {
        return db.getAllRequests();
    }

    // Returns all requests submitted by a specific customer
    public List<ServiceRequest> getCustomerRequests(Customer customer) {
        return db.getRequestsByCustomer(customer);
    }

    // Returns all requests assigned to a specific staff member
    public List<ServiceRequest> getStaffRequests(Staff staff) {
        return db.getRequestsByStaff(staff);
    }

    // Assigns a staff member to a request and saves the change to MySQL
    public void assignStaff(Admin admin, Staff staff, ServiceRequest request) {
        admin.assignStaff(staff, request);
        db.updateRequest(request);
    }

    // Updates the status of a request and saves the change to MySQL
    public void updateStatus(Staff staff, ServiceRequest request, String status) {
        staff.updateStatus(request, status);
        db.updateRequest(request);
    }

    // Marks a request as completed with a remark
    // and saves the change to MySQL
    public void completeRequest(Staff staff, ServiceRequest request, String remark) {
        staff.addCompletionRemark(request, remark);
        db.updateRequest(request);
    }

    // Returns all staff members from the database
    public List<Staff> getAllStaff() {
        return db.getAllStaff();
    }

    // Returns service categories from the database
    // for example Hardware, Software, Maintenance
    public List<String> getCategories() {
        return db.getAllCategories();
    }

    // Returns service types from the database
    // for example IT Support, Laptop Servicing
    public List<String> getServiceTypes() {
        return db.getAllServiceTypes();
    }

    // Returns priority levels from the database
    // for example Rush, Standard
    public List<String> getPriorities() {
        return db.getAllPriorities();
    }

    // Checks if the description is long enough
    // The minimum length is 10 characters
    public boolean validateDescription(String description) {
        return description != null && description.trim().length() >= 10;
    }
}