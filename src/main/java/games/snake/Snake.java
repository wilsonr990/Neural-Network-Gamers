package games.snake;

import helpers.PhysicalCircle;

import java.awt.*;
import java.util.ArrayList;

public class Snake {
    // scoring constants:
    private static final int healthbonus = 10; // Added each time snake eats
    private static final double healthdecrement = .02; // decremented each loop
    private double health;
    private double angle;
    SnakeBody body;

    public Snake(Point p) {
        health = healthbonus * 3 / 2;
        this.angle = Math.atan2(p.y, p.x);

        body = new SnakeBody(p);
    }

    public boolean isAlive() {
        return health > 0;
    }

    public void update() {
        health -= healthdecrement;
        if (!isAlive()) {
            body.deathAnimation();
        }
    }

    public SnakeBody getBody() {
        return body;
    }

    public void eat(Food n) {
        health += n.getNutritiveValue();
//        if (health > healthbonus * 3)
//            health = healthbonus * 3;
        body.addSegment();
    }

    public double getHealth() {
        return health;
    }

    public int getLength() {
        return body.getSegments().size();
    }

    public ArrayList<PhysicalCircle> getSegments() {
        return body.getSegments();
    }

    public double getAngle() {
        return angle;
    }

    public void setColor(float color) {
        body.setColor(color);
    }

    public boolean isVisible() {
        return body.isVisible();
    }

    @Deprecated
    public void doDamage(int i) {
        health -= i;
    }
}
