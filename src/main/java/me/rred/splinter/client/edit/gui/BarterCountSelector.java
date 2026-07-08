package me.rred.splinter.client.edit.gui;

import me.rred.splinter.Splinter;
import me.rred.splinter.client.routing.triggers.Trigger;
import me.rred.splinter.client.utils.SplinterColors;
import me.rred.splinter.client.widgets.SplinterSmallButton;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;

import static net.minecraft.client.gui.DrawableHelper.fill;

public class BarterCountSelector {
    private final int x, y, width, height;
    private boolean visible;
    private int selectedCap;
    private SplinterSmallButton decrement;
    private SplinterSmallButton increment;

    public BarterCountSelector(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void openSelector(int initialCap) {
        this.selectedCap = initialCap;
        visible = true;
        int offset = 1;
        int scalar = height - 2 - offset * 2;
        int buttonsY = y + 1 + offset;
        decrement = new SplinterSmallButton(x + 1 + offset, buttonsY, scalar,
                "<", SplinterColors.TEXT, () -> selectedCap--);

        increment = new SplinterSmallButton(x + width - scalar, buttonsY, scalar,
                ">", SplinterColors.TEXT, () -> selectedCap++);
    }

    public void close() {
        visible = false;
    }

    public void render(MatrixStack matrixStack, TextRenderer textRenderer, int mouseX, int mouseY) {
        // border then inside fill
        if (!visible) return;
        fill(matrixStack, x, y, x + width, y + height, SplinterColors.BORDER);
        fill(matrixStack, x + 1, y + 1, x + width - 1, y + height - 1, SplinterColors.MODAL_BG);

        if (decrement != null) {
            decrement.renderButton(matrixStack, mouseX, mouseY);
        }
        if (increment != null) {
            increment.renderButton(matrixStack, mouseX, mouseY);
        }
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width &&
                mouseY >= y && mouseY <= y + height;
    }

    public boolean handleClick(double mouseX, double mouseY, int button) {
        if (decrement != null && decrement.handleClick(mouseX, mouseY, button)) return true;
        if (increment != null && increment.handleClick(mouseX, mouseY, button)) return true;
        return false;
    }


}
