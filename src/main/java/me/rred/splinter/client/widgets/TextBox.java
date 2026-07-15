package me.rred.splinter.client.widgets;

import me.rred.splinter.client.utils.SplinterColors;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;

import java.util.List;

public class TextBox {
    private final int x, y;
    List<String> messages;

    public TextBox(int x, int y, List<String> messages) {
        this.x = x;
        this.y = y;
        this.messages = messages;
    }

    public void updateMessages(List<String> messages) {
        this.messages = messages;
    }

    public void render(MatrixStack matrixStack, TextRenderer textRenderer) {
        int lineHeight = textRenderer.fontHeight * 2;
        for (int i = 0; i < messages.size(); i++) {
            textRenderer.drawWithShadow(matrixStack, messages.get(i), x, y + lineHeight * i, SplinterColors.TEXT);
        }
    }

    public int getHeight(TextRenderer textRenderer) {
        int lineHeight = textRenderer.fontHeight * 2;
        return messages.size() * lineHeight;
    }

}
