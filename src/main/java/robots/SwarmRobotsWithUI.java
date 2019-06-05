package robots;

import sim.display.Console;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.portrayal.continuous.ContinuousPortrayal2D;
import sim.portrayal.grid.SparseGridPortrayal2D;
import sim.portrayal.simple.OvalPortrayal2D;
import sim.portrayal3d.grid.SparseGrid2DPortrayal3D;
import sim.portrayal3d.simple.Arrow;

import javax.swing.*;
import java.awt.*;

public class SwarmRobotsWithUI extends GUIState {

    public Display2D display;
    public JFrame displayFrame;
    ContinuousPortrayal2D spacePortrayal = new ContinuousPortrayal2D();
    SparseGridPortrayal2D pheromonesPortrayal = new SparseGridPortrayal2D();

    public static void main(String[] args) {
        SwarmRobotsWithUI vid = new SwarmRobotsWithUI();
        Console c = new Console(vid);
        c.setVisible(true);
    }

    public SwarmRobotsWithUI(SimState state) {
        super(state);
    }

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
        spacePortrayal.setPortrayalForAll(new OvalPortrayal2D());

        pheromonesPortrayal.setPortrayalForAll(new ContinuousPortrayal2D());

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
}
