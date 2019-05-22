package ants;

import sim.display.Console;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.portrayal.continuous.ContinuousPortrayal2D;
import sim.portrayal.simple.OvalPortrayal2D;

import javax.swing.*;
import java.awt.*;

public class AntsSimWithUI extends GUIState {

    public Display2D display2D;
    public JFrame displayFrame;
    ContinuousPortrayal2D spacePortrayal = new ContinuousPortrayal2D();

    public AntsSimWithUI() {
        super(new AntsSim(System.currentTimeMillis()));
    }

    public AntsSimWithUI(SimState state) {
        super(state);
    }

    public static void main(String[] args) {
        AntsSimWithUI vid = new AntsSimWithUI();
        new Console(vid).setVisible(true);
    }

    public static String getName() {
        return "Ants Colony Optimization";
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
        AntsSim colony = (AntsSim) state;

        spacePortrayal.setField(colony.getSeachSpace());
        spacePortrayal.setPortrayalForAll(new OvalPortrayal2D());

        display2D.reset();
        display2D.setBackdrop(Color.white);

        display2D.repaint();
    }

    public void init(Controller controller) {

        super.init(controller);
        display2D = new Display2D(600, 600, this);
        display2D.setClipping(false);

        displayFrame = display2D.createFrame();
        displayFrame.setTitle(getName());
        controller.registerFrame(displayFrame);
        displayFrame.setVisible(true);
        display2D.attach(spacePortrayal, "Space");
    }

    @Override
    public void quit() {
        super.quit();
        if (displayFrame != null) displayFrame.dispose();
        displayFrame = null;
        display2D = null;
    }
}
