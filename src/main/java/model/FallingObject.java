package model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class FallingObject {
    protected double x, y;
    protected double width, height;
    protected boolean caught;

    public FallingObject(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.caught = false;
    }

    public void update() {
        y += 5; // Falling speed
    }

    public void render(GraphicsContext gc) {
        gc.setFill(getColor());
        gc.fillRect(x, y, width, height);
    }

    public boolean collidesWith(Basket basket) {
        return x < basket.getX() + basket.getWidth() && x + width > basket.getX() && y < basket.getY() + basket.getHeight() && y + height > basket.getY();
    }

    public void setCaught(boolean caught) {
        this.caught = caught;
    }

    public boolean isCaught() {
        return caught;
    }

    protected abstract Color getColor();
}
