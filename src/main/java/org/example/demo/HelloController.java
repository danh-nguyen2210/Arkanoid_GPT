package org.example.demo;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.prefs.Preferences;

public class HelloController {

    @FXML private AnchorPane gamePane;
    @FXML private Label scoreLabel;
    @FXML private Label topScoreLabel;
    @FXML private Label levelLabel;
    @FXML private Button pauseButton;

    private List<Brick> bricks = new ArrayList<>();
    private Ball ball;
    private ImageView ballImage;
    private Paddle paddle;
    private ImageView paddleImage;

    private boolean moveLeft = false;
    private boolean moveRight = false;

    private AnimationTimer gameTimer;
    private boolean paused = false;

    private int score = 0;
    private int level = 1;
    private int topScore = 0;

    // Preferences để lưu top score
    private final Preferences prefs = Preferences.userNodeForPackage(HelloController.class);
    private final String PREF_HIGHSCORE_KEY = "arkanoid_highscore";

    // Mỗi lần hit sẽ cộng bao nhiêu điểm
    private final int POINTS_PER_HIT = 100;

    public void initialize() {
        // load persisted highscore
        topScore = prefs.getInt(PREF_HIGHSCORE_KEY, 0);
        updateScoreLabels();

        setupBricks();
        setupPaddle();
        setupBall();
        setupKeyControls();
        setupPauseButton();
        startGameLoop();
    }

    private void setupBricks() {
        Image brickImg = new Image(getClass().getResourceAsStream("/images/BlueBrick.png"));
        int rows = 6, cols = 12;
        double brickWidth = 32, brickHeight = 16;

        double startY = 80;

        // đảm bảo bricks căn giữa khi pane đã có kích thước
        gamePane.widthProperty().addListener((obs, oldW, newW) -> drawBricks(brickImg, rows, cols, brickWidth, brickHeight, startY));
        // gọi lần đầu (nếu width lúc này > 0)
        drawBricks(brickImg, rows, cols, brickWidth, brickHeight, startY);
    }

