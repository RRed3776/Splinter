package me.rred.splinter.client.bartertracking;

import me.rred.splinter.Splinter;

public class BarterTracker {
    private int totalBarters = 0;

    public void increment() {
        totalBarters++;
        Splinter.LOGGER.info("barters: {}", totalBarters);
    }

    public int getTotalBarters() {
        return totalBarters;
    }
}
