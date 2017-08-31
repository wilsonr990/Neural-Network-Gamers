package games.snake;

import gameEngine.EngineLoop;
import games.uiTemplates.Nibble;
import helpers.PhysicalCircle;

import java.awt.*;
import java.util.ArrayList;

public class Snake {

    // scoring constants:
    private static final double nibblebonus = 20;
    private static final int healthbonus = 10; // Added each time snake eats
    private static final double healthdecrement = .02; // decremented each loop
    private ArrayList<PhysicalCircle> snakeSegments = new ArrayList<PhysicalCircle>(100);
    private double health;
    SnakeBody body;
    private double angle;

    public Snake(Point p) {
        health = healthbonus * 3 / 2;
        snakeSegments.add(new PhysicalCircle(p.x, p.y, EngineLoop.globalCircleRadius));
        this.angle = Math.atan2(p.y, p.x);

        body = new SnakeBody();
    }

    public boolean isAlive() {
        return health > 0;
    }

    public void update() {
        health -= healthdecrement;
    }

    public SnakeBody getBody() {
        return body;
    }

    public void eat(Food n) {
        health += n.getNutritiveValue();
        if (health > healthbonus * 3)
            health = healthbonus * 3;
        snakeSegments.add(new PhysicalCircle(snakeSegments.get(snakeSegments.size() - 1).x, snakeSegments.get(snakeSegments.size() - 1).y, EngineLoop.globalCircleRadius));
    }

    public double getHealth() {
        return health;
    }

    public int getLength() {
        return snakeSegments.size();
    }

    public ArrayList<PhysicalCircle> getSegments() {
        return snakeSegments;
    }

    public double getAngle() {
        return angle;
    }
}
