package model;

// This is the parent class for all users in the system
// It is abstract because we never create a plain User object
// Every user must be a Customer, Staff or Admin
public abstract class User {

    // These fields are private so they cannot be accessed directly
    // from outside this class - this is encapsulation
    private int userID;
    private String username;
    private String password;
    private String fullName;

    // Constructor to set up a new user with their details
    public User(int userID, String username, String password, String fullName) {
        this.userID   = userID;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
    }

    // This method checks if the username and password entered
    // by the user match what is stored in the system
    public boolean login(String inputUsername, String inputPassword) {
        return this.username.equals(inputUsername)
            && this.password.equals(inputPassword);
    }

    // This method is called when a user logs out
    public void logout() {
        System.out.println(fullName + " has logged out.");
    }

    // This method is abstract which means every subclass
    // must provide their own version of it
    // Customer returns "Customer", Staff returns "Staff" etc
    // This is polymorphism
    public abstract String getRole();

    // Getters allow other classes to read private fields
    // Setters allow other classes to update private fields
    public int getUserID()                   { return userID; }
    public void setUserID(int userID)        { this.userID = userID; }

    public String getUsername()              { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword()              { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName()              { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
}