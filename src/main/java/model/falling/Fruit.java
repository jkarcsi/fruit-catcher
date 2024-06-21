package model.falling;

import static utils.ResourcePaths.IMAGE_FRUIT_PNG;

public class Fruit extends FallingObject {
    public Fruit(double x, double y, double speed, double width, double height) {
        super(x, y, speed, width, height, IMAGE_FRUIT_PNG);
    }
}
