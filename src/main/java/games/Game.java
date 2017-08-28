package games;

import helpers.PhysicalCircle;
import players.Player;

import java.util.LinkedList;

public interface Game {
    World getWorld();
    void reset();
    void update(int width, int height);
    LinkedList<PhysicalCircle> getDrawables();
    double getWidth();
    double getHeight();
    void addPlayer(Player player);
}
