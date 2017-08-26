package games.snake;

import games.Game;

public class SnakeGame implements Game {
    public World world = new World();
    public static final int numNibbles = 4;

    public SnakeGame() {
        world.height = 200;
        world.width = 300;
    }

    public World getWorld() {
        return world;
    }

    public void reset() {
        world.reset();
    }

    public void prepare() {
        world.newNibble(numNibbles);
    }

    public void update(int width, int height) {
        world.update(width, height);
    }
}
