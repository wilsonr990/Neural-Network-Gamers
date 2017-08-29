package games.snake;

import gameEngine.EngineLoop;
import games.Game;
import helpers.PhysicalCircle;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

import static players.Player.wallCollisionThreshold;

public class Snake {

    // scoring constants:
    public static final double nibblebonus = 20;
    public static final int healthbonus = 10; // Added each time snake eats
    public static final double healthdecrement = .02; // decremented each loop
    public ArrayList<PhysicalCircle> snakeSegments = new ArrayList<PhysicalCircle>(100);
    public double health;

    public Snake(Point p) {
        health = healthbonus * 3 / 2;
        snakeSegments.add(new PhysicalCircle(p.x, p.y, EngineLoop.globalCircleRadius));
    }

    public boolean isAlive() {
        return health>0;
    }

    public void update() {
        health-=healthdecrement;
    }
}
