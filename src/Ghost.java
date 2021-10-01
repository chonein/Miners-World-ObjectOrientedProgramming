import java.util.List;
import java.util.Optional;
import java.util.Random;

import processing.core.PImage;

public class Ghost extends MovingEntity {
    // private static final int[] colorIndexList =new int[] {0, 8, 16, 24};
    private Random rand = new Random();
    private int chosenColorNum;

    public Ghost(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
        chosenColorNum = rand.nextInt(4);
        int chosenColor = chosenColorNum * 8;
        setImages(images.subList(chosenColor, chosenColor + 8));
    }

    public Ghost(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod,
            int chosenColorNum) {
        super(id, position, images, actionPeriod, animationPeriod);
        this.chosenColorNum = chosenColorNum;
        int chosenColor = chosenColorNum * 8;
        setImages(images.subList(chosenColor, chosenColor + 8));
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> notFullTarget = world.findNearest(getPosition(), Pacman.class);

        if (!notFullTarget.isPresent() || !moveTo(world, notFullTarget.get(), scheduler)) {
            scheduler.scheduleEvent(this, Factory.createActivityAction(this, world, imageStore),
                    this.getActionPeriod());
        } else {
            scheduler.scheduleEvent(this, Factory.createActivityAction(this, world, imageStore),
                    this.getActionPeriod());
        }
    }

    @Override
    protected boolean moveToHelper(WorldModel world, Entity target, EventScheduler scheduler) {
        if (getPosition().adjacent(target.getPosition())) {
            Point targerPos = target.getPosition();
            world.removeEntity(target);
            scheduler.unscheduleAllEvents(target);
            world.moveEntity(this, targerPos);
            return true;
        }
        return false;
    }

    @Override
    protected Point nextPosition(WorldModel world, Point destPos) {

        List<Point> pathList = getStrategy().computePath(getPosition(), destPos,
                (pos -> world.withinBounds(pos) && !world.isOccupied(pos)), (p1, p2) -> p1.neighbors(p2),
                PathingStrategy.CARDINAL_NEIGHBORS);

        if (pathList.isEmpty())
            return getPosition();
        else
            return pathList.get(0);
    }

    public void transformToEdible(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        EdibleGhostRegular edibleGhost = Factory.createEdibleGhostRegular(getId(), getPosition(), getActionPeriod(),
                getAnimationPeriod(), imageStore.getImageList("edibleghost"), chosenColorNum);

        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);

        world.addEntity(edibleGhost);
        edibleGhost.scheduleActions(scheduler, world, imageStore);
    }
}
