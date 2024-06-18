package utils;

public enum Language {
    ENGLISH("English"),
    HUNGARIAN("Hungarian");

    private final String value;


    Language(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
