package games.snake;

import org.junit.Assert;
import org.junit.Test;

public class SnakeTest {
    @Test
    public void newSnakesAreAliveAndHaveknownHeath(){
        Snake snake = new Snake();

        Assert.assertTrue(snake.isAlive());
    }

}