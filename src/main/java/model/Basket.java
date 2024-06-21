package model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import static utils.ResourcePaths.IMAGE_BASKET_PNG;

public class Basket {
    private double x;
    private double y;
    private final double width;
    private final double height;
    private double velocityX;
    private Image basketImage;

    // Eredeti konstruktor, amely képet tölt be
    public Basket(double x, double y, double width, double height) {
        this(x, y, width, height, true);
    }

    public Basket(double x, double y, double width, double height, boolean loadImage) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.velocityX = 0;
        if (loadImage) {
            this.basketImage = new Image(IMAGE_BASKET_PNG); // Kép betöltése, ha szükséges
        }
    }

    public void moveLeft() {
        velocityX = -5;
    }

    public void moveRight() {
        velocityX = 5;
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
