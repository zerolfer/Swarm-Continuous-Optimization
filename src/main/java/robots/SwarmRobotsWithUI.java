package robots;

import sim.display.Console;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.portrayal.Inspector;
import sim.portrayal.continuous.ContinuousPortrayal2D;
import sim.portrayal.grid.ObjectGridPortrayal2D;
import sim.portrayal.simple.CircledPortrayal2D;
import sim.portrayal.simple.LabelledPortrayal2D;
import sim.portrayal.simple.OvalPortrayal2D;
import sim.portrayal.simple.RectanglePortrayal2D;

import javax.swing.*;
import java.awt.*;

public class SwarmRobotsWithUI extends GUIState {

    private Display2D display;
    private JFrame displayFrame;
    private ContinuousPortrayal2D spacePortrayal = new ContinuousPortrayal2D();
    private ObjectGridPortrayal2D pheromonesPortrayal = new ObjectGridPortrayal2D();

    public static void main(String[] args) {
        SwarmRobotsWithUI vid = new SwarmRobotsWithUI();
        Console c = new Console(vid);
        c.setVisible(true);
    }

    public SwarmRobotsWithUI(SimState state) {
        super(state);
    }

    @SuppressWarnings("WeakerAccess")
    public SwarmRobotsWithUI() {
        super(new SwarmRobotSim(System.currentTimeMillis()));
    }

    public static String getName() {
        return "Swarm Robot Optimization";
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
        pheromonesPortrayal.setField(swarm.pheromoneGrid);
        spacePortrayal.setPortrayalForAll(
                new LabelledPortrayal2D(
                        new CircledPortrayal2D(
                                new OvalPortrayal2D(),
                                0, 1.1, Color.blue, true),
                        0, 0, -0.45, 0.07, new Font("SansSerif", Font.BOLD, 15),
                        LabelledPortrayal2D.ALIGN_LEFT, null, Color.MAGENTA, false)


        );

        pheromonesPortrayal.setPortrayalForAll(new RectanglePortrayal2D(Color.black, false)/*Double2DPortrayal2D()*/);
        pheromonesPortrayal.setPortrayalForAll(new ArrowGridPortrayal2D(Color.black, false));

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
        display.attach(spacePortrayal, "Space");
        display.attach(pheromonesPortrayal, "pheromones trail");
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
