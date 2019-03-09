package gameEngine.view;

import gameEngine.EngineLoop;

import javax.swing.*;
import java.awt.*;

public class EngineUI extends JFrame{

    public EngineUI(){
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize( 1000, 600);
        setTitle("Neural Net and Genetic Algorithm");
        setExtendedState(MAXIMIZED_BOTH);
    }

    public void init(EngineLoop engineLoop){
        Component mainComponent = new MainComponent(engineLoop, engineLoop.getGame());
        add(mainComponent);
        setVisible(true);
    }

}
