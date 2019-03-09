package players;

import games.snake.Snake;
import games.snake.SnakeGame;
import games.uiTemplates.Nibble;
import genetics.DNA;
import helpers.DoubleMath;
import helpers.PhysicalCircle;
import neuralNetwork.NeuralNet;
import neuralNetwork.Stage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

public class Player {

    // Movement constants:
    private static final double maximumForwardSpeed = 5;
    private static final double maximumAngularSpeed = Math.PI / 32d;
    private static final double wallCollisionThreshold = 4;
    // view constants:
    private static final double maximumSightDistance = 300;
    private static final double fieldOfView = Math.PI * 2 / 3;
    // neural net constants:
    private static final int FOVDIVISIONS = 8;
    private static final int FIRSTSTAGESIZE = FOVDIVISIONS * 2 * 3;
    private static final int[] stageSizes = new int[]{FIRSTSTAGESIZE, 16, 16, 2};
    private static final boolean isNNSymmetric = false;

    // scoring constants:
    private static final double nibblebonus = 20;
    public static final int healthbonus = 10; // Added each time snake eats

    // misc:
    private boolean snakeInertia = false;
    // basic snake attributes:
    private ArrayList<PhysicalCircle> snakeSegments = new ArrayList<PhysicalCircle>(100);
    public DNA dna;
    public NeuralNet brainNet;
    private double age = 0;
    private double angle;
    private double score;
    private boolean isDead;
    private float hue;
    private double health;
    private boolean lastUpdate;
    public boolean ended = false;

    /**
     * Initializes a new snake with given DNA
     *
     * @param dna  if null, it generates a random new DNA
     */

    public Player(DNA dna) {
        int dnalength = NeuralNet.calcNumberOfCoeffs(stageSizes, isNNSymmetric) + 1;
        if (dna == null) {
            this.dna = new DNA(false, dnalength);
        } else {
            this.dna = dna;
        }
        // setup brain:
        brainNet = new NeuralNet(stageSizes);
        reloadFromDNA();
    }

    public void startPlaying(Snake snake) {
        snakeSegments = snake.getSegments();
        snake.setColor(hue);
        this.angle = snake.getAngle();
        score = 0;
        isDead = false;
        age = 0;
    }

    /**
     * reloads the network and the color from DNA
     */
    private void reloadFromDNA() {
        if (isNNSymmetric)
            brainNet.loadCoeffsSymmetrical(this.dna.data);
        else
            brainNet.loadCoeffs(this.dna.data);
        this.hue = (float) this.dna.data[this.dna.data.length - 1] / 256f;
    }

    /**
     * Movement, aging and collisions
     *
     * @param game  reference to the GameInterface
     * @param snake representation of player in game
     */
    public void update(SnakeGame game, Snake snake) {
        if(!snake.isVisible()){
            ended=true;
        }
        if (isDead) {
            lastUpdate = true;
            return;
        }
        age += .1;
        double slowdown = 49d / (48d + snakeSegments.size());
        PhysicalCircle head = snakeSegments.get(0);
        // calculate neural net
        double angleIncrement = brain(game);

        angle += slowdown * angleIncrement;
        angle = DoubleMath.doubleModulo(angle, Math.PI * 2);

        // collision with wall:
        if (head.x - head.rad < wallCollisionThreshold) {
            score /= 2;
            snake.doDamage(100);
            isDead = true;
        }
        if (head.x + head.rad > game.getWidth() - wallCollisionThreshold) {
            score /= 2;
            snake.doDamage(100);
            isDead = true;
        }
        if (head.y - head.rad < wallCollisionThreshold) {
            score /= 2;
            snake.doDamage(100);
            isDead = true;
        }
        if (head.y + head.rad > game.getHeight() - wallCollisionThreshold) {
            score /= 2;
            snake.doDamage(100);
            isDead = true;
        }
        // Main movement:
        head.vx = maximumForwardSpeed * slowdown * Math.cos(angle);
        head.vy = maximumForwardSpeed * slowdown * Math.sin(angle);

        PhysicalCircle previous = head;
        for (int i = 0; i < snakeSegments.size(); i++) {
            PhysicalCircle c = snakeSegments.get(i);
            if (snakeInertia) {
                c.followBouncy(previous);
            } else {
                c.followStatic(previous);
            }

            c.updatePosition();
            for (int j = 0; j < i; j++) {
                c.collideStatic(snakeSegments.get(j));
            }
            previous = c;
            if (i > 1 && head.isColliding(c, 0)) {
                isDead = true;
                score /= 2;
                snake.doDamage(100);
                break;
            }
        }
        // Check eaten nibbles:
        LinkedList<Nibble> nibblesToRemove = new LinkedList<Nibble>();
        int nibbleEatCount = 0;
        for (Nibble nibble : game.getNibbles()) {
            if (head.isColliding(nibble, -10)) {
                score += game.calcValue(nibble);
                nibblesToRemove.add(nibble);
                nibbleEatCount++;
                snake.eat(nibble);
            }
        }
        score += nibbleEatCount * nibblebonus;
        game.createNibbles(nibbleEatCount);
        game.removeNibbles(nibblesToRemove);

        health = snake.getHealth();

        if (!snake.isAlive()) {
            isDead = true;
            score /= 2;
        }
        lastUpdate = !isDead;
    }

