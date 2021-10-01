import java.util.List;

import processing.core.PImage;

public abstract class Entity {
    private final String id;
    private List<PImage> images;
    private Point position;

    public Entity(String id, Point position, List<PImage> images) {
        this.id = id;
        this.position = position;
        this.images = images;
}

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    protected List<PImage> getImages() {
        return images;
    }

    public void setImages(List<PImage> images) {
        this.images = images;
    }

    //overridden in AnimatedEntity
    public PImage getCurrentImage() {
        return images.get(0);
    }

    protected String getId() {
        return id;
    }
}
