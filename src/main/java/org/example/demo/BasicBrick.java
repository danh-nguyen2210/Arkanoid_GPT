package org.example.demo;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.animation.AnimationTimer;

public class BasicBrick extends Brick {
    private ImageView imageView;
    private Image spriteSheet;

    private final int frameWidth = 32;
    private final int frameHeight = 16;
    private final int totalFrames = 8;
    private int currentFrame = 0;
    private boolean breaking = false;
    private boolean destroyed = false;

    private AnimationTimer breakAnimation;

    public BasicBrick(double x, double y, double width, double height, int hitPoints, String color, Image spriteSheet) {
        super(x, y, width, height, hitPoints, color);
        this.spriteSheet = spriteSheet;

        imageView = new ImageView(spriteSheet);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setLayoutX(x);
        imageView.setLayoutY(y);
        imageView.setViewport(new Rectangle2D(0, 0, frameWidth, frameHeight));
    }

    @Override
    public void takeHit() {
        if (breaking || destroyed) return;

        hitPoints--;
        if (hitPoints > 0) {
            updateFrame();
        } else {
            startBreakAnimation();
        }
    }

    @Override
    public void updateFrame() {
        if (currentFrame < totalFrames - 2) {
            currentFrame++;
            imageView.setViewport(new Rectangle2D(currentFrame * frameWidth, 0, frameWidth, frameHeight));
        }
    }

    private void startBreakAnimation() {
        breaking = true;
        currentFrame++;

        breakAnimation = new AnimationTimer() {
            private long lastFrameTime = 0;
            private final long frameDelay = 70_000_000; // 70ms / frame

            @Override
            public void handle(long now) {
                if (now - lastFrameTime < frameDelay) return;
                lastFrameTime = now;

                if (currentFrame < totalFrames) {
                    imageView.setViewport(new Rectangle2D(currentFrame * frameWidth, 0, frameWidth, frameHeight));
                    currentFrame++;
                } else {
                    imageView.setVisible(false);
                    destroyed = true; // đánh dấu là đã tan biến hoàn toàn
                    stop();
                }
            }
        };
        breakAnimation.start();
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void render() {}
}
