package functions;

public class TestFuntion1 extends AbstractTestFunction {

    @Override
    protected double f1(double x1) {
        return x1;
    }

    @Override
    protected double g(double x2) {
        return 1 + 9 * x2;
    }

    @Override
    protected double h(double f1, double g) {
        return Math.pow(1 - (f1 / g), 2);
    }
}
