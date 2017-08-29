package games.snake;

import gameEngine.EngineLoop;
import helpers.PhysicalCircle;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;

public class Snake {

    // scoring constants:
    public static final double nibblebonus = 20;
    public static final int healthbonus = 10; // Added each time snake eats
    public static final double healthdecrement = .02; // decremented each loop
    public ArrayList<PhysicalCircle> snakeSegments = new ArrayList<PhysicalCircle>(100);
    public double health;
    SnakeBody body;

    public Snake(Point p) {
        health = healthbonus * 3 / 2;
        snakeSegments.add(new PhysicalCircle(p.x, p.y, EngineLoop.globalCircleRadius));
        body = new SnakeBody();
    }

    public boolean isAlive() {
        return health>0;
    }

    public void update() {
        health-=healthdecrement;
    }

    public SnakeBody getBody() {
        return body;
    }
}
