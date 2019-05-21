package ru.hse.lyubortk.cannon;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;

import java.util.function.Consumer;

/** This class represents currently active (flying) cannon shells. Used in CannonGameCore logic */
class Shell {
    private static final double GRAVITY_ACCELERATION = 300;

    private double speedX;
    private double speedY;
    private Point2D coordinate;
    ShellType shellType = null;

    private Node node = null;
    private Consumer<Point2D> explosionListener = null;

    /** Creates a shell with given initial parameters. */
    Shell(double speedX, double speedY, Point2D startCoordinate, ShellType type) {
        this.speedX = speedX;
        this.speedY = speedY;
        this.coordinate = startCoordinate;
        this.shellType = type;
    }

    /** Binds a javafx ui node to this shell. */
    void setUINode(Node node) {
        this.node = node;
    }

    /** Sets listener which will be called in order to create explosion. */
    void setExplosionListener(Consumer<Point2D> consumer) {
        explosionListener = consumer;
    }

    /**
     * Updates shell position.
     * @param timeDeltaSeconds time since last update.
     */
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

    /**
     * Kills this shell and creates explosion if specified. Tries to delete corresponding javafx
     * node from its father (father should be javafx Group)
     * @param explosion create explosion.
     */
    void kill(boolean explosion) {
        if (node != null && node.getParent() instanceof Group) {
            ((Group) node.getParent()).getChildren().remove(node);
        }

        if (explosion && explosionListener != null) {
            explosionListener.accept(coordinate);
        }
    }
}
