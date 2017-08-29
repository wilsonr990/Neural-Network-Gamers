package games.snake;

import helpers.PhysicalCircle;
import org.junit.Assert;
import org.junit.Test;
import players.Player;

import java.util.LinkedList;
import java.util.Set;

public class SnakeGameTest {
    @Test
    public void gameStartsWithFourNibblesAndNoSnakes() {
        SnakeGame game = new SnakeGame();

        // just created
        LinkedList<PhysicalCircle> nibbles = game.getNibbles();
        Set<Snake> snakes = game.getSnakes();
        Assert.assertEquals(4, nibbles.size());
        Assert.assertEquals(0, snakes.size());

        // onReset
        game.reset();
        nibbles = game.getNibbles();
        snakes = game.getSnakes();
        Assert.assertEquals(4, nibbles.size());
        Assert.assertEquals(0, snakes.size());
    }

    @Test
    public void addingPlayersToGame() {
        SnakeGame game = new SnakeGame();

        // adding a Player creates a snake in the game
        Player player = new Player(null, game);
        game.addPlayer(player);
        LinkedList<PhysicalCircle> nibbles = game.getNibbles();
        Set<Snake> snakes = game.getSnakes();
        Assert.assertEquals(4, nibbles.size());
        Assert.assertEquals(1, snakes.size());

        // onReset players are removed
        game.reset();
        nibbles = game.getNibbles();
        snakes = game.getSnakes();
        Assert.assertEquals(4, nibbles.size());
        Assert.assertEquals(0, snakes.size());
    }
}