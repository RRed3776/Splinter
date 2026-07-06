package me.rred.splinter.client.edit.gui;

import me.rred.splinter.client.SplinterClient;
import me.rred.splinter.client.edit.EditSession;
import me.rred.splinter.client.rendering.BlockOutlineRenderer;
import me.rred.splinter.client.routing.triggers.BlockBreakTrigger;
import me.rred.splinter.client.routing.triggers.PositionTrigger;
import me.rred.splinter.client.routing.triggers.TradeStartTrigger;
import me.rred.splinter.client.routing.triggers.Trigger;
import me.rred.splinter.client.utils.TriggersSharePos;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import org.w3c.dom.Text;

import java.awt.*;

public class EditOutlines {
    public static void render(EditSession editSession) {
        if (!SplinterClient.ssm.isInMap()) return;
        Trigger ogStart = editSession.getOgStart();
        Trigger ogEnd = editSession.getOgEnd();
        boolean isStart = editSession.getActiveSlot() == Trigger.TriggerSlot.START;

        // extract the hovered block for selection and draw outline
        Color hoverColor = Color.WHITE;
        if (isStart) {
            hoverColor = new Color(0, 100, 50);
        } else {
            hoverColor = new Color(100, 0, 50);
        }

        BlockPos hoveredPos = getHoveredPos(editSession.getActiveType());
        editSession.setHoveredPos(hoveredPos);
        if (hoveredPos != null) {
            new BlockOutlineRenderer(hoveredPos, hoverColor).render();
        }

        // selected block outlines
        boolean activeShared = TriggersSharePos.check(ogStart, ogEnd);
        renderTriggerOutline(ogStart, false, Color.GREEN , 0f);
        renderTriggerOutline(ogEnd, false, Color.RED, activeShared ? 0.05f : 0f);

        // pending block outlines
        Trigger pendingStart = editSession.getPendingStart();
        Trigger pendingEnd = editSession.getPendingEnd();

        boolean pendingShared = TriggersSharePos.check(pendingStart, pendingEnd);
        boolean startsShare = TriggersSharePos.check(pendingStart, ogStart);
        boolean endsShare = TriggersSharePos.check(pendingEnd, ogEnd);

        if (pendingStart != null && !startsShare) {
            renderTriggerOutline(pendingStart, true, new Color(0, 200, 100), 0f);
        }
        if (pendingEnd != null && !endsShare) {
            renderTriggerOutline(pendingEnd, true, new Color(200, 0, 100), pendingShared ? 0.05f : 0f);
        }
    }

    private static BlockPos getHoveredPos(Trigger.TriggerType type) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return null;
        if (!(client.crosshairTarget instanceof BlockHitResult hit)) return null;
        if (type == null) return null;

        // block break: return non-air selected block
        switch (type) {
            case BLOCK_BREAK -> {
                BlockPos pos = hit.getBlockPos();
                return client.world.getBlockState(pos).isAir() ? null : pos;
            }
            case POSITION, TRADE_START -> {
                BlockPos pos = hit.getBlockPos();
                return client.world.getBlockState(pos).isAir() ? pos : pos.offset(hit.getSide());
            }
        }
        return null;
    }

    private static void renderTriggerOutline(Trigger trigger, boolean pending, Color color, float padding) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return;

        if (trigger instanceof BlockBreakTrigger bt && bt.getPos() != null) {
            // check if its air and a pending change
            if (pending && client.world.getBlockState(bt.getPos()).isAir()) {
                bt.setPos(null);
            } else {
                new BlockOutlineRenderer(bt.getPos(), color, padding).render();
            }
        }

        if (trigger instanceof PositionTrigger pt && pt.getPos() != null) {
            new BlockOutlineRenderer(pt.getPos(), color, padding).render();
        }

        if (trigger instanceof TradeStartTrigger tst && tst.getPos() != null) {
            new BlockOutlineRenderer(tst.getPos(), color, padding).render();
        }
    }

}
