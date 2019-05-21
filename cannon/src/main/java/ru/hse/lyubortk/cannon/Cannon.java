package ru.hse.lyubortk.cannon;

import javafx.geometry.Point2D;

import java.util.List;

class Cannon {
    static final int MOVEMENT_SPEED = 100;
    static final int TOWER_SPEED = 100;
    static final int CANNON_RADIUS = 10;
    static final int TOWER_WIDTH = 10;
    static final int TOWER_LENGTH = 20;
    static final int WHEEL_RADIUS = 10;

    private Point2D coordinate;
    private double towerAngle = 0;
    private List<Point2D> ground;

    Cannon(Point2D startCoordinate, List<Point2D> ground) {
        coordinate = startCoordinate;
        this.ground = ground;
    }

    void update(double timeDeltaSeconds, CannonGameCore.MoveDirection cannonMove,
                CannonGameCore.MoveDirection towerMove) {
        moveCannon(timeDeltaSeconds * MOVEMENT_SPEED, cannonMove);
        towerAngle = towerAngle -timeDeltaSeconds*TOWER_SPEED*towerMove.getValue();
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

    Point2D getCoordinate() {
        return coordinate;
    }

    double getTowerAngle() {
        return towerAngle;
    }
}
