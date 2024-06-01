package model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.Objects;

public class Basket {
    private double x;
    private double y;
    private double width;
    private double height;
    private Image basketImage;

    public Basket(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.basketImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                "/fruitcatchgame/image/basket.png")));
    }

    public void moveLeft() {
        if (x > 0) {
            x -= 20;
        }
    }

    public void moveRight(double canvasWidth) {
        if (x + width < canvasWidth) {
            x += 20;
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
