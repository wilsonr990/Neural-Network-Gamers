package gameEngine.view;

import gameEngine.GameLoop;
import gameEngine.Snake;
import games.GameInterface;

import javax.swing.*;
import java.awt.*;

public class MainComponent extends JComponent{
    private GameLoop gameLoop;  //TODO: delete this dependency
    private GameInterface gameInterface;  //TODO: delete this dependency

    public MainComponent(GameLoop gameLoop, GameInterface gameInterface) {
        this.gameLoop = gameLoop;
        this.gameInterface = gameInterface;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Background:
        g.setColor(Color.black);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Stats:
        if (gameLoop.displayStatisticsActive) {
            g.setColor(Color.DARK_GRAY);
            g.setFont(new Font("Arial", 0, 64));
            g.drawString("t = " + Long.toString(gameLoop.clock / 1000), 20, 105);

            g.drawString("g = " + Integer.toString((int) gameLoop.currentGeneration), 20, 205);
            g.setFont(new Font("Arial", 0, 32));
            g.drawString("Mut. Prob.: " + String.format("%1$,.3f", gameLoop.mutationrate), 20, 305);
            g.drawString("Max fitness: " + Integer.toString((int) gameLoop.currentMaxFitness), 20, 355);
            g.drawString("Best fitness: " + Integer.toString((int) gameLoop.bestscore), 20, 405);

            // print timeline:
            synchronized (gameLoop.fitnessTimeline) {
                if (!gameLoop.fitnessTimeline.isEmpty()) {
                    double last = gameLoop.fitnessTimeline.getFirst();
                    int x = 0;
                    double limit = getHeight();
                    if (limit < gameLoop.bestscore)
                        limit = gameLoop.bestscore;
                    for (Double d : gameLoop.fitnessTimeline) {
                        g.setColor(new Color(0, 1, 0, .5f));
                        g.drawLine(x, (int) (getHeight() - getHeight() * last / limit), x + 2, (int) (getHeight() - getHeight() * d / limit));
                        last = d;
                        x += 2;
                    }
                }
            }
        }
        // neural net:
        if (gameLoop.singleSnakeModeActive) {
            gameLoop.snakes.getFirst().brainNet.display(g, 0, gameInterface.getWidth(), gameInterface.getHeight());
        }
        // snakes:
        synchronized (gameLoop.snakes) {
            for (Snake s : gameLoop.snakes)
                s.draw(g);
            gameInterface.draw(g);
        }
    }
}
