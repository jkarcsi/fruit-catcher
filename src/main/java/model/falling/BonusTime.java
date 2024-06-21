package model.falling;

import static utils.ResourcePaths.IMAGE_CLOCK_PNG;

public class BonusTime extends FallingObject {
    public BonusTime(double x, double y, double speed, double width, double height) {
        super(x, y, speed, width, height, IMAGE_CLOCK_PNG);
    }
}
