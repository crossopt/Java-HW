package ru.hse.crossopt.Cannon;

import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Random;

/**
 * The class that stores and draws a landscape (the static elements) for the Cannon game.
 * The landscape contains several mountains, a target for the cannon and instructions.
 * Both the mountains and the target are generated randomly.
 */
public class Landscape {
    /** The screen width in Landscape units. */
    public static final int WIDTH = 1200;
    /** The screen height in Landscape units. */
    public static final int HEIGHT = 800;

    private final @NotNull Drawer drawer;
    private final @NotNull ArrayList<Mountain> mountains;
    private final @NotNull Target target;

    /** Creates a random landscape that uses the given Drawer to display its objects. */
    public Landscape(@NotNull Drawer drawer) {
        this.drawer = drawer;
        Random random = new Random(System.currentTimeMillis());
        mountains = new ArrayList<>();
        int currentX = 0;
        int xCoefficient = WIDTH / 5;
        int yCoefficient = HEIGHT / 5;
        while (currentX + xCoefficient < WIDTH) {
            int y = (int)((1.5 + random.nextDouble()) * yCoefficient);
            int nextX = (int)(currentX + (0.5 + random.nextDouble()) * xCoefficient);
            mountains.add(new Mountain(currentX, y, nextX));
            currentX = nextX;
        }
        mountains.add(new Mountain(currentX, (int)((1.5 + random.nextDouble()) * yCoefficient), WIDTH));
        target = new Target();
    }

    /**
     * Returns the Y-point that corresponds to the given X-point on the landscape.
     * If the X-point is not on any mountain, the corresponding Y-point is 0.
     **/
    public int getYForX(int x) {
        for (var mountain : mountains) {
            if (mountain.isPointInMountain(x)) {
                return mountain.getYForX(x);
            }
        }
        return 0;
    }

    /** Draws all of the objects on the landscape. */
    public void draw() {
        writeInstructions();
        for (var mountain : mountains) {
            mountain.draw();
        }
        if (!wasTargetShot()) {
            target.draw();
        }
    }

    private void writeInstructions() {
        drawer.write( "Welcome to the Cannon Game!\n" +
                "Press:\n" +
                "UP/DOWN to change the shooting thing's angle.\n" +
                "LEFT/RIGHT to move cannon.\n" +
                "1, 2, 3 to change bullet size.\n" +
                "ENTER to shoot.\n" +
                "The game ends when you explode the target.",
                Landscape.WIDTH / 10, Landscape.HEIGHT * 9 / 10, Color.BLACK);
    }

    /**
     * Checks whether a bullet with the given radius will hit something and explode in the coordinates (X, Y).
     * @param x the bullet's X-coordinate.
     * @param y the bullet's Y-coordinate.
     * @param radius the bullet's radius.
     * @return true if the bullet has collided with a mountain, false otherwise.
     */
    public boolean willExplode(int x, int y, int radius) {
        for (var mountain : mountains) {
            if (mountain.isPointInMountain(x)) {
                return mountain.isHitBy(x, y, radius);
            }
        }
        return false;
    }

    /** Updates the target's status by checking whether the explosion in (X, Y) with given radius has touched it. */
    public void updateTarget(int x, int y, int explosionRadius) {
        target.updateTarget(x, y, explosionRadius);
    }

    /** Returns true if target has exploded already, or false otherwise. */
    public boolean wasTargetShot() {
        return !target.isAlive;
    }

    /** A class for the mountains on the landscape. */
    private class Mountain {
        private int start;
        private int end;
        private int peak;

        private Mountain(int start, int peak, int end) {
            this.start = start;
            this.end = end;
            this.peak = peak;
        }

        private void draw() {
            drawer.drawTriangle(new int[]{start, (start + end) / 2, end}, new int[]{0, peak, 0}, Color.BLACK);
        }

        private boolean isPointInMountain(int x) {
            return start <= x && x <= end;
        }

        private int getYForX(int x) {
            if (2 * x < end + start) {
                return (peak * 2 * (x - start)) / (end - start);
            } else {
                return (peak * 2 * (end - x)) / (end - start);
            }
        }

        private boolean isHitBy(int x, int y, int radius) {
            return getYForX(x) + 3 * radius >= y || getYForX(x + radius) + 2 * radius >= y;
        }
    }

    /** Class for the target on the landscape. */
    private class Target {
        private static final int SIZE = Landscape.WIDTH / 80;
        private final int x;
        private final int y;
        private boolean isAlive = true;

        private Target() {
            x = new Random(System.currentTimeMillis()).nextInt(Landscape.WIDTH);
            y = getYForX(x);
        }

        private void draw() {
            drawer.drawCircle(x, y, SIZE, Color.MAROON);
        }

        private void updateTarget(int x, int y, int explosionRadius) {
            if ((x - this.x) * (x - this.x) + (y - this.y) * (y - this.y) <= explosionRadius * explosionRadius) {
                isAlive = false;
            }
        }
    }
}
