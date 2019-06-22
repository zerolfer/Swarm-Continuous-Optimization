package robots;

import map.MapElements;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Double2D;

import static java.lang.Math.abs;
import static java.lang.Math.toRadians;

public class RobotVersionExploreModeCentralizado implements Steppable {

    private Double2D velocity;
    private Double2D localBestPosition;
    private Double2D currentPosition;
    int degrees = 10;
    int MAX_ROT_ITER = 18;
    boolean obstaculo = false;

    public RobotVersionExploreModeCentralizado(Double2D position) {
        this(position, new Double2D(1, 1));
    }

    public RobotVersionExploreModeCentralizado(Double2D position, Double2D initialVelocity) {
        this.velocity = initialVelocity;
        localBestPosition = position;
        currentPosition = position;
    }

    private Double2D generateRandomVelocity(SwarmRobotSim swarm) {
        double vel_x = swarm.random.nextDouble() * swarm.getMaxVelocity(),
                vel_y = swarm.random.nextDouble() * swarm.getMaxVelocity();
        if (swarm.random.nextBoolean()) vel_x *= -1;
        if (swarm.random.nextBoolean()) vel_y *= -1;
        return new Double2D(vel_x, vel_y);
    }

    @Override
    public void step(SimState state) {
        SwarmRobotSim swarm = (SwarmRobotSim) state;

        Double2D currentPosition = swarm.space.getObjectLocation(this);

        double initialSocialLearningFactor = 0;
        double initialSelfLearningFactor = 0;
        double initialInertia = 0;

        if (swarm.exploreMode) {
            initialInertia = swarm.getInertiaWeight();
            swarm.setInertiaWeight(1);

            initialSocialLearningFactor = swarm.getSocialLearningFactor();
            swarm.setSocialLearningFactor(1);

            initialSelfLearningFactor = swarm.getSelfLearningFactor();
//            swarm.setSelfLearningFactor(0);
        } else {
            initialSelfLearningFactor = swarm.getSelfLearningFactor();
            swarm.setSelfLearningFactor(0);
        }
//        double initialInertia = swarm.getInertiaWeight();
//        if (swarm.exploreMode) {
//            swarm.setInertiaWeight(1);
//            swarm.setSocialLearningFactor(0);
//            swarm.setSelfLearningFactor(0);
//        }
        Double2D[] raw = readPheromones(swarm, currentPosition);
        Double2D socialData = raw[0];
        Double2D newPosition = raw[1];

        if (swarm.exploreMode) {

            double pos_x, pos_y;

            // avoidance strategy
            int iteration = 1;
            Double2D vel = generateRandomVelocity(swarm);
            pos_x = vel.x + currentPosition.x;
            pos_y = vel.y + currentPosition.y;

            if (pos_y >= swarm.pheromoneGrid.getHeight()) pos_y = swarm.pheromoneGrid.getHeight() - 1;
            if (pos_x >= swarm.pheromoneGrid.getWidth()) pos_x = swarm.pheromoneGrid.getWidth() - 1;
            if (pos_y < 0) pos_y = 0;
            if (pos_x < 0) pos_x = 0;


            while (swarm.map.get((int) pos_x, (int) pos_y) == MapElements.BLACK && iteration < MAX_ROT_ITER) {
                vel = rotateVelocityDegrees(degrees * iteration, swarm);

                this.obstaculo = true;

                pos_x = vel.x + currentPosition.x;
                pos_y = vel.y + currentPosition.y;

                if (pos_y >= swarm.pheromoneGrid.getHeight()) pos_y = swarm.pheromoneGrid.getHeight() - 1;
                if (pos_x >= swarm.pheromoneGrid.getWidth()) pos_x = swarm.pheromoneGrid.getWidth() - 1;
                if (pos_y < 0) pos_y = 0;
                if (pos_x < 0) pos_x = 0;
                iteration++;

                if (iteration >= MAX_ROT_ITER) {
                    pos_x = currentPosition.x;
                    pos_y = currentPosition.y;
                }
            }

            newPosition = new Double2D(pos_x, pos_y);

            swarm.setInertiaWeight(initialInertia);
        }

        if (!swarm.buildPheromoneMap && swarm.exploreMode)
            writePheromones(currentPosition, newPosition, socialData, swarm);

//        if (swarm.exploreMode) {
//            double x = currentPosition.x + velocity.x, y = currentPosition.y + velocity.y;
//            if (x > swarm.space.width) x = swarm.space.width - 1;
//            if (y > swarm.space.height) y = swarm.space.height - 1;
//            newPosition = new Double2D(x, y);
//        }

        swarm.space.setObjectLocation(this, newPosition);
        this.currentPosition = newPosition;

        if (swarm.exploreMode) {
            swarm.setSocialLearningFactor(initialSocialLearningFactor);
            swarm.setInertiaWeight(initialInertia);
        }
        swarm.setSelfLearningFactor(initialSelfLearningFactor);

//        System.out.println(getClass().getName() + "@" + Integer.toHexString(hashCode()) + "  at " + toString());

//        if(swarm.bestPosition==null||Utils.esMejor(f(swarm.bestPosition),f(newPosition))) // TODO:a implementar
    }

    Double2D rotateVelocityDegrees(int degrees, SwarmRobotSim swarm) {
        if (swarm.random.nextBoolean()) degrees *= -1; // left or right, randomly
        return velocity.rotate(toRadians(degrees));
    }

    private void writePheromones(Double2D currentPosition, Double2D newPosition, Double2D readData, SwarmRobotSim
            swarm) {

        double fitness_newPosition = swarm.function.fitness(newPosition);
        double f = (fitness_newPosition - swarm.function.fitness(currentPosition));
        if (f > 0) {
            this.localBestPosition = newPosition; // update best local position

            // if the solution is better than the best of the swarm, we safe the data
            // in order to know the final result found by the system
            if (fitness_newPosition > swarm.getBestFitness()) {
                swarm.setBestPosition(localBestPosition);
                swarm.setBestFitness(fitness_newPosition);
            }

        }
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

    private Double2D[] readPheromones(SwarmRobotSim st, Double2D currentPosition) {
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
}
