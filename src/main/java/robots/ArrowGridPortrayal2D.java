package robots;

import sim.portrayal.DrawInfo2D;
import sim.portrayal.simple.RectanglePortrayal2D;
import sim.util.Double2D;

import java.awt.*;
import java.awt.geom.Line2D;

public class ArrowGridPortrayal2D extends RectanglePortrayal2D {

    Line2D.Double line = new Line2D.Double();

    ArrowGridPortrayal2D(Paint paint, boolean filled) {
        super(paint, 1.0, filled);
    }

    @Override
    public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
        super.draw(object, graphics, info);

        Double2D endPoint = (Double2D) object;

//
//        int r = (int) (100 + 155 * endPoint.length() / 10f);
//        graphics.setColor(new Color(r > 255 ? 255 : r, 87, 51, 70));
//
//        graphics.fillRect((int) (info.draw.x - info.draw.width / 2.0), (int) (info.draw.y - info.draw.width / 2.0),
//                (int) info.draw.width, (int) info.draw.height);


        Color c = Color.RED;
        graphics.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 127));
        graphics.setStroke(new BasicStroke(2));


//        Point sw = new Point(info.clip.width/2, info.clip.height/2);
//        Point ne = new Point(w*7/8, h/8);
//        g2.draw(new Line2D.Double(sw, ne));
//        drawArrowHead(g2, sw, ne, Color.red);
//        drawArrowHead(g2, ne, sw, Color.blue);


        if (!info.precise) {
            int x = (int) (info.draw.x);
            int y = (int) (info.draw.y);
//            endPoint.resize()
            int endX = x + (int) endPoint.getX() * 2;
            int endY = y + (int) endPoint.getY() * 2;
            graphics.drawLine(x, y,
                    /*endX > info.draw.width ? (int) info.draw.width : */endX,
                    /*endY > info.draw.height ? (int) info.draw.height : */endY
            );
//            graphics.fillOval((int) (info.draw.x - info.draw.width / 2), (int) (info.draw.y - info.draw.height / 4),
//                    (int) (info.draw.width), (int) (info.draw.height / 2));
//        } else {
//            line.setFrame(info.draw.x - info.draw.width / 2.0, info.draw.y - info.draw.height / 4.0,
//                    info.draw.width, info.draw.height / 2.0);
//            graphics.fill(line);
            graphics.setStroke(new BasicStroke(1));
        }
    }
}
