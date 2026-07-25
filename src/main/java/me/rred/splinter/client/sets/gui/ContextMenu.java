package me.rred.splinter.client.sets.gui;

import me.rred.splinter.Splinter;
import me.rred.splinter.client.sets.SplinterSet;
import me.rred.splinter.client.utils.SplinterColors;
import me.rred.splinter.client.utils.TruncateText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;

public class ContextMenu {
    public static class Option {
        public final String label;
        public final Runnable action;
        public final int color;
        public final boolean active;

        public Option(String label, Runnable action, int color, boolean active) {
            this.label = label;
            this.action = action;
            this.color = color;
            this.active = active;
        }

        public String getLabel() {
            return label;
        }
    }

    private List<Option> options = new ArrayList<>();
    private int x, y;
    private static final int WIDTH = 80;
    private static final int ITEM_HEIGHT = 12;
    private boolean visible = false;
    private String label;
    private int hoveredOption = -1;

    public void open(int x, int y, int screenBottom, String label, List<Option> options) {
        this.x = x;
        this.y = y;
        this.label = label;
        this.options = options;
        shiftMenu(screenBottom);
        this.visible = true;
    }

    public void render(MatrixStack matrixStack, TextRenderer textRenderer, int mouseX, int mouseY) {
        if (!visible) return;
        int totalHeight = ITEM_HEIGHT * (options.size() + 1) + 1;
        hoveredOption = -1;
        int disabledColor = SplinterColors.alpha(SplinterColors.MUTED_SLATE, 0xE0);

        // background
        DrawableHelper.fill(matrixStack, x, y, x + WIDTH, y + totalHeight, SplinterColors.CONTEXT_MENU);

        // name header
        int headerColor = SplinterColors.alpha(SplinterColors.SOFT_BLUE, 0xE0);
        textRenderer.drawWithShadow(matrixStack, TruncateText.truncate(label, WIDTH - 3, textRenderer), x + 3, y + 2, headerColor);

        // header divider
        DrawableHelper.fill(matrixStack, x, y + ITEM_HEIGHT, x + WIDTH, y + ITEM_HEIGHT + 1, disabledColor);

        // options
        for (int i = 0; i < options.size(); i++) {
            Option option = options.get(i);
            int optionY = y + ITEM_HEIGHT + (i * ITEM_HEIGHT) + 1;

            boolean isHovered = (
                option.active &&
                mouseX >= x && mouseX <= x + WIDTH &&
                mouseY >= optionY && mouseY < optionY + ITEM_HEIGHT
            );

            if (isHovered) {
                hoveredOption = i;
                DrawableHelper.fill(matrixStack, x, optionY, x + WIDTH, optionY + ITEM_HEIGHT, disabledColor);
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

    public boolean handleClick() {
        if (!visible) return false;
        if (hoveredOption >= 0 && hoveredOption < options.size()) {
            Option selected = options.get(hoveredOption);
            selected.action.run();
            if (!selected.getLabel().equals("Route Options")) {
                close();
            }
            return true;
        }
        close();
        return false;
    }

    public int[] getPos() {
        return new int[]{x, y};
    }

    private void shiftMenu(int screenBottom) {
        int totalHeight = ITEM_HEIGHT * (options.size() + 1) + 1;
        if (y + totalHeight < screenBottom) return;
        this.y = y - totalHeight;
    }

}
