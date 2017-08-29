package games.snake;

import gameEngine.EngineLoop;
import games.Game;
import games.GameInterface;
import games.GameInterface;
import helpers.PhysicalCircle;
import players.Player;

import java.awt.*;
import java.util.*;

public class SnakeGame implements Game {
    public GameInterface GameInterface = new GameInterface(this);
    public static final int numNibbles = 4;
    private LinkedList<PhysicalCircle> nibbles = new LinkedList<PhysicalCircle>();
    public int height, width;

    // TODO: Just for now player-game interface will be here but it should be moved
    Map<Snake, Player> snakes = new HashMap<Snake, Player>();

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

    public LinkedList<PhysicalCircle> getNibbles() {
        return nibbles;
    }

    public Set<Snake> getSnakes() {
        return snakes.keySet();
    }

    public void addPlayer(Player player) {
        Random random = new Random();
        int x = (int) (random.nextDouble() * (getWidth() - 2 * EngineLoop.globalCircleRadius) + EngineLoop.globalCircleRadius);
        random.setSeed(random.nextLong());
        int y = (int) (random.nextDouble() * (getHeight() - 2 * EngineLoop.globalCircleRadius) + EngineLoop.globalCircleRadius);
        Snake snake = new Snake(new Point(x, y));
        snakes.put(snake, player);
    }

    public void update(int w, int h) {
        this.width = w;
        this.height = h;
        for (PhysicalCircle p : nibbles) {
            p.updatePosition();
            p.collideWall(50, 50, w - 50, h - 50);
        }
        for (Snake s : snakes.keySet()) {
            s.update();
            snakes.get(s).update(this);
        }
    }

    public LinkedList<PhysicalCircle> getDrawables() {
        LinkedList<PhysicalCircle> drawables = new LinkedList<PhysicalCircle>();
        drawables.addAll(nibbles);
        return drawables;
    }

    public GameInterface getGameInterface() {
        return GameInterface;
    }

    public void reset() {
        nibbles.clear();
        snakes.clear();
        snakes.clear();
        prepare();
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }
}
