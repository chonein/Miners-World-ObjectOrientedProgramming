import java.util.List;

import processing.core.PImage;

public class Factory {
    private static final String QUAKE_ID = "quake";
    private static final int QUAKE_ACTION_PERIOD = 1100;
    private static final int QUAKE_ANIMATION_PERIOD = 100;

    public static Action createAnimationAction(AnimatedEntity entity, int repeatCount) {
        return new Animation(entity, repeatCount);
    }

    public static Action createActivityAction(ActiveEntity entity, WorldModel world, ImageStore imageStore) {
        return new Activity(entity, world, imageStore);
    }

    public static Blacksmith createBlacksmith(String id, Point position, List<PImage> images) {
        return new Blacksmith(id, position, images);
    }

    public static MinerFull createMinerFull(String id, int resourceLimit, Point position, int actionPeriod,
            int animationPeriod, List<PImage> images) {
        return new MinerFull(id, position, images, resourceLimit, actionPeriod, animationPeriod);
    }

    public static MinerNotFull createMinerNotFull(String id, int resourceLimit, Point position, int actionPeriod,
            int animationPeriod, List<PImage> images) {
        return new MinerNotFull(id, position, images, resourceLimit, actionPeriod, animationPeriod);
    }

    public static Ghost createGhostMiner(String id, Point position, int actionPeriod, int animationPeriod,
            List<PImage> images) {
        return new Ghost(id, position, images, actionPeriod, animationPeriod);
    }

    public static Ghost createGhost(String id, Point position, int actionPeriod, int animationPeriod,
            List<PImage> images, int choseColorIdx) {
        return new Ghost(id, position, images, actionPeriod, animationPeriod, choseColorIdx);
    }

    public static EdibleGhostRegular createEdibleGhostRegular(String id, Point position, int actionPeriod,
            int animationPeriod, List<PImage> images, int choseColorIdx) {
        return new EdibleGhostRegular(id, position, images, actionPeriod, animationPeriod, choseColorIdx);
    }

    public static EdibleGhostExpire createEdibleGhostExpire(String id, Point position, int actionPeriod,
            int animationPeriod, List<PImage> images, int choseColorIdx) {
        return new EdibleGhostExpire(id, position, images, actionPeriod, animationPeriod, choseColorIdx);
    }

    public static Pacman createPacman(String id, Point position, int actionPeriod, int animationPeriod,
            List<PImage> images) {
        return new Pacman(id, position, images, actionPeriod, animationPeriod);
    }

    public static Obstacle createObstacle(String id, Point position, List<PImage> images) {
        return new Obstacle(id, position, images);
    }

    public static Ore createOre(String id, Point position, int actionPeriod, List<PImage> images) {
        return new Ore(id, position, images, actionPeriod);
    }

    public static OreBlob createOreBlob(String id, Point position, int actionPeriod, int animationPeriod,
            List<PImage> images) {
        return new OreBlob(id, position, images, actionPeriod, animationPeriod);
    }

    public static Quake createQuake(Point position, List<PImage> images) {
        return new Quake(QUAKE_ID, position, images, QUAKE_ACTION_PERIOD, QUAKE_ANIMATION_PERIOD);
    }

    public static Vein createVein(String id, Point position, int actionPeriod, List<PImage> images) {
        return new Vein(id, position, images, actionPeriod);
    }

}
