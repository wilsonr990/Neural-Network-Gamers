package gameEngine.view;

import gameEngine.GameLoop;

import javax.swing.*;
import java.awt.*;

public class EngineUI extends JFrame{
    private GameLoop gameLoop;  //TODO: delete this dependency
    private Component mainComponent;

    public EngineUI(GameLoop gameLoop){
        this.gameLoop = gameLoop;
        mainComponent = new MainComponent(this.gameLoop);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize( 1000, 600);
        setExtendedState(MAXIMIZED_BOTH);
        setTitle("Neural Net and Genetic Algorithm");
        add(mainComponent);
        setVisible(true);
    }

}
