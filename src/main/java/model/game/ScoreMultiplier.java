package model.game;

import static util.ResourcePaths.IMAGE_BONUS_PNG;

public class ScoreMultiplier extends FallingObject {
    public ScoreMultiplier(double x, double y, double speed, double width, double height) {
        super(x, y, speed, width, height, IMAGE_BONUS_PNG);
    }
}
