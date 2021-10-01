import java.util.List;
import processing.core.PImage;

public class EdibleGhostExpire extends EdibleGhost {
    public EdibleGhostExpire(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod,
            int colorIdx) {
        super(id, position, images, actionPeriod, animationPeriod, colorIdx);
        setActionRepeatCount(3);
    }

    @Override
    public void transformToNextGhost(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        Ghost ghost = Factory.createGhost(getId(), getPosition(), getActionPeriod(), getAnimationPeriod(),
                imageStore.getImageList("ghost"), getOriginalGhostColorIdx());

        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);

        world.addEntity(ghost);
        ghost.scheduleActions(scheduler, world, imageStore);
    }
}
