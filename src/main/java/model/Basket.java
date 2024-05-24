package model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Basket {
    private double x, y;
    private double width, height;

    public Basket(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void moveLeft() {
        if (x > 0) {
            x -= 20;
        }
    }

    public void moveRight() {
        if (x + width < 600) { // Feltételezve, hogy a canvas szélessége 600
            x += 20;
        }
    }

    public void render(GraphicsContext gc) {
        gc.setFill(Color.BLUE);
        gc.fillRect(x, y, width, height);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }
}
