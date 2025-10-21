package org.example.demo;

import java.awt.*;

public abstract class Brick extends GameObject {
    protected int hitPoints;

    protected String type;

    public Brick() {
        super();
        this.hitPoints = 0;
        this.type = "";
    }

    public Brick(double x, double y, double width, double height, int hitPoints, String type) {
        super(x, y, width, height);
        this.hitPoints = hitPoints;
        this.type = type;
    }



    public void takeHit() {
        hitPoints--;
    }

    public boolean isDestroyed() {
        return hitPoints <= 0;
    }

    @Override
    public void update() {

    }

    public abstract void updateFrame();

    public void render(Graphics g) {

    }
}
