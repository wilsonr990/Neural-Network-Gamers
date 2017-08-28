package games.snake;

import games.Game;
import helpers.PhysicalCircle;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedList;

public class SnakeGameTest {
    @Test
    public void gameStartsWithFourNibblesAndNoSnakes() {
        SnakeGame game = new SnakeGame();

        // just created
        LinkedList<PhysicalCircle> nibbles = game.getNibbles();
        LinkedList<Snake> snakes = game.getSnakes();
        Assert.assertEquals(4, nibbles.size());
        Assert.assertEquals(0, snakes.size());

        // onReset
        game.reset();
        nibbles = game.getNibbles();
        snakes = game.getSnakes();
        Assert.assertEquals(4, nibbles.size());
        Assert.assertEquals(0, snakes.size());
    }
}