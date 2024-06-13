package utils;

public enum UserRole {
    ADMIN("admin"),
    USER("user");

    private final String strength;
    UserRole(final String role) {
        this.strength = role;
    }

    public String value() { return this.strength; }
}
