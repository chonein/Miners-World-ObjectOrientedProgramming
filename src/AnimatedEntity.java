import java.util.List;

import processing.core.PImage;

public abstract class AnimatedEntity extends ActiveEntity {
    private final int animationPeriod;
    private int repeatCount;
    private int imageIndex;

    public AnimatedEntity(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod) {
        super(id, position, images, actionPeriod);
        this.animationPeriod = animationPeriod;
        this.imageIndex = 0;
        this.repeatCount = 0;
    }

    protected void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    public int getAnimationPeriod() {
        return animationPeriod;
    }

    public void nextImage() {
        imageIndex = (imageIndex + 1) % getImages().size();
    }
    
    @Override
    public PImage getCurrentImage() {
        return getImages().get(imageIndex);
    }

    @Override
    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        super.scheduleActions(scheduler, world, imageStore);
        scheduler.scheduleEvent(this, Factory.createAnimationAction(this, repeatCount), getAnimationPeriod());
    }
}
