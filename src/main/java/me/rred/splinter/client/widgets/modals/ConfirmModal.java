package me.rred.splinter.client.widgets.modals;

import me.rred.splinter.client.utils.SplinterColors;
import me.rred.splinter.client.widgets.SplinterButton;
import me.rred.splinter.client.widgets.SplinterExitButton;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import org.lwjgl.glfw.GLFW;

import java.util.List;

import static net.minecraft.client.gui.DrawableHelper.fill;

public class ConfirmModal extends SplinterModal{
    private final Runnable onConfirm;
    private int startTextY;
    private SplinterExitButton exitButton;

    public ConfirmModal(String message, Runnable onConfirm) {
        this.message = message;
        this.onConfirm = onConfirm;
    }

    public ConfirmModal(List<String> messages, Runnable onConfirm) {
        this.messages = messages;
        this.onConfirm = onConfirm;
    }

    public void openModal(int screenWidth, int screenHeight) {
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;
        visible = true;

        if (message != null) {
            this.width = textRenderer.getWidth(message) + 20;
        } else { // read messages
            int maxWidth = 0;
            for (String text : messages) {
                if (textRenderer.getWidth(text) > maxWidth) {
                    maxWidth = textRenderer.getWidth(text);
                }
            }
            this.width = maxWidth + 20;
        }

        int lineHeight = textRenderer.fontHeight * 2;
        this.height = lineHeight * 3;
        // extend height if there are multiple lines
        if (messages != null) {
            this.height = lineHeight * (2 + messages.size());
        }

        this.x = (screenWidth - width) / 2;
        this.y = (screenHeight - height) / 2;
        // initialize exit button top right with scalar 9
        exitButton = new SplinterExitButton(x + width - 10, y + 1, 9, this::close);

        startTextY = this.y + (int) (lineHeight * 0.5);

        int buttonWidth = (int)(width * 0.75);
        int buttonX = x + (width - buttonWidth) / 2;
        // account for multiple lines, move the confirm button farther down
        int buttonY = messages == null ? startTextY + lineHeight : startTextY + lineHeight * messages.size();

        confirmButton = new SplinterButton(buttonX, buttonY, buttonWidth, lineHeight,
                new LiteralText("CONFIRM"),
                onConfirm
        );
    }

    public void render(MatrixStack matrixStack, TextRenderer textRenderer,
                       int mouseX, int mouseY) {
        // push the modal 1 pixel in Z to put it in front of the main GUI
        if (!visible) return;
        matrixStack.push();
        matrixStack.translate(0, 0, 1);

        // border then inside fill
        fill(matrixStack, x, y, x + width, y + height, SplinterColors.BORDER);
        fill(matrixStack, x + 1, y + 1, x + width - 1, y + height - 1, SplinterColors.MODAL_BG);

        int lineHeight = textRenderer.fontHeight * 2;
        if (message != null) {
            int textX = x + (width / 2) - textRenderer.getWidth(message) / 2;
            textRenderer.drawWithShadow(matrixStack, message, textX, startTextY, SplinterColors.TEXT);
        } else {
            for (int i = 0; i < messages.size(); i++) {
                int textX = x + (width / 2) - textRenderer.getWidth(messages.get(i)) / 2;
                textRenderer.drawWithShadow(matrixStack, messages.get(i), textX, startTextY + lineHeight * i, SplinterColors.TEXT);
            }
        }

        if (confirmButton != null) {
            confirmButton.render(matrixStack, mouseX, mouseY, 0);
        }

        if (exitButton != null) {
            exitButton.renderButton(matrixStack, mouseX, mouseY);
        }
        matrixStack.pop();
    }

    public boolean handleClick(double mouseX, double mouseY, int button) {
        if (exitButton != null && exitButton.handleClick(mouseX, mouseY, button)) return true;
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
