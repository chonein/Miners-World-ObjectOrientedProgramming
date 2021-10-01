import java.util.List;
import java.util.Optional;

import processing.core.PImage;

public class MinerFull extends Miners {

    public MinerFull(String id, Point position, List<PImage> images, int resourceLimit, int actionPeriod,
            int animationPeriod) {
        super(id, position, images, resourceLimit, actionPeriod, animationPeriod);
    }

    @Override
    protected boolean moveToHelper(WorldModel world, Entity target, EventScheduler scheduler) {
        if (getPosition().adjacent(target.getPosition())) {
            return true;
        }
        return false;
    }

    private void transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        MinerNotFull miner = Factory.createMinerNotFull(getId(), getResourceLimit(), getPosition(), getActionPeriod(),
                getAnimationPeriod(), getImages());

        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);

        world.addEntity(miner);
        miner.scheduleActions(scheduler, world, imageStore);
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fullTarget = world.findNearest(getPosition(), Blacksmith.class);

        if (fullTarget.isPresent() && moveTo(world, fullTarget.get(), scheduler)) {
            transform(world, scheduler, imageStore);
        } else {
            scheduler.scheduleEvent(this, Factory.createActivityAction(this, world, imageStore), getActionPeriod());
        }
    }
}
