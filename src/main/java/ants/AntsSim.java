package ants;

import functions.Cone;
import functions.TestFunction;
import sim.engine.SimState;
import sim.field.continuous.Continuous3D;
import sim.util.Double3D;

public class AntsSim extends SimState {

    TestFunction funtion = new Cone();
    private Continuous3D seachSpace = new Continuous3D(1d, 200, 200, 200);
    private int numAnts = 10000;
    double[][] pheromoneMatrix = new double[numAnts][numAnts];

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

            double x = getSeachSpace().getWidth() */* 0.5 + */random.nextDouble();
            double y = getSeachSpace().getHeight() */*0.5 + */random.nextDouble();
            getSeachSpace().setObjectLocation(ant, new Double3D(x, y, 0/*funtion.fitness(x,y)*/));
            // FIXME inicializacion en todo el espacio de busqueda y no solo el visible

            schedule.scheduleRepeating(ant);
//            colony.add(ant);
        }
    }

    Continuous3D getSeachSpace() {
        return seachSpace;
    }

    int getNumAnts() {
        return numAnts;
    }

    void setNumAnts(int numAnts) {
        this.numAnts = numAnts;
    }

    double[][] getPheromoneMatrix() {
        return pheromoneMatrix;
    }
}
