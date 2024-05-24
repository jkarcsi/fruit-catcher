package model;

import javafx.scene.paint.Color;

public class Leaf extends FallingObject {
    public Leaf(double x, double y) {
        super(x, y, 20, 20);
    }

    @Override
    protected Color getColor() {
        return Color.GREEN;
    }
}
