package org.example.demo;

import java.awt.*;

public class Paddle extends MovableObject{
    private double speed, currentPowerUp;

    public double getSpeed() {
        return speed;
    }

    public Paddle() {
        super();
        speed = 0;
        currentPowerUp = 0;
    }

    public Paddle(double speed, double currentPowerUp) {
        super(speed, 0);
        this.speed = speed;
        this.currentPowerUp = currentPowerUp;
    }

    public Paddle(double x, double y, double width, double height, double speed, double currentPowerUp) {
        super(x, y, width, height, speed, 0);
        this.speed = speed;
        this.currentPowerUp = currentPowerUp;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getCurrentPowerUp() {
        return currentPowerUp;
    }

    public void setCurrentPowerUp(double currentPowerUp) {
        this.currentPowerUp = currentPowerUp;
    }

    public void moveLeft() {
        this.setDx(-speed);
        this.setDy(0);
    }

    public void moveRight() {
        this.setDx(speed);
        this.setDy(0);
    }

    public void applyPowerUp() {
        if (currentPowerUp == 1) {
            // PowerUp loại 1: tăng tốc
            speed *= 1.5;
        } else if (currentPowerUp == 2) {
            // PowerUp loại 2: mở rộng chiều rộng thanh đỡ
            setWidth(getWidth() * 1.2);
        }
    }

    public void render() {

    }
}
