package games;

import gameEngine.World;

public interface Game {
    World getWorld();
    void reset();
    void prepare();
    void update(int width, int height);
}
