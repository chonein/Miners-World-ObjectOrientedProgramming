import java.util.List;
import java.util.stream.Collectors;

import processing.core.PImage;

public abstract class Miners extends MovingEntity {
    private final int resourceLimit;

    public Miners(String id, Point position, List<PImage> images, int resourceLimit, int actionPeriod,
            int animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
        this.resourceLimit = resourceLimit;
    }

    public void transformToGhostArmy(WorldModel world, EventScheduler scheduler, ImageStore imageStore,
            Point armyCenterPoint) {
        Point originalLoc = getPosition();
        int addedMiner = 0;
        List<Point> spawnLocs = PathingStrategy.CARDINAL_NEIGHBORS.apply(armyCenterPoint).collect(Collectors.toList());
        spawnLocs.remove(originalLoc);
        Ghost ghost = Factory.createGhost(getId(), getPosition(), 400, getAnimationPeriod() / 100,
                imageStore.getImageList("ghost"), addedMiner++);
        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);

        world.addEntity(ghost);
        ghost.scheduleActions(scheduler, world, imageStore);
        for (int i = 0; i < 3; i++) {
            ghost = Factory.createGhost(getId(), spawnLocs.get(i), 400, getAnimationPeriod() / 100,
                    imageStore.getImageList("ghost"), addedMiner);
            world.addEntity(ghost);
            ghost.scheduleActions(scheduler, world, imageStore);
            addedMiner++;
        }
    }

    protected int getResourceLimit() {
        return resourceLimit;
    }

    protected Point nextPosition(WorldModel world, Point destPos) {

        List<Point> pathList = getStrategy().computePath(getPosition(), destPos,
                (pos -> world.withinBounds(pos) && !world.isOccupied(pos)), (p1, p2) -> p1.neighbors(p2),
                PathingStrategy.CARDINAL_NEIGHBORS);

        if (pathList.isEmpty())
            return getPosition();
        else
            return pathList.get(0);
    }
}
