package gameEngine;

import gameEngine.view.EngineUI;

public class EngineLoop implements Runnable {
    private final ModelContainer model;
    private final EngineUI ui;

    public EngineLoop(ModelContainer model, EngineUI ui) {
        this.model = model;
        this.ui = ui;
    }

    public void start() {
        new Thread(this).start();
    }

    public void run() {
        model.simulationLastMillis = System.currentTimeMillis() + 100;
        model.statisticsLastMillis = 0;
        while (true) {
            if (System.currentTimeMillis() - model.simulationLastMillis > ModelContainer.UPDATE_PERIOD) {
                synchronized (model.players) { // protect read
                    long currentTime = System.currentTimeMillis();
                    // Controls
                    ui.controlEvents();
                    // computation:
                    model.runIteration(ui.getWidth(), ui.getHeight());

                    ui.repaint();

                    model.per = System.currentTimeMillis() - currentTime;
                    model.simulationLastMillis += ModelContainer.UPDATE_PERIOD;
                }
            }
        }
    }
}
