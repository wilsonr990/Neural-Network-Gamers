package games.snake;

import games.uiTemplates.Nibble;
import org.junit.Assert;
import org.junit.Test;

import java.awt.*;

public class SnakeTest {
    @Test
    public void newSnakesAreAliveAndHaveOnlyOneSegment() {
        Snake snake = new Snake(new Point(1, 1));

        Assert.assertTrue(snake.isAlive());
        Assert.assertEquals(15, snake.getHealth(), 0.1);
        Assert.assertEquals(1, snake.getLength());
        Assert.assertEquals(Math.PI/4, snake.getAngle(), 0.1);
    }

    @Test
    public void whenEatNibbleGainHealthUntilSaturationAndExtendsSegments() {
        Point p = new Point(0, 0);

        Snake snake = new Snake(p);
        Nibble n = new Nibble(p);

        snake.eat(n);
        Assert.assertTrue(snake.isAlive());
        Assert.assertEquals(25, snake.getHealth(), 0.1);
        Assert.assertEquals(2, snake.getLength());

        for (int i = 0; i < 30 / 10; i++) snake.eat(n);
        Assert.assertTrue(snake.isAlive());
        Assert.assertEquals(30, snake.getHealth(), 0.1);
        Assert.assertEquals(5, snake.getLength());
    }

    @Test
    public void whenHealthGotLowerThanZeroSnakesDie() {
        Snake snake = new Snake(new Point(0, 0));

        for (int i = 0; i < 15 / 0.02 + 1; i++) snake.update();
        Assert.assertTrue(!snake.isAlive());
    }
}