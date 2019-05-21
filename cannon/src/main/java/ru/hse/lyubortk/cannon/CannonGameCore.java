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

public class CannonGameCore {
    public static final int TARGET_RADIUS = 20;
    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;

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

    public void update(long currentTimeNano) {
        double timeDifferenceSec = (currentTimeNano - lastUpdateTimeNano) / 1e9;

        cannon.update(timeDifferenceSec, cannonMove, towerMove);

        if (cannonFire && Duration.ofNanos(currentTimeNano - lastCannonFireNano).toSeconds() > 1) {
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

            if (shell.getPoint().getX() < 0 || shell.getPoint().getX() > WIDTH) {
                shell.kill(false);
                iterator.remove();
            } else if (shell.getPoint().distance(targetCoordinate.subtract(0, TARGET_RADIUS))
                       < shell.getType().getBulletRadius() + TARGET_RADIUS) {
                shell.kill(true);
                if (gameOverListener != null) {
                    gameOverListener.accept("You won");
                }
                iterator.remove();
            } else if (groundPolygon.contains(shell.getPoint())) {
                shell.kill(true);

                if (shell.getPoint().distance(cannon.getCoordinate())
                    < shell.getType().getExplosionRadius()) {
                    if (gameOverListener != null) {
                        gameOverListener.accept("You killed yourself");
                    }
                } else if (shell.getPoint().distance(targetCoordinate.subtract(0, TARGET_RADIUS))
                           < shell.getType().getExplosionRadius() + TARGET_RADIUS) {
                    if (gameOverListener != null) {
                        gameOverListener.accept("You won");
                    }
                }
                iterator.remove();
            }
        }

        lastUpdateTimeNano = currentTimeNano;
    }

    public void setCannonFire(boolean fire) {
        cannonFire = fire;
    }

    public void setCannonMove(MoveDirection cannonMove) {
        this.cannonMove = cannonMove;
    }

    public void setTowerMove(MoveDirection towerMove) {
        this.towerMove = towerMove;
    }

    public void setExplosionListener(Consumer<Point2D> listener) {
        explosionListener = listener;
    }

    public void setShellType(ShellType shellType) {
        this.shellType = shellType;
    }

    public void setShellNodeSupplier(Supplier<Node> supplier) {
        shellNodeSupplier = supplier;
    }

    public void setGameOverListener(Consumer<String> listener) {
        gameOverListener = listener;
    }

    public Point2D getCannonCoordinate() {
        return cannon.getCoordinate();
    }

    public double getTowerAngle() {
        return cannon.getTowerAngle();
    }

    public Point2D getTargetCoordinate() {
        return targetCoordinate;
    }

    public List<Double> getGroundPoints() {
        return groundPolygon.getPoints();
    }
}
