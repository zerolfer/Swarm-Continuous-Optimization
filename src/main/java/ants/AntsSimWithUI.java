package ants;

import sim.display.Console;
import sim.display.Controller;
import sim.display.GUIState;
import sim.display3d.Display3D;
import sim.engine.SimState;
import sim.portrayal3d.continuous.ContinuousPortrayal3D;
import sim.portrayal3d.simple.SpherePortrayal3D;
import sim.portrayal3d.simple.WireFrameBoxPortrayal3D;

import javax.swing.*;

public class AntsSimWithUI extends GUIState {

    public Display3D display3D;
    public JFrame displayFrame;
    ContinuousPortrayal3D spacePortrayal = new ContinuousPortrayal3D();
    WireFrameBoxPortrayal3D wireFrameP;

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
        spacePortrayal.setPortrayalForAll(new SpherePortrayal3D());
//        groundPortrayal.setField(new CubePortrayal3D());

        //        display3D.setBackdrop(Color.white);
        display3D.createSceneGraph();
        display3D.reset();

//        display3D.repaint();
    }

    public void init(Controller controller) {

        super.init(controller);

        AntsSim sim = (AntsSim) state;
        display3D = new Display3D(600, 600, this);
        wireFrameP = new WireFrameBoxPortrayal3D(0, 0, 0, sim.getSeachSpace().width, sim.getSeachSpace().height,
                0);
//        display3D.setClipping(false);

        double width = 100;
        display3D.translate(-width / 2d, -width / 2d, 0);
        display3D.scale(2d / width);


        displayFrame = display3D.createFrame();
        displayFrame.setTitle(getName());
        controller.registerFrame(displayFrame);
        displayFrame.setVisible(true);
        display3D.attach(spacePortrayal, "Space");
        display3D.attach(wireFrameP, "Space Delimiter");
    }

    @Override
    public void quit() {
        super.quit();
        if (displayFrame != null) displayFrame.dispose();
        displayFrame = null;
        display3D = null;
    }

}
