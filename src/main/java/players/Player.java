package players;

import gameEngine.EngineLoop;
import games.Game;
import games.snake.Snake;
import games.snake.SnakeGame;
import games.uiTemplates.Nibble;
import genetics.DNA;
import helpers.DoubleMath;
import helpers.PhysicalCircle;
import neuralNetwork.NeuralNet;
import neuralNetwork.Stage;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;

public class Player {

    // Movement constants:
    public static final double maximumForwardSpeed = 5;
    public static final double maximumAngularSpeed = Math.PI / 32d;
    public static final double wallCollisionThreshold = 4;
    // view constants:
    public static final double maximumSightDistance = 300;
    public static final double fieldOfView = Math.PI * 2 / 3;
    // neural net constants:
    public static final int FOVDIVISIONS = 8;
    public static final int FIRSTSTAGESIZE = FOVDIVISIONS * 2 * 3;
    public static final int stageSizes[] = new int[]{FIRSTSTAGESIZE, 16, 16, 2};
    public static final boolean isNNSymmetric = false;

    // scoring constants:
    public static final double nibblebonus = 20;
    public static final int healthbonus = 10; // Added each time snake eats

    // misc:
    public final boolean displayCuteEyes = false; // try it out yourself :)
    public final boolean snakeInertia = false;

    // basic snake attributes:
    public ArrayList<PhysicalCircle> snakeSegments = new ArrayList<PhysicalCircle>(100);
    public DNA dna;
    public NeuralNet brainNet;
    public double age = 0;
    public double angle;
    public double score;
    public boolean isDead;
    public float hue;
    public double deathFade = 180;
    public double health;
    static Random random = new Random(System.currentTimeMillis());
    private boolean lastUpdate;

    /**
     * Initializes a new snake with given DNA
     *
     * @param dna  if null, it generates a random new DNA
     * @param game
     */

    public Player(DNA dna, Game game) {

        random.setSeed(random.nextLong());
        double x = random.nextDouble() * (game.getWidth() - 2 * wallCollisionThreshold - 2 * EngineLoop.globalCircleRadius) + wallCollisionThreshold
                + EngineLoop.globalCircleRadius;
        random.setSeed(random.nextLong());
        double y = random.nextDouble() * (game.getHeight() - 2 * wallCollisionThreshold - 2 * EngineLoop.globalCircleRadius) + wallCollisionThreshold
                + EngineLoop.globalCircleRadius;

        int dnalength = NeuralNet.calcNumberOfCoeffs(stageSizes, isNNSymmetric) + 1;
        if (dna == null) {
            this.dna = new DNA(false, dnalength);
        } else {
            this.dna = dna;
        }
        snakeSegments.clear();
        for (int i = 0; i < 1; i++) {
            snakeSegments.add(new PhysicalCircle(x, y, EngineLoop.globalCircleRadius));
        }
        this.angle = Math.atan2(game.getHeight() / 2 - y, game.getWidth() / 2 - x);
        // setup brain:
        brainNet = new NeuralNet(stageSizes);
        reloadFromDNA();
        score = 0;
        deathFade = 180;
        isDead = false;
        age = 0;
    }

    /**
     * reloads the network and the color from DNA
     */
    public void reloadFromDNA() {
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
     * @param snake
     * @return true when snake died that round.
     */
    public boolean update(SnakeGame game, Snake snake) {
        if (isDead) {
            deathFade -= .6;
            lastUpdate = true;
            return true;
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
            isDead = true;
        }
        if (head.x + head.rad > game.getWidth() - wallCollisionThreshold) {
            score /= 2;
            isDead = true;
        }
        if (head.y - head.rad < wallCollisionThreshold) {
            score /= 2;
            isDead = true;
        }
        if (head.y + head.rad > game.getHeight() - wallCollisionThreshold) {
            score /= 2;
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
                break;
            }
        }
        // Check eaten nibbles:
        LinkedList<PhysicalCircle> nibblesToRemove = new LinkedList<PhysicalCircle>();
        int nibbleEatCount = 0;
        for (Nibble nibble : game.getNibbles()) {
            if (head.isColliding(nibble, -10)) {
                score += game.calcValue(nibble);
                snakeSegments.add(new PhysicalCircle(snakeSegments.get(snakeSegments.size() - 1).x, snakeSegments.get(snakeSegments.size() - 1).y, nibble.rad));
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
        return !isDead;
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
        public double distance = maximumSightDistance;
        public int type = 0;
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
        Thing input[] = new Thing[FOVDIVISIONS * 2];
        for (int i = 0; i < FOVDIVISIONS * 2; i++)
            input[i] = new Thing();
        // nibbles:
        input = updateVisualInput(input, game.getNibbles(), 2);
        // snake:
        input = updateVisualInput(input, snakeSegments, 1);
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
        input = updateVisualInput(input, walls, 0);

        // convert to input vector for neural net
        double stageA[] = new double[FIRSTSTAGESIZE]; // zeros initialized ;)
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
        double output[] = brainNet.calc(stageA);
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
    private Thing[] updateVisualInput(Thing input[], Collection objects, int type) {
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
        return input;
    }

    /**
     * Draws the snake to Graphics
     *
     * @param g Graphics object to draw to
     */
    public void draw(Graphics g) {
        // Player body
        int alpha = (int) deathFade;
        for (int i = 0; i < snakeSegments.size(); i++) {
            Color c = new Color(Color.HSBtoRGB(hue, 1 - (float) i / ((float) snakeSegments.size() + 1f), 1));
            g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha));
            PhysicalCircle p = snakeSegments.get(i);
            g.fillOval((int) (p.x - p.rad), (int) (p.y - p.rad), (int) (2 * p.rad + 1), (int) (2 * p.rad + 1));
        }
        // Cute Eyes. A bit computationally expensive, so can be turned of
        if (displayCuteEyes) {

            PhysicalCircle p = snakeSegments.get(0); // get head
            double dist = p.rad / 2.3;
            double size = p.rad / 3.5;
            g.setColor(new Color(255, 255, 255, alpha));
            g.fillOval((int) (p.x + p.vy * dist / p.getAbsoluteVelocity() - size), (int) (p.y - p.vx * dist / p.getAbsoluteVelocity() - size),
                    (int) (size * 2 + 1), (int) (size * 2 + 1));
            g.fillOval((int) (p.x - p.vy * dist / p.getAbsoluteVelocity() - size), (int) (p.y + p.vx * dist / p.getAbsoluteVelocity() - size),
                    (int) (size * 2 + 1), (int) (size * 2 + 1));
            size = p.rad / 6;
            g.setColor(new Color(0, 0, 0, alpha));
            g.fillOval((int) (p.x + p.vy * dist / p.getAbsoluteVelocity() - size), (int) (p.y - p.vx * dist / p.getAbsoluteVelocity() - size),
                    (int) (size * 2 + 1), (int) (size * 2 + 1));
            g.fillOval((int) (p.x - p.vy * dist / p.getAbsoluteVelocity() - size), (int) (p.y + p.vx * dist / p.getAbsoluteVelocity() - size),
                    (int) (size * 2 + 1), (int) (size * 2 + 1));
        }
    }
}
