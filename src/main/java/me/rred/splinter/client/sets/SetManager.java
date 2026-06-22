package me.rred.splinter.client.sets;

import me.rred.splinter.client.SplinterClient;
import me.rred.splinter.client.routing.Route;

import java.util.ArrayList;
import java.util.List;

public class SetManager {
    private List<SplinterSet> sets = new ArrayList<>();
    private SplinterSet activeSet = null;
    private SplinterSet displayedSetA = null;
    private SplinterSet displayedSetB = null;


    public SetManager() {
         Route defaultRoute = SplinterClient.routeRegistry.getDefaultRoute();
         SplinterSet generalSet = new SplinterSet("general", defaultRoute);
         sets.add(generalSet);
         activeSet = generalSet;
         displayedSetA = generalSet;
    }

    public void addTime(long ms) {
        activeSet.addTime(ms);
    }

    public void createSet(String name) {
        if (sets.size() >= 20) return; // arbitrary cap of 20 for now
        Route defaultRoute = SplinterClient.routeRegistry.getDefaultRoute();
        SplinterSet newSet = new SplinterSet(name, defaultRoute);
        sets.add(newSet);
    }
    public void addSet(SplinterSet set) {
        if (sets.size() >= 20) return;
        sets.add(set);
    }

    public void deleteSet(SplinterSet set) {
        if (sets.size() == 1) return;
        sets.remove(set);
        if (activeSet == set) activeSet = sets.get(0); // fall back first set
        if (displayedSetA == set) displayedSetA = null;
        if (displayedSetB == set) displayedSetB = null;
    }

    public SplinterSet getActiveSet() {
        return activeSet;
    }

    public void setActiveSet(SplinterSet set) {
        activeSet = set;
    }

    public SplinterSet getDisplayedSetA() { return displayedSetA; };

    public void setDisplayedSetA(SplinterSet set) {
        // swap
        if (set == displayedSetB) {
            displayedSetB = displayedSetA;
        }
        displayedSetA = set;
    }
    public void clearDisplayedSetA() {
        displayedSetA = null;
    }


    public SplinterSet getDisplayedSetB() { return displayedSetB; };

    public void setDisplayedSetB(SplinterSet set) {
        // swap
        if (set == displayedSetA) {
            displayedSetA = displayedSetB;
        }
        displayedSetB = set;
    }

    public void clearDisplayedSetB() {
        displayedSetB = null;
    }

    public List<SplinterSet> getAllSets() {
        return sets;
    }

}
