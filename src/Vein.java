import java.util.List;
import java.util.Optional;

import processing.core.PImage;

public class Vein extends ActiveEntity {
    private static final String ORE_ID_PREFIX = "ore -- ";
    private static final int ORE_CORRUPT_MIN = 9000;
    private static final int ORE_CORRUPT_MAX = 20000;


    public Vein(String id, Point position, List<PImage> images, int actionPeriod) {
        super(id, position, images, actionPeriod);
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Point> openPt = world.findOpenAround(getPosition());

        if (openPt.isPresent()) {
            Ore ore = Factory.createOre(ORE_ID_PREFIX + getId(), openPt.get(),
                    ORE_CORRUPT_MIN
                            + ParserLoader.rand.nextInt(ORE_CORRUPT_MAX - ORE_CORRUPT_MIN),
                    imageStore.getImageList(ParserLoader.ORE_KEY));
            world.addEntity(ore);
            ore.scheduleActions(scheduler, world, imageStore);
        }

        scheduler.scheduleEvent(this, Factory.createActivityAction(this, world, imageStore), getActionPeriod());
    }
}
