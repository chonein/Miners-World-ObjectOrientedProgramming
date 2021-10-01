import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import processing.core.PImage;

public class Pacman extends MovingEntity {
    private Map<String, Integer> directionToIndex = new HashMap<>();
    private List<PImage> allDirectionsImages;
    private char direction = 'l';
    private int score;

    public Pacman(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
        score = 0;
        allDirectionsImages = images;
        directionToIndex.put("right", 0);
        directionToIndex.put("up", 5);
        directionToIndex.put("left", 10);
        directionToIndex.put("down", 15);
        setImages(allDirectionsImages.subList(directionToIndex.get("left"), directionToIndex.get("left") + 5));
    }

    public int getScore() {
        return score;
    }

    @Override
    protected boolean moveToHelper(WorldModel world, Entity target, EventScheduler scheduler) {
        Optional<Entity> chosenNeigh = Optional.empty();
        switch (direction) {
            case 'u':
                chosenNeigh = world.getOccupant(new Point(getPosition().x, getPosition().y - 1));
                break;
            case 'd':
                chosenNeigh = world.getOccupant(new Point(getPosition().x, getPosition().y + 1));
                break;
            case 'l':
                chosenNeigh = world.getOccupant(new Point(getPosition().x - 1, getPosition().y));
                break;
            case 'r':
                chosenNeigh = world.getOccupant(new Point(getPosition().x + 1, getPosition().y));
                break;
            case 'a':
                if (getPosition().adjacent(target.getPosition())) {
                    chosenNeigh = world.getOccupant(target.getPosition());
                }
                break;
        }
        if (chosenNeigh.isPresent()
                && (chosenNeigh.get().getClass().equals(Ore.class) || chosenNeigh.get() instanceof EdibleGhost)) {
            setDirection(chosenNeigh.get().getPosition());
            world.moveEntity(this, chosenNeigh.get().getPosition());
            world.removeEntity(chosenNeigh.get());
            scheduler.unscheduleAllEvents(chosenNeigh.get());
            if (chosenNeigh.get() instanceof Ore) {
                score += 100;
            } else {
                score += 200;
            }
            return true;
        }
        return false;
    }

    public void moveToKeyPress(WorldModel world, char pressedKey, EventScheduler scheduler) {
        switch (pressedKey) {
            case 'h':
            case 'a':
                direction = 'l';
                break;
            case 'l':
            case 'd':
                direction = 'r';
                break;
            case 'j':
            case 'w':
                direction = 'u';
                break;
            case 'k':
            case 's':
                direction = 'd';
                break;
            case 'q': // autopilot
                direction = 'a';
            default:
                break;
        }
    }

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> target = world.findNearest(getPosition(), EdibleGhostRegular.class);
        if (!target.isPresent()) {
            target = world.findNearest(getPosition(), EdibleGhostExpire.class);
        }
        if (!target.isPresent()) {
            target = world.findNearest(getPosition(), Ore.class);
        }
        if (!target.isPresent()) {
            target = world.findNearest(getPosition(), Vein.class);
        }
        if (!target.isPresent()) {
            target = world.findNearest(getPosition(), Obstacle.class);
        }
        // if (target.isPresent())
        // System.out.println(target.get().getClass());
        if (target.isPresent() && moveTo(world, target.get(), scheduler)) {
            if (!(!world.entitiesOfType(EdibleGhostRegular.class).isEmpty()
                    || !world.entitiesOfType(EdibleGhostExpire.class).isEmpty()))
                world.entitiesOfType(Ghost.class)
                        .forEach(entity -> ((Ghost) entity).transformToEdible(world, scheduler, imageStore));
        }
        scheduler.scheduleEvent(this, Factory.createActivityAction(this, world, imageStore), getActionPeriod());
    }

    private void setDirection(Point nextPos) {
        if (nextPos.x - getPosition().x > 0)
            setImages(allDirectionsImages.subList(directionToIndex.get("right"), directionToIndex.get("right") + 5));
        else if (nextPos.x - getPosition().x < 0)
            setImages(allDirectionsImages.subList(directionToIndex.get("left"), directionToIndex.get("left") + 5));
        else if (nextPos.y - getPosition().y > 0)
            setImages(allDirectionsImages.subList(directionToIndex.get("down"), directionToIndex.get("down") + 5));
        else
            setImages(allDirectionsImages.subList(directionToIndex.get("up"), directionToIndex.get("up") + 5));
    }

    @Override
    protected Point nextPosition(WorldModel world, Point destPos) {

        Point chosenNeigh = null;
        switch (direction) {
            case 'u':
                chosenNeigh = new Point(getPosition().x, getPosition().y - 1);
                break;
            case 'd':
                chosenNeigh = new Point(getPosition().x, getPosition().y + 1);
                break;
            case 'l':
                chosenNeigh = new Point(getPosition().x - 1, getPosition().y);
                break;
            case 'r':
                chosenNeigh = new Point(getPosition().x + 1, getPosition().y);
                break;
            case 'a':
                chosenNeigh = destPos;
                break;
        }
        switch (direction) {
            case 'a':
                List<Point> pathList = getStrategy().computePath(getPosition(), chosenNeigh,
                        (pos -> world.withinBounds(pos) && !world.isOccupied(pos)), (p1, p2) -> p1.neighbors(p2),
                        PathingStrategy.CARDINAL_NEIGHBORS);
                if (!pathList.isEmpty()) {
                    setDirection(pathList.get(0));
                    return pathList.get(0);
                }
                break;
            default:
                if (chosenNeigh != null && world.withinBounds(chosenNeigh) && !world.isOccupied(chosenNeigh)) {
                    setDirection(chosenNeigh);
                    return chosenNeigh;
                }
                break;
        }
        return getPosition();
    }
}
