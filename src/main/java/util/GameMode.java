package util;

public enum GameMode implements Localizable {
    NORMAL("gameMode.normal", "Normal"),
    FREEPLAY("gameMode.freeplay", "Freeplay"),
    PLAYGROUND("gameMode.playground", "Playground");

    private final String key;
    private final String value;

    GameMode(String key, String value) {
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
