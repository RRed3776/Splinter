package me.rred.splinter.client.edit.gui;

import me.rred.splinter.Splinter;
import me.rred.splinter.client.routing.triggers.Trigger;
import me.rred.splinter.client.utils.ScissorUtil;
import me.rred.splinter.client.utils.SplinterColors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static net.minecraft.client.gui.DrawableHelper.fill;

public class TriggerTypeSlider {
    private final int x, y, width, height;
    private int selectedIdx;
    private final List<Trigger.TriggerType> types;
    private Consumer<Trigger.TriggerType> onSelectionChanged;

    private static final long ANIM_DURATION_MS = 150L;
    private long animStartMs = -ANIM_DURATION_MS; // start finished
    private float animDirection = 0f; // +1 = forward, -1 = backwards

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

    public void setOnSelectionChanged(Consumer<Trigger.TriggerType> listener) {
        this.onSelectionChanged = listener;
    }

    public void render(MatrixStack matrixStack, TextRenderer textRenderer,
                       int mouseX, int mouseY) {
        // border then inside fill
        fill(matrixStack, x, y, x + width, y + height, SplinterColors.BORDER);
        fill(matrixStack, x + 1, y + 1, x + width - 1, y + height - 1, SplinterColors.MODAL_BG);

        MinecraftClient client = MinecraftClient.getInstance();
        double scale = client.getWindow().getScaleFactor();
        ScissorUtil.enable(scale, x + 1, y, width - 2, height);

        int textGap = width / 5;
        int nextIdx = (selectedIdx + 1) % types.size();
        int prevIdx = (selectedIdx + types.size() - 1) % types.size();

        long elapsed = System.currentTimeMillis() - animStartMs;
        float t = Math.min(1f, elapsed / (float) ANIM_DURATION_MS);
        float ease = 1f - (1f - t) * (1f - t);
        int offset = Math.round(animDirection * textGap * (1f - ease));

        // selected type
        String selectedTypeText = getSelected().toString();
        int selectedTextWidth = textRenderer.getWidth(selectedTypeText);
        int centerX = x + (width - selectedTextWidth) / 2;
        int textY = y + (height - textRenderer.fontHeight) / 2;
        textRenderer.drawWithShadow(matrixStack, selectedTypeText, centerX + offset, textY, SplinterColors.TEXT);

        // prev type
        String prevText = types.get(prevIdx).toString();
        int prevX = centerX - textGap - textRenderer.getWidth(prevText);
        textRenderer.drawWithShadow(matrixStack, prevText, prevX + offset, textY, SplinterColors.TEXT);

        // next type
        String nextText = types.get(nextIdx).toString();
        int nextX = centerX + selectedTextWidth + textGap;
        textRenderer.drawWithShadow(matrixStack, nextText, nextX + offset, textY, SplinterColors.TEXT);
        ScissorUtil.disable();
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width &&
                mouseY >= y && mouseY <= y + height;
    }

    public void scroll(double amount) {
        if (amount > 0) {
            selectedIdx = (selectedIdx + 1) % types.size();
            animDirection = 1f;
        } else {
            selectedIdx = (selectedIdx - 1 + types.size()) % types.size();
            animDirection = -1f;
        }
        animStartMs = System.currentTimeMillis();

        if (onSelectionChanged != null) {
            onSelectionChanged.accept(getSelected());
        }
    }
}
