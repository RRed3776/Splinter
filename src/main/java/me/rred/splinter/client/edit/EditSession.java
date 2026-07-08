package me.rred.splinter.client.edit;

import me.rred.splinter.client.SplinterClient;
import me.rred.splinter.client.edit.gui.EditHud;
import me.rred.splinter.client.edit.gui.EditOutlines;
import me.rred.splinter.client.routing.triggers.*;
import me.rred.splinter.client.edit.gui.EditScreen;
import me.rred.splinter.client.keyboard.KeyInputHandler;
import me.rred.splinter.client.rendering.BlockOutlineRenderer;
import me.rred.splinter.client.sets.SplinterSet;
import me.rred.splinter.client.utils.TriggersSharePos;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

import java.awt.*;

public class EditSession {
    private Trigger activeTrigger;
    private Trigger oldActiveTrigger;
    private Trigger ogStart;
    private Trigger ogEnd;
    private Trigger pendingStart;
    private Trigger pendingEnd;
    private final SplinterSet editSet;
    private BlockPos hoveredPos;


    public EditSession(SplinterSet set) {
        this.editSet = set; // most likely just the active set for now
        this.ogStart = set.getRoute().getStartTrigger();
        this.ogEnd = set.getRoute().getEndTrigger();
        this.pendingStart = ogStart;
        this.pendingEnd = ogEnd;
        this.activeTrigger = pendingStart; // initially edit the start trigger
    }

    public void render(MatrixStack matrixStack, TextRenderer textRenderer) {
        EditHud.render(matrixStack, textRenderer, this);
        EditOutlines.render(this);
    }

    public void selectActive() {
        if (activeTrigger == null) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        switch (getActiveType()) {
            case MAP -> {
                if (getActiveSlot() == Trigger.TriggerSlot.START) {
                    pendingStart = new MapTrigger(Trigger.TriggerSlot.START);
                } else {
                    pendingEnd = new MapTrigger(Trigger.TriggerSlot.END);
                }
            }
            case BLOCK_BREAK -> {
                if (hoveredPos == null) return;
                // prevent selection if position already in use
                Trigger other = getActiveSlot() == Trigger.TriggerSlot.START ? pendingEnd : pendingStart;
                if (other instanceof BlockBreakTrigger bt && hoveredPos.equals(bt.getPos())) {
                    client.player.sendMessage( new LiteralText("block already used by other trigger")
                            .styled(s -> s.withColor(Formatting.RED)), false);
                    return;
                }
                if (other instanceof PositionTrigger pt && hoveredPos.equals(pt.getPos())) {
                    client.player.sendMessage( new LiteralText("block already used by other trigger")
                            .styled(s -> s.withColor(Formatting.RED)), false);
                    return;
                }

                if (getActiveSlot() == Trigger.TriggerSlot.START) {
                    pendingStart = new BlockBreakTrigger(Trigger.TriggerSlot.START, hoveredPos);
                } else {
                    pendingEnd = new BlockBreakTrigger(Trigger.TriggerSlot.END, hoveredPos);
                }
            }
            case POSITION -> {
                if (hoveredPos == null) return;
                if (getActiveSlot() == Trigger.TriggerSlot.START) {
                    pendingStart = new PositionTrigger(Trigger.TriggerSlot.START, hoveredPos);
                } else {
                    pendingEnd = new PositionTrigger(Trigger.TriggerSlot.END, hoveredPos);
                }
            }
            case TRADE_START -> {
                if (hoveredPos == null) return;
                pendingStart = new TradeStartTrigger(Trigger.TriggerSlot.START, hoveredPos);
            }
            case TRADE_END -> {
                pendingEnd = activeTrigger.copy();
            }
        }
    }

    public void setActiveSlot(Trigger.TriggerSlot slot) {
        if (getActiveSlot() == slot) return;
        Trigger tempTrigger = activeTrigger;
        if (oldActiveTrigger != null) {
            activeTrigger = oldActiveTrigger;
        } else {
            activeTrigger = slot == Trigger.TriggerSlot.START ? pendingStart : pendingEnd;
        }
        oldActiveTrigger = tempTrigger;
    }

    public void toggleActiveSlot() {
        activeTrigger = getActiveSlot() == Trigger.TriggerSlot.START ? pendingEnd : pendingStart;
    }

    public void setActiveType(Trigger.TriggerType type) {
        if (activeTrigger == null) return;
        switch (type) {
            case MAP -> {
                if (getActiveSlot() == Trigger.TriggerSlot.START) {
                    activeTrigger = new MapTrigger(Trigger.TriggerSlot.START);
                } else {
                    activeTrigger = new MapTrigger(Trigger.TriggerSlot.END);
                }
            }
            case BLOCK_BREAK -> {
                if (getActiveSlot() == Trigger.TriggerSlot.START) {
                    activeTrigger = new BlockBreakTrigger(Trigger.TriggerSlot.START, null);
                } else {
                    activeTrigger = new BlockBreakTrigger(Trigger.TriggerSlot.END, null);
                }
            }
            case POSITION -> {
                if (getActiveSlot() == Trigger.TriggerSlot.START) {
                    activeTrigger = new PositionTrigger(Trigger.TriggerSlot.START, null);
                } else {
                    activeTrigger = new PositionTrigger(Trigger.TriggerSlot.END, null);
                }
            }
            case TRADE_START -> {
                activeTrigger = new TradeStartTrigger(Trigger.TriggerSlot.START, null);
            }
            case TRADE_END -> {
                activeTrigger = new TradeEndTrigger(Trigger.TriggerSlot.END, 50);
            }
        }
    }

    public int getBarterCap() {
        if (activeTrigger instanceof TradeEndTrigger tet) {
            return tet.getBarterCap();
        } else {
            return -1;
        }
    }

    public void setSlotAndType(Trigger.TriggerSlot slot, Trigger.TriggerType type) {
        setActiveSlot(slot);
        setActiveType(type);
    }

    public void confirm() {
        if (pendingStart == null || pendingEnd == null) return;
        ogStart = pendingStart;
        ogEnd = pendingEnd;
        editSet.getRoute().setStartTrigger(pendingStart);
        editSet.getRoute().setEndTrigger(pendingEnd);
        SplinterClient.ssm.setIdle();

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        if (client.currentScreen instanceof EditScreen) {
            client.openScreen(null);
        }
    }

    public void cancel() {
        pendingStart = ogStart;
        pendingEnd = ogEnd;
    }

    public void setHoveredPos(BlockPos pos) {
        hoveredPos = pos;
    }

    public boolean hasChanges() {
        return !pendingStart.equals(ogStart)
                || !pendingEnd.equals(ogEnd);
    }

    public Trigger getOgStart() {
        return ogStart;
    }

    public Trigger getOgEnd() {
        return ogEnd;
    }

    public Trigger getPendingStart() {
        return pendingStart;
    }

    public Trigger getPendingEnd() {
        return pendingEnd;
    }

    public Trigger.TriggerSlot getActiveSlot() {
        return activeTrigger == null ? Trigger.TriggerSlot.START : activeTrigger.getTriggerSlot();
    }

    public Trigger.TriggerType getActiveType() {
        return activeTrigger == null ? Trigger.TriggerType.MAP : activeTrigger.getType();
    }


}
