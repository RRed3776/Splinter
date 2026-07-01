package me.rred.splinter.client.edit.gui;

import me.rred.splinter.Splinter;
import me.rred.splinter.client.routing.triggers.Trigger;
import me.rred.splinter.client.utils.SplinterColors;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.client.gui.DrawableHelper.fill;

public class TriggerTypeSlider {
    private int x, y, width, height;
    private int selectedIdx;
    private List<Trigger.TriggerType> types = new ArrayList<>();

    public TriggerTypeSlider(int x, int y, int width, int height, Trigger.TriggerType initial) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        types = List.of(Trigger.TriggerType.values());
        setSelectedIdx(initial);

    }

    public Trigger.TriggerType getSelected() {
        return types.get(selectedIdx);
    }

    private void setSelectedIdx(Trigger.TriggerType type) {
        selectedIdx = types.indexOf(type);
    }

    public void render(MatrixStack matrixStack, TextRenderer textRenderer,
                       int mouseX, int mouseY) {
        // border then inside fill
        fill(matrixStack, x, y, x + width, y + height, SplinterColors.BORDER);
        fill(matrixStack, x + 1, y + 1, x + width - 1, y + height - 1, SplinterColors.MODAL_BG);

        String selectedTypeText = getSelected().toString();
        int selectedTextWidth = textRenderer.getWidth(selectedTypeText);
        int centerX = x + (width - selectedTextWidth) / 2;
        int textY = y + (height - textRenderer.fontHeight) / 2;

        textRenderer.drawWithShadow(matrixStack, selectedTypeText, centerX, textY, SplinterColors.TEXT);
        int
    }

}
