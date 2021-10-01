import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import processing.core.PImage;

public final class WorldModel {
    private final int numRows;
    private final int numCols;
    private final Background background[][];
    private final Entity occupancy[][];
    private final Set<Entity> entities;

    private static final int ORE_REACH = 1;

    public Set<Entity> getEntities() {
        return entities;
    }

    public int getNumCols() {
        return numCols;
    }

    public int getNumRows() {
        return numRows;
    }

    public WorldModel(int numRows, int numCols, Background defaultBackground) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.background = new Background[numRows][numCols];
        this.occupancy = new Entity[numRows][numCols];
        this.entities = new HashSet<>();

        for (int row = 0; row < numRows; row++) {
            Arrays.fill(this.background[row], defaultBackground);
        }
    }

    private Background getBackgroundCell(Point pos) {
        return background[pos.getY()][pos.getX()];
    }

    public boolean withinBounds(Point pos) {
        return pos.getY() >= 0 && pos.getY() < numRows && pos.getX() >= 0 && pos.getX() < numCols;
    }

    public Optional<PImage> getBackgroundImage(Point pos) {
        if (withinBounds(pos)) {
            return Optional.of(getBackgroundCell(pos).getCurrentImage());
        } else {
            return Optional.empty();
        }
    }

    private void setBackgroundCell(Point pos, Background background) {
        this.background[pos.getY()][pos.getX()] = background;
    }

    public void setBackground(Point pos, Background background) {
        if (withinBounds(pos)) {
            setBackgroundCell(pos, background);
        }
    }

    public void setOccupancyCell(Point pos, Entity entity) {
        occupancy[pos.getY()][pos.getX()] = entity;
    }

    private Entity getOccupancyCell(Point pos) {
        return occupancy[pos.getY()][pos.getX()];
    }

    public Optional<Entity> getOccupant(Point pos) {
        if (isOccupied(pos)) {
            return Optional.of(getOccupancyCell(pos));
        } else {
            return Optional.empty();
        }
    }

    public boolean isOccupied(Point pos) {
        return withinBounds(pos) && getOccupancyCell(pos) != null;
    }

    private static Optional<Entity> nearestEntity(List<Entity> entities, Point pos) {
        if (entities.isEmpty()) {
            return Optional.empty();
        } else {
            Entity nearest = entities.get(0);
            int nearestDistance = Point.distanceSquared(nearest.getPosition(), pos);

            for (Entity other : entities) {
                int otherDistance = Point.distanceSquared(other.getPosition(), pos);

                if (otherDistance < nearestDistance) {
                    nearest = other;
                    nearestDistance = otherDistance;
                }
            }

            return Optional.of(nearest);
        }
    }

    public Optional<Entity> findNearest(Point pos, Class<?> entityClass) {
        List<Entity> ofType = new LinkedList<>();
        for (Entity entity : entities) {
            if (entity.getClass().equals(entityClass)) {
                ofType.add(entity);
            }
        }
        return nearestEntity(ofType, pos);
    }

    public List<Entity> entitiesOfType(Class<?> entityClass) {
        List<Entity> ofType = new LinkedList<>();
        for (Entity entity : entities) {
            if (entity.getClass().equals(entityClass)) {
                ofType.add(entity);
            }
        }
        return ofType;
    }

    public void removeEntityAt(Point pos) {
        if (withinBounds(pos) && getOccupancyCell(pos) != null) {
            Entity entity = getOccupancyCell(pos);

            /*
             * This moves the entity just outside of the grid for debugging purposes.
             */
            entity.setPosition(new Point(-1, -1));
            entities.remove(entity);
            setOccupancyCell(pos, null);
        }
    }

    public Optional<Point> findOpenAround(Point pos) {
        for (int dy = -ORE_REACH; dy <= ORE_REACH; dy++) {
            for (int dx = -ORE_REACH; dx <= ORE_REACH; dx++) {
                Point newPt = new Point(pos.getX() + dx, pos.getY() + dy);
                if (withinBounds(newPt) && !isOccupied(newPt)) {
                    return Optional.of(newPt);
                }
            }
        }

        return Optional.empty();
    }

    public void addEntity(Entity entity) {// move done
        if (withinBounds(entity.getPosition())) {
            setOccupancyCell(entity.getPosition(), entity);
            getEntities().add(entity);
        }
    }

    public void moveEntity(Entity entity, Point pos) {// move done
        Point oldPos = entity.getPosition();
        if (withinBounds(pos) && !pos.equals(oldPos)) {
            setOccupancyCell(oldPos, null);
            removeEntityAt(pos);
            setOccupancyCell(pos, entity);
            entity.setPosition(pos);
        }
    }

    public void removeEntity(Entity entity) { // to be moved
        removeEntityAt(entity.getPosition());
    }

    public void tryAddEntity(Entity entity) {// to be moved
        if (isOccupied(entity.getPosition())) {
            // arguably the wrong type of exception, but we are not
            // defining our own exceptions yet
            throw new IllegalArgumentException("position occupied");
        }

        addEntity(entity);
    }

    public void removeScheduledEntity(Entity entity, EventScheduler scheduler) {
        removeScheduledEntityAt(entity.getPosition(), scheduler);
    }

    public void removeScheduledEntityAt(Point pos, EventScheduler scheduler) {
        if (withinBounds(pos) && getOccupancyCell(pos) != null) {
            Entity entity = getOccupancyCell(pos);
            /*
             * This moves the entity just outside of the grid for debugging purposes.
             */
            entity.setPosition(new Point(-1, -1));
            scheduler.unscheduleAllEvents(entity);
            entities.remove(entity);
            setOccupancyCell(pos, null);
        }
    }

    public void addEntityGrid(Function<Point, Entity> entityCreatorAtPos, EventScheduler scheduler, Point topLeft,
            Point bottomRight) {
        for (int curX = topLeft.x; curX <= bottomRight.x; curX++) {
            for (int curY = topLeft.y; curY <= bottomRight.y; curY++) {
                if (withinBounds(new Point(curX, curY))) {
                    removeScheduledEntityAt(new Point(curX, curY), scheduler);
                    addEntity(entityCreatorAtPos.apply(new Point(curX, curY)));
                }
            }
        }
    }

    public List<Point> changeGridBackground(Background newBackground, Point topLeft, Point bottomRight) {
        List<Point> updatedGrid = new LinkedList<>();
        for (int curX = topLeft.x; curX <= bottomRight.x; curX++) {
            for (int curY = topLeft.y; curY <= bottomRight.y; curY++) {
                if (withinBounds(new Point(curX, curY))) {
                    setBackground(new Point(curX, curY), newBackground);
                    updatedGrid.add(new Point(curX, curY));
                }
            }
        }
        return updatedGrid;
    }
}