    private void drawBricks(Image brickImg, int rows, int cols, double brickWidth, double brickHeight, double startY) {
        double startX = (gamePane.getWidth() - (cols * brickWidth)) / 2;
        if (Double.isNaN(startX)) startX = 100; // fallback
        // remove previous bricks ImageView but keep UI labels/buttons (children khác)
        // chúng ta chỉ remove nodes do bricks tạo ra (ImageView của BasicBrick)
        gamePane.getChildren().removeIf(node -> node.getUserData() == Boolean.TRUE);

        bricks.clear();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Brick brick = new BasicBrick(
                        startX + col * brickWidth,
                        startY + row * brickHeight,
                        brickWidth,
                        brickHeight,
                        4, // hit points
                        "blue",
                        brickImg
                );
                bricks.add(brick);
                ImageView iv = ((BasicBrick) brick).getImageView();
                // mark node so we can remove it later
                iv.setUserData(Boolean.TRUE);
                gamePane.getChildren().add(iv);
            }
        }
    }

    private void setupPaddle() {
        Image paddleImg = new Image(getClass().getResourceAsStream("/images/paddle.png"));
        paddleImage = new ImageView(paddleImg);
        paddleImage.setFitWidth(80);
        paddleImage.setFitHeight(16);
        // initial position
        paddle = new Paddle(5, 0);
        paddle.setWidth(80);
        paddle.setHeight(16);
        paddle.setX((gamePane.getPrefWidth() - paddle.getWidth()) / 2.0);
        paddle.setY(gamePane.getPrefHeight() - 70);

        paddleImage.setLayoutX(paddle.getX());
        paddleImage.setLayoutY(paddle.getY());

        // mark it so we don't remove it when clearing bricks
        paddleImage.setUserData("PADDLE_IMG");
        gamePane.getChildren().add(paddleImage);
    }

    private void setupBall() {
        Image ballImg = new Image(getClass().getResourceAsStream("/images/ball.png"));
        ballImage = new ImageView(ballImg);
        ballImage.setFitWidth(16);
        ballImage.setFitHeight(16);

        ball = new Ball(4, 1, -1, ballImg);
        ball.setWidth(16);
        ball.setHeight(16);
        // đặt bóng lên trên paddle ban đầu
        ball.setX(paddle.getX() + paddle.getWidth() / 2 - ball.getWidth() / 2);
        ball.setY(paddle.getY() - 20);

        ballImage.setLayoutX(ball.getX());
        ballImage.setLayoutY(ball.getY());

        ballImage.setUserData("BALL_IMG");
        gamePane.getChildren().add(ballImage);
    }

    private void setupKeyControls() {
        gamePane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                Scene scene = newScene;
                scene.setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.LEFT) moveLeft = true;
                    if (event.getCode() == KeyCode.RIGHT) moveRight = true;
                    if (event.getCode() == KeyCode.SPACE) togglePause();
                });
                scene.setOnKeyReleased(event -> {
                    if (event.getCode() == KeyCode.LEFT) moveLeft = false;
                    if (event.getCode() == KeyCode.RIGHT) moveRight = false;
                });
                // request focus để nhận phím
                gamePane.requestFocus();
            }
        });
    }

    private void setupPauseButton() {
        if (pauseButton != null) {
            pauseButton.setOnAction(e -> togglePause());
        }
    }

    private void startGameLoop() {
        gameTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!paused) update();
            }
        };
        gameTimer.start();
    }

    private void togglePause() {
        paused = !paused;
        if (pauseButton != null) {
            pauseButton.setText(paused ? "RESUME" : "PAUSE");
        }
    }

    private void update() {
        movePaddle();
        moveBall();
        checkWallCollision();
        checkBrickCollision();
        checkPaddleCollision();
        cleanupDestroyedBricks();
    }

    private void movePaddle() {
        if (moveLeft) {
            paddle.setX(Math.max(0, paddle.getX() - paddle.getSpeed()));
        }
        if (moveRight) {
            double maxX = gamePane.getWidth() - paddle.getWidth();
            paddle.setX(Math.min(maxX, paddle.getX() + paddle.getSpeed()));
        }
        paddleImage.setLayoutX(paddle.getX());
        paddleImage.setLayoutY(paddle.getY());
    }

    private void moveBall() {
        ball.setX(ball.getX() + ball.getDirectionX() * ball.getSpeed());
        ball.setY(ball.getY() + ball.getDirectionY() * ball.getSpeed());
        ballImage.setLayoutX(ball.getX());
        ballImage.setLayoutY(ball.getY());
    }

    private void checkWallCollision() {
        double paneWidth = gamePane.getWidth();
        double paneHeight = gamePane.getHeight();

        if (ball.getX() <= 0 || ball.getX() + ball.getWidth() >= paneWidth) {
            ball.setDirectionX(ball.getDirectionX() * -1);
        }
        if (ball.getY() <= 0) {
            ball.setDirectionY(ball.getDirectionY() * -1);
        }
        if (ball.getY() + ball.getHeight() >= paneHeight) {
            // rơi xuống -> reset ball lên paddle
            resetBall();
        }
    }

    private void checkBrickCollision() {
        for (Brick brick : bricks) {
            if (brick instanceof BasicBrick b && !b.isDestroyed() && ball.checkCollision(brick)) {
                // Bóng bật lại theo logic hiện tại
                ball.bounceOff(brick);

                // Ghi nhận hit rồi cộng điểm
                brick.takeHit();
                addScore(POINTS_PER_HIT);

                // không remove ở đây; BasicBrick tự animate rồi đánh dấu destroyed khi xong
                break; // chỉ xử lý 1 gạch mỗi frame
            }
        }
    }

    private void checkPaddleCollision() {
        if (ball.checkCollision(paddle)) {
            ball.bounceOff(paddle);

            double paddleCenter = paddle.getX() + paddle.getWidth() / 2;
            double hitPos = (ball.getX() + ball.getWidth() / 2 - paddleCenter) / (paddle.getWidth() / 2);

            ball.setDirectionX(hitPos);
            ball.setDirectionY(-Math.abs(ball.getDirectionY()));

            double length = Math.sqrt(ball.getDirectionX() * ball.getDirectionX() + ball.getDirectionY() * ball.getDirectionY());
            ball.setDirectionX(ball.getDirectionX() / length);
            ball.setDirectionY(ball.getDirectionY() / length);
        }
    }

    private void addScore(int delta) {
        score += delta;
        if (scoreLabel != null) scoreLabel.setText("SCORE: " + score);

        if (score > topScore) {
            topScore = score;
            if (topScoreLabel != null) topScoreLabel.setText("TOP SCORE: " + topScore);
            // persist highscore
            prefs.putInt(PREF_HIGHSCORE_KEY, topScore);
        }
    }

    private void resetBall() {
        ball.setX(paddle.getX() + paddle.getWidth() / 2 - ball.getWidth() / 2);
        ball.setY(paddle.getY() - 20);
        ball.setDirectionY(-1);
        ball.setDirectionX(0.8); // set default direction
    }

    // Xóa bricks đã hoàn toàn tan biến khỏi gamePane & danh sách
    private void cleanupDestroyedBricks() {
        Iterator<Brick> iterator = bricks.iterator();
        while (iterator.hasNext()) {
            Brick brick = iterator.next();
            if (brick instanceof BasicBrick b && b.isDestroyed()) {
                // remove imageView và entry
                gamePane.getChildren().remove(b.getImageView());
                iterator.remove();
            }
        }
    }

    private void updateScoreLabels() {
        if (scoreLabel != null) scoreLabel.setText("SCORE: " + score);
        if (topScoreLabel != null) topScoreLabel.setText("TOP SCORE: " + topScore);
        if (levelLabel != null) levelLabel.setText("LEVEL: " + level);
    }
}
