package gameEngine.view;

import gameEngine.EngineLoop;
import helpers.KeyboardListener;

import javax.swing.*;
import java.awt.*;

public class EngineUI extends JFrame{
    public enum ActionControl{
        TEST_ONE,
        PAUSE,
        RESUME,
        SHOW_STATS,
        HIDE_STATS,
        NONE
    }

    KeyboardListener keyb = new KeyboardListener();

    public EngineUI(){
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize( 1000, 600);
        setTitle("Neural Net and Genetic Algorithm");
        setExtendedState(MAXIMIZED_BOTH);
        addKeyListener(keyb);
    }

    public void init(EngineLoop engineLoop){
        Component mainComponent = new MainComponent(engineLoop, engineLoop.getGame());
        add(mainComponent);
        setVisible(true);
    }

    public ActionControl getActionControl() {
        char keyCode = (char) keyb.getKey();
        switch (keyCode) {
            case ' ': // space
                return ActionControl.TEST_ONE;
            case 'A': // a = pause
                return ActionControl.PAUSE;
            case 'B': // b = resume
                return ActionControl.RESUME;
            case 'C': // c = show stats
                return ActionControl.SHOW_STATS;
            case 'D': // d = hide stats
                return ActionControl.HIDE_STATS;
            default:
                return ActionControl.NONE;
        }
    }
}
