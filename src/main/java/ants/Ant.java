package ants;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.continuous.Continuous2D;
import sim.field.continuous.Continuous3D;
import sim.util.Double2D;
import sim.util.Double3D;

public class Ant implements Steppable {

    @Override
    public void step(SimState simState) {

        AntsSim sim = (AntsSim) simState;

        AntsSim colony = (AntsSim) simState;
        Continuous3D space = colony.getSeachSpace();

//        Double3D location = space.getObjectLocation(this);

        double x = sim.getSeachSpace().getWidth() */* 0.5 + */sim.random.nextDouble();
        double y =  sim.getSeachSpace().getHeight() */*0.5 + */sim.random.nextDouble();
        sim.getSeachSpace().setObjectLocation(this, new Double3D(x,y,sim.funtion.fitness(x,y)));

    }
}
