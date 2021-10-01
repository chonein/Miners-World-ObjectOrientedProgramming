import java.util.List;
import java.util.Optional;

import processing.core.PImage;

public class OreBlob extends MovingEntity {
    private static final String QUAKE_KEY = "quake";

    public OreBlob(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
    }

    protected Point nextPosition(WorldModel world, Point destPos) {

        List<Point> pathList = getStrategy().computePath(getPosition(), destPos,
                (pos -> world.withinBounds(pos) && !(world.getOccupant(pos).isPresent()
                        && !world.getOccupant(pos).get().getClass().equals(Ore.class))),
                (p1, p2) -> p1.neighbors(p2), PathingStrategy.CARDINAL_NEIGHBORS);

        if (pathList.isEmpty())
            return getPosition();
        else
            return pathList.get(0);
        // int horiz = Integer.signum(destPos.getX() - getPosition().getX());
        // Point newPos = new Point(getPosition().getX() + horiz, getPosition().getY());

        // Optional<Entity> occupant = world.getOccupant(newPos);

        // if (horiz == 0 || (occupant.isPresent() &&
        // !(occupant.get().getClass().equals(Ore.class)))) {
        // int vert = Integer.signum(destPos.getY() - getPosition().getY());
        // newPos = new Point(getPosition().getX(), getPosition().getY() + vert);
        // occupant = world.getOccupant(newPos);

        // if (vert == 0 || (occupant.isPresent() &&
        // !(occupant.get().getClass().equals(Ore.class)))) {
        // newPos = getPosition();
        // }
        // }

        // return newPos;
    }

    @Override
    protected boolean moveToHelper(WorldModel world, Entity target, EventScheduler scheduler) {
        if (getPosition().adjacent(target.getPosition())) {
            world.removeEntity(target);
            scheduler.unscheduleAllEvents(target);
            return true;
        }
        return false;
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> blobTarget = world.findNearest(getPosition(), Vein.class);
        long nextPeriod = getActionPeriod();

        if (blobTarget.isPresent()) {
            Point tgtPos = blobTarget.get().getPosition();

            if (moveTo(world, blobTarget.get(), scheduler)) {
                Quake quake = Factory.createQuake(tgtPos, imageStore.getImageList(QUAKE_KEY));

                world.addEntity(quake);
                nextPeriod += getActionPeriod();
                quake.scheduleActions(scheduler, world, imageStore);
            }
        }

        scheduler.scheduleEvent(this, Factory.createActivityAction(this, world, imageStore), nextPeriod);
    }
}
