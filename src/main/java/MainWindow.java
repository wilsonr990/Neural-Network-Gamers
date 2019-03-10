import gameEngine.EngineLoop;
import gameEngine.view.EngineUI;
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
	private MainWindow() {
		EngineUI ui = new EngineUI();
		Game snakeGame = new SnakeGame(ui.getWidth(), ui.getHeight());

		EngineLoop loop = new EngineLoop(snakeGame, ui);
		ui.init(loop);
		loop.start();
	}

}

