package ru.hse.crossopt.Cannon;

import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Math.max;
import static java.lang.Math.min;

/**  A class that implements the cannon's actions, including moving and shooting. */
public class Cannon {
    private final static int WHEEL_SIZE = Landscape.WIDTH / 60;
    private final static int SHOOTER_LENGTH = Landscape.WIDTH / 60;
    private final static int WHEEL_STEP = WHEEL_SIZE / 4;
    private final static double ANGLE_STEP = Math.PI / 20;

    /** The size of the largest bullet the cannon can possibly shoot. Sizes are numbered from 1, default is 1. */
    public final static int LARGEST_BULLET_RADIUS = 3;

    private double angle;
    private int x;
    private @Nullable Bullet currentBullet = null;
    private int currentBulletRadius = 1;

    private @NotNull Landscape landscape;
    private @NotNull Drawer drawer;

    /** Creates a cannon on the given landscape, that uses the given Drawer to display its objects. */
    public Cannon(@NotNull Landscape landscape, @NotNull Drawer drawer) {
        this.drawer = drawer;
        this.landscape = landscape;
        x = WHEEL_SIZE * 2;
    }

    /** Moves the cannon one step to the right. */
    public void moveRight() {
        x = min(x + Cannon.WHEEL_STEP, Landscape.WIDTH);
    }

    /** Moves the cannon one step to the left. */
    public void moveLeft() {
        x = max(x - WHEEL_STEP, 0);
    }

    /** Moves the cannon's shooting tube thing more to the right. */
    public void decreaseAngle() {
        angle = max(0, angle - ANGLE_STEP);
    }

    /** Moves the cannon's shooting tube thing more to the left. */
    public void increaseAngle() {
        angle = min(Math.PI, angle + ANGLE_STEP);
    }

    /** Draws the cannon as a wheel and the shooting thing, also draws the cannon's bullet if it exists. */
    public void draw() {
        drawer.drawCircle(x, landscape.getYForX(x), WHEEL_SIZE, Color.RED);
        drawer.drawLine(x, landscape.getYForX(x) + WHEEL_SIZE / 4, getShooterX(), getShooterY(), Color.RED);
        if (currentBullet != null && currentBullet.exists()) {
            currentBullet.draw();
        }
    }

    /**
     * Shoots a bullet out of the cannon at the angle the shooting tube is pointed to.
     * As with Scorched Earth, only one bullet per cannon is possible at any point of time.
     * If there is already a bullet on the landscape, this method does nothing.
     */
    public void shoot() {
        if (currentBullet == null || !currentBullet.exists()) {
            currentBullet = new Bullet(currentBulletRadius, getShooterX(), getShooterY(), angle);
        }
    }

    /**
     * Changes the bullet radius to the given radius. Larger bullets are slower, heavier and more explosive.
     * @param radius the bullet radius, an integer from 1 to LARGEST_BULLET_RADIUS.
     */
    public void setBulletRadius(int radius) {
        checkArgument(1 <= radius && radius <= LARGEST_BULLET_RADIUS, "Bullet size out of range.");
        currentBulletRadius = radius;
    }

    private int getShooterX() {
        return x + (int)(SHOOTER_LENGTH * Math.cos(angle));
    }

    private int getShooterY() {
        return landscape.getYForX(x) + (int)(SHOOTER_LENGTH * Math.sin(angle)) + WHEEL_SIZE / 4;
    }

    /** The class for the cannon's bullets. */
    private class Bullet {
        private final Random random = new Random(System.currentTimeMillis());
        private static final int BASE_RADIUS = Landscape.WIDTH / 400;
        private static final int BASE_SPEED = 6;
        private static final double GRAVITY = 0.05;
        private static final int TICK_SIZE = 20;
        private static final int EXPLOSION_COEFFICIENT = 20;
        private static final int EXPLOSION_LENGTH = 120;

        private final int xOriginal;
        private final int yOriginal;
        private final double angle;
        private final long startTime;
        private final int bulletRadius;
        private int explosionTime = 0;

        private Bullet(int radius, int x, int y, double angle) {
            this.bulletRadius = radius;
            this.xOriginal = x;
            this.yOriginal = y;
            this.angle = angle;
            this.startTime = System.currentTimeMillis();
        }

        private int getXForTime(int time) {
            return (int)(xOriginal + getSpeed() * time * Math.cos(angle));
        }

        private int getYForTime(int time) {
            return (int)(yOriginal + getSpeed() * time * Math.sin(angle) - GRAVITY * time * time);
        }

        private boolean hasExploded() {
            if (explosionTime != 0) {
                return true;
            }
            int currentTime = getCurrentTime();
            boolean doesHit = landscape.willExplode(getXForTime(currentTime), getYForTime(currentTime), getRadius());
            if (doesHit) {
                explosionTime = currentTime;
                landscape.updateTarget(getXForTime(currentTime), getYForTime(currentTime),
                        getRadius() * EXPLOSION_COEFFICIENT);
            }
            return doesHit;
        }

        private boolean exists() {
            int currentTime = getCurrentTime();
            return (getXForTime(currentTime) > 0 && getXForTime(currentTime) < Landscape.WIDTH) &&
                    (!hasExploded() || (currentTime - explosionTime <= EXPLOSION_LENGTH));
        }

        private void draw() {
            int timeToDraw = hasExploded() ? explosionTime : getCurrentTime();
            int x = getXForTime(timeToDraw);
            int y = getYForTime(timeToDraw);
            int realRadius = getRadius();
            if (hasExploded()) {
                realRadius *= (EXPLOSION_COEFFICIENT * 0.3 + random.nextInt(EXPLOSION_COEFFICIENT));
            }
            drawer.drawCircle(x, y, realRadius, Color.RED);
        }

        private int getCurrentTime() {
            return (int)((System.currentTimeMillis() - startTime) / TICK_SIZE);
        }

        private int getRadius() {
            return BASE_RADIUS * bulletRadius;
        }

        private int getSpeed() {
            return BASE_SPEED / bulletRadius;
        }
    }
}