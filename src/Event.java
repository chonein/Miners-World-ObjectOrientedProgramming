import java.util.List;

public final class Event
{
    private final Action action;
    private final long time;
    private final Entity entity;

    public Action getAction() {
        return action;
    }
    public long getTime() {
        return time;
    }

    public Event(Action action, long time, Entity entity) {
        this.action = action;
        this.time = time;
        this.entity = entity;
    }

    public void removePendingEvent(EventScheduler scheduler) {
        List<Event> pending = scheduler.getPendingEvents().get(entity);

        if (pending != null) {
            pending.remove(this);
        }
    }
}
