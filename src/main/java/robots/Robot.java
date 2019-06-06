package robots;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Double2D;

public class Robot implements Steppable {

    private Double2D velocity;
    private Double2D localBestPosition;

    private double evaporationFactor = 0.5; // TODO: tune parameters
    private double superpositionFactor = 1;

    Robot(Double2D position) {
        this(position, new Double2D(1, 1));
    }

    Robot(Double2D position, Double2D initialVelocity) {
        this.velocity = initialVelocity;
        localBestPosition = position;
    }

    @Override
    public void step(SimState state) {
        SwarmRobotSim swarm = (SwarmRobotSim) state;

        Double2D currentPosition = swarm.space.getObjectLocation(this);

        Double2D[] raw = readPheromones(swarm, currentPosition);
        Double2D socialData = raw[0];
        Double2D newPosition = raw[1];

        System.out.println(toString() + " at (" + newPosition.x + ", " + newPosition.y + ")");

        if (!swarm.buildPheromoneMap)
            writePheromones(currentPosition, newPosition, socialData, swarm);

        swarm.space.setObjectLocation(this, newPosition);

//        if(swarm.bestPosition==null||Utils.esMejor(f(swarm.bestPosition),f(newPosition))) // TODO:a implementar
    }

    private void writePheromones(Double2D currentPosition, Double2D newPosition, Double2D readData, SwarmRobotSim swarm) {
        double f = swarm.function.fitness(newPosition) - swarm.function.fitness(currentPosition);
        Double2D d = newPosition.subtract(currentPosition);
        Double2D newTrail = d.multiply(f / Math.pow(d.length(), 2));
        Double2D newPheromoneTrail = readData.multiply(1 - evaporationFactor).add(newTrail.multiply(superpositionFactor));
        writeTag(newPheromoneTrail, currentPosition, swarm); // vector addition
    }

    private Double2D[] readPheromones(SwarmRobotSim st, Double2D currentPosition) {
        try {

            // get data
            Double2D socialData = readTag(st, currentPosition);

            // update velocity
            velocity = calculateNewVelocity(currentPosition, socialData, st);

            // to ensure that the velocity don't increase too much
            if (velocity.length() > st.getMaxVelocity()) velocity.resize(st.getMaxVelocity());


            // move the robot to the next position
            Double2D newPosition = currentPosition.add(velocity);

            if (newPosition.x < 0) newPosition = new Double2D(0, newPosition.y);
            if (newPosition.y < 0) newPosition = new Double2D(newPosition.x, 0);

            if (newPosition.x >= st.space.getWidth())
                newPosition = new Double2D(st.space.getWidth() - 1, newPosition.y);
            if (newPosition.y >= st.space.getHeight())
                newPosition = new Double2D(newPosition.x, st.space.getHeight() - 1);

            return new Double2D[]{socialData, newPosition};

        } catch (IllegalStateException ex) {
            throw new IllegalStateException("Error: pheromones not found at position (" +
                    currentPosition.x + ", " + currentPosition.y + ").");
        }

    }

    private Double2D calculateNewVelocity(Double2D currentPosition, Double2D socialData, SwarmRobotSim st) {

        Double2D inertiaComponent = velocity.multiply(st.getInertiaWeight());
        Double2D cognitiveComponent = localBestPosition.subtract(currentPosition)
                .multiply(st.random.nextDouble() * st.getSelfLearningFactor());
        Double2D socialComponent = socialData.multiply(st.random.nextDouble() * st.getSocialLearningFactor());
        return inertiaComponent.add(cognitiveComponent).add(socialComponent);

    }

    private Double2D readTag(SwarmRobotSim st, Double2D currentPosition) throws IllegalStateException {
        Double2D position = currentPosition.multiply(st.getPrecisionFactor());
        return (Double2D) st.pheromoneGrid.get((int) position.x, (int) position.y); // discretized
    }

    private void writeTag(Double2D newPheromoneTrail, Double2D currentPosition, SwarmRobotSim swarm) {
        swarm.pheromoneGrid.set((int) currentPosition.x, (int) currentPosition.y, newPheromoneTrail); // discretized
    }

}
