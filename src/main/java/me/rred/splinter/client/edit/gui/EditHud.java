package me.rred.splinter.client.edit.gui;

import me.rred.splinter.client.SplinterClient;
import me.rred.splinter.client.edit.EditSession;
import me.rred.splinter.client.keyboard.KeyInputHandler;
import me.rred.splinter.client.routing.triggers.*;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.ARBTextureEnvDot3;

public class EditHud {
    public static void render(MatrixStack matrixStack, TextRenderer textRenderer, EditSession editSession) {
        Trigger ogStart = editSession.getOgStart();
        Trigger ogEnd = editSession.getOgEnd();
        Trigger pendingStart = editSession.getPendingStart();
        Trigger pendingEnd = editSession.getPendingEnd();

        // HUD text;
        int x = 10;
        int y = 25; // below state indicator

        // start and end changes text

        String startText = "START: " + getTriggerHandle(ogStart);
        String endText = "END: " + getTriggerHandle(ogEnd);
        int textHeight = textRenderer.fontHeight + 3;

        if (!pendingStart.equals(ogStart)) startText += " → " + getTriggerHandle(pendingStart);
        if (!pendingEnd.equals(ogEnd)) endText += " → " + getTriggerHandle(pendingEnd);

        textRenderer.drawWithShadow(matrixStack, startText, x, y, 0xFFBB00);
        textRenderer.drawWithShadow(matrixStack, endText, x, y + textHeight, 0xFFBB00);

        // confirm message
        if(editSession.hasChanges()) {
            String confirmMessage = "Changes Made. Confirm or Cancel in GUI";
            textRenderer.drawWithShadow(matrixStack, confirmMessage, x, y + textHeight * 2, 0xFF6A00);
        }

        // hotkey tips, gui and selection
        int gap = 40;
        // gui open
        String guiOpenBind = KeyInputHandler.GUI_EDIT_BIND.getKeyBinding().getBoundKeyLocalizedText().getString();
        String guiOpenText = "Open Edit GUI - " + guiOpenBind;
        textRenderer.drawWithShadow(matrixStack, guiOpenText, x, y + textHeight + gap, 0xFFBB00);
        // edit select
        // edit mode hint
        String editSelectBind = KeyInputHandler.EDIT_SELECT_BIND.getKeyBinding().getBoundKeyLocalizedText().getString();
        String editSelectText = "Select Block Pos - " + editSelectBind;
        textRenderer.drawWithShadow(matrixStack, editSelectText, x, y + textHeight * 2 + gap, 0xFFBB00);
    }

    private static String getTriggerHandle(Trigger trigger) {
        if (trigger == null) return "NONE";
        return switch (trigger.getType()) {
            case MAP -> "MAP";
            case BLOCK_BREAK -> {
                BlockPos pos = ((BlockBreakTrigger) trigger).getPos();
                yield pos != null ? "BREAK (" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")" : "BREAK (unset)";
            }
            case POSITION -> {
                BlockPos pos = ((PositionTrigger) trigger).getPos();
                yield pos != null ? "POS (" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")" : "POS (unset)";
            }
            case TRADE_START -> {
                BlockPos pos = ((TradeStartTrigger) trigger).getPos();
                yield pos != null ? " TRADE_START (" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")" : "POS (unset)";
            }
            case TRADE_END -> {
                int barterCap = ((TradeEndTrigger) trigger).getBarterCap();
                yield "TRADE_END: " + barterCap + " barters";
            }
        };
    }
}
