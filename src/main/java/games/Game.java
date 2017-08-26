package games;

import games.snake.World;

public interface Game {
    World getWorld();
    void reset();
    void prepare();
    void update(int width, int height);
}
