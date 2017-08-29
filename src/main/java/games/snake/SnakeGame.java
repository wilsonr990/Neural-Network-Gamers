package games.snake;

import gameEngine.EngineLoop;
import games.Game;
import games.GameInterface;
import games.uiTemplates.Nibble;
import games.uiTemplates.Drawable;
import games.uiTemplates.Wall;
import helpers.PhysicalCircle;
import players.Player;

import java.awt.*;
import java.util.*;

public class SnakeGame implements Game {
    public static final int numNibbles = 4;
    private LinkedList<Nibble> nibbles = new LinkedList<Nibble>();
    private LinkedList<Wall> walls = new LinkedList<Wall>();

    public int height, width;
    // TODO: Just for now player-game interface will be here but it should be moved
    Map<Snake, Player> snakes = new HashMap<Snake, Player>();

    public SnakeGame() {
        height = 200;
        width = 300;
        prepare();
    }

    public void prepare() {
        createNibbles(numNibbles);
        createWalls();
    }

    private void createWalls() {
        walls.add(new Wall(4, 4, 4, height - 8));
        walls.add(new Wall(4, height - 8, width - 8, height - 8));
        walls.add(new Wall(width - 8, height - 8, width - 8, 4));
        walls.add(new Wall(width - 8, 4, 4, 4));
    }

    public void createNibbles(int n) {
        for (int i = 0; i < n; i++) {
            Nibble nibble = new Nibble(0, 0, EngineLoop.globalCircleRadius);
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

    public LinkedList<Nibble> getNibbles() {
        return nibbles;
    }

    public Set<Snake> getSnakes() {
        return snakes.keySet();
    }

    public void addPlayer(Player player) {
        if (!snakes.containsValue(player)) {
            Random random = new Random();
            int x = (int) (random.nextDouble() * (getWidth() - 2 * EngineLoop.globalCircleRadius) + EngineLoop.globalCircleRadius);
            random.setSeed(random.nextLong());
            int y = (int) (random.nextDouble() * (getHeight() - 2 * EngineLoop.globalCircleRadius) + EngineLoop.globalCircleRadius);
            Snake snake = new Snake(new Point(x, y));
            snakes.put(snake, player);
        }
    }

    public void removePlayer(Player p) {
        for (Snake s : snakes.keySet()) {
            if (snakes.get(s).equals(p)) {
                snakes.remove(s);
                break;
            }
        }
    }

    public void update(int w, int h) {
        this.width = w;
        this.height = h;
        walls.get(0).setLine(4, 4, 4, height - 8);
        walls.get(1).setLine(4, height - 8, width - 8, height - 8);
        walls.get(2).setLine(width - 8, height - 8, width - 8, 4);
        walls.get(3).setLine(width - 8, 4, 4, 4);

        for (Nibble n : nibbles) {
            n.updatePosition();
            n.collideWall(50, 50, w - 50, h - 50);
        }
        for (Snake s : snakes.keySet()) {
            snakes.get(s).update(this, s);
            s.update();
        }
    }

    public LinkedList<Drawable> getDrawables() {
        LinkedList<Drawable> drawables = new LinkedList<Drawable>();
        drawables.addAll(nibbles);
        drawables.addAll(walls);
        for (Snake s : snakes.keySet()) {
            drawables.add(s.getBody());
        }
        return drawables;
    }

    public GameInterface getGameInterface() {
        return new GameInterface(getDrawables());
    }

    public void reset() {
        nibbles.clear();
        snakes.clear();
        walls.clear();
        prepare();
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public LinkedList<Wall> getWalls() {
        return walls;
    }
}
