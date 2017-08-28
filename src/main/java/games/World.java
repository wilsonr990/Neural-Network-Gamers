package games;

import gameEngine.EngineLoop;
import games.Game;
import helpers.PhysicalCircle;
import games.GameInterface;

import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;

public class World implements GameInterface {
    Game game;
    private LinkedList<PhysicalCircle> drawables = new LinkedList<PhysicalCircle>();

    public World(Game game) {
        this.game = game;
    }

    public void draw(Graphics g) {
        drawables = game.getDrawables();
        g.setColor(Color.RED);
        for (PhysicalCircle object : drawables) {
            g.fillOval((int) (object.x - object.rad), (int) (object.y - object.rad), (int) (2 * object.rad + 1), (int) (2 * object.rad + 1));
        }
    }
}
