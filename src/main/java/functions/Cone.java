package functions;

import sim.util.Double2D;

public class Cone implements TestFunction {
    @Override
    public double fitness(Double2D location) {
        return fitness(location.x, location.y);
    }

    @Override
    public double fitness(double x, double y) {
//        return Math.pow(x*x+y*y,0.5);
        return -x * x + y - 5;
    }

    @Override
    public Double2D gradient(int x, int y) {
        int a = -x * x - y * y;
        return new Double2D(
                (1 - 2 * x * x) * Math.exp(a),  // partial derivative respect x
                -2 * x * y * Math.exp(a));
    }
}
