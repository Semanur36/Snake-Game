package com.example.finalproject;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;


import java.io.*;
import java.util.*;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.StageStyle;

public class HelloApplication extends Application {
    static int speed = 5;
    static int foodcolor = 0;
    static int width = 20;
    static int height = 20;
    static int foodX = 0;
    static int foodY = 0;
    static int cornersize = 35;
    static List<Corner> snake = new ArrayList<>();
    static Dir direction = Dir.left;
    static boolean gameOver = false;
    static Random rand = new Random();
    static Boolean scoreUpdated;

    public enum Dir {
        left, right, up, down
    }

    public static class Corner {
        int x;
        int y;

        public Corner(int x, int y) {
            this.x = x;
            this.y = y;
        }

    }

    public  void start(Stage primaryStage) {

        Image splashImage = new Image(getClass().getResource("images/splash.png").toExternalForm());
        ImageView imageView = new ImageView(splashImage);


        StackPane root = new StackPane(imageView);
        Scene splashScene = new Scene(root, 1080, 720);

        Stage splashStage = new Stage();
       splashStage.initStyle(StageStyle.UNDECORATED);
        splashStage.setScene(splashScene);
        splashStage.centerOnScreen();
        splashStage.show();

        // Simulate loading
        Task<Void> sleeper = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(3000);
                return null;
            }
        };

        sleeper.setOnSucceeded(event -> {

            Helper.display();
            splashStage.close();
            speed = Helper.score + 5;

            startGame(primaryStage); // Call your main stage method
        });

        new Thread(sleeper).start();




    }

    public  void startGame(Stage primaryStage)
    {
            try {
                newFood();
                VBox root = new VBox();
                Canvas c = new Canvas(width * cornersize, height * cornersize);
                GraphicsContext gc = c.getGraphicsContext2D();
                root.getChildren().add(c);

                new AnimationTimer() {
                    long lastTick = 0;

                    public void handle(long now) {
                        if (lastTick == 0) {
                            lastTick = now;
                            tick(gc);
                            return;
                        }

                        if (now - lastTick > 1000000000 / speed) {
                            lastTick = now;
                            tick(gc);
                        }
                    }

                }.start();

                Scene scene = new Scene(root, width * cornersize, height * cornersize);

                // control
                scene.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
                    if (key.getCode() == KeyCode.W) {
                        direction = Dir.up;
                    }
                    if (key.getCode() == KeyCode.A) {
                        direction = Dir.left;
                    }
                    if (key.getCode() == KeyCode.S) {
                        direction = Dir.down;
                    }
                    if (key.getCode() == KeyCode.D) {
                        direction = Dir.right;
                    }

                });

                // add start snake parts
                snake.add(new Corner(width / 2, height / 2));
                snake.add(new Corner(width / 2, height / 2));
                snake.add(new Corner(width / 2, height / 2));
                //If you do not want to use css style, you can just delete the next line.
                // scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

                primaryStage.setResizable(false);
                primaryStage.setScene(scene);
                primaryStage.setTitle("SNAKE GAME");
                primaryStage.setOnCloseRequest(event -> {
                    int score = (speed - 6 < 0) ? 1 : speed - 6;

                    Helper.updateScore(score);
//                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                    alert.setHeaderText(null);
//                    alert.setContentText("Your score has been saved.");
//                    alert.showAndWait();
                });

                primaryStage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    // tick
    public  void tick(GraphicsContext gc) {
        if (gameOver) {
            gc.setFill(Color.RED);
            gc.setFont(new Font("", 50));
            gc.fillText("GAME OVER", 100, 250);
            return;
        }


        for (int i = snake.size() - 1; i >= 1; i--) {
            snake.get(i).x = snake.get(i - 1).x;
            snake.get(i).y = snake.get(i - 1).y;
        }

        switch (direction) {
            case up:
                snake.get(0).y--;
                if (snake.get(0).y < 0) {
                    gameOver = true;
                }
                break;
            case down:
                snake.get(0).y++;
                if (snake.get(0).y > height) {
                    gameOver = true;
                }
                break;
            case left:
                snake.get(0).x--;
                if (snake.get(0).x < 0) {
                    gameOver = true;
                }
                break;
            case right:
                snake.get(0).x++;
                if (snake.get(0).x > width) {
                    gameOver = true;
                }
                break;

        }

        // eat food
        if (foodX == snake.get(0).x && foodY == snake.get(0).y) {
            snake.add(new Corner(-1, -1));
            newFood();
        }

        // self destroy
        for (int i = 1; i < snake.size(); i++) {
            if (snake.get(0).x == snake.get(i).x && snake.get(0).y == snake.get(i).y) {
                gameOver = true;
            }
        }

//        // fill
//        // background
//        gc.setFill(Color.BLACK);
//        gc.fillRect(0, 0, width * cornersize, height * cornersize);
//
//        // score
//        gc.setFill(Color.WHITE);
//        gc.setFont(new Font("", 30));
//        gc.fillText("Score: " + (speed - 6), 10, 30);
//
//        // random foodcolor
//        Color cc = Color.WHITE;
//
//        switch (foodcolor) {
//            case 0:
//                cc = Color.PURPLE;
//                break;
//            case 1:
//                cc = Color.LIGHTBLUE;
//                break;
//            case 2:
//                cc = Color.YELLOW;
//                break;
//            case 3:
//                cc = Color.PINK;
//                break;
//            case 4:
//                cc = Color.ORANGE;
//                break;
//        }
//        gc.setFill(cc);
//        gc.fillOval(foodX * cornersize, foodY * cornersize, cornersize, cornersize);
//
//        // snake
//        for (Corner c : snake) {
//            gc.setFill(Color.LIGHTGREEN);
//            gc.fillRect(c.x * cornersize, c.y * cornersize, cornersize - 1, cornersize - 1);
//            gc.setFill(Color.GREEN);
//            gc.fillRect(c.x * cornersize, c.y * cornersize, cornersize - 2, cornersize - 2);
//
//        }

        // fill
        // background (light grey as base)
        gc.setFill(Color.web("#E0E0E0")); // Açık gri arka plan
        gc.fillRect(0, 0, width * cornersize, height * cornersize);

        // score text
        gc.setFill(Color.web("#A76545")); // Kahverengi skor metni
        gc.setFont(new Font("", 30));
        gc.fillText("Score: " + (speed - 6), 10, 30);

        // food (random pastel tone from the palette)
        Color[] foodColors = {
                Color.web("#FFA55D"),  // Turuncu
                Color.web("#FFDF88"),  // Sarı
                Color.web("#ACC572")   // Açık yeşil
        };
        Color cc = foodColors[foodcolor % foodColors.length];
        gc.setFill(cc);
        gc.fillOval(foodX * cornersize, foodY * cornersize, cornersize, cornersize);

        // snake (gövde turuncu, kenarlık kahverengi gibi)
        for (Corner c : snake) {
            gc.setFill(Color.web("#FFA55D")); // Turuncu yılan gövdesi
            gc.fillRect(c.x * cornersize, c.y * cornersize, cornersize - 1, cornersize - 1);
            gc.setFill(Color.web("#A76545")); // Kenar - toprak kahverengi
            gc.fillRect(c.x * cornersize, c.y * cornersize, cornersize - 2, cornersize - 2);
        }

    }

    // food
    public static void newFood() {
        start: while (true) {
            foodX = rand.nextInt(width);
            foodY = rand.nextInt(height);

            for (Corner c : snake) {
                if (c.x == foodX && c.y == foodY) {
                    continue start;
                }
            }
            foodcolor = rand.nextInt(5);
            speed++;
            break;

        }
    }

    public static void main(String[] args) {
        launch(args);
    }

 }

