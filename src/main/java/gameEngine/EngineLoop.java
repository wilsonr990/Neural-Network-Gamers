package gameEngine;

import gameEngine.view.EngineUI;
import games.Game;
import games.snake.SnakeGame;
import genetics.DNA;
import helpers.KeyboardListener;
import players.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class EngineLoop implements Runnable {
    private EngineUI ui;
    public long clock;

    // main update frequency:
    public static final long UPDATEPERIOD = 8;
    private final KeyboardListener keyb;
    public double per = UPDATEPERIOD;

    // constants:
    public static final int globalCircleRadius = 20;
    public static final int numPlayers = 8;

    // Genetics parameter initialization:
    public static double mutationrate = .02;
    public double currentGeneration = 0;

    // world snake players initialization:
    public LinkedList<Player> players = new LinkedList<Player>();
    public LinkedList<Player> backupPlayers = new LinkedList<Player>(); // to
    // resume
    // from
    // single
    // mode

    // Best:
    public DNA bestDna = null;
    public double bestscore = 0;

    // Statistics:
    public LinkedList<Double> fitnessTimeline = new LinkedList<Double>();
    public double currentMaxFitness = 0;

    // Mode control:
    public boolean singleSnakeModeActive = false;
    public boolean displayStatisticsActive = false;
    public boolean simulationPaused = false;
    private long simulationLastMillis;
    private long statisticsLastMillis;
    private Game game;

    public EngineLoop(Game game) {
        keyb = new KeyboardListener();
        this.game = game;
        ui = new EngineUI(this);
        ui.addKeyListener(keyb);
    }

    public void createFirstGenerationOfPlayers(int n) {
        players.clear();
        for (int i = 0; i < n; i++) {
            players.add(new Player(null, game));
        }
        game.reset();
        clock = 0;
    }

    public ArrayList<Player> makeMatingpool() {
        ArrayList<Player> matingpool = new ArrayList<Player>();
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

    public void newPlayers() {
//        mutationrate = (1-currentMaxFitness/bestscore)*0.1;
        mutationrate = 10 / currentMaxFitness;
        ArrayList<Player> matingpool = makeMatingpool();
        int idx1 = (int) (Math.random() * matingpool.size());
        int idx2 = (int) (Math.random() * matingpool.size());
        DNA parentA = matingpool.get(idx1).dna;
        DNA parentB = matingpool.get(idx2).dna;
//        players.add(new Player(bestDna.crossoverBytewise(parentB, mutationrate), game.getWorld()));
        players.add(new Player(parentA.crossoverBytewise(parentB, mutationrate), game));
    }

    public void start() {
        new Thread(this).start();
    }

    public void run() {
        simulationLastMillis = System.currentTimeMillis() + 100;
        statisticsLastMillis = 0;
        while (true) {
            if (System.currentTimeMillis() - simulationLastMillis > UPDATEPERIOD) {
                synchronized (players) { // protect read
                    long currentTime = System.currentTimeMillis();
                    // Controls
                    char keyCode = (char) keyb.getKey();
                    switch (keyCode) {
                        case ' ': // space
                            if (!singleSnakeModeActive) {
                                singleSnakeModeActive = true;
                                displayStatisticsActive = false;
                                backupPlayers.clear();
                                backupPlayers.addAll(players);
                                players.clear();
                                players.add(new Player(bestDna, game));
                            }
                            break;
                        case 'A': // a = pause
                            simulationPaused = true;
                            break;
                        case 'B': // b = resume
                            simulationPaused = false;
                            break;
                        case 'C': // c = show stats
                            displayStatisticsActive = true;
                            break;
                        case 'D': // d = hide stats
                            displayStatisticsActive = false;
                            break;
                    }
                    // initilize first generation:
                    if (players.isEmpty()) {
                        createFirstGenerationOfPlayers(numPlayers);
                    }
                    // computation:
                    if (!simulationPaused) {
                        int deadCount = 0;
                        game.update(ui.getWidth(), ui.getHeight());
                        clock += EngineLoop.UPDATEPERIOD;
                        synchronized (fitnessTimeline) {
                            if (clock - statisticsLastMillis > 1000 && !singleSnakeModeActive) {
                                fitnessTimeline.addLast(currentMaxFitness);
                                currentMaxFitness = 0;
                                if (fitnessTimeline.size() >= ui.getWidth() / 2) {
                                    fitnessTimeline.removeFirst();
                                }
                                statisticsLastMillis = clock;
                            }
                        }
                        for (Player p : players) {
                            if (!p.update((SnakeGame) game)) {
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

                        } else {
                            // new players
                            for (int i = 0; i < deadCount; i++) {
                                newPlayers();
                                currentGeneration += 1 / (double) numPlayers;
                            }
                        }
                        Iterator<Player> it = players.iterator();
                        while (it.hasNext()) {
                            Player s = it.next();
                            if (s.deathFade <= 0) {
                                it.remove();
                            }
                        }
                    } else {
                        // print status:
                        players.get(0).brain((SnakeGame) game);
                    }

                    ui.repaint();
                    per = System.currentTimeMillis() - currentTime;
                    simulationLastMillis += UPDATEPERIOD;
                }
            }
        }
    }

    public Game getGame() {
        return game;
    }
}
