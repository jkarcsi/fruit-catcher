package model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Basket {
    private double x;
    private double y;
    private final double width;
    private final double height;
    private double velocityX;
    private final Image basketImage;

    public Basket(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.velocityX = 0;
        this.basketImage = new Image("/image/basket.png");
    }

    public void moveLeft() {
        velocityX = -5; // Adjust the speed as needed
    }

    public void moveRight() {
        velocityX = 5; // Adjust the speed as needed
    }

    public void stop() {
        velocityX = 0;
    }

    public void update(double canvasWidth, double canvasHeight) {
        y = canvasHeight - 180;
        x += velocityX;
        if (x < 0) {
            x = 0;
        } else if (x + width > canvasWidth) {
            x = canvasWidth - width;
        }
    }

    public void render(GraphicsContext gc) {
        gc.drawImage(basketImage, x, y, width, height);
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
