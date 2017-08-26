import gameEngine.GameLoop;
import games.Game;
import games.snake.SnakeGame;

public class MainWindow{
	/**
	 * main function of the whole simulation
	 */
	public static void main(String[] args) {
		new MainWindow();
	}
	/**
	 * Simple JFrame as user interface
	 */
	public MainWindow() {
		Game snakeGame = new SnakeGame();

		GameLoop loop = new GameLoop(snakeGame);
		loop.start();
	}

}

