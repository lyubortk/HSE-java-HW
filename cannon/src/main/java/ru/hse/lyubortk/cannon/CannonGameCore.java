package ru.hse.lyubortk.cannon;


import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** This class represents game model */
public class CannonGameCore {
    public static final int TARGET_RADIUS = 20;
    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;
    public static final int FIRE_COOLDOWN_MILLIS = 250;

    private long lastUpdateTimeNano = 0;

    private Cannon cannon;

    private Point2D targetCoordinate;

    private MoveDirection cannonMove = MoveDirection.NONE;
    private MoveDirection towerMove = MoveDirection.NONE;
    private boolean cannonFire = false;

    private List<Point2D> ground = new ArrayList<>();
    private Polygon groundPolygon;

    private List<Shell> shells = new ArrayList<>();
    private long lastCannonFireNano = -1;
    private Supplier<Node> shellNodeSupplier;
    private Consumer<Point2D> explosionListener;
    private ShellType shellType = ShellType.SMALL;

    private Consumer<String> gameOverListener = null;

    /** Initializes model and loads map from resources */
    public CannonGameCore() {
        var mapStream = CannonGameCore.class.getResourceAsStream("/map.txt");
        var in = new Scanner(mapStream);

        double cannonX = in.nextInt();
        double cannonY = in.nextInt();

        double targetX = in.nextInt();
        double targetY = in.nextInt();
        targetCoordinate = new Point2D(targetX, targetY);

        int pointsNumber = in.nextInt();
        for (int i = 0; i < pointsNumber; i++) {
            int x = in.nextInt();
            int y = in.nextInt();
            ground.add(new Point2D(x, y));
        }

        List<Double> points = ground.stream().flatMap(a -> Stream.of(a.getX(), a.getY()))
                        .collect(Collectors.toList());
        points.addAll(Arrays.asList((double)WIDTH, (double)HEIGHT, 0.0, (double)HEIGHT));

        groundPolygon = new Polygon();
        groundPolygon.getPoints().addAll(points);

        cannon = new Cannon(new Point2D(cannonX, cannonY), ground);
    }

    /** Direction of different movements */
    public enum MoveDirection {
        LEFT(-1), RIGHT(1), NONE(0);

        private int value;

        MoveDirection(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * Updates game state.
     * @param currentTimeNano current frame timestamp
     */
    public void update(long currentTimeNano) {
        double timeDifferenceSec = (currentTimeNano - lastUpdateTimeNano) / 1e9;

        cannon.update(timeDifferenceSec, cannonMove, towerMove);

        if (cannonFire && Duration.ofNanos(currentTimeNano - lastCannonFireNano).toMillis()
                          >= FIRE_COOLDOWN_MILLIS) {
            lastCannonFireNano = currentTimeNano;
            double towerAngleRad = Math.toRadians(cannon.getTowerAngle());

            var shellCoordinate = cannon.getCoordinate().add(
                    Cannon.TOWER_LENGTH * Math.cos(towerAngleRad),
                    (-1) * Cannon.WHEEL_RADIUS - Cannon.TOWER_LENGTH * Math.sin(towerAngleRad)
            );

            double speedX = shellType.getStartSpeed() * Math.cos(towerAngleRad);
            double speedY = (-1) * shellType.getStartSpeed() * Math.sin(towerAngleRad);
            var shell = new Shell(speedX, speedY, shellCoordinate, shellType);
            shell.setExplosionListener(explosionListener);
            shell.setUINode(shellNodeSupplier.get());
            shells.add(shell);
        }

        var iterator = shells.iterator();
        while (iterator.hasNext()) {
            var shell = iterator.next();
            shell.update(timeDifferenceSec);

            if (gameOverListener == null) {
                gameOverListener = (string -> {});
            }

            if (shell.getPoint().getX() < 0 || shell.getPoint().getX() > WIDTH) {
                shell.kill(false);
                iterator.remove();
            } else if (shell.getPoint().distance(targetCoordinate.subtract(0, TARGET_RADIUS))
                       < shell.getType().getBulletRadius() + TARGET_RADIUS) {
                shell.kill(true);
                gameOverListener.accept("You won");
                iterator.remove();
            } else if (groundPolygon.contains(shell.getPoint())) {
                shell.kill(true);

                if (shell.getPoint().distance(cannon.getCoordinate())
                    < shell.getType().getExplosionRadius()) {
                    gameOverListener.accept("You killed yourself");
                } else if (shell.getPoint().distance(targetCoordinate.subtract(0, TARGET_RADIUS))
                           < shell.getType().getExplosionRadius() + TARGET_RADIUS) {
                        gameOverListener.accept("You won");
                }
                iterator.remove();
            }
        }

        lastUpdateTimeNano = currentTimeNano;
    }

    /** Sets whether player is trying to fire */
    public void setCannonFire(boolean fire) {
        cannonFire = fire;
    }

    /** Sets current cannon movement direction */
    public void setCannonMove(MoveDirection cannonMove) {
        this.cannonMove = cannonMove;
    }

    /** Sets current tower movement direction */
    public void setTowerMove(MoveDirection towerMove) {
        this.towerMove = towerMove;
    }

    /**
     * Sets listener which will be called when cannon shell touches the ground or the target.
     * Listener accepts touch point.
     */
    public void setExplosionListener(Consumer<Point2D> listener) {
        explosionListener = listener;
    }

    public void setShellType(ShellType shellType) {
        this.shellType = shellType;
    }

    /** Sets supplier which will be called for creating cannon shell visual nodes */
    public void setShellNodeSupplier(Supplier<Node> supplier) {
        shellNodeSupplier = supplier;
    }

    /**
     * Sets listener which will be called when the game ends.
     * Listener accepts additional message.
     */
    public void setGameOverListener(Consumer<String> listener) {
        gameOverListener = listener;
    }

    public Point2D getCannonCoordinate() {
        return cannon.getCoordinate();
    }

    /** Returns tower angle in degrees */
    public double getTowerAngle() {
        return cannon.getTowerAngle();
    }

    public Point2D getTargetCoordinate() {
        return targetCoordinate;
    }

    /**
     *  Get a list of points which represent ground surface.
     * The format is compatible with javafx.Polygon methods.
     */
    public List<Double> getGroundPoints() {
        return groundPolygon.getPoints();
    }
}
