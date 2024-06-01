package model;

public class GameLevel {
    private double fruitSpeed;
    private double leafSpeed;
    private double fruitSize;
    private double leafSize;
    private double fruitSpawnRate;
    private double leafSpawnRate;

    public GameLevel(double fruitSpeed, double leafSpeed, double fruitSize, double leafSize, double fruitSpawnRate, double leafSpawnRate) {
        this.fruitSpeed = fruitSpeed;
        this.leafSpeed = leafSpeed;
        this.fruitSize = fruitSize;
        this.leafSize = leafSize;
        this.fruitSpawnRate = fruitSpawnRate;
        this.leafSpawnRate = leafSpawnRate;
    }

    public double getFruitSpeed() {
        return fruitSpeed;
    }

    public double getLeafSpeed() {
        return leafSpeed;
    }

    public double getFruitSize() {
        return fruitSize;
    }

    public double getLeafSize() {
        return leafSize;
    }

    public double getFruitSpawnRate() {
        return fruitSpawnRate;
    }

    public double getLeafSpawnRate() {
        return leafSpawnRate;
    }
}
