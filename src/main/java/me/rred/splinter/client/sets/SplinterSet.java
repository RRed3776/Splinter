package me.rred.splinter.client.sets;

import com.mojang.serialization.Codec;
import me.rred.splinter.client.routing.Route;
import me.rred.splinter.client.routing.triggers.MapTrigger;
import me.rred.splinter.client.routing.triggers.Trigger;

import java.util.ArrayList;
import java.util.List;

public class SplinterSet {
    private String name;
    private List<Long> times = new ArrayList<>(); // for now, data will just be non-persistent
    private Route route;

    public SplinterSet(String name, Route route) {
        this.name = name;
        this.route = route;
    }

    public void addTime(long ms) {
        times.add(ms);
    }

    public void removeTime(int idx) {
        if (idx < 0 || idx > times.size() - 1) {
            return;
        }
        times.remove(idx);
    }

    public List<Long> getTimes() {
        return times;
    }

    public int getTimesSize() {
        return times.size();
    }

    public long getAverage() {
        return (long) times.stream().mapToLong(Long::longValue).average().orElse(0);
    }

    public long getStdDev() {
        if (times.size() < 2) return 0;
        double avg = getAverage();
        double variance = times.stream()
                .mapToDouble(t -> Math.pow(t - avg, 2))
                .average()
                .orElse(0);
        return (long) Math.sqrt(variance);
    }

    public long getBest() {
        return (long) times.stream().mapToLong(Long::longValue).min().orElse(0);
    }

    public String getName() {
        return name;
    }

    public void renameSet(String newName) {
        this.name = newName;
    }

    public void clearSet() {
        times.clear();
    }

    public boolean isEmpty() {
        return times.isEmpty();
    }

    public Route getRoute() {
        return route;
    }

    public Route getEditableRoute() {
        if (route.isDefault()) {
            String routeName = name + " Route";
            MapTrigger start = new MapTrigger(Trigger.TriggerSlot.START);
            MapTrigger end = new MapTrigger(Trigger.TriggerSlot.END);
            return new Route(start, end, routeName);
        }
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SplinterSet)) return false;
        SplinterSet other = (SplinterSet) o;

        // sets with the same name will be treated as the same set.
        return this.getName().equals(other.getName());
    }
}
