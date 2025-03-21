package model.user;

public class User {
    private String username;
    private String password;
    private String passwordReminder;
    private final String role;
    private final String status;

    public User(String username, String password, String passwordReminder, String role, String status) {
        this.username = username;
        this.password = password;
        this.passwordReminder = passwordReminder;
        this.role = role;
        this.status = status;
    }

    // Getters and setters

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordReminder() {
        return passwordReminder;
    }

    public void setPasswordReminder(String passwordReminder) {
        this.passwordReminder = passwordReminder;
    }

    public String getRole() {
        return role;
    }

    public String getStatus() {
        return status;
    }

}
