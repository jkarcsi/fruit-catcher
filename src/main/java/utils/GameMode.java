package utils;

public enum GameMode {
    NORMAL("Normal"),
    FREEPLAY("Freeplay"),
    PLAYGROUND("Playground");

    private final String value;


    GameMode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
