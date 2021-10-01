import java.util.List;
import java.util.Optional;

import processing.core.PImage;

public class MinerNotFull extends Miners {
    private int resourceCount;

    public MinerNotFull(String id, Point position, List<PImage> images, int resourceLimit, int actionPeriod,
            int animationPeriod) {
        super(id, position, images, resourceLimit, actionPeriod, animationPeriod);
        resourceCount = 0;
    }

    @Override
    protected boolean moveToHelper(WorldModel world, Entity target, EventScheduler scheduler) {
        if (getPosition().adjacent(target.getPosition())) {
            resourceCount += 1;
            world.removeEntity(target);
            scheduler.unscheduleAllEvents(target);
            return true;
        }
        return false;
    }

    private boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (resourceCount >= getResourceLimit()) {
            MinerFull miner = Factory.createMinerFull(getId(), getResourceLimit(), getPosition(), getActionPeriod(),
                    getAnimationPeriod(), getImages());

            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(miner);
            miner.scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> notFullTarget = world.findNearest(getPosition(), Ore.class);

        if (!notFullTarget.isPresent() || !moveTo(world, notFullTarget.get(), scheduler)
                || !transform(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, Factory.createActivityAction(this, world, imageStore),
                    this.getActionPeriod());
        }
    }
}
