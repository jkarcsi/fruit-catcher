package model.game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class MovingCloud {
    private double x;
    private double y;
    private final double speed;
    private final Image image;
    private final boolean moveRight;
    private final double scaleFactor;

    public MovingCloud(double x, double y, double speed, boolean moveRight, Image image, double scaleFactor) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.moveRight = moveRight;
        this.image = image;
        this.scaleFactor = scaleFactor;
    }

    public void update(double canvasWidth) {
        if (moveRight) {
            x += speed;
            if (x > canvasWidth) {
                x = -image.getWidth() * scaleFactor;
            }
        } else {
            x -= speed;
            if (x < -image.getWidth() * scaleFactor) {
                x = canvasWidth;
            }
        }
    }

    public void render(GraphicsContext gc) {
        gc.drawImage(image, x, y, image.getWidth() * scaleFactor, image.getHeight() * scaleFactor);
    }
}
