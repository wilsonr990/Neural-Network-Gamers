package games.view;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Wall extends Line2D implements Drawable {
    private Point startPoint = new Point();
    private Point endPoint = new Point();

    public Wall(double x1, double y1, double x2, double y2) {
        setLine(x1, y1, x2, y2);
    }

    public double getX1() {
        return startPoint.getX();
    }

    public double getY1() {
        return startPoint.getY();
    }

    public Point2D getP1() {
        return startPoint;
    }

    public double getX2() {
        return endPoint.getX();
    }

    public double getY2() {
        return endPoint.getY();
    }

    public Point2D getP2() {
        return endPoint;
    }

    public void setLine(double x1, double y1, double x2, double y2) {
        startPoint.setLocation(x1, y1);
        endPoint.setLocation(x2, y2);
    }

    public Rectangle2D getBounds2D() {
        return super.getBounds();
    }

    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.drawLine((int) getX1(), (int) getY1(), (int) getX2(), (int) getY2());
    }
}
