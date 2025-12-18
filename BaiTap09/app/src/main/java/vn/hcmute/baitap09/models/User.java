package vn.hcmute.baitap09.models;

/**
 * Model class representing a user (customer or manager)
 */
public class User {
    private String userId;
    private String userName;
    private String userType; // "customer" or "manager"
    private boolean isOnline;

    public User() {
    }

    public User(String userId, String userName, String userType) {
        this.userId = userId;
        this.userName = userName;
        this.userType = userType;
        this.isOnline = false;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public boolean isManager() {
        return "manager".equals(userType);
    }

    public boolean isCustomer() {
        return "customer".equals(userType);
    }
}

