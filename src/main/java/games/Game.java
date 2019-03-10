package games;

import games.uiTemplates.Drawable;
import players.Player;

import java.util.LinkedList;

public interface Game {
    GameInterface getGameInterface();
    void reset();
    void update(int width, int height);
    LinkedList<Drawable> getDrawables();
    int getWidth();
    int getHeight();
    void addPlayer(Player player);
    void removePlayer(Player p);
}
