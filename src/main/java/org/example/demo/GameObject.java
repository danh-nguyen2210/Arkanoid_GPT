package org.example.demo;

import javafx.scene.image.Image;

public abstract class GameObject {
    protected double x, y, width, height;
    protected Image image;

    public GameObject() {
        x = 0;
        y = 0;
        width = 0;
        height = 0;
    }

    public GameObject(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public abstract void update();

    public abstract void render();

    public boolean checkCollision(GameObject other) {
        double leftA, leftB;
        double rightA, rightB;
        double topA, topB;
        double bottomA, bottomB;

        //Calculate the sides of rect A
        leftA = this.x;
        rightA = this.x + this.width;
        topA = this.y;
        bottomA = this.y + this.height;

        //Calculate the sides of rect B
        leftB = other.x;
        rightB = other.x + other.width;
        topB = other.y;
        bottomB = other.y + other.height;

        //If any of the sides from A are outside of B
        if( bottomA <= topB )
        {
            return false;
        }

        if( topA >= bottomB )
        {
            return false;
        }

        if( rightA <= leftB )
        {
            return false;
        }

        if( leftA >= rightB )
        {
            return false;
        }

        //If none of the sides from A are outside B
        return true;
    }
}

