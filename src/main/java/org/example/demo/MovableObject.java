package org.example.demo;

public abstract class MovableObject extends GameObject {
    protected double dx,dy;   //velocity

    public MovableObject() {
        super();
        this.dx = 0;
        this.dy = 0;
    }

    public MovableObject(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public MovableObject(double x, double y, double width, double height, double dx, double dy) {
        super(x, y, width, height);
        this.dx = dx;
        this.dy = dy;
    }

    public double getDx() {
        return dx;
    }

    public void setDx(double dx) {
        this.dx = dx;
    }

    public double getDy() {
        return dy;
    }

    public void setDy(double dy) {
        this.dy = dy;
    }

    @Override
    public void update() {
        move();
    }

    public void move () {
        this.setX(this.getX() + dx);
        this.setY(this.getY() + dy);
    }
}
