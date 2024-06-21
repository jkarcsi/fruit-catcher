package utils;

public enum Language implements Localizable {
    ENGLISH("language.english", "English"),
    HUNGARIAN("language.hungarian", "Hungarian");

    private final String key;
    private final String value;

    Language(String key, String value) {
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
