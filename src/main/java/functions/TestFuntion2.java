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

    @Override
    public Double2D gradient(int x, int y) {
        return new Double2D(-(x - 8), -(y - 8));
    }

    @Override
    public String toString() {
        return "f(x,y) = -(x-8)^2 - (y-8)^2 + 128";
    }
}
