package ru.hse.lyubortk.cannon;

import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

import java.time.Duration;
import java.util.Random;
import java.util.function.Consumer;

/** Implementation of consumer that could be used in CannonGameCore to draw shell explosions */
public class ExplosionListener implements Consumer<Point2D> {
    private static final int SIZE = 5;
    private static final Paint COLOR = Color.hsb(20, 1, 1);
    private static final long DURATION_MILLIS = 500;
    private static final int DENSITY = 400;

    private final Group group;
    private final double radius;
    private final Random random = new Random();

    /** Creates listener which will draw explosions of given radius in given javafx group */
    public ExplosionListener(Group group, double radius) {
        this.group = group;
        this.radius = radius + 10;
    }

    /** Draws an explosion in given point */
    @Override
    public void accept(Point2D point2D) {
        final Rectangle[] rectangles = new Rectangle[DENSITY];
        final long[] delaysMillis = new long[DENSITY];
        final double[] angles = new double[DENSITY];

        for (int i = 0; i < DENSITY; i++) {
            rectangles[i] = new Rectangle(SIZE, SIZE, COLOR);
            rectangles[i].setOpacity(0);
            delaysMillis[i] = (long) (random.nextDouble() / 2 * DURATION_MILLIS);
            angles[i] = 2 * Math.PI * random.nextDouble();
        }

        Group currentRectangles = new Group();
        currentRectangles.getChildren().addAll(rectangles);
        group.getChildren().add(currentRectangles);

        new AnimationTimer() {
            long startTimeNanos = -1;

            @Override
            public void handle(long nowNanos) {
                if (startTimeNanos == -1) {
                    startTimeNanos = nowNanos;
                }

                long timeDeltaMillis = Duration.ofNanos(nowNanos - startTimeNanos).toMillis();
                double progress = timeDeltaMillis / (double) DURATION_MILLIS;

                if (progress >= 1) {
                    stop();
                    group.getChildren().remove(currentRectangles);
                    return;
                }

                for (int i = 0; i < DENSITY; i++) {
                    Rectangle rectangle = rectangles[i];
                    double angle = angles[i];
                    long currentTimeMillis = (timeDeltaMillis - delaysMillis[i]);
                    double distance = currentTimeMillis * radius / DURATION_MILLIS;

                    if (distance < 0) {
                        rectangle.setOpacity(0);
                    } else {
                        rectangle.setOpacity((1 - progress));
                        rectangle.setTranslateX(Math.cos(angle) * distance + point2D.getX());
                        rectangle.setTranslateY(Math.sin(angle) * distance + point2D.getY());
                    }
                }
            }
        }.start();
    }
}
