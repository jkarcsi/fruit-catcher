package util;

public enum PasswordStrength {
    WEAK("Weak"),
    MEDIUM("Medium"),
    STRONG("Strong");

    private final String strength;
    PasswordStrength(final String strength) {
        this.strength = strength;
    }

    public String value() { return this.strength; }
}
