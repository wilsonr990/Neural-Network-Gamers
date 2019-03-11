package gameEngine.view;

import gameEngine.ModelContainer;
import gameEngine.commands.Command;
import helpers.KeyboardListener;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class EngineUI extends JFrame{
    private KeyboardListener keyb = new KeyboardListener();
    private Map<Command.Type, Command> commands;

    public EngineUI(Map<Command.Type, Command> commands){
        this.commands = commands;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize( 1000, 600);
        setTitle("Neural Net and Genetic Algorithm");
        setExtendedState(MAXIMIZED_BOTH);
        addKeyListener(keyb);
    }

    public void init(ModelContainer modelContainer){
        Component mainComponent = new MainComponent(modelContainer, modelContainer.getGame());
        add(mainComponent);
        setVisible(true);
    }

    public void controlEvents() {
        char keyCode = (char) keyb.getKey();
        switch (keyCode) {
            case ' ': // space
                commands.get(Command.Type.TEST_ONE).execute();
                break;
            case 'A': // a = pause
                commands.get(Command.Type.PAUSE).execute();
                break;
            case 'B': // b = resume
                commands.get(Command.Type.PAUSE).unExecute();
                break;
            case 'C': // c = show stats
                commands.get(Command.Type.SHOW_STATS).execute();
                break;
            case 'D': // d = hide stats
                commands.get(Command.Type.SHOW_STATS).unExecute();
                break;
            default:
        }
    }
}
