package model;

import java.util.ArrayList;
import java.util.List;

// Staff extends User which means it inherits all common fields
// A Staff member is a technician who handles service requests
public class Staff extends User {

    // Specialization stores what area the staff member works in
    // for example Hardware, Software or Network
    private String specialization;

    // This tracks whether the staff member is available
    // true means available, false means they are busy
    private boolean availabilityStatus;

    // This list stores all requests that have been assigned
    // to this staff member
    private List<ServiceRequest> assignedRequests;

    // Constructor - calls super() to set up the common User fields
    public Staff(int userID, String username, String password,
                 String fullName, String specialization, boolean availabilityStatus) {
        super(userID, username, password, fullName);
        this.specialization     = specialization;
        this.availabilityStatus = availabilityStatus;
        this.assignedRequests   = new ArrayList<>();
    }

    // Returns all requests currently assigned to this staff member
    public List<ServiceRequest> viewAssignedRequests() {
        return assignedRequests;
    }

    // Updates the status of a service request
    // for example changes it from Assigned to In Progress
    public void updateStatus(ServiceRequest request, String newStatus) {
        request.setStatus(newStatus);
    }

    // Marks a request as Completed and adds a note explaining
    // what was done to fix the problem
    // A remark is required before the request can be completed
    public void addCompletionRemark(ServiceRequest request, String remark) {
        request.setCompletionRemark(remark);
        request.setStatus("Completed");
    }

    // Adds a request to this staff member's assigned list
    public void assignRequest(ServiceRequest request) {
        assignedRequests.add(request);
    }

    // Returns the role of this user
    // This overrides the abstract method in User - polymorphism
    @Override
    public String getRole() { return "Staff"; }

    // Getters and setters
    public String getSpecialization()                      { return specialization; }
    public void setSpecialization(String specialization)   { this.specialization = specialization; }

    public boolean isAvailabilityStatus()                  { return availabilityStatus; }
    public void setAvailabilityStatus(boolean b)           { this.availabilityStatus = b; }

    public List<ServiceRequest> getAssignedRequests()      { return assignedRequests; }
}