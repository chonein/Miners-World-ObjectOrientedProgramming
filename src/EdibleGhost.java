import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import processing.core.PImage;

public abstract class EdibleGhost extends MovingEntity {
    // private static final int[] colorIndexList =new int[] {0, 8, 16, 24};
    private int originalGhostColorIdx;
    private int actionCount;
    private int actionRepeatCount;

    public EdibleGhost(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod,
            int colorIdx) {
        super(id, position, images, actionPeriod, animationPeriod);
        originalGhostColorIdx = colorIdx;
        actionCount = 0;
    }

    public void setActionRepeatCount(int actionRepeatCount) {
        this.actionRepeatCount = actionRepeatCount;
    }
    public int getOriginalGhostColorIdx() {
        return originalGhostColorIdx;
    }

    public abstract void transformToNextGhost(WorldModel world, EventScheduler scheduler, ImageStore imageStore);

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> target = world.findNearest(getPosition(), Pacman.class);
        if (target.isPresent()) {
            moveTo(world, target.get(), scheduler);
            scheduler.scheduleEvent(this, Factory.createActivityAction(this, world, imageStore),
                    this.getActionPeriod());
        }
        if (actionCount > actionRepeatCount) {
            transformToNextGhost(world, scheduler, imageStore);
        }

        actionCount++;
    }

    @Override
    protected boolean moveToHelper(WorldModel world, Entity target, EventScheduler scheduler) {
        if (getPosition().adjacent(target.getPosition())) {
            Point targerPos = target.getPosition();
            world.moveEntity(this, opposePoint(world, targerPos));
            return true;
        }
        return false;
    }

    protected Point opposePoint(WorldModel world, Point calculatedNextPos) {
        List<Point> possibleMoves = PathingStrategy.CARDINAL_NEIGHBORS.apply(getPosition())
                .collect(Collectors.toList());
        if (calculatedNextPos.x - getPosition().x > 0 && !world.isOccupied(possibleMoves.get(2))
                && world.withinBounds(possibleMoves.get(2)))
            return possibleMoves.get(2);
        else if (calculatedNextPos.x - getPosition().x < 0 && !world.isOccupied(possibleMoves.get(3))
                && world.withinBounds(possibleMoves.get(3)))
            return possibleMoves.get(3);
        else if (calculatedNextPos.y - getPosition().y > 0 && !world.isOccupied(possibleMoves.get(0))
                && world.withinBounds(possibleMoves.get(0)))
            return possibleMoves.get(0);
        else if (calculatedNextPos.y - getPosition().y < 0 && !world.isOccupied(possibleMoves.get(1))
                && world.withinBounds(possibleMoves.get(1)))
            return possibleMoves.get(1);
        List<Point> validPossibleMoves = possibleMoves.stream()
                .filter(e -> !world.isOccupied(e) && world.withinBounds(e)).collect(Collectors.toList());
        return validPossibleMoves.get(rand.nextInt(validPossibleMoves.size()));
    }


    // protected Point opposePoint(WorldModel world, Point calculatedNextPos) {
    //     List<Point> possibleMoves = PathingStrategy.CARDINAL_NEIGHBORS.apply(getPosition())
    //             .collect(Collectors.toList());
    //     if (calculatedNextPos.x - getPosition().x > 0 && !world.isOccupied(possibleMoves.get(2))
    //             && world.withinBounds(possibleMoves.get(2)))
    //         return possibleMoves.get(2);
    //     else if (calculatedNextPos.x - getPosition().x < 0 && !world.isOccupied(possibleMoves.get(3))
    //             && world.withinBounds(possibleMoves.get(3)))
    //         return possibleMoves.get(3);
    //     else if (calculatedNextPos.y - getPosition().y > 0 && !world.isOccupied(possibleMoves.get(0))
    //             && world.withinBounds(possibleMoves.get(0)))
    //         return possibleMoves.get(0);
    //     else if (calculatedNextPos.y - getPosition().y < 0 && !world.isOccupied(possibleMoves.get(1))
    //             && world.withinBounds(possibleMoves.get(1)))
    //         return possibleMoves.get(1);
    //     List<Point> validPossibleMoves = possibleMoves.stream()
    //             .filter(e -> !world.isOccupied(e) && world.withinBounds(e)).collect(Collectors.toList());
    //     return validPossibleMoves.get(rand.nextInt(validPossibleMoves.size()));
    // }

    @Override
    protected Point nextPosition(WorldModel world, Point destPos) {

        List<Point> pathList = getStrategy().computePath(getPosition(), destPos,
                (pos -> world.withinBounds(pos) && !world.isOccupied(pos)), (p1, p2) -> p1.neighbors(p2),
                PathingStrategy.CARDINAL_NEIGHBORS);

        if (pathList.isEmpty())
            return getPosition();
        else
            return opposePoint(world, pathList.get(0));
    }
}
