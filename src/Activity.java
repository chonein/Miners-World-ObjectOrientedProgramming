public class Activity implements Action {
    private final ActiveEntity execuatableEntity;
    private final WorldModel world;
    private final ImageStore imageStore;

    // private final int repeatCount;
    // SpecialInterface
    public Activity(ActiveEntity execuatableEntity, WorldModel world, ImageStore imageStore) {
        this.execuatableEntity = execuatableEntity;
        this.world = world;
        this.imageStore = imageStore;
    }

    public void executeAction(EventScheduler scheduler) {
        execuatableEntity.executeActivity(world, imageStore, scheduler);
    }
}
