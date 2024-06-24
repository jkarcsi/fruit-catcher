package util;

public enum Difficulty implements Localizable {
    EASY("difficulty.easy", "Easy"),
    MEDIUM("difficulty.medium", "Medium"),
    HARD("difficulty.hard", "Hard");

    private final String key;
    private final String value;

    Difficulty(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getValue() {
        return value;
    }
}
