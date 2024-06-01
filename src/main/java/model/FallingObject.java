package model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public abstract class FallingObject {
    protected double x;
    protected double y;
    protected double speed;
    protected double width;
    protected double height;
    protected boolean caught;
    protected Image image;

    protected FallingObject(double x, double y, double speed, double width, double height, String imagePath) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.width = width;
        this.height = height;
        this.caught = false;
        this.image = new Image(getClass().getResourceAsStream(imagePath));
    }

    public void update() {
        y += speed;
    }

    public void render(GraphicsContext gc) {
        gc.drawImage(image, x, y, width, height);
    }

    public boolean collidesWith(Basket basket) {
        return x < basket.getX() + basket.getWidth() && x + width > basket.getX() &&
                y < basket.getY() + basket.getHeight() && y + height > basket.getY();
    }

    public void setCaught(boolean caught) {
        this.caught = caught;
    }

    public boolean isCaught() {
        return caught;
    }
}
