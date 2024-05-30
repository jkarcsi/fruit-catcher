package model;

import javafx.scene.paint.Color;

public class Fruit extends FallingObject {
    public Fruit(double x, double y) {
        super(x, y, 20, 20);
    }

    @Override
    protected Color getColor() {
        return Color.PEACHPUFF;
    }
}
