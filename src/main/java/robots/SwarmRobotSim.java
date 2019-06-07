package robots;

import functions.TestFunction;
import functions.TestFuntion2;
import sim.engine.SimState;
import sim.field.continuous.Continuous2D;
import sim.field.grid.ObjectGrid2D;
import sim.util.Double2D;

public class SwarmRobotSim extends SimState {

    boolean exploreMode = true;
    //
    private int numRobots = 1;
    private int xy_max = 10; // max value that x and y can take
    private int precisionFactor = 1; // size of each cell, (1 = 1x1, 2= 0.5x0.5, 3=0.25x0.25)

    Continuous2D space = new Continuous2D(precisionFactor, xy_max, xy_max);
    ObjectGrid2D pheromoneGrid = new ObjectGrid2D(precisionFactor * xy_max, precisionFactor * xy_max);

    TestFunction function = new TestFuntion2();

    private double inertiaWeight = .5;
    private double selfLearningFactor = 0;
    private double socialLearningFactor = .5;
    private double maxVelocity = 1; // TODO: tune parameters

    static Double2D bestPosition; //TODO: guarda la solucion final del algoritmo

    @SuppressWarnings("WeakerAccess")
    public SwarmRobotSim(long seed) {
        super(seed);
    }

    public static void main(String[] args) {
        doLoop(SwarmRobotSim.class, new String[]{"-time", "1000000", "-until", "50000000"});
        System.exit(0);
    }

    private void usePredefinedPheromoneMap() {
        inertiaWeight = .5;
        selfLearningFactor = 0;
        socialLearningFactor = .5;
        buildPheromoneMap = true;
    }

    private void buildPheromoneMap() {
        inertiaWeight = 1;
        selfLearningFactor = 0;
        socialLearningFactor = 0;
        buildPheromoneMap = false;

    }

    @Override
    public void start() {
        super.start();
        space.clear();

//        // set configuration
//        usePredefinedPheromoneMap();
        buildPheromoneMap();

        /////////////////////////////////////
        // initialize the pheromone matrix //
        /////////////////////////////////////
        for (int i = 0; i < pheromoneGrid.getWidth(); i++) {
            for (int j = 0; j < pheromoneGrid.getHeight(); j++) {
                Double2D vector = new Double2D(0, 0);

                if (buildPheromoneMap)
                    vector = function.gradient(i, j); // GRADIENT INICIALIZATION

                if (vector.length() > maxVelocity) vector.resize(maxVelocity);

                pheromoneGrid.set(i, j, vector);
            }
        }

        //////////////////////////////////////
        // initialize and locate the robots //
        //////////////////////////////////////
        for (int i = 0; i < numRobots; i++) {
//            Int2D pos = new Int2D(random.nextInt(space.getWidth()), random.nextInt(space.getHeight()));
            Double2D pos = new Double2D(random.nextDouble() * space.getWidth(), random.nextDouble() * space.getHeight());
            Robot bot = new Robot(pos);
            space.setObjectLocation(bot, pos);

            schedule.scheduleRepeating(bot);
        }


    }


    public int getNumRobots() {
        return numRobots;
    }

    public void setNumRobots(int numRobots) {
        this.numRobots = numRobots;
    }

    public double getInertiaWeight() {
        return inertiaWeight;
    }

    public void setInertiaWeight(double inertiaWeight) {
        this.inertiaWeight = inertiaWeight;
    }

    public double getSelfLearningFactor() {
        return selfLearningFactor;
    }

    public void setSelfLearningFactor(double selfLearningFactor) {
        this.selfLearningFactor = selfLearningFactor;
    }

    public double getSocialLearningFactor() {
        return socialLearningFactor;
    }

    public void setSocialLearningFactor(double socialLearningFactor) {
        this.socialLearningFactor = socialLearningFactor;
    }

    public double getMaxVelocity() {
        return maxVelocity;
    }

    public void setMaxVelocity(double maxVelocity) {
        this.maxVelocity = maxVelocity;
    }

    public int getPrecisionFactor() {
        return precisionFactor;
    }

    public int getXy_max() {
        return xy_max;
    }

    public TestFunction getFunction() {
        return function;
    }

    public static Double2D getBestPosition() {
        return bestPosition;
    }

    public boolean isExploreMode() {
        return exploreMode;
    }

    public void setExploreMode(boolean exploreMode) {
        this.exploreMode = exploreMode;
    }

    boolean buildPheromoneMap = false;
}
