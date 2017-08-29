package games.snake;

import games.uiTemplates.Nibble;
import games.uiTemplates.Wall;
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
        LinkedList<Nibble> nibbles = game.getNibbles();
        Set<Snake> snakes = game.getSnakes();
        LinkedList<Wall> walls = game.getWalls();
        Assert.assertEquals(4, nibbles.size());
        Assert.assertEquals(4, walls.size());
        Assert.assertEquals(0, snakes.size());

        // onReset
        game.reset();
        Assert.assertEquals(4, nibbles.size());
        Assert.assertEquals(4, walls.size());
        Assert.assertEquals(0, snakes.size());
    }

    @Test
    public void addingPlayersToGame() {
        SnakeGame game = new SnakeGame();

        // adding a Player creates a snake in the game
        Player player = new Player(null, game);
        game.addPlayer(player);
        Set<Snake> snakes = game.getSnakes();
        Assert.assertEquals(1, snakes.size());
        // Same player
        game.addPlayer(player);
        Assert.assertEquals(1, snakes.size());
        // Add another player
        player = new Player(null, game);
        game.addPlayer(player);
        Assert.assertEquals(2, snakes.size());

        // onReset players are removed
        game.reset();
        Assert.assertEquals(0, snakes.size());
    }
}