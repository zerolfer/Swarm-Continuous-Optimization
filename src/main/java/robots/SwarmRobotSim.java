package robots;

import sim.engine.SimState;
import sim.field.continuous.Continuous2D;
import sim.field.grid.ObjectGrid2D;
import sim.field.grid.SparseGrid2D;
import sim.util.Double2D;
import sim.util.Int2D;

import java.awt.geom.Point2D;

public class SwarmRobotSim extends SimState {

    private int precisionFactor = 2; // size of each cell, (1 = 1x1, 2= 0.5x0.5, 3=0.25x0.25)
    private int x_max = 10;
    private int y_max = 10;
    Continuous2D space = new Continuous2D(precisionFactor, x_max, y_max);
    //    SparseGrid2D pheromoneGrid = new SparseGrid2D(precisionFactor*x_max, precisionFactor*y_max);
    Double2DGrid pheromoneGrid = new Double2DGrid(precisionFactor * x_max, precisionFactor * y_max);
    private int numRobots = 5;


    private double inertiaWeight = 1;
    private double selfLearningFactor = 1;
    private double socialLearningFactor = 1;
    private double maxVelocity = 1000; // TODO: tune parameters


    public SwarmRobotSim(long seed) {
        super(seed);
    }

    public static void main(String[] args) {
        doLoop(SwarmRobotSim.class, args);
        System.exit(0);
    }

    @Override
    public void start() {
        super.start();
        space.clear();

        // initialize the pheromone matrix
        for (int i = 0; i < pheromoneGrid.getWidth(); i++) {
            for (int j = 0; j < pheromoneGrid.getHeight(); j++) {
                Double2D vector = new Double2D(0, 0);
                pheromoneGrid.setObjectLocation(vector, new Int2D(i, j));
            }
        }

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

    public void setPrecisionFactor(int precisionFactor) {
        this.precisionFactor = precisionFactor;
    }
}
