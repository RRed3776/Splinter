package me.rred.splinter.client.widgets.modals;

import me.rred.splinter.client.utils.SplinterColors;
import me.rred.splinter.client.widgets.SplinterButton;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import org.lwjgl.glfw.GLFW;

import static net.minecraft.client.gui.DrawableHelper.fill;

public class ConfirmModal extends SplinterModal{
    private final Runnable onConfirm;
    private int textY;

    public ConfirmModal(String message, Runnable onConfirm) {
        this.message = message;
        this.onConfirm = onConfirm;
    }

    public void openModal(int screenWidth, int screenHeight) {
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;

        this.width = textRenderer.getWidth(message) + 20;

        int lineHeight = textRenderer.fontHeight * 2;
        this.height = lineHeight * 3;

        this.x = (screenWidth - width) / 2;
        this.y = (screenHeight - height) / 2;
        visible = true;

        textY = this.y + (int)(lineHeight * 0.5);

        int buttonWidth = (int)(width * 0.75);
        int buttonX = x + (width - buttonWidth) / 2;
        int buttonY = textY + lineHeight;

        confirmButton = new SplinterButton(buttonX, buttonY, buttonWidth, lineHeight,
                new LiteralText("CONFIRM"),
                onConfirm
        );
    }

    public void render(MatrixStack matrixStack, TextRenderer textRenderer,
                       int mouseX, int mouseY) {
        // push the modal 1 pixel in Z to put it in front of the main GUI
        matrixStack.push();
        matrixStack.translate(0, 0, 1);

        // border then inside fill
        fill(matrixStack, x, y, x + width, y + height, SplinterColors.BORDER);
        fill(matrixStack, x + 1, y + 1, x + width - 1, y + height - 1, SplinterColors.MODAL_BG);

        int textX = x + (width / 2) - textRenderer.getWidth(message) / 2;

        textRenderer.drawWithShadow(matrixStack, message, textX, textY, SplinterColors.TEXT);
        if (confirmButton != null) {
            confirmButton.render(matrixStack, mouseX, mouseY, 0);
        }
        matrixStack.pop();
    }

    public boolean handleClick(double mouseX, double mouseY, int button) {
        if (confirmButton != null && confirmButton.mouseClicked(mouseX, mouseY, button)) {
            close();
            return true;
        }
        return false;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            close();
            return true;
        }
        return false;
    }
}
