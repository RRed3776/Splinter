package me.rred.splinter.client.bartertracking;

import me.rred.splinter.Splinter;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class BarterData {
    private int pearls;
    private int strings; // "string" will probably get confusing
    private int glowstone;
    private int obsidian;

    public BarterData(int pearls, int strings, int glowstone, int obsidian) {
        this.pearls = pearls;
        this.strings = strings;
        this.glowstone = glowstone;
        this.obsidian = obsidian;
    }

    public void updateAll(ItemStack itemstack) {
        String itemName = itemstack.getName().toString();
        if (itemName.contains("crying_obsidian")) return;

        if (itemName.contains("ender_pearl")) {
            pearls += itemstack.getCount();
            Splinter.LOGGER.info("pearls updated!");
        } else if (itemName.contains("string")) {
            strings += itemstack.getCount();
            Splinter.LOGGER.info("string updated!");
        } else if (itemName.contains("glowstone_dust")) {
            glowstone += itemstack.getCount();
            Splinter.LOGGER.info("glowstone updated!");
        } else if (itemName.contains("obsidian")) {
            obsidian += itemstack.getCount();
            Splinter.LOGGER.info("obby updated!");
        }
    }

    public void clearAll() {
        pearls = 0;
        strings = 0;
        glowstone = 0;
        obsidian = 0;
    }

    public int getPearls() {
        return pearls;
    }

    public int getStrings() {
        return strings;
    }

    public int getGlowstone() {
        return glowstone;
    }

    public int getObsidian() {
        return obsidian;
    }

}
