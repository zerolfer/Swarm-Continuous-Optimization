package functions;

import sim.util.Double2D;

public class TestFuntion2 implements TestFunction {

    @Override
    public double fitness(Double2D location) {
        return fitness(location.x, location.y);
    }

    @Override
    public double fitness(double x, double y) {
        return -Math.pow(x - 8, 2) - Math.pow(y - 8, 2) + 128;
    }

}
