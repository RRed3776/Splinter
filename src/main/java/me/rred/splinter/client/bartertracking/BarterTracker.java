package me.rred.splinter.client.bartertracking;

import me.rred.splinter.Splinter;
import net.minecraft.item.ItemStack;

public class BarterTracker {
    private int totalBarters = 0;
    BarterData currentBarterData = new BarterData(0, 0, 0, 0);;
    boolean locked = false;

    public void updateBarterData(ItemStack itemstack) {
        if (locked) return;
        currentBarterData.updateAll(itemstack);
        increment();
    }

    public void increment() {
        totalBarters++;
        Splinter.LOGGER.info("barters: {}", totalBarters);
    }

    public void clear() {
        totalBarters = 0;
        currentBarterData.clearAll();
        locked = false;
    }

    public void lockTracking() {
        locked = true;
    }

    public int getTotalBarters() {
        return totalBarters;
    }

    public int getPearls() {
        return currentBarterData.getPearls();
    }

    public int getStrings() {
        return currentBarterData.getStrings();
    }

    public int getGlowstone() {
        return currentBarterData.getGlowstone();
    }

    public int getObsidian() {
        return currentBarterData.getObsidian();
    }

}
