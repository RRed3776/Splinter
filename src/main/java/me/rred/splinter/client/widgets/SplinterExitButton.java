package me.rred.splinter.client.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import me.rred.splinter.client.utils.SplinterColors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class SplinterExitButton extends AbstractSplinterHoverButton {
    private final Runnable action;

    public SplinterExitButton(int x, int y, int scalar, Runnable action) {
        super(x, y, scalar);
        this.action = action;
    }

    @Override
    public void onPress() {
        action.run();
    }

    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY) {
        if (!visible) return;
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        TextRenderer textRenderer = minecraftClient.textRenderer;
        hovered = isMouseOver(mouseX, mouseY);

        int fillColor = hovered ? 0xFF666666 : 0x00000000;
        int deleteColor = 0xFFFF2222;

        // for now hardcode it for scalar 11
        int delTextX = 1 + x + (scalar - textRenderer.getWidth("x")) / 2; // 2 px from right edge
        int delTextY = y + (scalar - textRenderer.fontHeight) / 2;

        RenderSystem.enableDepthTest();

        DrawableHelper.fill(matrixStack, x, y, x + scalar, y + scalar, fillColor);
        textRenderer.draw(matrixStack, "x", delTextX, delTextY, deleteColor);
    }

    public boolean handleClick(double mouseX, double mouseY, int button) {
        if (!hovered || !visible) return false;
        if (button == 0) {
            visible = false;
            onPress();
            return true;
        }
        return false;
    }
}
