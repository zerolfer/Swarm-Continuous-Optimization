package robots;

import map.MapElements;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Double2D;

import static java.lang.Math.abs;
import static java.lang.Math.toRadians;

public class Robot implements Steppable {

    protected static int MAX_NUM_ITER_NO_IMPROVEMENT = 5;
    static int MAX_ROT_ITER = 18;

    protected int numExploringIter = 0;
    protected int numTotalIter = 0;
    protected int numIterNoImprovement = 0;

    boolean exploreMode = true;
    private RobotExploreMode exploreRobot;
    private RobotNoExploreMode noExploreRobot;

    Double2D currentPosition;
    private Double2D velocity;
    private Double2D localBestPosition;

    int degrees = 10;
    boolean obstaculo = false;

    public Robot(Double2D position) {
        this(position, new Double2D(1, 1));
    }

    Robot(Double2D position, Double2D initialVelocity) {
        this.velocity = initialVelocity;
        localBestPosition = position;
        currentPosition = position;

        exploreRobot = new RobotExploreMode(this);
        noExploreRobot = new RobotNoExploreMode(this);
    }

    Double2D generateRandomVelocity(SwarmRobotSim swarm) {
        double vel_x = swarm.random.nextDouble() * swarm.getMaxVelocity(),
                vel_y = swarm.random.nextDouble() * swarm.getMaxVelocity();
        if (swarm.random.nextBoolean()) vel_x *= -1;
        if (swarm.random.nextBoolean()) vel_y *= -1;
        return new Double2D(vel_x, vel_y);
    }

    @Override
    public void step(SimState state) {
        SwarmRobotSim swarm = (SwarmRobotSim) state;

//        this.setExploreMode(numIterNoImprovement > MAX_NUM_ITER_NO_IMPROVEMENT);
        exploreMode = false;
        numTotalIter++;

        if (numTotalIter <= swarm.maxIterExecution) {
            if (numIterNoImprovement > MAX_NUM_ITER_NO_IMPROVEMENT) {
                numExploringIter = 0;
                numIterNoImprovement = 0;
                exploreMode = true;
            }

            if (numExploringIter <= swarm.numExploringIter) {
                this.exploreMode = true;
                numExploringIter++;
            }
        }
        if (this.exploreMode)
            exploreRobot.execute(swarm);
        else
            noExploreRobot.execute(swarm);

//        System.out.println(getClass().getName() + "@" + Integer.toHexString(hashCode()) + "  at " + toString());

//        if(swarm.bestPosition==null||Utils.esMejor(f(swarm.bestPosition),f(newPosition)))
    }

    Double2D rotateVelocityDegrees(int degrees, SwarmRobotSim swarm) {
        if (swarm.random.nextBoolean()) degrees *= -1; // left or right, randomly
        return velocity.rotate(toRadians(degrees));
    }

    protected void writePheromones(Double2D currentPosition, Double2D newPosition, Double2D readData, SwarmRobotSim
            swarm) {

        double fitness_newPosition = swarm.function.fitness(newPosition);
        double f = (fitness_newPosition - swarm.function.fitness(currentPosition));
        if (f > 0 && swarm.function.fitness(localBestPosition) > fitness_newPosition) {
            this.localBestPosition = newPosition; // update best local position

            // if the soFIlution is better than the best of the swarm, we safe the data
            // in order to know the final result found by the system
            if (fitness_newPosition > swarm.getBestFitness()) {
                swarm.setBestPosition(localBestPosition);
                swarm.setBestFitness(fitness_newPosition);
            } else
                numIterNoImprovement = 0;
        } else if (!exploreMode) numIterNoImprovement++;
//        f = f > 0 ? min(f, 2) : min(f, 2);
        Double2D vectorAtoB = newPosition.subtract(currentPosition);
        Double2D newTrail;
        // if the bot found an obtacle, compute the directon to get out, else, use the signal (f) direction
        if (obstaculo) {
            f = abs(f);
            obstaculo = false;
        }
        if (Math.pow(vectorAtoB.length(), 2) == 0) newTrail = vectorAtoB.multiply(0);
        else newTrail = vectorAtoB.multiply(f / Math.pow(vectorAtoB.length(), 2));
        Double2D newPheromoneTrail = readData.multiply(1 - swarm.getEvaporationFactor())
                .add(newTrail.multiply(swarm.getSuperpositionFactor()));
        writeTag(newPheromoneTrail, currentPosition, swarm); // vector addition
//            }
    }

