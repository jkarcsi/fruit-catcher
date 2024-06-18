package utils;

public enum Difficulty {

    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard");

    private final String value;


    Difficulty(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
