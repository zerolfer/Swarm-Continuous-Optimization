package robots;

import map.MapElements;
import sim.util.Double2D;

import static robots.Robot.MAX_ROT_ITER;

class RobotExploreMode {

    private final Robot bot;
    private double socialLearningFactor = 1; // because the movement should go to promising positions
//    private double selfLearningFactor = 0;
    private double inertia = 1;  // because we want movement


    RobotExploreMode(Robot bot) {
        this.bot = bot;
    }

    void execute(SwarmRobotSim swarm) {
        Double2D currentPosition = swarm.space.getObjectLocation(bot);

        double initialSocialLearningFactor = swarm.getSocialLearningFactor();
//        double initialSelfLearningFactor = swarm.getSelfLearningFactor();
        double initialInertia = swarm.getInertiaWeight();

        swarm.setInertiaWeight(inertia);
        swarm.setSocialLearningFactor(socialLearningFactor);


        Double2D[] raw = bot.readPheromones(swarm, currentPosition);
        Double2D socialData = raw[0];
        Double2D newPosition;

        double pos_x, pos_y;

        // avoidance strategy
        int iteration = 1;
        Double2D vel = bot.generateRandomVelocity(swarm);
        pos_x = vel.x + currentPosition.x;
        pos_y = vel.y + currentPosition.y;

        if (pos_y >= swarm.pheromoneGrid.getHeight()) pos_y = swarm.pheromoneGrid.getHeight() - 1;
        if (pos_x >= swarm.pheromoneGrid.getWidth()) pos_x = swarm.pheromoneGrid.getWidth() - 1;
        if (pos_y < 0) pos_y = 0;
        if (pos_x < 0) pos_x = 0;


        while (swarm.map.get((int) pos_x, (int) pos_y) == MapElements.BLACK && iteration < MAX_ROT_ITER) {
            vel = bot.rotateVelocityDegrees(bot.degrees * iteration, swarm);

            bot.obstaculo = true;

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

        if (!swarm.buildPheromoneMap)
            bot.writePheromones(currentPosition, newPosition, socialData, swarm);

        swarm.space.setObjectLocation(bot, newPosition);
        bot.currentPosition = newPosition;

        swarm.setSocialLearningFactor(initialSocialLearningFactor);
        swarm.setInertiaWeight(initialInertia);
//        swarm.setSelfLearningFactor(initialSelfLearningFactor);
    }

}