    protected Double2D[] readPheromones(SwarmRobotSim st, Double2D currentPosition) {
        try {

            // get data
            Double2D socialData = readTag(st, currentPosition);

            Double2D newPosition = setNewVelocityAndCalculateNewPosition(st, socialData);

            int iter = 0;
            while (st.map.get((int) newPosition.x, (int) newPosition.y) == MapElements.BLACK && iter++ < MAX_ROT_ITER) {

                velocity = rotateVelocityDegrees(degrees, st);
                newPosition = setNewVelocityAndCalculateNewPosition(st, socialData);

                obstaculo = true;

                if (iter >= MAX_ROT_ITER) return new Double2D[]{socialData, currentPosition};

            }

            return new Double2D[]{socialData, newPosition};

        } catch (IllegalStateException ex) {
            throw new IllegalStateException("Error: pheromones not found at position (" +
                    currentPosition.x + ", " + currentPosition.y + ").");
        }

    }

    private Double2D setNewVelocityAndCalculateNewPosition(SwarmRobotSim st, Double2D socialData) {
        velocity = calculateNewVelocity(currentPosition, socialData, st);

        // to ensure that the velocity don't increase too much
        if (velocity.length() > st.getMaxVelocity()) velocity = velocity.resize(st.getMaxVelocity());


        // move the robot to the next position
        Double2D newPosition = currentPosition.add(velocity);

        if (newPosition.x < 0) newPosition = new Double2D(0, newPosition.y);
        if (newPosition.y < 0) newPosition = new Double2D(newPosition.x, 0);

        if (newPosition.x >= st.space.getWidth())
            newPosition = new Double2D(st.space.getWidth() - 1, newPosition.y);
        if (newPosition.y >= st.space.getHeight())
            newPosition = new Double2D(newPosition.x, st.space.getHeight() - 1);

        return newPosition;
    }

    private Double2D calculateNewVelocity(Double2D currentPosition, Double2D socialData, SwarmRobotSim st) {

        Double2D inertiaComponent = velocity.multiply(st.getInertiaWeight());
        Double2D cognitiveComponent = localBestPosition.subtract(currentPosition)
                .multiply(st.random.nextDouble() * st.getSelfLearningFactor());
        Double2D socialComponent = socialData.multiply(
                st.random.nextDouble(false, true) * st.getSocialLearningFactor()
        );
        return inertiaComponent.add(cognitiveComponent).add(socialComponent);

    }

    Double2D readTag(SwarmRobotSim st, Double2D currentPosition) throws IllegalStateException {
        return (Double2D) st.pheromoneGrid.get((int) currentPosition.x, (int) currentPosition.y); // discretized
    }

    void writeTag(Double2D newPheromoneTrail, Double2D currentPosition, SwarmRobotSim swarm) {
        swarm.pheromoneGrid.set((int) currentPosition.x, (int) currentPosition.y, newPheromoneTrail); // discretized
    }

    @Override
    public String toString() {
        return String.format("(%.2f, %.2f)", currentPosition.x, currentPosition.y);
    }

    public Double2D getVelocity() {
        return velocity;
    }

    public void setVelocity(Double2D velocity) {
        this.velocity = velocity;
    }

    public Double2D getLocalBestPosition() {
        return localBestPosition;
    }

    public void setLocalBestPosition(Double2D localBestPosition) {
        this.localBestPosition = localBestPosition;
    }

    public Double2D getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(Double2D currentPosition) {
        this.currentPosition = currentPosition;
    }

    public boolean isExploreMode() {
        return exploreMode;
    }

    public void setExploreMode(boolean exploreMode) {
        this.exploreMode = exploreMode;
    }

    public int getNumIterNoImprovement() {
        return numIterNoImprovement;
    }
}
