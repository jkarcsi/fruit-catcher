package model.game;

import static util.ResourcePaths.IMAGE_BLACK_PNG;

public class BlackFruit extends FallingObject {
    public BlackFruit(double x, double y, double speed, double width, double height) {
        super(x, y, speed, width, height, IMAGE_BLACK_PNG);
    }
}
