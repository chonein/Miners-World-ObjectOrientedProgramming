import java.util.List;
import processing.core.PImage;

public class EdibleGhostRegular extends EdibleGhost {

    public EdibleGhostRegular(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod,
            int colorIdx) {
        super(id, position, images, actionPeriod, animationPeriod, colorIdx);
        setActionRepeatCount(7);
    }

    @Override
    public void transformToNextGhost(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        EdibleGhostExpire ghost = Factory.createEdibleGhostExpire(getId(), getPosition(), getActionPeriod(),
                getAnimationPeriod() / 1000, imageStore.getImageList("edibleghostexpire"), getOriginalGhostColorIdx());

        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);

        world.addEntity(ghost);
        ghost.scheduleActions(scheduler, world, imageStore);
    }

}
