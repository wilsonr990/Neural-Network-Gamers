package games.view;

import helpers.PhysicalCircle;

import java.awt.*;

public class Nibble extends PhysicalCircle implements Drawable {

    public Nibble(double x, double y, double rad) {
        super(x, y, rad);
    }

    public Nibble(Point p) {
        super(p);
    }

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval((int) (x - rad), (int) (y - rad), (int) (2 * rad + 1), (int) (2 * rad + 1));
    }
}
