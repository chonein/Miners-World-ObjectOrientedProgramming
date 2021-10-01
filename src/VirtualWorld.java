import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import processing.core.*;

public final class VirtualWorld extends PApplet {
    private static final int TIMER_ACTION_PERIOD = 100;

    // private static final int VIEW_WIDTH = 640;
    // private static final int VIEW_HEIGHT = 480;
    private static final int VIEW_WIDTH = 1280;
    private static final int VIEW_HEIGHT = 960;
    private static final int TILE_WIDTH = 32;
    private static final int TILE_HEIGHT = 32;
    // private static final int WORLD_WIDTH_SCALE = 2;
    // private static final int WORLD_HEIGHT_SCALE = 2;
    private static final int WORLD_WIDTH_SCALE = 1;
    private static final int WORLD_HEIGHT_SCALE = 1;

    private static final int VIEW_COLS = VIEW_WIDTH / TILE_WIDTH;
    private static final int VIEW_ROWS = VIEW_HEIGHT / TILE_HEIGHT;
    private static final int WORLD_COLS = VIEW_COLS * WORLD_WIDTH_SCALE;
    private static final int WORLD_ROWS = VIEW_ROWS * WORLD_HEIGHT_SCALE;

    private static final String IMAGE_LIST_FILE_NAME = "imagelist";
    private static final String DEFAULT_IMAGE_NAME = "background_default";
    private static final int DEFAULT_IMAGE_COLOR = 0x808080;

    private static final String LOAD_FILE_NAME = "world.sav";

    private static final String FAST_FLAG = "-fast";
    private static final String FASTER_FLAG = "-faster";
    private static final String FASTEST_FLAG = "-fastest";
    private static final double FAST_SCALE = 0.5;
    private static final double FASTER_SCALE = 0.25;
    private static final double FASTEST_SCALE = 0.10;

    private static double timeScale = 1.0;

    private ImageStore imageStore;
    private WorldModel world;
    private WorldView view;
    private EventScheduler scheduler;

    private long nextTime;

    private Pacman userControlledPac;

    public void settings() {
        size(VIEW_WIDTH, VIEW_HEIGHT);
        // fullScreen();
    }

    /*
     * Processing entry point for "sketch" setup.
     */
    public void setup() {
        this.imageStore = new ImageStore(createImageColored(TILE_WIDTH, TILE_HEIGHT, DEFAULT_IMAGE_COLOR));
        this.world = new WorldModel(WORLD_ROWS, WORLD_COLS, createDefaultBackground(imageStore));
        this.view = new WorldView(VIEW_ROWS, VIEW_COLS, this, world, TILE_WIDTH, TILE_HEIGHT);
        this.scheduler = new EventScheduler(timeScale);

        loadImages(IMAGE_LIST_FILE_NAME, imageStore, this);
        loadWorld(world, LOAD_FILE_NAME, imageStore);

        scheduleActions(world, scheduler, imageStore);
        nextTime = System.currentTimeMillis() + TIMER_ACTION_PERIOD;
    }

    public void draw() {
        long time = System.currentTimeMillis();
        if (time >= nextTime) {
            this.scheduler.updateOnTime(time);
            nextTime = time + TIMER_ACTION_PERIOD;
        }
        // textSize(32);
        // text("word", 10, 30);
        // fill(0, 102, 153);
        // text("word", 10, 60);
        // fill(0, 102, 153, 51);
        // text("word", 10, 90);

        view.drawViewport();
    }

    public void keyPressed() {
        if (key == CODED) {
            int dx = 0;
            int dy = 0;

            switch (keyCode) {
                case UP:
                    dy = -1;
                    break;
                case DOWN:
                    dy = 1;
                    break;
                case LEFT:
                    dx = -1;
                    break;
                case RIGHT:
                    dx = 1;
                    break;
            }
            view.shiftView(dx, dy);
        }
        if (key >= 97 && key <= 122 && userControlledPac != null) {
            userControlledPac.moveToKeyPress(world, key, scheduler);
        }
    }

