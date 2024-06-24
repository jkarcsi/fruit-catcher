package model.game;

import static util.ResourcePaths.IMAGE_LEAF_PNG;

public class Leaf extends FallingObject {
        public Leaf(double x, double y, double speed, double width, double height) {
        super(x, y, speed, width, height, IMAGE_LEAF_PNG);
    }
}
