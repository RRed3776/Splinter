package me.rred.splinter.client.widgets.modals;

import me.rred.splinter.client.utils.SplinterColors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;

public class PopUpMessage {
    protected final int x, y;
    protected final String message;
    public boolean active = false;
    public PopUpMessage(int x, int y, String message) {
        this.x = x;
        this.y = y;
        this.message = message;
    }

    public void render(MatrixStack matrixStack, TextRenderer textRenderer) {
        if (!active) return;
        textRenderer.drawWithShadow(matrixStack, message, x, y, SplinterColors.TEXT);
    }
}