    /**
     * Fitness function
     *
     * @return a value representing the fitness of the snake
     */
    public double getFitness() {
        return score + health / 4;
    }

    public boolean getLastUpdateResponse() {
        return lastUpdate;
    }

    /**
     * Struct for see-able objects No enum was used for simpler conversion to
     * array
     */
    public class Thing {
        double distance = maximumSightDistance;
        int type = 0;
        // Wall = 0;
        // Player = 1;
        // Nibble = 2;
    }

    /**
     * Main calculation
     *
     * @param game reference to the GameInterface for environment information
     * @return angle increment to move
     */
    public double brain(SnakeGame game) {
        // init input vector:
        Thing[] input = new Thing[FOVDIVISIONS * 2];
        for (int i = 0; i < FOVDIVISIONS * 2; i++)
            input[i] = new Thing();
        // nibbles:
        updateVisualInput(input, game.getNibbles(), 2);
        // snake:
        updateVisualInput(input, snakeSegments, 1);
        // walls:
        /*
         * (This should be replaced by a better algorithm) It is basically a
		 * brute force attempt converting the continuous border lines into many
		 * PhysicalCirle objects to apply the same algorithm When someone comes
		 * up with a better algorithm, please let me know :)
		 */
        int step = (int) (maximumSightDistance * Math.sin(fieldOfView / (FOVDIVISIONS * 1d))) / 20;
        LinkedList<PhysicalCircle> walls = new LinkedList<PhysicalCircle>();
        for (int x = 0; x < game.getWidth(); x += step) {
            walls.add(new PhysicalCircle(x, 0, 1));
            walls.add(new PhysicalCircle(x, game.getHeight(), 1));
        }
        for (int y = 0; y < game.getHeight(); y += step) {
            walls.add(new PhysicalCircle(0, y, 1));
            walls.add(new PhysicalCircle(game.getWidth(), y, 1));
        }
        updateVisualInput(input, walls, 0);

        // convert to input vector for neural net
        double[] stageA = new double[FIRSTSTAGESIZE]; // zeros initialized ;)
        if (isNNSymmetric) {
            for (int i = 0; i < FOVDIVISIONS; i++) {
                stageA[input[i].type * FOVDIVISIONS + i] = Stage.signalMultiplier * (maximumSightDistance - input[i].distance) / maximumSightDistance;
                stageA[FIRSTSTAGESIZE - 1 - (input[i + FOVDIVISIONS].type * FOVDIVISIONS + i)] = Stage.signalMultiplier
                        * (maximumSightDistance - input[i + FOVDIVISIONS].distance) / maximumSightDistance;
            }
        } else {
            for (int i = 0; i < FOVDIVISIONS; i++) {
                stageA[input[i].type * FOVDIVISIONS * 2 + i] = Stage.signalMultiplier * (maximumSightDistance - input[i].distance) / maximumSightDistance;
                stageA[input[i + FOVDIVISIONS].type * FOVDIVISIONS * 2 + FOVDIVISIONS * 2 - 1 - i] = Stage.signalMultiplier
                        * (maximumSightDistance - input[i + FOVDIVISIONS].distance) / maximumSightDistance;
            }
        }
        double[] output = brainNet.calc(stageA);
        double delta = output[0] - output[1];
        double angleIncrement = 10 * maximumAngularSpeed / Stage.signalMultiplier * delta;
        if (angleIncrement > maximumAngularSpeed)
            angleIncrement = maximumAngularSpeed;
        if (angleIncrement < -maximumAngularSpeed)
            angleIncrement = -maximumAngularSpeed;
        return angleIncrement;
    }

    /**
     * Function to update input vector Input Vector contains distance and type
     * of closest objects seen by each visual cell This function replaces those
     * by "Things" closer to the head of the snake Objects further away or
     * outside the FOV are ignored
     *
     * @param input   Array of the current things seen by the snake
     * @param objects List of objects to be checked
     * @param type    Thing-Type: 0: Wall, 1: Player, 2: Nibble
     * @return Updated input array
     */
    private void updateVisualInput(Thing[] input, Collection objects, int type) {
        PhysicalCircle head = snakeSegments.get(0);
        for (Object obj : objects) {
            PhysicalCircle n = (PhysicalCircle) obj;
            if (head == n)
                continue;
            double a = DoubleMath.signedDoubleModulo(head.getAngleTo(n) - angle, Math.PI * 2);
            double d = head.getDistanceTo(n);
            if (a >= 0 && a < fieldOfView) {
                if (d < input[(int) (a * FOVDIVISIONS / fieldOfView)].distance) {
                    input[(int) (a * FOVDIVISIONS / fieldOfView)].distance = d;
                    input[(int) (a * FOVDIVISIONS / fieldOfView)].type = type;
                }
            } else if (a <= 0 && -a < fieldOfView) {
                if (d < input[(int) (-a * FOVDIVISIONS / fieldOfView) + FOVDIVISIONS].distance) {
                    input[(int) (-a * FOVDIVISIONS / fieldOfView) + FOVDIVISIONS].distance = d;
                    input[(int) (-a * FOVDIVISIONS / fieldOfView) + FOVDIVISIONS].type = type;
                }
            }
        }
    }
}
