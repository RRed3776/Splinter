package me.rred.splinter.client.routing;

import me.rred.splinter.client.routing.triggers.MapTrigger;
import me.rred.splinter.client.routing.triggers.Trigger;
import me.rred.splinter.client.sets.SplinterSet;

public class Route {
    public String name;
    private Trigger startTrigger;
    private Trigger endTrigger;
    private boolean def = false;

    public Route() {
        startTrigger = new MapTrigger(Trigger.TriggerSlot.START);
        endTrigger = new MapTrigger(Trigger.TriggerSlot.END);
        name = "default";
        def = true;
    }

    public Route(String name) {
        startTrigger = new MapTrigger(Trigger.TriggerSlot.START);
        endTrigger = new MapTrigger(Trigger.TriggerSlot.END);
        this.name = name;
    }

    public Route(Route other) {
        this.startTrigger = other.startTrigger.copy();
        this.endTrigger = other.endTrigger.copy();
        this.name = other.name;
    }

    public Route(Trigger start, Trigger end, String name) {
        this.startTrigger = start;
        this.endTrigger = end;
        this.name = name;
    }

    public Trigger getStartTrigger() {
        return startTrigger;
    }

    public Trigger getEndTrigger() {
        return endTrigger;
    }

    public String getName() { return name; }

    public void setStartTrigger(Trigger trigger) {
        startTrigger = trigger;
    }

    public void setEndTrigger(Trigger trigger) {
        endTrigger = trigger;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDefault() {
        return def;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Route)) return false;
        Route other = (Route) o;

        // routes with the same name will be treated as the same route.
        return this.getName().equals(other.getName());
    }

    public boolean nameEquals(String name) {
        return this.name.equals(name);
    }
}
