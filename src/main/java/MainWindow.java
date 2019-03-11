import gameEngine.EngineLoop;
import gameEngine.ModelContainer;
import gameEngine.commands.*;
import gameEngine.view.EngineUI;
import games.snake.SnakeGame;

import java.util.HashMap;
import java.util.Map;

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
		SnakeGame snakeGame = new SnakeGame();
		ModelContainer model = new ModelContainer(snakeGame);

		Map<Command.Type, Command> commands = new HashMap<>();
		commands.put(Command.Type.PAUSE, new PauseCommand(model));
		// TODO: we will separate UI from model so that model don't implement this show command. Instade an interface will receive this command
		commands.put(Command.Type.SHOW_STATS, new ShowCommand(model));
		// TODO: The ability to run a singular agent should be separated.
		commands.put(Command.Type.TEST_ONE, new TestCommand(model));
		EngineUI ui = new EngineUI(commands);
		ui.init(model);
		snakeGame.prepare(ui.getWidth(), ui.getHeight());

		EngineLoop loop = new EngineLoop(model, ui);
		loop.start();
	}

}

