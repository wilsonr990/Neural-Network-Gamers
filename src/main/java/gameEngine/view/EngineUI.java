package gameEngine.view;

import gameEngine.EngineLoop;

import javax.swing.*;
import java.awt.*;

public class EngineUI extends JFrame{
    private EngineLoop engineLoop;  //TODO: delete this dependency
    private Component mainComponent;

    public EngineUI(EngineLoop engineLoop){
        this.engineLoop = engineLoop;
        mainComponent = new MainComponent(this.engineLoop, this.engineLoop.getGame());

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize( 1000, 600);
        setExtendedState(MAXIMIZED_BOTH);
        setTitle("Neural Net and Genetic Algorithm");
        add(mainComponent);
        setVisible(true);
    }

}
