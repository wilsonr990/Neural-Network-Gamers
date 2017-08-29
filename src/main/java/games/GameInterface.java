package games;

import games.uiTemplates.Drawable;

import java.awt.*;
import java.util.*;
import java.util.List;

public class GameInterface implements Drawable{
    private List<Drawable> drawables = new LinkedList<Drawable>();

    public GameInterface(List<Drawable> drawables) {
        this.drawables = drawables;
    }

    public void draw(Graphics g) {
        for (Drawable object : drawables) {
            object.draw(g);
        }
    }
}
