package ants;

import map.MapElements;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Int2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static java.lang.Math.pow;

public class Ant implements Steppable {

    private Int2D velocity;
    private Int2D localBestPosition;
    private Int2D currentPosition;
    private int degrees = 10;
    private int MAX_ROT_ITER = 18;
    private boolean obstaculo = false;

    private Stack<Int2D> camino = new Stack<>();


    Ant(Int2D position) {
        this(position, new Int2D(1, 1));
    }

    Ant(Int2D position, Int2D initialVelocity) {
        this.velocity = initialVelocity;
        localBestPosition = position;
        currentPosition = position;
        camino.add(currentPosition);
    }


    @Override
    public void step(SimState state) {
        AntColonySim colony = (AntColonySim) state;

        Int2D currentPosition = colony.space.getObjectLocation(this);

        if (colony.exploreMode) {

            Int2D newPosition = readPheromones(colony, currentPosition);
            this.currentPosition = newPosition;
            camino.add(currentPosition);
            writePheromones(newPosition, colony);
        } else {
            this.currentPosition = camino.pop();
        }


//        if (!colony.buildPheromoneMap && colony.exploreMode)
//            writePheromones(currentPosition, newPosition, socialData, colony);

//        if (colony.exploreMode) {
//            double x = currentPosition.x + velocity.x, y = currentPosition.y + velocity.y;
//            if (x > colony.space.width) x = colony.space.width - 1;
//            if (y > colony.space.height) y = colony.space.height - 1;
//            newPosition = new Int2D(x, y);
//        }

        colony.space.setObjectLocation(this, this.currentPosition);

        System.out.println(getClass().getName() + "@" + Integer.toHexString(hashCode()) + "  at " + toString());

    }

    protected void writePheromones(Int2D position, AntColonySim colony) {

        double newPheromoneTrail = (1 - colony.getEvaporationFactor()) * readTag(colony, position)
                + colony.getEvaporationFactor() * colony.getTau_0();
        writeTag(newPheromoneTrail, currentPosition, colony); // vector addition

    }

    private Int2D readPheromones(AntColonySim st, Int2D currentPosition) {
        try {

            Int2D newPosition = calculateNewPosition(st);

            int iter = 0;
            while (st.map.get(newPosition.x, newPosition.y) == MapElements.BLACK && iter++ < MAX_ROT_ITER) {

                newPosition = calculateNewPosition(st);

                obstaculo = true;

                if (iter >= MAX_ROT_ITER) return currentPosition;

            }

            return newPosition;

        } catch (IllegalStateException ex) {
            throw new IllegalStateException("Error: pheromones not found at position (" +
                    currentPosition.x + ", " + currentPosition.y + ").");
        }

    }

    private void addIf(AntColonySim st, List<Int2D> posicions, Int2D position) {
        if (position.x >= 0 && position.x < st.pheromoneGrid.getWidth()
                && position.y >= 0 && position.y < st.pheromoneGrid.getHeight()
                && st.map.get(position.x, position.y) != MapElements.BLACK)
            posicions.add(position);
    }

    private Int2D calculateNewPosition(AntColonySim st) {
        List<Int2D> positions = new ArrayList<>();
        addIf(st, positions,/*up        = */new Int2D(currentPosition.x, currentPosition.y - 1));
        addIf(st, positions,/*left      = */ new Int2D(currentPosition.x - 1, currentPosition.y));
        addIf(st, positions,/*upLeft    = */new Int2D(currentPosition.x - 1, currentPosition.y - 1));
        addIf(st, positions,/*right     = */new Int2D(currentPosition.x + 1, currentPosition.y));
        addIf(st, positions,/*upRight   = */new Int2D(currentPosition.x + 1, currentPosition.y - 1));
        addIf(st, positions,/*downRight = */new Int2D(currentPosition.x + 1, currentPosition.y + 1));
        addIf(st, positions,/*down      = */new Int2D(currentPosition.x, currentPosition.y + 1));
        addIf(st, positions,/*downLeft  = */new Int2D(currentPosition.x - 1, currentPosition.y + 1));

        double[] socialData = new double[positions.size()];
        double[] fitness = new double[positions.size()];
        double sum = 0;

        double[] temp = new double[positions.size()];

        for (
                int i = 0;
                i < positions.size(); i++) {
            Int2D posicion = positions.get(i);
            socialData[i] = readTag(st, posicion);
            fitness[i] = st.function.fitness((double) posicion.x, (double) posicion.y);
            temp[i] = pow(socialData[i], st.alpha) * pow(fitness[i], st.beta);
            sum += temp[i];
        }

        double[] probabilities = new double[positions.size()];
        double probSum = 0;
        for (
                int i = 0;
                i < temp.length; i++) {
            probabilities[i] = temp[i] / sum;
            probSum += probabilities[i];
        }

        // choose next position randomly given those probabilities
        double rand = st.random.nextDouble() * probSum;
        int i = 0;
        double addition = 0;
        while (addition < rand) {
            addition += probabilities[i];
            i++;
        }

        return positions.get(i);

    }


    private double readTag(AntColonySim st, Int2D currentPosition) throws IllegalStateException {
        return (double) st.pheromoneGrid.get(currentPosition.x, currentPosition.y);
    }

    private void writeTag(double newPheromoneTrail, Int2D currentPosition, AntColonySim colony) {
        colony.pheromoneGrid.set(currentPosition.x, currentPosition.y, newPheromoneTrail);
    }

    @Override
    public String toString() {
        return String.format("(%d, %d)", currentPosition.x, currentPosition.y);
    }

    public Int2D getVelocity() {
        return velocity;
    }

    public void setVelocity(Int2D velocity) {
        this.velocity = velocity;
    }

    public Int2D getLocalBestPosition() {
        return localBestPosition;
    }

    public void setLocalBestPosition(Int2D localBestPosition) {
        this.velocity = localBestPosition;
    }

    public Int2D getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(Int2D currentPosition) {
        this.velocity = currentPosition;
    }
}
