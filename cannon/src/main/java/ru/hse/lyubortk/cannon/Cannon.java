package ru.hse.lyubortk.cannon;

import javafx.geometry.Point2D;

import java.util.List;

/** This class represents cannon and contains methods which manage cannon's movement and position*/
public class Cannon {
    public static final int MOVEMENT_SPEED = 100;
    public static final int TOWER_SPEED = 100;
    public static final int CANNON_RADIUS = 10;
    public static final int TOWER_WIDTH = 10;
    public static final int TOWER_LENGTH = 20;
    public static final int WHEEL_RADIUS = 10;

    private Point2D coordinate;
    private double towerAngle;
    private List<Point2D> ground;

    /**
     * This constructor accepts an initial position of cannon and a list of points which
     * represent surface (used to perform movements).
     */
    public Cannon(Point2D startCoordinate, List<Point2D> ground) {
        coordinate = startCoordinate;
        this.ground = ground;
    }

    /**
     * Updates position of cannon and its tower.
     * @param timeDeltaSeconds time since last update.
     * @param cannonMove current cannon movement direction
     * @param towerMove current tower movement direction
     */
    public void update(double timeDeltaSeconds, CannonGameCore.MoveDirection cannonMove,
                CannonGameCore.MoveDirection towerMove) {
        moveCannon(timeDeltaSeconds * MOVEMENT_SPEED, cannonMove);
        towerAngle = towerAngle - timeDeltaSeconds * TOWER_SPEED * towerMove.getValue();
        towerAngle %= 360;
    }

    private void moveCannon(double dist, CannonGameCore.MoveDirection direction) {
        if (direction == CannonGameCore.MoveDirection.NONE) {
            return;
        }

        int curSegment = 0;
        while (curSegment < ground.size() - 2
               && coordinate.getX() >= ground.get(curSegment + 1).getX()) {
            curSegment++;
        }

        while (dist > 0 && curSegment < ground.size() - 1 && curSegment >= 0) {
            Point2D next;
            if (direction == CannonGameCore.MoveDirection.RIGHT) {
                next = ground.get(curSegment + 1);
            } else {
                next = ground.get(curSegment);
            }

            double distanceToNext = coordinate.distance(next);
            if (dist > distanceToNext) {
                dist -= distanceToNext;
                curSegment += direction.getValue();
                coordinate = next;
            } else {
                double fraction = dist/distanceToNext;
                coordinate = coordinate.add(
                        (next.getX() - coordinate.getX()) * fraction,
                        (next.getY() - coordinate.getY()) * fraction
                );
                dist = 0;
            }
        }
    }

    public Point2D getCoordinate() {
        return coordinate;
    }

    public double getTowerAngle() {
        return towerAngle;
    }
}
