package gameEngine;

import gameEngine.commands.PauseCommand;
import gameEngine.commands.ShowCommand;
import gameEngine.commands.TestCommand;
import games.Game;
import games.snake.SnakeGame;
import genetics.DNA;
import players.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class ModelContainer implements PauseCommand.Pausable, ShowCommand.Showable, TestCommand.Testeable {
    public long clock;

    // main update frequency:
    static final long UPDATE_PERIOD = 8;
    double per = UPDATE_PERIOD;

    // constants:
    public static final int globalCircleRadius = 20;
    private static final int numPlayers = 8;

    // Genetics parameter initialization:
    public static double mutationRate = .02;
    public double currentGeneration = 0;

    // GameInterface snake players initialization:
    public final LinkedList<Player> players = new LinkedList<>();
    private LinkedList<Player> backupPlayers = new LinkedList<>(); // to
    // resume
    // from
    // single
    // mode

    // Best:
    private DNA bestDna = null;
    public double bestscore = 0;

    // Statistics:
    public final LinkedList<Double> fitnessTimeline;
    public double currentMaxFitness = 0;

    // Mode control:
    public boolean singleSnakeModeActive = false;
    public boolean displayStatisticsActive = false;
    private boolean simulationPaused = false;
    long simulationLastMillis;
    long statisticsLastMillis;
    private Game game;

    public ModelContainer(Game game) {
        this.game = game;
        fitnessTimeline = new LinkedList<>();
    }

    private void createFirstGenerationOfPlayers() {
        players.clear();
        game.reset();
        for (int i = 0; i < numPlayers; i++) {
            Player p = new Player(null);
            players.add(p);
            game.addPlayer(p);
        }
        clock = 0;
    }

    private ArrayList<Player> makeMatingpool() {
        ArrayList<Player> matingpool = new ArrayList<>();
        // get maximum fitness:
        double maxscore = 0;
        for (Player s : players) {
            if (s.getFitness() > maxscore) {
                maxscore = s.getFitness();
            }
        }
        // Add players according to fitness
        for (Player s : players) {
            int amount = (int) (s.getFitness() * 100 / maxscore);
            for (int i = 0; i < amount; i++) {
                matingpool.add(s);
            }
        }
        return matingpool;
    }

    private void newPlayers() {
//        mutationRate = (1-currentMaxFitness/bestscore)*0.1;
        mutationRate = 10 / currentMaxFitness;
        ArrayList<Player> matingpool = makeMatingpool();
        int idx1 = (int) (Math.random() * matingpool.size());
        int idx2 = (int) (Math.random() * matingpool.size());
        DNA parentA = matingpool.get(idx1).dna;
        DNA parentB = matingpool.get(idx2).dna;

        Player p;
        double random0 = Math.random();
        if (random0 < 0.2) {
            double random = Math.random();
            if (random < 0.3)
                p = new Player(parentA.crossoverBytewise(bestDna, mutationRate));
            else if (random < 0.6)
                p = new Player(parentA.crossover(bestDna, mutationRate));
            else if (random < 0.9)
                p = new Player(parentA.crossover(bestDna, mutationRate));
            else
                p = new Player(bestDna);
        } else {
            double random = Math.random();
            if (random < 0.3)
                p = new Player(parentA.crossoverBytewise(parentB, mutationRate));
            else if (random < 0.6)
                p = new Player(parentA.crossover(parentB, mutationRate));
            else if (random < 0.9)
                p = new Player(parentA.crossover(parentB, mutationRate));
            else
                p = new Player(parentA.crossover(new Player(null).dna, mutationRate));
        }
        players.add(p);
        game.addPlayer(p);
    }

    public Game getGame() {
        return game;
    }

    private void enableSnakeMode() {
        if (!singleSnakeModeActive) {
            singleSnakeModeActive = true;
            displayStatisticsActive = false;
            backupPlayers.clear();
            backupPlayers.addAll(players);
            players.clear();
            Player p = new Player(bestDna);
            players.add(p);
            game.reset();
            game.addPlayer(p);
        }
    }

    void runIteration(Integer w, Integer h) {
        // initilize first generation:
        if (players.isEmpty()) {
            createFirstGenerationOfPlayers();
        }
        if (!simulationPaused) {
            int deadCount = 0;
            clock += ModelContainer.UPDATE_PERIOD;
            synchronized (fitnessTimeline) {
                if (clock - statisticsLastMillis > 1000 && !singleSnakeModeActive) {
                    fitnessTimeline.addLast(currentMaxFitness);
                    currentMaxFitness = 0;
                    if (fitnessTimeline.size() >= game.getWidth() / 2) {
                        fitnessTimeline.removeFirst();
                    }
                    statisticsLastMillis = clock;
                }
            }
            game.update(w, h);
            for (Player p : players) {
                if (!p.getLastUpdateResponse()) {
                    deadCount++;
                }
                if (p.getFitness() > currentMaxFitness)
                    currentMaxFitness = p.getFitness();
                if (p.getFitness() > bestscore) {
                    bestscore = p.getFitness();
                    bestDna = p.dna;
                    try {
                        bestDna.saveToFile((int) bestscore);
                    } catch (IOException e) {
                        System.out.println("Failed saving adn to file!");
                    }
                }
            }
            if (deadCount > 0 && singleSnakeModeActive) {
                singleSnakeModeActive = false;
                players.clear();
                players.addAll(backupPlayers);
                game.reset();
                for (Player p : backupPlayers)
                    game.addPlayer(p);

            } else {
                // new players
                for (int i = 0; i < deadCount; i++) {
                    newPlayers();
                    currentGeneration += 1 / (double) ModelContainer.numPlayers;
                }
            }
            Iterator<Player> it = players.iterator();
            while (it.hasNext()) {
                Player s = it.next();
                if (s.ended) {
                    game.removePlayer(s);
                    it.remove();
                }
            }
        } else {
            // print status:
            players.get(0).brain((SnakeGame) game);
        }
    }

    @Override
    public void pause() {
        simulationPaused = true;
    }

    @Override
    public void unPause() {
        simulationPaused = false;
    }

    @Override
    public void show() {
        displayStatisticsActive = true;
    }

    @Override
    public void unShow() {
        displayStatisticsActive = false;
    }

    @Override
    public void test() {
        enableSnakeMode();
    }
}
