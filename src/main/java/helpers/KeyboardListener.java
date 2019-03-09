package helpers;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyboardListener implements KeyListener {

    private int code = 0;

    public void keyPressed(KeyEvent e) {
        code = e.getKeyCode();
    }

    public void keyReleased(KeyEvent e) {
        code = 0;
    }

    public void keyTyped(KeyEvent arg0) {
    }

    public int getKey() {
        return code;
    }
}
