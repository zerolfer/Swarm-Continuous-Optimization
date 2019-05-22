package functions;

import sim.util.Double2D;

abstract class AbstractTestFunction implements TestFunction {
    private static final double OBJ1_PONDERATION = 1;
    private static final double OBJ2_PONDERATION = 1;

    @Override
    public double fitness(Double2D location) {
        return fitness(location.x, location.y);
    }

    public double fitness(double x, double y) {
        double r1 = f1(x);
        double valorG = g(y);
        double r2 = valorG * h(r1, valorG);
        return resume(r1, r2);
    }

    protected double resume(double r1, double r2) {
        return r1 * OBJ1_PONDERATION + r2 * OBJ2_PONDERATION;
    }

    protected abstract double f1(double x1);

    protected abstract double g(double x2);

    protected abstract double h(double f1, double g);
}
