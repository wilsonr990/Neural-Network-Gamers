package gameEngine;

import gameEngine.view.EngineUI;
import games.Game;
import genetics.DNA;
import helpers.KeyboardListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class GameLoop implements Runnable {
    private EngineUI ui;
    public long clock;

    // main update frequency:
    public static final long UPDATEPERIOD = 8;
    private final KeyboardListener keyb;
    public double per = UPDATEPERIOD;

    // constants:
    public static final int globalCircleRadius = 20;
    public static final int numSnakes = 8;

    // Genetics parameter initialization:
    public static double mutationrate = .02;
    public double currentGeneration = 0;

    // world snake snakes initialization:
    public LinkedList<Snake> snakes = new LinkedList<Snake>();
    public LinkedList<Snake> backupSnakes = new LinkedList<Snake>(); // to
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

    /**
     * Component with the main loop This should be separated from the graphics,
     * but I was to lazy.
     */
    public GameLoop(Game game) {
        keyb = new KeyboardListener();
        this.game = game;
        ui = new EngineUI(this);
        ui.addKeyListener(keyb);
    }

    /**
     * initializes snake array with n fresh snakes
     *
     * @param n amount of snakes
     */
    public void createFirtGenerationOfPlayers(int n) {
        snakes.clear();
        for (int i = 0; i < n; i++) {
            snakes.add(new Snake(null, game.getWorld()));
        }
        game.reset();
        clock = 0;
    }

    /**
     * Creates the mating pool out of the snake-list
     *
     * @return Mating pool as list
     */
    public ArrayList<Snake> makeMatingpool() {
        ArrayList<Snake> matingpool = new ArrayList<Snake>();
        // get maximum fitness:
        double maxscore = 0;
        for (Snake s : snakes) {
            if (s.getFitness() > maxscore) {
                maxscore = s.getFitness();
            }
        }
        // Add snakes according to fitness
        for (Snake s : snakes) {
            int amount = (int) (s.getFitness() * 100 / maxscore);
            for (int i = 0; i < amount; i++) {
                matingpool.add(s);
            }
        }
        return matingpool;
    }

    /**
     * Creates a new snake using the genetic algorithm snake adds it to the
     * snake-list
     */
    public void newSnake() {
//        mutationrate = (1-currentMaxFitness/bestscore)*0.1;
        mutationrate = 10 / currentMaxFitness;
        ArrayList<Snake> matingpool = makeMatingpool();
        int idx1 = (int) (Math.random() * matingpool.size());
        int idx2 = (int) (Math.random() * matingpool.size());
        DNA parentA = matingpool.get(idx1).dna;
        DNA parentB = matingpool.get(idx2).dna;
//        snakes.add(new Snake(bestDna.crossoverBytewise(parentB, mutationrate), game.getWorld()));
        snakes.add(new Snake(parentA.crossoverBytewise(parentB, mutationrate), game.getWorld()));
    }

    public void start() {
        new Thread(this).start();
    }

    public void run() {
        simulationLastMillis = System.currentTimeMillis() + 100;
        statisticsLastMillis = 0;
        while (true) {
            if (System.currentTimeMillis() - simulationLastMillis > UPDATEPERIOD) {
                synchronized (snakes) { // protect read
                    long currentTime = System.currentTimeMillis();
                    // Controls
                    char keyCode = (char) keyb.getKey();
                    switch (keyCode) {
                        case ' ': // space
                            if (!singleSnakeModeActive) {
                                singleSnakeModeActive = true;
                                displayStatisticsActive = false;
                                backupSnakes.clear();
                                backupSnakes.addAll(snakes);
                                snakes.clear();
                                snakes.add(new Snake(bestDna, game.getWorld()));
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
                    if (snakes.isEmpty()) {
                        createFirtGenerationOfPlayers(numSnakes);
                        game.prepare();
                    }
                    // computation:
                    if (!simulationPaused) {
                        int deadCount = 0;
                        game.update(ui.getWidth(), ui.getHeight());
                        clock += GameLoop.UPDATEPERIOD;
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
                        for (Snake s : snakes) {
                            if (!s.update(game.getWorld())) {
                                deadCount++;
                            }
                            if (s.getFitness() > currentMaxFitness)
                                currentMaxFitness = s.getFitness();
                            if (s.getFitness() > bestscore) {
                                bestscore = s.getFitness();
                                bestDna = s.dna;
                                try {
                                    bestDna.saveToFile((int) bestscore);
                                } catch (IOException e) {
                                    System.out.println("Failed saving adn to file!");
                                }
                            }
                        }
                        if (deadCount > 0 && singleSnakeModeActive) {
                            singleSnakeModeActive = false;
                            snakes.clear();
                            snakes.addAll(backupSnakes);

                        } else {
                            // new snakes
                            for (int i = 0; i < deadCount; i++) {
                                newSnake();
                                currentGeneration += 1 / (double) numSnakes;
                            }
                        }
                        Iterator<Snake> it = snakes.iterator();
                        while (it.hasNext()) {
                            Snake s = it.next();
                            if (s.deathFade <= 0) {
                                it.remove();
                            }
                        }
                    } else {
                        // print status:
                        snakes.get(0).brain(game.getWorld());
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
