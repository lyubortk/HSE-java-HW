package ru.hse.lyubortk.cannon;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;

import java.time.Duration;
import java.util.function.Consumer;

class Shell {
    private static final double GRAVITY_ACCELERATION = 300;

    private double speedX;
    private double speedY;
    private Point2D coordinate;
    ShellType shellType = null;

    private Node node = null;
    private Consumer<Point2D> explosionListener = null;


    Shell(double speedX, double speedY, Point2D startCoordinate, ShellType type) {
        this.speedX = speedX;
        this.speedY = speedY;
        this.coordinate = startCoordinate;
        this.shellType = type;
    }

    void setUINode(Node node) {
        this.node = node;
    }

    void setExplosionListener(Consumer<Point2D> consumer) {
        explosionListener = consumer;
    }

    void update(double timeDeltaSeconds) {
        coordinate = coordinate.add(speedX * timeDeltaSeconds,
                speedY * timeDeltaSeconds
                + GRAVITY_ACCELERATION / 2 * timeDeltaSeconds * timeDeltaSeconds);
        speedY += GRAVITY_ACCELERATION / 2 * timeDeltaSeconds;

        if (node != null) {
            node.setTranslateX(coordinate.getX());
            node.setTranslateY(coordinate.getY());
        }
    }

    ShellType getType() {
        return shellType;
    }

    Point2D getPoint() {
        return coordinate;
    }

    void kill(boolean explosion) {
        if (node != null && node.getParent() instanceof Group) {
            ((Group) node.getParent()).getChildren().remove(node);
        }

        if (explosion && explosionListener != null) {
            explosionListener.accept(coordinate);
        }
    }
}
