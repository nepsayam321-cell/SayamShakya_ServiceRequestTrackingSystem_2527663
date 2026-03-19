package model;

// Admin extends User which means it inherits all common fields
// An Admin manages the whole system including staff and requests
public class Admin extends User {

    // adminLevel determines what level of access the admin has
    // 1 is a standard admin
    private int adminLevel;

    // Constructor - calls super() to set up common User fields
    public Admin(int userID, String username, String password,
                 String fullName, int adminLevel) {
        super(userID, username, password, fullName);
        this.adminLevel = adminLevel;
    }

    // This method assigns a staff member to a service request
    // It links the staff to the request and changes the status to Assigned
    public void assignStaff(Staff staff, ServiceRequest request) {
        request.setAssignedStaff(staff);
        staff.assignRequest(request);
        request.setStatus("Assigned");
    }

    // This method changes the priority of a request
    // Priority can be Rush or Standard
    public void prioritizeRequest(ServiceRequest request, String priority) {
        request.setPriority(priority);
    }

    // Returns the role of this user
    // This overrides the abstract method in User - polymorphism
    @Override
    public String getRole() { return "Admin"; }

    // Getters and setters
    public int getAdminLevel()                { return adminLevel; }
    public void setAdminLevel(int adminLevel) { this.adminLevel = adminLevel; }
}