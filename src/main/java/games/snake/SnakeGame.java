package games.snake;

import gameEngine.EngineLoop;
import games.Game;
import games.World;
import helpers.PhysicalCircle;

import java.util.LinkedList;

public class SnakeGame implements Game {
    public World world = new World(this);
    public static final int numNibbles = 4;
    public LinkedList<Snake> snakes = new LinkedList<Snake>();
    private LinkedList<PhysicalCircle> nibbles = new LinkedList<PhysicalCircle>();
    public int height, width;

    public SnakeGame() {
        height = 200;
        width = 300;
        prepare();
    }

    public void prepare() {
        newNibble(numNibbles);
    }

    public void newNibble(int n) {
        for (int i = 0; i < n; i++) {
            PhysicalCircle nibble = new PhysicalCircle(0, 0, EngineLoop.globalCircleRadius);
            nibble.x = Math.random() * (width - 2 * nibble.rad) + nibble.rad;
            nibble.y = Math.random() * (height - 2 * nibble.rad) + nibble.rad;

            nibble.vx = 2 * (Math.random() - .5);
            nibble.vy = 2 * (Math.random() - .5);
            nibble.t = 0;
            nibbles.add(nibble);
        }
    }

    public void removeNibbles(LinkedList<PhysicalCircle> rem) {
        for (PhysicalCircle p : rem) {
            nibbles.remove(p);
        }
    }

    public int calcValue(PhysicalCircle p) {
        return (int) (5 + (8d * Math.min(Math.exp(-(double) (p.t - 800) / 2000d), 1)));
    }

    public void update(int w, int h) {
        this.width = w;
        this.height = h;
        for (PhysicalCircle p : nibbles) {
            p.updatePosition();
            p.collideWall(50, 50, w - 50, h - 50);
        }
    }

    public LinkedList<PhysicalCircle> getDrawables() {
        LinkedList<PhysicalCircle> drawables = new LinkedList<PhysicalCircle>();
        drawables.addAll(nibbles);
        return drawables;
    }

    public World getWorld() {
        return world;
    }

    public void reset() {
        nibbles.clear();
        prepare();
    }

    public LinkedList<PhysicalCircle> getNibbles() {
        return nibbles;
    }

    public LinkedList<Snake> getSnakes() {
        return snakes;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }
}
