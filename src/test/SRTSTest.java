package test;

import model.*;
import service.RequestService;
import java.util.List;

// This class tests all the core features of the system
// It runs 12 test cases and prints PASS or FAIL for each one
// Run this by right clicking and selecting Run As Java Application
public class SRTSTest {

    // These count how many tests pass and fail
    static int passed = 0;
    static int failed = 0;

    public static void main(String[] args) {
        System.out.println("===== SRTS Test Results =====\n");

        // Run all test cases
        testValidLogin();
        testInvalidLogin();
        testAdminLogin();
        testStaffLogin();
        testRegisterCustomer();
        testDuplicateUsername();
        testSubmitRequest();
        testShortDescription();
        testValidDescription();
        testGetCustomerRequests();
        testAssignStaff();
        testCompleteRequest();

        // Print the final results
        System.out.println("\n===== " + passed + " PASSED  |  "
                + failed + " FAILED =====");
    }

    // TC-01: Test that a customer can login with correct credentials
    static void testValidLogin() {
        RequestService s = new RequestService();
        User u = s.login("Diken", "Diken123");
        check("TC-01: Valid customer login", u instanceof Customer);
    }

    // TC-02: Test that login fails with a wrong password
    static void testInvalidLogin() {
        RequestService s = new RequestService();
        User u = s.login("Diken", "wrongpass");
        check("TC-02: Wrong password rejected", u == null);
    }

    // TC-03: Test that an admin can login successfully
    static void testAdminLogin() {
        RequestService s = new RequestService();
        User u = s.login("Sayam", "Sayam123");
        check("TC-03: Admin login works", u instanceof Admin);
    }

    // TC-04: Test that a staff member can login successfully
    static void testStaffLogin() {
        RequestService s = new RequestService();
        User u = s.login("Pujan", "Pujan123");
        check("TC-04: Staff login works", u instanceof Staff);
    }

    // TC-05: Test that a new customer can register successfully
    static void testRegisterCustomer() {
        RequestService s = new RequestService();
        Customer c = s.registerCustomer(
                "newuser99", "pass99", "New Person", "07999", "");
        check("TC-05: New customer registered", c != null);
    }

    // TC-06: Test that registration fails if username already exists
    static void testDuplicateUsername() {
        RequestService s = new RequestService();
        Customer c = s.registerCustomer(
                "Diken", "anypass", "Diken 2", "", "");
        check("TC-06: Duplicate username rejected", c == null);
    }

    // TC-07: Test that a service request is saved to the database
    static void testSubmitRequest() {
        RequestService s  = new RequestService();
        Customer customer = (Customer) s.login("Diken", "Diken123");
        int before        = s.getAllRequests().size();
        s.submitRequest(customer, "Software", "IT Support",
                "Standard", "Cannot open PDF files on my laptop.");
        int after = s.getAllRequests().size();
        check("TC-07: Request submitted and saved", after == before + 1);
    }

    // TC-08: Test that a short description under 10 characters is rejected
    static void testShortDescription() {
        RequestService s = new RequestService();
        check("TC-08: Short description rejected",
                !s.validateDescription("Short"));
    }

    // TC-09: Test that a valid description of 10 or more characters passes
    static void testValidDescription() {
        RequestService s = new RequestService();
        check("TC-09: Valid description accepted",
                s.validateDescription("This is a valid description."));
    }

    // TC-10: Test that we can get all requests for a customer
    static void testGetCustomerRequests() {
        RequestService s  = new RequestService();
        Customer customer = (Customer) s.login("Diken", "Diken123");
        List<ServiceRequest> list = s.getCustomerRequests(customer);
        check("TC-10: Customer requests retrieved", list != null);
    }

    // TC-11: Test that an admin can assign a staff member to a request
    static void testAssignStaff() {
        RequestService s     = new RequestService();
        Admin admin          = (Admin) s.login("Sayam", "Sayam123");
        Staff staff          = s.getAllStaff().get(0);
        List<ServiceRequest> all = s.getAllRequests();
        if (!all.isEmpty()) {
            ServiceRequest r = all.get(0);
            s.assignStaff(admin, staff, r);
            check("TC-11: Staff assigned to request",
                    r.getStatus().equals("Assigned"));
        } else {
            System.out.println("SKIP - TC-11: No requests in database");
        }
    }

    // TC-12: Test that a staff member can complete a request with a remark
    static void testCompleteRequest() {
        RequestService s     = new RequestService();
        Admin admin          = (Admin) s.login("Sayam", "Sayam123");
        Staff staff          = s.getAllStaff().get(0);
        List<ServiceRequest> all = s.getAllRequests();
        if (!all.isEmpty()) {
            ServiceRequest r = all.get(0);
            s.assignStaff(admin, staff, r);
            s.completeRequest(staff, r, "Fixed the issue successfully.");
            check("TC-12: Request completed with remark",
                    r.getStatus().equals("Completed")
                    && !r.getCompletionRemark().isEmpty());
        } else {
            System.out.println("SKIP - TC-12: No requests in database");
        }
    }

    // Helper method that prints PASS or FAIL
    // and updates the counters
    static void check(String name, boolean condition) {
        if (condition) {
            System.out.println("PASS  -  " + name);
            passed++;
        } else {
            System.out.println("FAIL  -  " + name);
            failed++;
        }
    }
}