package me.rred.splinter.client.widgets.modals;

import com.sun.jna.platform.unix.X11;
import me.rred.splinter.Splinter;
import me.rred.splinter.client.utils.SplinterColors;
import me.rred.splinter.client.widgets.SplinterButton;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.CallbackI;

import static net.minecraft.client.gui.DrawableHelper.fill;

public class InputModal extends SplinterModal{
    private final String allowedChars;
    private final Runnable onConfirm;
    private TextFieldWidget input;
    private int inputX;
    private int mainTextY;
    private int subTextY;
    private int popUpTextY;
    private boolean popUpActive;

    public InputModal(String message, Runnable onConfirm) {
        this.message = message;
        this.onConfirm = onConfirm;
        this.allowedChars = "[a-zA-z0-9_ ]";
    }

    public InputModal(String message, Runnable onConfirm, String allowedChars) {
        this.message = message;
        this.onConfirm = onConfirm;
        this.allowedChars = allowedChars;
    }

    public void openModal(int screenWidth, int screenHeight) {
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;

        this.width = (int)(screenWidth * 0.30);
        int subMessageWidth = textRenderer.getWidth(subMessage) + 20;
        if (width < subMessageWidth) {
            this.width = subMessageWidth;
        } // extend if the submessage is longer thn the normal width

        // make room for sub message if necessary
        int lineHeight = textRenderer.fontHeight * 2;
        if (subMessage != null) {
            this.height = (int)(lineHeight * 6); // 5.25, + .75; .5 for text, .25 for gap between messages
        } else {
            this.height = (int)(lineHeight * 5.25); // 4.5
        }

        this.x = (screenWidth - width) / 2;
        this.y = (screenHeight - height) / 2;
        visible = true;

        // build the lines from top to bottom
        mainTextY = this.y + (int)(lineHeight * 0.5); // text always has a .5 gap from top
        subTextY = mainTextY + (int)(lineHeight * 0.75);
        // shift if there is a submessage
        // +.75, .5 for message height, .25 for gap
        popUpTextY = subMessage == null ? mainTextY + (int)(lineHeight * 0.75) : subTextY + (int)(lineHeight * 0.75);

        int inputWidth = (int)(width * 0.85);
        inputX = x + (width - inputWidth) / 2;
        // +1, .5 for message height, .5 for gap
        int inputY = popUpTextY + lineHeight;

        int buttonWidth = (int)(width * 0.5);
        // +1.5, 1 for input length, .5 for gap between.
        // panel height must be .5 longer than the sum of these so there is a .5 gap from bottom
        // maybe height can be built after somehow, but probably not since y is dependent on it
        int buttonY = inputY + (int)(lineHeight * 1.5);

        input = new TextFieldWidget(textRenderer, inputX, inputY, inputWidth, lineHeight, new LiteralText(""));
        input.setMaxLength(20);
        input.setFocusUnlocked(true);

        confirmButton = new SplinterButton(inputX - 1, buttonY, buttonWidth, lineHeight,
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

        // pop up message
        if (popUpActive) {
            String popUpMessage = "Name Already Exists!";
            textRenderer.drawWithShadow(matrixStack, popUpMessage, inputX - 1 , popUpTextY, SplinterColors.TEXT);
        }

        if (subMessage != null) {
            textRenderer.drawWithShadow(matrixStack, message, inputX - 1 , mainTextY, SplinterColors.TEXT);
            textRenderer.drawWithShadow(matrixStack, subMessage, inputX - 1, subTextY, SplinterColors.SUB_TEXT);
        } else {
            textRenderer.drawWithShadow(matrixStack, message, inputX - 1, mainTextY, SplinterColors.TEXT);
        }

        if (confirmButton != null) {
            confirmButton.render(matrixStack, mouseX, mouseY, 0);
        }

        if (input != null) {
            input.render(matrixStack, mouseX, mouseY, 0);
        }
        matrixStack.pop();
    }

    public String getTextInput() {
        if (input == null) return null;
        return input.getText().trim();
    }

    public void setPopUp(boolean active) {
        popUpActive = active;
    }

    public boolean handleClick(double mouseX, double mouseY, int button) {
        if (input != null) input.mouseClicked(mouseX, mouseY, button);
        if (confirmButton != null && confirmButton.mouseClicked(mouseX, mouseY, button)) {
            if (closeGuard) return false;
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
        if (input != null) return input.keyPressed(keyCode, scanCode, modifiers);
        return false;
    }

    @Override
    public boolean charTyped(char chr, int keyCode) {
        // block unallowed chars, perhaps switch with a message later
        if (!String.valueOf(chr).matches(allowedChars)) return false;
        if (input != null) return input.charTyped(chr, keyCode);
        return false;
    }
}
