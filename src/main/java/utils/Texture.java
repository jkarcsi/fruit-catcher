package utils;

public enum Texture {
    FOREST("Forest", "forest.css"),
    RETRO("Retro","retro.css"),
    FUTURISTIC("Futuristic", "futuristic.css");

    private final String textureName;
    private final String cssFile;


    Texture(String textureName, String cssFile) {
        this.cssFile = cssFile;
        this.textureName = textureName;
    }

    public String getCssFile() {
        return cssFile;
    }

    public String getTextureName() {
        return textureName;
    }
}
