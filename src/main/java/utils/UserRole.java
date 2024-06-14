package utils;

public enum UserRole {
    ADMIN("admin"),
    USER("user");

    private final String role;
    UserRole(final String role) {
        this.role = role;
    }

    public String value() { return this.role; }
}
