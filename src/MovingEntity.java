import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import processing.core.PImage;

public abstract class MovingEntity extends AnimatedEntity {
    private PathingStrategy strategy = new AStarPathingStrategy();
    Random rand = new Random();

    public PathingStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(PathingStrategy strategy) {
        this.strategy = strategy;
    }

    public MovingEntity(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
        strategy = new AStarPathingStrategy(); // default strategy
    }

    protected abstract Point nextPosition(WorldModel world, Point destPos);

    protected boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if (moveToHelper(world, target, scheduler))
            return true;
        // if (getPosition().adjacent(target.getPosition())) {
        // return true;}
        else if (strategy != null) {
            Point nextPos = nextPosition(world, target.getPosition());
            if (!getPosition().equals(nextPos)) {
                Optional<Entity> occupant = world.getOccupant(nextPos);
                if (occupant.isPresent()) {
                    scheduler.unscheduleAllEvents(occupant.get());
                }
                world.moveEntity(this, nextPos);
            }
            return false;
        }
        return false;
    }
    protected abstract boolean moveToHelper(WorldModel world, Entity target, EventScheduler scheduler);
}
