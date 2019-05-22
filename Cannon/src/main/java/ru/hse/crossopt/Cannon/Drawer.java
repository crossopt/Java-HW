package ru.hse.crossopt.Cannon;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Class that draws the necessary objects for the game.
 * Also responsible for converting coordinates from Landscape units to something recognized by JavaFX.
 * Not related to furniture.
 */
public class Drawer {
    private static final int TRIANGLE_EDGES = 3;
    private final @NotNull GraphicsContext graphicsContext;

    /** Creates a Drawer that will draw on the given GraphicsContext. */
    public Drawer(@NotNull GraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;
    }

    /**
     * Draws a circle with the given parameters.
     * @param xCenter the circle's center's X-coordinate.
     * @param yCenter the circle's center's Y-coordinate.
     * @param radius the circle's radius.
     * @param color the circle's color.
     */
    public void drawCircle(int xCenter, int yCenter, int radius, @NotNull Color color) {
        checkArgument(radius > 0, "Radius should be positive.");
        setColor(color);
        double newRadius = convertLength(radius);
        double newX = convertXPoint(xCenter) - newRadius / 2;
        double newY = convertYPoint(yCenter) - newRadius / 2;
        graphicsContext.fillOval(newX, newY, newRadius, newRadius);
    }

    /**
     * Draws a triangle with the given parameters.
     * @param xCoordinates an array with the triangle's points' X-coordinates, in order.
     * @param yCoordinates an array with the triangle's points' Y-coordinates, in order.
     * @param color the triangle's color.
     */
    public void drawTriangle(@NotNull int[] xCoordinates, @NotNull int[] yCoordinates, @NotNull Color color) {
        checkArgument(xCoordinates.length == TRIANGLE_EDGES && yCoordinates.length == TRIANGLE_EDGES,
                "Triangle should have 3 angles");
        setColor(color);
        double[] newX = Arrays.stream(xCoordinates).mapToDouble(this::convertXPoint).toArray();
        double[] newY = Arrays.stream(yCoordinates).mapToDouble(this::convertYPoint).toArray();
        graphicsContext.fillPolygon(newX, newY, TRIANGLE_EDGES);
    }

    /**
     * Draws the line with the given parameters.
     * @param x0 the X-coordinate of the line's start.
     * @param y0 the Y-coordinate of the line's start.
     * @param x1 the X-coordinate of the line's end.
     * @param y1 the Y-coordinate of the line's end.
     * @param color the line's color.
     */
    public void drawLine(int x0, int y0, int x1, int y1, @NotNull Color color) {
        setColor(color);
        graphicsContext.strokeLine(convertXPoint(x0), convertYPoint(y0), convertXPoint(x1), convertYPoint(y1));
    }

    /** Clears everything already drawn. */
    public void clear() {
        graphicsContext.clearRect(0, 0, graphicsContext.getCanvas().getWidth(), graphicsContext.getCanvas().getHeight());
    }

    /**
     * Writes the text with the given parameters.
     * @param text the text.
     * @param x the X-coordinate where the text should be written.
     * @param y the Y-coordinate where the text should be written.
     * @param color the text's color.
     */
    public void write(@NotNull String text, int x, int y, @NotNull Color color) {
        setColor(color);
        graphicsContext.strokeText(text, convertXPoint(x), convertYPoint(y));
    }

    private double convertXPoint(int x) {
        return x * widthCoefficient();
    }

    private double convertYPoint(int y) {
        return (Landscape.HEIGHT - y) * heightCoefficient();
    }

    private double convertLength(int length) {
        return length * widthCoefficient();
    }

    private void setColor(@NotNull Color color) {
        graphicsContext.setFill(color);
        graphicsContext.setStroke(color);
    }

    private double widthCoefficient() {
        return graphicsContext.getCanvas().getWidth() / Landscape.WIDTH;
    }

    private double heightCoefficient() {
        return graphicsContext.getCanvas().getHeight() / Landscape.HEIGHT;
    }
}
