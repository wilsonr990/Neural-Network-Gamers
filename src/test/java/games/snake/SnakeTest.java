package games.snake;

import org.junit.Assert;
import org.junit.Test;

import java.awt.*;

public class SnakeTest {
    @Test
    public void newSnakesAreAliveAndHaveOnlyOneSegment() {
        Snake snake = new Snake(new Point(0, 0));

        Assert.assertTrue(snake.isAlive());
    }

    @Test
    public void whenHealthGotLowerThanZeroSnakesDie() {
        Snake snake = new Snake(new Point(0, 0));

        for (int i = 0; i < 15 / 0.02 + 1; i++) snake.update();
        Assert.assertTrue(!snake.isAlive());
    }

}