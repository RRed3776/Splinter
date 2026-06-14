package me.rred.splinter.client.widgets;

import net.minecraft.client.util.math.MatrixStack;

public abstract class AbstractSplinterHoverButton {
    protected int x, y, scalar;
    protected boolean hovered;
    public boolean visible = true;

    public AbstractSplinterHoverButton(int x, int y, int len) {
        this.x = x;
        this.y = y;
        this.scalar = len;
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + scalar &&
                mouseY >= y && mouseY <= y + scalar;
    }

    public abstract void onPress();

    public abstract void renderButton(MatrixStack matrixStack, int mouseX, int mouseY);
}
