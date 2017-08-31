package games.snake;

import gameEngine.EngineLoop;
import games.uiTemplates.Drawable;
import helpers.PhysicalCircle;

import java.awt.*;
import java.util.ArrayList;

public class SnakeBody implements Drawable {
    private ArrayList<PhysicalCircle> snakeSegments = new ArrayList<PhysicalCircle>(100);
    public double deathFade = 180;
    public final boolean displayCuteEyes = false; // try it out yourself :)
    private float hue;

    public SnakeBody(Point p) {
        snakeSegments.add(new PhysicalCircle(p.x, p.y, EngineLoop.globalCircleRadius));
    }

    public void draw(Graphics g) {
        // Player body
        int alpha = (int) deathFade;
        for (int i = 0; i < snakeSegments.size(); i++) {
            Color c = new Color(Color.HSBtoRGB(hue, 1 - (float) i / ((float) snakeSegments.size() + 1f), 1));
            g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha));
            PhysicalCircle p = snakeSegments.get(i);
            g.fillOval((int) (p.x - p.rad), (int) (p.y - p.rad), (int) (2 * p.rad + 1), (int) (2 * p.rad + 1));
        }
        // Cute Eyes. A bit computationally expensive, so can be turned of
        if (displayCuteEyes) {
            PhysicalCircle p = snakeSegments.get(0); // get head
            double dist = p.rad / 2.3;
            double size = p.rad / 3.5;
            g.setColor(new Color(255, 255, 255, alpha));
            g.fillOval((int) (p.x + p.vy * dist / p.getAbsoluteVelocity() - size), (int) (p.y - p.vx * dist / p.getAbsoluteVelocity() - size),
                    (int) (size * 2 + 1), (int) (size * 2 + 1));
            g.fillOval((int) (p.x - p.vy * dist / p.getAbsoluteVelocity() - size), (int) (p.y + p.vx * dist / p.getAbsoluteVelocity() - size),
                    (int) (size * 2 + 1), (int) (size * 2 + 1));
            size = p.rad / 6;
            g.setColor(new Color(0, 0, 0, alpha));
            g.fillOval((int) (p.x + p.vy * dist / p.getAbsoluteVelocity() - size), (int) (p.y - p.vx * dist / p.getAbsoluteVelocity() - size),
                    (int) (size * 2 + 1), (int) (size * 2 + 1));
            g.fillOval((int) (p.x - p.vy * dist / p.getAbsoluteVelocity() - size), (int) (p.y + p.vx * dist / p.getAbsoluteVelocity() - size),
                    (int) (size * 2 + 1), (int) (size * 2 + 1));
        }

    }

    public void deathAnimation() {
        deathFade -= .6;
    }

    public void setColor(float color) {
        this.hue = color;
    }

    public void addSegment() {
        snakeSegments.add(new PhysicalCircle(snakeSegments.get(snakeSegments.size() - 1).x, snakeSegments.get(snakeSegments.size() - 1).y, EngineLoop.globalCircleRadius));
    }

    public ArrayList<PhysicalCircle> getSegments() {
        return snakeSegments;
    }

    public boolean isVisible() {
        return deathFade > 0;
    }
}
