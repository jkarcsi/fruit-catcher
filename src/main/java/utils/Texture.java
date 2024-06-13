package utils;

public enum Texture {
    FOREST("forest.css"),
    RETRO("retro.css"),
    FUTURISTIC("futuristic.css");

    private final String cssFile;

    Texture(String cssFile) {
        this.cssFile = cssFile;
    }

    public String getCssFile() {
        return cssFile;
    }
}
