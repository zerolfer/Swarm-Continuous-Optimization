package ants;

import functions.TestFunction;
import map.MapCreator;
import map.MapElements;
import sim.engine.SimState;
import sim.field.grid.DoubleGrid2D;
import sim.field.grid.ObjectGrid2D;
import sim.field.grid.SparseGrid2D;
import sim.util.Bag;
import sim.util.Int2D;

import java.util.List;

public class AntColonySim extends SimState {

    public double alpha = 1;
    public double beta = 1;
    boolean exploreMode = true;
    //
    private int numRobots = 50;

    private int precisionFactor = 20; // size of each cell, (1 = 1x1, 2= 0.5x0.5, 3=0.25x0.25)

    SparseGrid2D space;// = new Continuous2D(precisionFactor, xy_max, xy_max);
    ObjectGrid2D map;
    ObjectGrid2D pheromoneGrid; // = new ObjectGrid2D(precisionFactor * xy_max, precisionFactor * xy_max);

    List<TestFunction> functions;

    private double inertiaWeight = .5;
    private double selfLearningFactor = .5;
    private double socialLearningFactor = .5;
    private double evaporationFactor = .1;
    private double superpositionFactor = .5;


    private double maxVelocity = 1;

    private Int2D bestPosition;
    private double bestFitness;
    private String imageName = "simple1.png";

    private double tau_0 = 1/numRobots;

    @SuppressWarnings("WeakerAccess")
    public AntColonySim(long seed) {
        super(seed);
    }

    public static void main(String[] args) {
        doLoop(AntColonySim.class, new String[]{"-time", "1000000", "-until", "50000000", "-for", "20"});
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
    @SuppressWarnings("unchecked")
    public void start() {
        super.start();
        if (space != null) space.clear();
        this.bestPosition = null;
        this.bestFitness = 0;

        Bag bag = MapCreator.createMap(imageName, precisionFactor);
        map = (ObjectGrid2D) bag.get(0);
        functions = (List<TestFunction>) bag.get(1);
        //start=(Int2D) bag.get(2);
        space = new SparseGrid2D(map.getWidth(), map.getHeight());
        pheromoneGrid = new ObjectGrid2D(map.getWidth(), map.getHeight(), tau_0);

        function = functions.get(0);

//        /////////////////////////////////////
//        // initialize the pheromone matrix //
//        /////////////////////////////////////
//        for (int i = 0; i < pheromoneGrid.getWidth(); i++)
//            for (int j = 0; j < pheromoneGrid.getHeight(); j++)
//                pheromoneGrid.set(i, j, tau_0);

        //////////////////////////////////////
        // initialize and locate the robots //
        //////////////////////////////////////
        for (int i = 0; i < numRobots; i++) {
//            Int2D pos = new Int2D(random.nextInt(space.getWidth()), random.nextInt(space.getHeight()));
            Int2D pos;
            do {
                pos = new Int2D(random.nextInt(space.getWidth()), random.nextInt(space.getHeight()));
            } while (map.get(pos.x, pos.y) == MapElements.BLACK); // que no cominece en una posición prohibida

            Ant bot = new Ant(pos);

            space.setObjectLocation(bot, pos);

            schedule.scheduleRepeating(bot);
        }

        problemSolverAlgorithm();


    }

    TestFunction function;

    private void problemSolverAlgorithm() {
//        final int NUM_EXPLORING_ITERATIONS = 20;
//        for (int i = 0; i < NUM_EXPLORING_ITERATIONS; i++) {
//            for (Object bot : space.getAllObjects())
//                schedule.scheduleRepeating((Steppable) bot);
//        }
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

//    public int getXy_max() {
//        return xy_max;
//    }

    public TestFunction getFunction() {
        return function;
    }

    public Int2D getBestPosition() {
        return bestPosition;
    }


    void setBestPosition(Int2D bestPosition) {
        this.bestPosition = bestPosition;
    }

    public double getBestFitness() {
        return bestFitness;
    }

    void setBestFitness(double bestFitness) {
        this.bestFitness = bestFitness;
    }

    public boolean isExploreMode() {
        return exploreMode;
    }

    public void setExploreMode(boolean exploreMode) {
        this.exploreMode = exploreMode;
    }

    boolean buildPheromoneMap = false;

    public double getEvaporationFactor() {
        return evaporationFactor;
    }

    public void setEvaporationFactor(double evaporationFactor) {
        this.evaporationFactor = evaporationFactor;
    }

    public double getSuperpositionFactor() {
        return superpositionFactor;
    }

    public void setSuperpositionFactor(double superpositionFactor) {
        this.superpositionFactor = superpositionFactor;
    }

    public void setPrecisionFactor(int precisionFactor) {
        this.precisionFactor = precisionFactor;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public void domImageName(String imageName) {
        this.imageName = imageName;
    }

    public double getTau_0() {
        return tau_0;
    }
}
