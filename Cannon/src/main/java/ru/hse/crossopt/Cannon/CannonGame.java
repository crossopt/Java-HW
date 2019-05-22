package ru.hse.crossopt.Cannon;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

/** A simplistic game inspired by Scorched Earth. */
public class CannonGame extends Application {
    private static final Duration animationSpeed = Duration.millis(16);

    /** Creates the landscape, cannon and shows the game screen to the player. */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Cannon game");
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(400);

        var center = new GridPane();
        center.setPrefSize(Landscape.WIDTH, Landscape.HEIGHT);

        Canvas canvas = new Canvas();
        center.getChildren().add(canvas);
        canvas.widthProperty().bind(center.widthProperty());
        canvas.heightProperty().bind(center.heightProperty());


        Scene scene = new Scene(center);
        primaryStage.setScene(scene);
        primaryStage.show();

        Drawer drawer = new Drawer(canvas.getGraphicsContext2D());
        Landscape landscape = new Landscape(drawer);
        Cannon cannon = new Cannon(landscape, drawer);

        primaryStage.getScene().setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            if (code == KeyCode.LEFT) {
                cannon.moveLeft();
            } else if (code == KeyCode.RIGHT) {
                cannon.moveRight();
            } else if (code == KeyCode.UP) {
                cannon.increaseAngle();
            } else if (code == KeyCode.DOWN) {
                cannon.decreaseAngle();
            } else if (code == KeyCode.ENTER) {
                cannon.shoot();
            } else if (code.isDigitKey() && Integer.parseInt(code.getChar()) <= Cannon.LARGEST_BULLET) {
                cannon.setBulletType(Integer.parseInt(code.getChar()));
            }
        });

        Timeline mainLoop = new Timeline();
        mainLoop.setCycleCount(Timeline.INDEFINITE);
        mainLoop.getKeyFrames().add(new KeyFrame(animationSpeed, event -> {
            drawer.clear();
            landscape.draw();
            cannon.draw();
            if (landscape.wasTargetShot()) {
                primaryStage.getScene().setOnKeyPressed(null);
                drawer.write("Congratulations! You have won!", Landscape.WIDTH / 2, Landscape.HEIGHT / 2, Color.RED);
            }
        }));
        mainLoop.play();
    }


    /** Starts the application. */
    public static void main(String[] args) {
        Application.launch(args);
    }
}
