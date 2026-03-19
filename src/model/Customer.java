package model;

import java.util.ArrayList;
import java.util.List;

// Customer extends User which means it inherits all the fields
// and methods from the User class
// A Customer is a user who submits IT service requests
public class Customer extends User {

    // These are fields specific to a Customer only
    private String contactNumber;
    private String address;

    // This list stores all requests submitted by this customer
    private List<ServiceRequest> myRequests;

    // Constructor - we call super() to pass the common fields
    // up to the parent User class
    public Customer(int userID, String username, String password,
                    String fullName, String contactNumber, String address) {
        super(userID, username, password, fullName);
        this.contactNumber = contactNumber;
        this.address       = address;
        this.myRequests    = new ArrayList<>();
    }

    // This method adds a new service request to the customer's list
    public void submitRequest(ServiceRequest request) {
        myRequests.add(request);
    }

    // This method returns all requests submitted by this customer
    public List<ServiceRequest> viewRequestStatus() {
        return myRequests;
    }

    // This overrides the abstract method from User
    // It returns the role of this user as a String
    // This is an example of polymorphism
    @Override
    public String getRole() { return "Customer"; }

    // Getters and setters for customer specific fields
    public String getContactNumber()                   { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getAddress()             { return address; }
    public void setAddress(String address) { this.address = address; }

    public List<ServiceRequest> getMyRequests() { return myRequests; }
}