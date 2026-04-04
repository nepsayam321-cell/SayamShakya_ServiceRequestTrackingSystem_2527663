package database;

import model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// DatabaseManager handles all communication with the MySQL database
// It uses the Singleton design pattern which means only one instance
// of this class can exist at any time
public class DatabaseManager {

    private static DatabaseManager instance;
    private Connection connection;

    // Change DB_PASS to your MySQL password
    private static final String DB_URL  = "jdbc:mysql://localhost:3306/srts_db";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Sayam1995#";

    // Private constructor - Singleton pattern
    private DatabaseManager() {
        connectDB();
    }

    // Only way to get the instance
    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    // Connect to MySQL
    // setAutoCommit(true) ensures every INSERT and UPDATE is saved
    // to the database immediately without needing a manual commit
    // Without this, new users would appear to register successfully
    // but disappear when the app closes because MySQL never saved them
    public Connection connectDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                DB_URL + "?useSSL=false&allowPublicKeyRetrieval=true"
                       + "&serverTimezone=UTC",
                DB_USER, DB_PASS);
            connection.setAutoCommit(true); // FIX: save every write immediately
            System.out.println("Database connected successfully!");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    // Check connection is still alive
    // If connection dropped reconnect automatically
    private Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                System.out.println("Connection lost - reconnecting...");
                connectDB();
            }
        } catch (SQLException e) {
            System.out.println("Connection check error: " + e.getMessage());
            connectDB();
        }
        return connection;
    }

    // USER METHODS

    // Find user by username and password - used for login
    public User findUser(String username, String password) {
        String sql = "SELECT * FROM users " +
                     "WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return buildUser(rs);
            }
        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
        }
        return null;
    }

    // Check if a username already exists
    public boolean usernameExists(String username) {
        String sql = "SELECT userID FROM users WHERE username = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                boolean exists = rs.next();
                System.out.println("Username check '" + username
                    + "': " + (exists ? "EXISTS" : "available"));
                return exists;
            }
        } catch (SQLException e) {
            System.out.println("Username check error: " + e.getMessage());
        }
        return false;
    }

    // Add a new customer to the database
    // MySQL AUTO_INCREMENT generates the ID automatically
    // RETURN_GENERATED_KEYS reads the real database ID back into
    // the Customer object so Java and MySQL stay in sync
    public void addCustomer(Customer customer) {
        String sql = "INSERT INTO users " +
                     "(username, password, fullName, role, " +
                     "contactNumber, address) " +
                     "VALUES (?, ?, ?, 'Customer', ?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, customer.getUsername());
            stmt.setString(2, customer.getPassword());
            stmt.setString(3, customer.getFullName());
            stmt.setString(4, customer.getContactNumber());
            stmt.setString(5, customer.getAddress());
            int rows = stmt.executeUpdate();
            System.out.println("addCustomer rows affected: " + rows);
            if (rows > 0) {
                // Read the AUTO_INCREMENT ID that MySQL assigned
                // and store it back in the Customer object
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) customer.setUserID(keys.getInt(1));
                }
                System.out.println("New customer saved: "
                    + customer.getFullName());
            } else {
                System.out.println("WARNING: Customer not saved!");
            }
        } catch (SQLException e) {
            System.out.println("Add customer error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Add a new staff member - called by Admin only
    // MySQL AUTO_INCREMENT generates the ID automatically
    // RETURN_GENERATED_KEYS reads the real database ID back into
    // the Staff object so Java and MySQL stay in sync
    public void addStaff(Staff staff) {
        String sql = "INSERT INTO users " +
                     "(username, password, fullName, role, " +
                     "specialization, availabilityStatus) " +
                     "VALUES (?, ?, ?, 'Staff', ?, TRUE)";
        try (PreparedStatement stmt = getConnection().prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, staff.getUsername());
            stmt.setString(2, staff.getPassword());
            stmt.setString(3, staff.getFullName());
            stmt.setString(4, staff.getSpecialization());
            int rows = stmt.executeUpdate();
            System.out.println("addStaff rows affected: " + rows);
            if (rows > 0) {
                // Read the AUTO_INCREMENT ID that MySQL assigned
                // and store it back in the Staff object
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) staff.setUserID(keys.getInt(1));
                }
                System.out.println("New staff saved: "
                    + staff.getFullName());
            } else {
                System.out.println("WARNING: Staff not saved!");
            }
        } catch (SQLException e) {
            System.out.println("Add staff error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Get all customers from database
    public List<Customer> getAllCustomers() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'Customer'";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) list.add((Customer) buildUser(rs));
        } catch (SQLException e) {
            System.out.println("Get customers error: " + e.getMessage());
        }
        return list;
    }

    // Get all staff from database
    public List<Staff> getAllStaff() {
        List<Staff> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'Staff'";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) list.add((Staff) buildUser(rs));
        } catch (SQLException e) {
            System.out.println("Get staff error: " + e.getMessage());
        }
        return list;
    }

    // SERVICE TYPE METHODS

    // Get all service types from database
    public List<String> getAllServiceTypes() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT serviceTypeName FROM service_types";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(rs.getString("serviceTypeName"));
        } catch (SQLException e) {
            System.out.println("Get service types error: "
                + e.getMessage());
        }
        return list;
    }

    // Get serviceTypeID by name - used for foreign key
    private int getServiceTypeID(String serviceTypeName) {
        String sql = "SELECT serviceTypeID FROM service_types "
                   + "WHERE serviceTypeName = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, serviceTypeName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("serviceTypeID");
            }
        } catch (SQLException e) {
            System.out.println("Get service type ID error: "
                + e.getMessage());
        }
        return 1;
    }

    // SERVICE CATEGORY METHODS

    // Get all categories from database
    public List<String> getAllCategories() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT categoryName FROM service_categories";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(rs.getString("categoryName"));
        } catch (SQLException e) {
            System.out.println("Get categories error: "
                + e.getMessage());
        }
        return list;
    }

    // Get categoryID by name - used for foreign key
    private int getCategoryID(String categoryName) {
        String sql = "SELECT categoryID FROM service_categories "
                   + "WHERE categoryName = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, categoryName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("categoryID");
            }
        } catch (SQLException e) {
            System.out.println("Get category ID error: "
                + e.getMessage());
        }
        return 1;
    }

    // PRIORITY METHODS

    // Get all priorities from database
    public List<String> getAllPriorities() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT priorityName FROM priority_levels";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(rs.getString("priorityName"));
        } catch (SQLException e) {
            System.out.println("Get priorities error: "
                + e.getMessage());
        }
        return list;
    }

    // Get priorityID by name - used for foreign key
    private int getPriorityID(String priorityName) {
        String sql = "SELECT priorityID FROM priority_levels "
                   + "WHERE priorityName = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, priorityName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("priorityID");
            }
        } catch (SQLException e) {
            System.out.println("Get priority ID error: "
                + e.getMessage());
        }
        return 1;
    }

    // SERVICE REQUEST METHODS

    // Save a new service request to the database
    // Uses foreign key IDs for category, service type and priority
    public void saveRequest(ServiceRequest request) {
        String sql = "INSERT INTO service_requests " +
                     "(categoryID, serviceTypeID, priorityID, status, " +
                     "description, completionRemark, " +
                     "submissionDate, customerID) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt   (1, getCategoryID(request.getCategory()));
            stmt.setInt   (2, getServiceTypeID(request.getServiceType()));
            stmt.setInt   (3, getPriorityID(request.getPriority()));
            stmt.setString(4, request.getStatus());
            stmt.setString(5, request.getDescription());
            stmt.setString(6, request.getCompletionRemark());
            stmt.setString(7, request.getSubmissionDate());
            stmt.setInt   (8, request.getCustomer().getUserID());
            int rows = stmt.executeUpdate();
            System.out.println("saveRequest rows affected: " + rows);
        } catch (SQLException e) {
            System.out.println("Save request error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Update an existing request - status, remark and staffID
    public void updateRequest(ServiceRequest request) {
        String sql = "UPDATE service_requests " +
                     "SET status = ?, completionRemark = ?, staffID = ? " +
                     "WHERE requestID = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, request.getStatus());
            stmt.setString(2, request.getCompletionRemark());
            if (request.getAssignedStaff() != null) {
                stmt.setInt(3, request.getAssignedStaff().getUserID());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            stmt.setInt(4, request.getRequestID());
            int rows = stmt.executeUpdate();
            System.out.println("updateRequest rows affected: " + rows);
        } catch (SQLException e) {
            System.out.println("Update request error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Get all requests using JOIN to get names from lookup tables
    public List<ServiceRequest> getAllRequests() {
        List<ServiceRequest> list = new ArrayList<>();
        String sql =
            "SELECT sr.*, sc.categoryName, " +
            "st.serviceTypeName, pl.priorityName " +
            "FROM service_requests sr " +
            "JOIN service_categories sc " +
            "  ON sr.categoryID    = sc.categoryID " +
            "JOIN service_types st " +
            "  ON sr.serviceTypeID = st.serviceTypeID " +
            "JOIN priority_levels pl " +
            "  ON sr.priorityID    = pl.priorityID";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ServiceRequest req = buildRequest(rs);
                if (req != null) list.add(req);
            }
        } catch (SQLException e) {
            System.out.println("Get all requests error: "
                + e.getMessage());
        }
        return list;
    }

    // Get all requests for one specific customer
    public List<ServiceRequest> getRequestsByCustomer(
            Customer customer) {
        List<ServiceRequest> list = new ArrayList<>();
        String sql =
            "SELECT sr.*, sc.categoryName, " +
            "st.serviceTypeName, pl.priorityName " +
            "FROM service_requests sr " +
            "JOIN service_categories sc " +
            "  ON sr.categoryID    = sc.categoryID " +
            "JOIN service_types st " +
            "  ON sr.serviceTypeID = st.serviceTypeID " +
            "JOIN priority_levels pl " +
            "  ON sr.priorityID    = pl.priorityID " +
            "WHERE sr.customerID = ?";
        try (PreparedStatement stmt =
                getConnection().prepareStatement(sql)) {
            stmt.setInt(1, customer.getUserID());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ServiceRequest req = buildRequest(rs);
                    if (req != null) list.add(req);
                }
            }
        } catch (SQLException e) {
            System.out.println("Get customer requests error: "
                + e.getMessage());
        }
        return list;
    }

    // Get all requests assigned to one specific staff member
    public List<ServiceRequest> getRequestsByStaff(Staff staff) {
        List<ServiceRequest> list = new ArrayList<>();
        String sql =
            "SELECT sr.*, sc.categoryName, " +
            "st.serviceTypeName, pl.priorityName " +
            "FROM service_requests sr " +
            "JOIN service_categories sc " +
            "  ON sr.categoryID    = sc.categoryID " +
            "JOIN service_types st " +
            "  ON sr.serviceTypeID = st.serviceTypeID " +
            "JOIN priority_levels pl " +
            "  ON sr.priorityID    = pl.priorityID " +
            "WHERE sr.staffID = ?";
        try (PreparedStatement stmt =
                getConnection().prepareStatement(sql)) {
            stmt.setInt(1, staff.getUserID());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ServiceRequest req = buildRequest(rs);
                    if (req != null) list.add(req);
                }
            }
        } catch (SQLException e) {
            System.out.println("Get staff requests error: "
                + e.getMessage());
        }
        return list;
    }

    // HELPER METHODS

    // Converts a database row into the correct User subclass
    // Checks the role column to decide which object to create
    private User buildUser(ResultSet rs) throws SQLException {
        int    id       = rs.getInt("userID");
        String username = rs.getString("username");
        String password = rs.getString("password");
        String fullName = rs.getString("fullName");
        String role     = rs.getString("role");

        switch (role) {
            case "Customer":
                return new Customer(id, username, password, fullName,
                        rs.getString("contactNumber"),
                        rs.getString("address"));
            case "Staff":
                return new Staff(id, username, password, fullName,
                        rs.getString("specialization"),
                        rs.getBoolean("availabilityStatus"));
            case "Admin":
                return new Admin(id, username, password, fullName,
                        rs.getInt("adminLevel"));
            default:
                return null;
        }
    }

    // Converts a database row into a ServiceRequest object
    private ServiceRequest buildRequest(ResultSet rs) {
        try {
            Customer customer =
                findCustomerByID(rs.getInt("customerID"));
            if (customer == null) return null;

            ServiceRequest req = new ServiceRequest(
                    rs.getString("categoryName"),
                    rs.getString("serviceTypeName"),
                    rs.getString("priorityName"),
                    rs.getString("description"),
                    customer,
                    rs.getString("submissionDate")
            );

            req.setRequestID(rs.getInt("requestID"));
            req.setStatus(rs.getString("status"));
            req.setCompletionRemark(rs.getString("completionRemark"));

            // Set assigned staff if there is one
            int staffID = rs.getInt("staffID");
            if (!rs.wasNull()) {
                Staff staff = findStaffByID(staffID);
                if (staff != null) req.setAssignedStaff(staff);
            }

            return req;

        } catch (SQLException e) {
            System.out.println("Build request error: " + e.getMessage());
            return null;
        }
    }

    // Find a customer by their ID
    private Customer findCustomerByID(int id) {
        String sql = "SELECT * FROM users "
                   + "WHERE userID = ? AND role = 'Customer'";
        try (PreparedStatement stmt =
                getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return (Customer) buildUser(rs);
            }
        } catch (SQLException e) {
            System.out.println("Find customer error: "
                + e.getMessage());
        }
        return null;
    }

    // Find a staff member by their ID
    private Staff findStaffByID(int id) {
        String sql = "SELECT * FROM users "
                   + "WHERE userID = ? AND role = 'Staff'";
        try (PreparedStatement stmt =
                getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return (Staff) buildUser(rs);
            }
        } catch (SQLException e) {
            System.out.println("Find staff error: " + e.getMessage());
        }
        return null;
    }
}