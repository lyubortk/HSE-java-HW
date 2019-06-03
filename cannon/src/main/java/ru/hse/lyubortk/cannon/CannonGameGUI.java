package ru.hse.lyubortk.cannon;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.BLUEVIOLET;

/** Cannon game javafx GUI class */
public class CannonGameGUI extends Application {
    private CannonGameCore cannonCore = new CannonGameCore();
    private EnumSet<KeyCode> inputKeys = EnumSet.noneOf(KeyCode.class);
    private Group root = new Group();
    private Group shellGroup = new Group();

    private Polygon ground;
    private Circle cannon;
    private Rectangle tower;
    private Circle target;

    /** Launches the game. */
    public static void main(String[] args) {
        launch(args);
    }

    /** Javafx start method. */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Cannon");

        setupSprites();
        Canvas canvas = new Canvas(CannonGameCore.WIDTH, CannonGameCore.HEIGHT);

        root.getChildren().addAll(canvas, ground, cannon, shellGroup, tower, target);
        Scene mainScene = new Scene(root);

        setupShellMenu();

        mainScene.setOnKeyPressed(
                keyEvent -> {
                    var key = keyEvent.getCode();
                    if (!inputKeys.contains(key))
                        inputKeys.add(key);
                });

        mainScene.setOnKeyReleased(
                keyEvent -> {
                    var key = keyEvent.getCode();
                    inputKeys.remove(key);
                });

        new AnimationTimer() {
            public void handle(long currentNanoTime) {
                cannonCore.update(currentNanoTime);
                handleInput();

                double cannonX = cannonCore.getCannonCoordinate().getX();
                double cannonY = cannonCore.getCannonCoordinate().getY();
                double targetX = cannonCore.getTargetCoordinate().getX();
                double targetY = cannonCore.getTargetCoordinate().getY();
                double towerAngle = cannonCore.getTowerAngle();

                cannon.setTranslateX(cannonX);
                cannon.setTranslateY(cannonY - Cannon.CANNON_RADIUS);

                tower.setTranslateX(cannonX);
                tower.setTranslateY(cannonY - Cannon.CANNON_RADIUS - Cannon.TOWER_WIDTH / 2.0);
                tower.getTransforms().clear();
                tower.getTransforms().add(
                        new Rotate(-towerAngle, 0, Cannon.TOWER_WIDTH / 2.0));

                target.setTranslateX(targetX);
                target.setTranslateY(targetY - CannonGameCore.TARGET_RADIUS);

            }
        }.start();

        cannonCore.setGameOverListener(new Consumer<>() {
            private boolean wasCalled = false;

            @Override
            public void accept(String string) {
                if (wasCalled) {
                    return;
                }
                wasCalled = true;

                inputKeys.clear();
                Alert gameOverAlert = new Alert(Alert.AlertType.INFORMATION, string);
                Button exitButton = (Button) gameOverAlert.getDialogPane().lookupButton(
                        ButtonType.OK
                );
                exitButton.setText("Exit");
                exitButton.setOnTouchPressed(event -> Platform.exit());
                gameOverAlert.setHeaderText("Game Over");
                gameOverAlert.initModality(Modality.APPLICATION_MODAL);
                gameOverAlert.initOwner(primaryStage);
                gameOverAlert.setOnHidden(event -> Platform.exit());
                gameOverAlert.show();
            }
        });

        primaryStage.setResizable(false);
        primaryStage.setScene(mainScene);
        primaryStage.setWidth(CannonGameCore.WIDTH);
        primaryStage.setHeight(CannonGameCore.HEIGHT);
        primaryStage.show();
    }

    private void updateShellType(ShellType shellType) {
        cannonCore.setShellType(shellType);
        cannonCore.setShellNodeSupplier(() -> {
            var node = new Circle();
            node.setFill(BLACK);
            node.setRadius(shellType.getBulletRadius());
            shellGroup.getChildren().add(node);
            return node;
        });
        cannonCore.setExplosionListener(
                new ExplosionListener(root, shellType.getExplosionRadius()));
    }

    private void setupSprites() {
        ground = new Polygon();
        ground.getPoints().addAll(cannonCore.getGroundPoints());
        ground.setFill(new ImagePattern(
                new Image(CannonGameGUI.class.getResourceAsStream("/grass.png")),
                0, 0, 200, 200, false));

        cannon = new Circle(Cannon.CANNON_RADIUS);

        tower = new Rectangle(Cannon.TOWER_LENGTH, Cannon.TOWER_WIDTH);

        target = new Circle(CannonGameCore.TARGET_RADIUS);
        target.setFill(BLUEVIOLET);
    }

    private void setupShellMenu() {
        final int ITEM_SIZE = 24;
        ObservableList<String> list =
                FXCollections.observableList(
                        Arrays.stream(ShellType.values())
                                .map(Enum::name)
                                .collect(Collectors.toList())
                );

        var listView = new ListView<>(list);
        root.getChildren().add(listView);

        listView.setOnMouseClicked(event -> updateShellType(
                ShellType.values()[listView.getSelectionModel().getSelectedIndex()]));

        listView.getSelectionModel().select(0);
        listView.setFocusTraversable(false);
        listView.setPrefHeight(ITEM_SIZE * listView.getItems().size() + 2);

        updateShellType(ShellType.SMALL);
    }

    private void handleInput() {
        boolean left = inputKeys.contains(KeyCode.LEFT);
        boolean right = inputKeys.contains(KeyCode.RIGHT);
        boolean up = inputKeys.contains(KeyCode.UP);
        boolean down = inputKeys.contains(KeyCode.DOWN);
        boolean enter = inputKeys.contains(KeyCode.ENTER);

        if (!left && !right || left && right) {
            cannonCore.setCannonMove(CannonGameCore.MoveDirection.NONE);
        } else if (left) {
            cannonCore.setCannonMove(CannonGameCore.MoveDirection.LEFT);
        } else {
            cannonCore.setCannonMove(CannonGameCore.MoveDirection.RIGHT);
        }

        if (!up && !down || up && down) {
            cannonCore.setTowerMove(CannonGameCore.MoveDirection.NONE);
        } else if (up) {
            cannonCore.setTowerMove(CannonGameCore.MoveDirection.LEFT);
        } else {
            cannonCore.setTowerMove(CannonGameCore.MoveDirection.RIGHT);
        }

        if (enter) {
            cannonCore.setCannonFire(true);
        } else {
            cannonCore.setCannonFire(false);
        }
    }
}