    public void mousePressed() {
        Point pressed = mouseToPoint(mouseX, mouseY);
        // Point pacmanSpawnPos = new Point(pressed.x, pressed.y + 5);
        Point pacmanSpawnPos = new Point(19, 15);
        Miners detectedMiner = null;
        if (!world.isOccupied(pressed) && world.withinBounds(new Point(pressed.x, pressed.y + 3))
                && world.withinBounds(new Point(pressed.x, pressed.y - 3))
                && world.withinBounds(new Point(pressed.x - 3, pressed.y))
                && world.withinBounds(new Point(pressed.x + 3, pressed.y))) {
            world.removeScheduledEntityAt(pacmanSpawnPos, scheduler);

            world.addEntityGrid(pos -> Factory.createObstacle("obstacle", pos, imageStore.getImageList("obstacle")),
                    scheduler, new Point(pressed.x - 2, pressed.y - 2), new Point(pressed.x - 2, pressed.y + 2));
            world.addEntityGrid(pos -> Factory.createObstacle("obstacle", pos, imageStore.getImageList("obstacle")),
                    scheduler, new Point(pressed.x + 2, pressed.y - 2), new Point(pressed.x + 2, pressed.y + 2));
            world.addEntityGrid(pos -> Factory.createObstacle("obstacle", pos, imageStore.getImageList("obstacle")),
                    scheduler, new Point(pressed.x - 1, pressed.y + 2), new Point(pressed.x + 1, pressed.y + 2));
            world.addEntityGrid(pos -> Factory.createObstacle("obstacle", pos, imageStore.getImageList("obstacle")),
                    scheduler, new Point(pressed.x - 1, pressed.y - 2), new Point(pressed.x - 1, pressed.y - 2));
            world.addEntityGrid(pos -> Factory.createObstacle("obstacle", pos, imageStore.getImageList("obstacle")),
                    scheduler, new Point(pressed.x + 1, pressed.y - 2), new Point(pressed.x + 1, pressed.y - 2));

            List<Point> jailPoints = world.changeGridBackground(new Background("jail", imageStore.getImageList("jail")),
                    new Point(pressed.x - 1, pressed.y - 1), new Point(pressed.x + 1, pressed.y + 1));
            world.changeGridBackground(new Background("pinkBackground", imageStore.getImageList("pinkBackground")),
                    new Point(pressed.x, pressed.y - 2), new Point(pressed.x, pressed.y - 2));

            for (Point pos : jailPoints) {
                Optional<Entity> curEntity = world.getOccupant(pos);
                if (curEntity.isPresent()) {
                    if (detectedMiner == null && curEntity.get() instanceof Miners) {
                        detectedMiner = (Miners) curEntity.get();
                    } else {
                        world.removeScheduledEntity(curEntity.get(), scheduler);
                    }
                }
            }
            if (detectedMiner != null) {
                detectedMiner.transformToGhostArmy(world, scheduler, imageStore, pressed);
            }
            if (userControlledPac == null || userControlledPac.getPosition().equals(new Point(-1, -1))) {
                userControlledPac = Factory.createPacman("pacman", pacmanSpawnPos, 200, 5,
                        imageStore.getImageList("pacman"));
                world.addEntity(userControlledPac);
                userControlledPac.scheduleActions(scheduler, world, imageStore);
            }
        }
    }

    private Point mouseToPoint(int x, int y) {
        return new Point(mouseX / TILE_WIDTH + view.getViewport().getCol(),
                mouseY / TILE_HEIGHT + view.getViewport().getRow());
    }

    private static Background createDefaultBackground(ImageStore imageStore) {
        return new Background(DEFAULT_IMAGE_NAME, imageStore.getImageList(DEFAULT_IMAGE_NAME));
    }

    private static PImage createImageColored(int width, int height, int color) {
        PImage img = new PImage(width, height, RGB);
        img.loadPixels();
        for (int i = 0; i < img.pixels.length; i++) {
            img.pixels[i] = color;
        }
        img.updatePixels();
        return img;
    }

    private static void loadImages(String filename, ImageStore imageStore, PApplet screen) {
        try {
            Scanner in = new Scanner(new File(filename));
            ParserLoader.loadImages(in, imageStore, screen);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void loadWorld(WorldModel world, String filename, ImageStore imageStore) {
        try {
            Scanner in = new Scanner(new File(filename));
            ParserLoader.load(in, world, imageStore);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void scheduleActions(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        for (Entity entity : world.getEntities()) {
            if (entity instanceof ActiveEntity) {
                ((ActiveEntity) entity).scheduleActions(scheduler, world, imageStore);
            }

        }
    }

    private static void parseCommandLine(String[] args) {
        for (String arg : args) {
            switch (arg) {
                case FAST_FLAG:
                    timeScale = Math.min(FAST_SCALE, timeScale);
                    break;
                case FASTER_FLAG:
                    timeScale = Math.min(FASTER_SCALE, timeScale);
                    break;
                case FASTEST_FLAG:
                    timeScale = Math.min(FASTEST_SCALE, timeScale);
                    break;
            }
        }
    }

    public static void main(String[] args) {
        parseCommandLine(args);
        PApplet.main(VirtualWorld.class);
    }
}
