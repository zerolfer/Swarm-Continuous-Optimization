package functions;

import sim.util.Double2D;
import sim.util.Int2D;

public class TestFuntion2 implements TestFunction {

    private final Int2D goal;

    public TestFuntion2(Int2D goal) {
        this.goal = goal;
    }

    public TestFuntion2(int x, int y) {
        this.goal = new Int2D(x, y);
    }

    @Override
    public double fitness(Double2D location) {
        return fitness(location.x, location.y);
    }

    @Override
    public double fitness(double x, double y) {
        return -Math.pow(x - goal.x, 2) - Math.pow(y - goal.y, 2)/* + 128*/;
    }

    @Override
    public Double2D gradient(int x, int y) {
        return new Double2D(-(x - goal.x), -(y - goal.y));
    }

    @Override
    public String toString() {
        return "f(x,y) = -(x-"+goal.x+")^2 - (y-+"+goal.x+")^2 + 128";
    }
}
