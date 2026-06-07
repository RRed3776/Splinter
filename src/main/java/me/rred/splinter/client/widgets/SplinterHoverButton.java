package me.rred.splinter.client.widgets;

import net.minecraft.text.Text;

public class SplinterHoverButton extends AbstractSplinterHoverButton {
    private final Runnable action;
    private int x, y, width, height;
    private Text icon;

    public SplinterHoverButton(int x, int y, int width, int height, Text icon, Runnable action) {
        super(x, y, width, height);
        this.icon = icon;
        this.action = action;
    }

    public void onPress() {
        action.run();
    }




}
