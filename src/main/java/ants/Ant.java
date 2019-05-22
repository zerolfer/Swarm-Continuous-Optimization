package ants;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.continuous.Continuous2D;
import sim.util.Double2D;

public class Ant implements Steppable {

    @Override
    public void step(SimState simState) {
        AntsSim colony = (AntsSim) simState;
        Continuous2D space = colony.getSeachSpace();

        Double2D location = space.getObjectLocation(this);

        System.out.println(location);

    }
}
