package functions;

import sim.util.Double2D;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import static java.lang.Math.*;

public class TestFunctionAckley implements TestFunction {

    @Override
    public double fitness(Double2D location) {
        return fitness(location.x, location.y);
    }

    @Override
    public double fitness(double x, double y) {
        return f(x - 5, y - 5);
    }

    @Override
    public Double2D gradient(int x, int y) {
        throw new NotImplementedException();
    }

    private double f(double x, double y) {
        return -(-20 * exp(-0.2 * sqrt(0.5 * (x * x + y * y))) - exp(0.5 * cos(2 * PI * x) + cos(2 * PI * y)) + E + 20);
    }

    @Override
    public String toString() {
        return "f(x,y) =-(-20 * exp(-0.2 * sqrt(0.5 * (x * x + y * y))) - exp(0.5 * cos(2 * PI * x) + cos(2 * PI * y)" +
                ") + E + 20)";
    }
}
