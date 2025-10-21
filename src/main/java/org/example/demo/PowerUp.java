package org.example.demo;

public abstract class PowerUp extends GameObject{
    protected String type;
    protected Double duration;

    public PowerUp() {
        super();
        this.type = "";
        this.duration = 0.0;
    }

    public PowerUp(double x, double y, double width, double height, String type, Double duration) {
        super(x, y, width, height);
        this.type = type;
        this.duration = duration;
    }

    public abstract void applyEffect(Paddle paddle);

    public abstract void removeEffect(Paddle paddle);
}
