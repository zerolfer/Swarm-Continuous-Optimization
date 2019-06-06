package functions;

import sim.util.Double2D;

public interface TestFunction {
    double fitness(Double2D location);

    double fitness(double x, double y);

    Double2D gradient(int x, int y);
}
