package ru.hse.lyubortk.cannon;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/** This class represents currently active (flying) cannon shells. Used in CannonGameCore logic */
public class Shell {
    private static final double GRAVITY_ACCELERATION = 300;

    private double speedX;
    private double speedY;
    private Point2D coordinate;
    private ShellType shellType;

    private Node node;
    private Consumer<Point2D> explosionListener;

    /** Creates a shell with given initial parameters. */
    public Shell(double speedX, double speedY,
                 @NotNull Point2D startCoordinate, @NotNull ShellType type) {
        this.speedX = speedX;
        this.speedY = speedY;
        this.coordinate = startCoordinate;
        this.shellType = type;
    }

    /** Binds a javafx ui node to this shell. */
    public void setUINode(@Nullable Node node) {
        this.node = node;
    }

    /** Sets listener which will be called in order to create explosion. */
    public void setExplosionListener(@Nullable Consumer<Point2D> consumer) {
        explosionListener = consumer;
    }

    /**
     * Updates shell position.
     *
     * @param timeDeltaSeconds time since last update.
     */
    public void update(double timeDeltaSeconds) {
        coordinate = coordinate.add(speedX * timeDeltaSeconds,
                speedY * timeDeltaSeconds
                + GRAVITY_ACCELERATION / 2 * timeDeltaSeconds * timeDeltaSeconds);
        speedY += GRAVITY_ACCELERATION / 2 * timeDeltaSeconds;

        if (node != null) {
            node.setTranslateX(coordinate.getX());
            node.setTranslateY(coordinate.getY());
        }
    }

    public @NotNull ShellType getType() {
        return shellType;
    }

    public @NotNull Point2D getPoint() {
        return coordinate;
    }

    /**
     * Kills this shell and creates explosion if specified. Tries to delete corresponding javafx
     * node from its father (father should be javafx Group)
     *
     * @param explosion create explosion.
     */
    public void kill(boolean explosion) {
        if (node != null && node.getParent() instanceof Group) {
            ((Group) node.getParent()).getChildren().remove(node);
        }

        if (explosion && explosionListener != null) {
            explosionListener.accept(coordinate);
        }
    }
}
