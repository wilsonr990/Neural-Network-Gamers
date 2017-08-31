package gameEngine.view;

import gameEngine.EngineLoop;
import games.Game;
import players.Player;

import javax.swing.*;
import java.awt.*;

public class MainComponent extends JComponent{
    private EngineLoop engineLoop;  //TODO: delete this dependency
    private Game game;  //TODO: delete this dependency

    public MainComponent(EngineLoop engineLoop, Game game) {
        this.engineLoop = engineLoop;
        this.game = game;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Background:
        g.setColor(Color.black);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Stats:
        if (engineLoop.displayStatisticsActive) {
            g.setColor(Color.DARK_GRAY);
            g.setFont(new Font("Arial", 0, 64));
            g.drawString("t = " + Long.toString(engineLoop.clock / 1000), 20, 105);

            g.drawString("g = " + Integer.toString((int) engineLoop.currentGeneration), 20, 205);
            g.setFont(new Font("Arial", 0, 32));
            g.drawString("Mut. Prob.: " + String.format("%1$,.3f", engineLoop.mutationrate), 20, 305);
            g.drawString("Max fitness: " + Integer.toString((int) engineLoop.currentMaxFitness), 20, 355);
            g.drawString("Best fitness: " + Integer.toString((int) engineLoop.bestscore), 20, 405);

            // print timeline:
            synchronized (engineLoop.fitnessTimeline) {
                if (!engineLoop.fitnessTimeline.isEmpty()) {
                    double last = engineLoop.fitnessTimeline.getFirst();
                    int x = 0;
                    double limit = getHeight();
                    if (limit < engineLoop.bestscore)
                        limit = engineLoop.bestscore;
                    for (Double d : engineLoop.fitnessTimeline) {
                        g.setColor(new Color(0, 1, 0, .5f));
                        g.drawLine(x, (int) (getHeight() - getHeight() * last / limit), x + 2, (int) (getHeight() - getHeight() * d / limit));
                        last = d;
                        x += 2;
                    }
                }
            }
        }
        // neural net:
        if (engineLoop.singleSnakeModeActive) {
            engineLoop.players.getFirst().brainNet.display(g, 0, game.getWidth(), game.getHeight());
        }
        // players:
        game.getGameInterface().draw(g);
    }
}
