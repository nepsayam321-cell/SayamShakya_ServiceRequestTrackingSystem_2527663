package model;

// ServiceRequest stores all the details about one IT service request
// Every request goes through a lifecycle:
// Pending -> Assigned -> In Progress -> Completed
public class ServiceRequest {

    // This counter automatically gives each request a unique ID
    // It starts at 1000 so the first request gets ID 1001
    private static int counter = 1000;

    private int    requestID;
    private String category;       // Hardware, Software or Maintenance
    private String serviceType;    // IT Support or Laptop Servicing
    private String priority;       // Rush or Standard
    private String status;         // current stage in the lifecycle
    private String description;    // the problem described by the customer
    private String completionRemark; // notes added by staff when done
    private String submissionDate; // date the request was submitted

    // These store references to the customer who submitted
    // the request and the staff member assigned to handle it
    private Customer customer;
    private Staff    assignedStaff; // this is null until a staff is assigned

    // Constructor - the requestID is generated automatically
    // using the static counter so every request gets a unique ID
    public ServiceRequest(String category, String serviceType,
                          String priority, String description,
                          Customer customer, String submissionDate) {
        this.requestID        = ++counter;
        this.category         = category;
        this.serviceType      = serviceType;
        this.priority         = priority;
        this.description      = description;
        this.customer         = customer;
        this.submissionDate   = submissionDate;
        this.status           = "Pending"; // all new requests start as Pending
        this.completionRemark = "";
    }

    // Returns a short summary of the request details
    public String getDetails() {
        return "Request #" + requestID + " | " + category
             + " | " + serviceType + " | " + priority
             + " | Status: " + status;
    }

    // Getters and setters for all fields
    public int getRequestID()                    { return requestID; }
    public void setRequestID(int requestID)      { this.requestID = requestID; }

    public String getCategory()                  { return category; }
    public void setCategory(String category)     { this.category = category; }

    public String getServiceType()               { return serviceType; }
    public void setServiceType(String s)         { this.serviceType = s; }

    public String getPriority()                  { return priority; }
    public void setPriority(String priority)     { this.priority = priority; }

    public String getStatus()                    { return status; }
    public void setStatus(String status)         { this.status = status; }

    public String getDescription()               { return description; }
    public void setDescription(String d)         { this.description = d; }

    public String getCompletionRemark()          { return completionRemark; }
    public void setCompletionRemark(String r)    { this.completionRemark = r; }

    public String getSubmissionDate()            { return submissionDate; }

    public Customer getCustomer()                { return customer; }

    public Staff getAssignedStaff()              { return assignedStaff; }
    public void setAssignedStaff(Staff s)        { this.assignedStaff = s; }

    @Override
    public String toString() { return getDetails(); }
}