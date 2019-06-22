package robots;

import map.MapElements;
import sim.util.Double2D;

public class RobotNoExploreMode {

    private final Robot bot;

    public RobotNoExploreMode(Robot bot) {
        this.bot=bot;
    }


    public void execute(SwarmRobotSim swarm) {
        Double2D currentPosition = swarm.space.getObjectLocation(bot);

        Double2D[] raw = bot.readPheromones(swarm, currentPosition);
        Double2D socialData = raw[0];
        Double2D newPosition = raw[1];

        if (!swarm.buildPheromoneMap)
            bot.writePheromones(currentPosition, newPosition, socialData, swarm);


        swarm.space.setObjectLocation(bot, newPosition);
        bot.currentPosition = newPosition;

    }
}
