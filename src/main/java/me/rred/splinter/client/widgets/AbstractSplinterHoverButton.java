package me.rred.splinter.client.widgets;

public abstract class AbstractSplinterHoverButton {
    protected int x, y, width, height;

    public AbstractSplinterHoverButton(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width &&
                mouseY >= y && mouseY <= y + height;
    }
}
