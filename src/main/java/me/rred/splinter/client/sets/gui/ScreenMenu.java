package me.rred.splinter.client.sets.gui;

import me.rred.splinter.client.sets.SplinterSet;
import me.rred.splinter.client.utils.SplinterColors;
import me.rred.splinter.client.utils.TruncateText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;

public class ScreenMenu {
    private List<ContextMenu.Option> options = new ArrayList<>();
    private int x, y;
    private int width;
    private static final int ITEM_HEIGHT = 12;
    private boolean visible = false;
    private int hoveredOption = -1;

    public void open(int x, int y, int width, List<ContextMenu.Option> options) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.options = options;
        this.visible = true;
    }

    public void render(MatrixStack matrixStack, TextRenderer textRenderer, int mouseX, int mouseY) {
        if (!visible) return;
        int totalHeight = ITEM_HEIGHT * options.size();
        hoveredOption = -1;
        int disabledColor = SplinterColors.alpha(SplinterColors.MUTED_SLATE, 0xE0);

        // background
        DrawableHelper.fill(matrixStack, x, y, x + width, y + totalHeight, SplinterColors.CONTEXT_MENU);

        // options
        for (int i = 0; i < options.size(); i++) {
            ContextMenu.Option option = options.get(i);
            int optionY = y + (i * ITEM_HEIGHT);

            boolean isHovered = (
                    option.active &&
                            mouseX >= x && mouseX <= x + width &&
                            mouseY >= optionY && mouseY < optionY + ITEM_HEIGHT
            );

            if (isHovered) {
                hoveredOption = i;
                DrawableHelper.fill(matrixStack, x, optionY, x + width, optionY + ITEM_HEIGHT, disabledColor);
            }

            int textColor = !option.active ? disabledColor : option.color;
            textRenderer.drawWithShadow(matrixStack, option.label, x + 3, optionY + 2, textColor);
        }
    }

    public void close() {
        visible = false;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean handleClick(double mouseX, double mouseY) {
        if (hoveredOption >= 0 && hoveredOption < options.size()) {
            options.get(hoveredOption).action.run();
            close();
            return true;
        }
        return false;
    }
}
