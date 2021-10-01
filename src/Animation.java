public class Animation implements Action {
    private final AnimatedEntity entity;
    // private final WorldModel world;
    // private final ImageStore imageStore;
    private final int repeatCount;

    public Animation(AnimatedEntity entity, int repeatCount) {
        this.entity = entity;
        this.repeatCount = repeatCount;
    }

    public void executeAction(EventScheduler scheduler) {
        entity.nextImage();

        if (repeatCount != 1) {
            scheduler.scheduleEvent(entity, Factory.createAnimationAction(entity, Math.max(repeatCount - 1, 0)),
                    entity.getAnimationPeriod());
        }
    }
}
