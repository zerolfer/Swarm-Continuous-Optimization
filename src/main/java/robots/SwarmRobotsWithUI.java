package robots;

import map.MapElements;
import sim.display.Console;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.Inspector;
import sim.portrayal.continuous.ContinuousPortrayal2D;
import sim.portrayal.grid.ObjectGridPortrayal2D;
import sim.portrayal.simple.CircledPortrayal2D;
import sim.portrayal.simple.LabelledPortrayal2D;
import sim.portrayal.simple.OvalPortrayal2D;
import sim.portrayal.simple.RectanglePortrayal2D;
import sim.util.Double2D;
import sim.util.MutableInt2D;

import javax.swing.*;
import java.awt.*;

public class SwarmRobotsWithUI extends GUIState {

    private Display2D display;
    private JFrame displayFrame;
    private ContinuousPortrayal2D spacePortrayal = new ContinuousPortrayal2D();
    private ObjectGridPortrayal2D mapPortrayal = new ObjectGridPortrayal2D();
    private ObjectGridPortrayal2D pheromonesPortrayal = new ObjectGridPortrayal2D();
    private ObjectGridPortrayal2D pheromonesColourPortrayal = new ObjectGridPortrayal2D();

    public SwarmRobotsWithUI(SimState state) {
        super(state);
    }

    @SuppressWarnings("WeakerAccess")
    public SwarmRobotsWithUI() {
        super(new SwarmRobotSim(System.currentTimeMillis()));
    }

    public static void main(String[] args) {
        new Console(new SwarmRobotsWithUI()).setVisible(true);
    }

    public static String getName() {
        return "Swarm Firefly Optimization";
    }

    @Override
    public void start() {
        super.start();
        setupPortrayals();
    }

    @Override
    public void load(SimState state) {
        super.load(state);
        setupPortrayals();
    }

    private void setupPortrayals() {
        SwarmRobotSim swarm = (SwarmRobotSim) state;

        spacePortrayal.setField(swarm.space);
        spacePortrayal.setPortrayalForAll(
                new LabelledPortrayal2D(
                        new CircledPortrayal2D(
                                new OvalPortrayal2D() {
                                    @Override
                                    public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
                                        Robot bot = (Robot) object;

                                        if (bot.isExploreMode()) graphics.setColor(Color.LIGHT_GRAY);
                                        else graphics.setColor(Color.DARK_GRAY);

                                        super.draw(object, graphics, info);
                                    }
                                },
                                0, 1.1, Color.blue, true),
                        0, 0, -0.45, 0.07, new Font("SansSerif", Font.BOLD, 15),
                        LabelledPortrayal2D.ALIGN_LEFT, null, Color.BLUE, true)
        );

        pheromonesPortrayal.setField(swarm.pheromoneGrid);
        pheromonesColourPortrayal.setField(swarm.pheromoneGrid);
        mapPortrayal.setField(swarm.map);
        pheromonesPortrayal.setPortrayalForAll(new RectanglePortrayal2D(Color.BLACK, false) {
            @Override
            public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
                super.draw(object, graphics, info);

                Double2D endPoint = (Double2D) object;
                graphics.setStroke(new BasicStroke(2));


                if (!info.precise) {
                    int x = (int) (info.draw.x);
                    int y = (int) (info.draw.y);


                    Color c = Color.RED;
                    graphics.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 127));

                    //endPoint.resize()
                    int endX = x + (int) endPoint.getX()/* *2*/;
                    int endY = y + (int) endPoint.getY()/* *2*/;

                    graphics.drawLine(x, y, endX, endY);

                    graphics.setStroke(new BasicStroke(5));
                    graphics.drawLine(endX, endY, endX, endY);
                    graphics.setStroke(new BasicStroke(1));


                }
            }
        });//ArrowGridPortrayal2D(Color.black, false));
        pheromonesColourPortrayal.setPortrayalForAll(new RectanglePortrayal2D() {
            @Override
            public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
//                super.draw(object, graphics, info);
                Double2D endPoint = (Double2D) object;

                MutableInt2D location = (MutableInt2D) info.location;
                if (swarm.map.get(location.x, location.y) == MapElements.BLACK)
                    graphics.setColor(Color.BLACK);
                else {
                    int r = (int) (endPoint.length() * 5);
                    if (endPoint.length() <= 0)
                        if (r < 0) r = 0;
                    if (r > 255) r = 255;
                    graphics.setColor(new Color(255, r + 160 > 255 ? 255 : r + 160, r));

                    graphics.fillRect((int) (info.draw.x - info.draw.width / 2.0),
                            (int) (info.draw.y - info.draw.width / 2.0),
                            (int) info.draw.width, (int) info.draw.height);
                }
            }
        });
        mapPortrayal.setPortrayalForAll(new RectanglePortrayal2D() {
            @Override
            public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
                super.draw(object, graphics, info);
                MapElements element = (MapElements) object;

                Color color;
                switch (element) {
                    case BLACK:
                        color = Color.BLACK;
                        break;
                    case WHITE:
                        color = Color.WHITE;
                        break;
                    case GOAL:
                        color = Color.GREEN;
                        break;
                    case START:
                        color = Color.RED;
                        break;
                    default:
                        throw new RuntimeException("Error at drawing the map");
                }

                graphics.setColor(color);

                graphics.fillRect((int) (info.draw.x - info.draw.width / 2.0),
                        (int) (info.draw.y - info.draw.width / 2.0),
                        (int) info.draw.width, (int) info.draw.height);

            }
        });

        display.reset();
//        display.setBackdrop(Color.black);

        display.repaint();
    }

    @Override
    public void init(Controller controller) {
        super.init(controller);
        display = new Display2D(800, 800, this);
        display.setClipping(false);

        displayFrame = display.createFrame();
        displayFrame.setTitle(getName());
        controller.registerFrame(displayFrame);
        displayFrame.setVisible(true);
        display.attach(mapPortrayal, "Map");
        display.attach(pheromonesColourPortrayal, "Pheromones gradient color", false);
        display.attach(pheromonesPortrayal, "Pheromones trail vector");
        display.attach(spacePortrayal, "Robots");
    }

    @Override
    public void quit() {
        super.quit();
        if (displayFrame != null) displayFrame.dispose();
        displayFrame = null;
        display = null;
    }

    @Override
    public Object getSimulationInspectedObject() {
        return state;
    }

    @Override
    public Inspector getInspector() {
        Inspector inspector = super.getInspector();
        inspector.setVolatile(true); // force updating every timestep
        return inspector;
    }
}
