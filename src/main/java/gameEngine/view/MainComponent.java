package gameEngine.view;

import gameEngine.ModelContainer;
import games.Game;

import javax.swing.*;
import java.awt.*;

public class MainComponent extends JComponent{
    private ModelContainer engineLoop;  //TODO: delete this dependency
    private Game game;  //TODO: delete this dependency

    MainComponent(ModelContainer modelContainer, Game game) {
        this.engineLoop = modelContainer;
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
            g.setFont(new Font("Arial", Font.PLAIN, 64));
            g.drawString("t = " + engineLoop.clock / 1000, 20, 105);

            g.drawString("g = " + (int) engineLoop.currentGeneration, 20, 205);
            g.setFont(new Font("Arial", Font.PLAIN, 32));
            g.drawString("Mut. Prob.: " + String.format("%1$,.3f", ModelContainer.mutationRate), 20, 305);
            g.drawString("Max fitness: " + (int) engineLoop.currentMaxFitness, 20, 355);
            g.drawString("Best fitness: " + (int) engineLoop.bestscore, 20, 405);

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
