package ants;

import functions.TestFunction;
import functions.TestFuntion1;
import sim.engine.SimState;
import sim.field.continuous.Continuous2D;
import sim.util.Double2D;

public class AntsSim extends SimState {

    private Continuous2D seachSpace = new Continuous2D(1d, 100, 100);
    private int numAnts = 10000;
    TestFunction funtion = new TestFuntion1();

    public AntsSim(long seed) {
        super(seed);
    }

    public static void main(String[] args) {
        doLoop(AntsSim.class, args);
        System.exit(0);
    }

    public void start() {
        super.start();

        getSeachSpace().clear();

        addAntsToTheSpace();
    }

    private void addAntsToTheSpace() {
        for (int i = 0; i < getNumAnts(); i++) {

            Ant ant = new Ant();

            getSeachSpace().setObjectLocation(ant,
                    new Double2D(getSeachSpace().getWidth() */* 0.5 + */random.nextDouble(),
                            getSeachSpace().getHeight() */*0.5 + */random.nextDouble()));
            // FIXME inicializacion en todo el espacio de busqueda y no solo el visible

            schedule.scheduleRepeating(ant);

        }
    }

    Continuous2D getSeachSpace() {
        return seachSpace;
    }

    int getNumAnts() {
        return numAnts;
    }

    void setNumAnts(int numAnts) {
        this.numAnts = numAnts;
    }
}
