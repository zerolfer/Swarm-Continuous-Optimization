package functions;

import sim.util.Double2D;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import static java.lang.Math.pow;

public class TestFunctionHimmelblau implements TestFunction {

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
        return -pow((x * x + y - 11), 2) - pow((x + y * y - 7), 2);
    }

    @Override
    public String toString() {
        return "f(x,y) = -(x * x + y - 11)^2 - (x + y * y - 7)^2";
    }
}
