import gameEngine.EngineLoop;
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

		EngineLoop loop = new EngineLoop(snakeGame);
		loop.start();
	}

}

