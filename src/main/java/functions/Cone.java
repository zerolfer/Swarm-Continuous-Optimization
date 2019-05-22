package functions;

import sim.util.Double2D;

public class Cone implements TestFunction {
    @Override
    public double fitness(Double2D location) {
        return fitness(location.x,location.y);
    }

    @Override
    public double fitness(double x, double y) {
        return Math.pow(x*x+y*y,0.5);
    }
}
