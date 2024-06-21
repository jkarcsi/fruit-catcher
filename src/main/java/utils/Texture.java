package utils;

public enum Texture implements Localizable {
    FOREST("texture.forest", "Forest", "forest.css"),
    RETRO("texture.retro", "Retro", "retro.css"),
    FUTURISTIC("texture.futuristic", "Futuristic", "futuristic.css");

    private final String key;
    private final String value;
    private final String cssFile;

    Texture(String key, String value, String cssFile) {
        this.key = key;
        this.value = value;
        this.cssFile = cssFile;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getValue() {
        return value;
    }

    public String getCssFile() {
        return cssFile;
    }
}
