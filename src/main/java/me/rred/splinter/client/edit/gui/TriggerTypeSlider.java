package me.rred.splinter.client.edit.gui;

import me.rred.splinter.Splinter;
import me.rred.splinter.client.routing.triggers.Trigger;
import me.rred.splinter.client.utils.ScissorUtil;
import me.rred.splinter.client.utils.SplinterColors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.system.CallbackI;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static net.minecraft.client.gui.DrawableHelper.fill;

public class TriggerTypeSlider {
    private final int x, y, width, height;
    private int selectedIdx;
    private List<Trigger.TriggerType> types;
    private Consumer<Trigger.TriggerType> onSelectionChanged;

    private static final long ANIM_DURATION_MS = 250L;
    private long animStartMs;
    private float animDirection = 0f; // +1 = forward, -1 = backwards

    public TriggerTypeSlider(int x, int y, int width, int height, Trigger.TriggerType initial, Trigger.TriggerSlot slot) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        updateTypes(slot);
        setSelectedIdx(initial);
    }

    public void updateTypes(Trigger.TriggerSlot slot) {
        types = new ArrayList<>(List.of(Trigger.TriggerType.values()));
        if (slot == Trigger.TriggerSlot.START) {
            types.remove(Trigger.TriggerType.TRADE_END);
        } else {
            types.remove(Trigger.TriggerType.TRADE_START);
        }
    }

    public Trigger.TriggerType getSelected() {
        return types.get(selectedIdx);
    }

    public void setSelectedIdx(Trigger.TriggerType type) {
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

        int centerX = x + width / 2;
        int textSpacing = width / 3;
        int textY = y + (height - textRenderer.fontHeight) / 2;

        // sliding animation offset
        long elapsed = System.currentTimeMillis() - animStartMs;
        float t = Math.min(1f, elapsed / (float) ANIM_DURATION_MS);
        float ease = 1f - (1f - t) * (1f - t);
        float offset = animDirection * (1f - ease);

        // draw five types (two offscreen, center is selected)
        for (int i = -2; i <= 2; i++) {
            int idx = ((selectedIdx + i) + types.size()) % types.size();
            boolean isOuter = Math.abs(i) == 2;
            if (isOuter && elapsed >= ANIM_DURATION_MS) continue; // skip offscreen during rest

            String text = types.get(idx).toString();
            int textWidth = textRenderer.getWidth(text);
            float textCenterX = centerX + (i + offset) * textSpacing;
            int textX = Math.round(textCenterX - textWidth / 2f);

            int color = (i == 0) ? SplinterColors.HIGHLIGHTED_TEXT : SplinterColors.TEXT;
            textRenderer.drawWithShadow(matrixStack, text, textX, textY, color);
        }

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
