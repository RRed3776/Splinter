package me.rred.splinter.client.edit.gui;

import me.rred.splinter.client.edit.EditSession;
import me.rred.splinter.client.SplinterClient;
import me.rred.splinter.client.routing.triggers.Trigger;
import me.rred.splinter.client.keyboard.KeyInputHandler;
import me.rred.splinter.client.utils.ScissorUtil;
import me.rred.splinter.client.utils.SplinterColors;
import me.rred.splinter.client.widgets.SplinterButton;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import org.lwjgl.glfw.GLFW;

public class EditScreen extends Screen {

    private int screenTop, screenBottom, screenLeft, screenRight;
    private int offset = 50;
    private EditSession editSession;
    private static final int textColor = 0xFFFFFF;
    private int borderWidth = 1;

    private TriggerTypeSlider slider;

    public EditScreen(EditSession editSession) {
        super(new LiteralText("Edit Route - " + SplinterClient.setManager.getActiveSet().getName()));
        this.editSession = editSession;
    }

    @Override
    protected void init() {
        buttons.clear();
        children.clear();

        // edit UI screen dimensions
        screenTop = offset - 25;
        screenBottom = height - offset;
        screenLeft = offset;
        screenRight = width - offset;

        int btnHeight = height / 12;
        int btnY = screenBottom - (int)(btnHeight * 1.25);
        int btnWidth = width / 6;
        int totalBtnWidth = (int)(btnWidth * 3.5); // leave .25 between each button
        int btnStartX = (width - totalBtnWidth) / 2;

        // TriggerType slider
        int sliderOffset = width / 6;
        int sliderWidth = width - sliderOffset * 2;
        int sliderHeight = height / 10;
        int sliderY = btnY - sliderHeight - (int)(btnHeight * 1.25);
        slider = new TriggerTypeSlider(sliderOffset, sliderY, sliderWidth, sliderHeight, editSession.getActiveType());
        slider.setOnSelectionChanged(type -> editSession.setActiveType(type));

        // cancel button
        addButton(new SplinterButton(
                btnStartX, btnY, btnWidth, btnHeight,
                new LiteralText("CANCEL"),
                editSession::cancel
        ));

        // switch slot button
        int btnStart2X = btnStartX + (int)(btnWidth * 0.25) + btnWidth;
        addButton(new SplinterButton(
                btnStart2X, btnY, btnWidth, btnHeight,
                new LiteralText("SWITCH SLOT"),
                editSession::toggleActiveSlot

        ));
        int btnStart3X = btnStart2X + (int)(btnWidth * 0.25) + btnWidth;
        // confirm button
        if (editSession.hasChanges()) {
            addButton(new SplinterButton(
                    btnStart3X, btnY, btnWidth, btnHeight,
                    new LiteralText("CONFIRM"),
                    editSession::confirm
            ));
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        // GUI title
        drawCenteredText(matrixStack, textRenderer, title, width / 2, 10, textColor);

        int middlePanelColor = SplinterColors.alpha(SplinterColors.MIDDLE_PANEL, 0xE0); // 88% opacity
        fill(matrixStack, screenLeft, screenTop, screenRight, screenBottom, middlePanelColor);

        // outer border, screen is inside the border
        int outerBorderColor = SplinterColors.BORDER;
        // top
        fill(matrixStack, screenLeft - borderWidth, screenTop - borderWidth, screenRight + borderWidth, screenTop, outerBorderColor);
        // bottom
        fill(matrixStack, screenLeft - borderWidth, screenBottom, screenRight + borderWidth, screenBottom + borderWidth, outerBorderColor);
        // left
        fill(matrixStack, screenLeft - borderWidth, screenTop, screenLeft, screenBottom, outerBorderColor);
        // right
        fill(matrixStack, screenRight, screenTop, screenRight + borderWidth, screenBottom, outerBorderColor);

        // current active slot
        String slotText = editSession.getActiveSlot() == Trigger.TriggerSlot.START ?
                "Start" : "End";
        int slotX = (width - textRenderer.getWidth(slotText)) / 2;
        textRenderer.drawWithShadow(matrixStack, slotText, slotX, screenTop + 10, textColor);

        // render slider
        slider.render(matrixStack, textRenderer, mouseX, mouseY);
        super.render(matrixStack, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // leave screen with Esc or specified hotkey
        if (keyCode == GLFW.GLFW_KEY_ESCAPE || KeyInputHandler.GUI_EDIT_BIND.getKeyBinding().matchesKey(keyCode, scanCode)) {
            EditScreen.toggle();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (slider.isMouseOver(mouseX, mouseY)) {
            slider.scroll(amount);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    public static void toggle() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.currentScreen instanceof EditScreen) {
            client.openScreen(null);
        } else {
            EditSession edit = SplinterClient.ssm.getEditSession();
            if (edit != null) client.openScreen(new EditScreen(edit));
        }
    }
}
