package me.rred.splinter.client.edit;

import me.rred.splinter.client.SplinterClient;
import me.rred.splinter.client.edit.gui.EditHud;
import me.rred.splinter.client.edit.gui.EditOutlines;
import me.rred.splinter.client.network.ClientEventEmitter;
import me.rred.splinter.client.routing.Route;
import me.rred.splinter.client.routing.triggers.*;
import me.rred.splinter.client.edit.gui.EditScreen;
import me.rred.splinter.client.keyboard.KeyInputHandler;
import me.rred.splinter.client.rendering.BlockOutlineRenderer;
import me.rred.splinter.client.sets.SplinterSet;
import me.rred.splinter.client.utils.TriggersSharePos;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.launch.platform.MixinPlatformAgentMinecraftForge;

import java.awt.*;

public class EditSession {
    private Trigger activeTrigger;
    private Trigger oldActiveTrigger;
    private Trigger ogStart;
    private Trigger ogEnd;
    private Trigger pendingStart;
    private Trigger pendingEnd;

    private final SplinterSet editSet;
    private final Route editRoute;

    private BlockPos hoveredPos;


    public EditSession(SplinterSet editSet, Route editRoute) {
        this.editSet = editSet;
        this.editRoute = editRoute;
        this.ogStart = editRoute.getStartTrigger();
        this.ogEnd = editRoute.getEndTrigger();
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
        if (!SplinterClient.ssm.isInMap()) return;

        switch (getActiveType()) {
            case MAP -> {
                if (activeIsStart()) {
                    pendingStart = new MapTrigger(Trigger.TriggerSlot.START);
                } else {
                    pendingEnd = new MapTrigger(Trigger.TriggerSlot.END);
                }
            }
            case BLOCK_BREAK -> {
                if (hoveredPos == null) return;
                // prevent selection if position already in use
                Trigger other = activeIsStart() ? pendingEnd : pendingStart;
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

                if (activeIsStart()) {
                    pendingStart = new BlockBreakTrigger(Trigger.TriggerSlot.START, hoveredPos);
                } else {
                    pendingEnd = new BlockBreakTrigger(Trigger.TriggerSlot.END, hoveredPos);
                }
            }
            case POSITION -> {
                if (hoveredPos == null) return;
                if (activeIsStart()) {
                    pendingStart = new PositionTrigger(Trigger.TriggerSlot.START, hoveredPos);
                } else {
                    pendingEnd = new PositionTrigger(Trigger.TriggerSlot.END, hoveredPos);
                }
            }
            case TRADE_START -> {
                if (hoveredPos == null) return;
                if (activeIsStart()) {
                    pendingStart = new TradeStartTrigger(Trigger.TriggerSlot.START, hoveredPos);
                } else {
                    pendingEnd = new TradeStartTrigger(Trigger.TriggerSlot.END, hoveredPos);
                }
            }
            case TRADE_END -> {
                pendingEnd = activeTrigger.copy();
            }
        }
    }

    public void toggleActiveSlot() {
        activeTrigger = getActiveSlot() == Trigger.TriggerSlot.START ? pendingEnd : pendingStart;
    }

    public void setActiveType(Trigger.TriggerType type) {
        if (activeTrigger == null) return;
        switch (type) {
            case MAP -> {
                if (activeIsStart()) {
                    activeTrigger = new MapTrigger(Trigger.TriggerSlot.START);
                    pendingStart = activeTrigger.copy();
                } else {
                    activeTrigger = new MapTrigger(Trigger.TriggerSlot.END);
                    pendingEnd = activeTrigger.copy();
                }
            }
            case BLOCK_BREAK -> {
                if (activeIsStart()) {
                    activeTrigger = new BlockBreakTrigger(Trigger.TriggerSlot.START, null);
                    pendingStart = activeTrigger.copy();
                } else {
                    activeTrigger = new BlockBreakTrigger(Trigger.TriggerSlot.END, null);
                    pendingEnd = activeTrigger.copy();
                }
            }
            case POSITION -> {
                if (activeIsStart()) {
                    activeTrigger = new PositionTrigger(Trigger.TriggerSlot.START, null);
                    pendingStart = activeTrigger.copy();
                } else {
                    activeTrigger = new PositionTrigger(Trigger.TriggerSlot.END, null);
                    pendingEnd = activeTrigger.copy();
                }
            }
            case TRADE_START -> {
                if (activeIsStart()) {
                    activeTrigger = new TradeStartTrigger(Trigger.TriggerSlot.START, null);
                    pendingStart = activeTrigger.copy();
                } else {
                    activeTrigger = new TradeStartTrigger(Trigger.TriggerSlot.END, null);
                    pendingEnd = activeTrigger.copy();
                }
            }
            case TRADE_END -> {
                activeTrigger = new TradeEndTrigger(Trigger.TriggerSlot.END, 100);
                pendingEnd = activeTrigger.copy();
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

    public void updateBarterCap(int cap) {
        if (activeTrigger instanceof TradeEndTrigger tet) {
            tet.setBarterCap(cap);
            pendingEnd = activeTrigger.copy();
        } else {
            activeTrigger = new TradeEndTrigger(Trigger.TriggerSlot.END, cap);
            pendingEnd = activeTrigger.copy();
        }
    }

    public void confirm() {
        if (pendingStart == null || pendingEnd == null) return;
        ogStart = pendingStart;
        ogEnd = pendingEnd;
        editRoute.setStartTrigger(pendingStart);
        editRoute.setEndTrigger(pendingEnd);
        editSet.setRoute(editRoute);
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

    public String getRouteName() {
        return editRoute.getName();
    }

    public void setHoveredPos(BlockPos pos) {
        hoveredPos = pos;
    }

    public boolean hasChanges() {
        return !pendingStart.equals(ogStart)
                || !pendingEnd.equals(ogEnd);
    }

    public boolean needsPos() {
        boolean startNeeds = false;
        boolean endNeeds = false;
        if (pendingStart instanceof PositionalTrigger pts) {
            startNeeds = pts.getPos() == null;
        }
        if (pendingEnd instanceof PositionalTrigger pte) {
            endNeeds = pte.getPos() == null;
        }
        return startNeeds || endNeeds;
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

    public boolean activeIsStart() {
        if (activeTrigger == null) return false;
        return activeTrigger.isStart();
    }


    public Trigger.TriggerType getActiveType() {
        return activeTrigger == null ? Trigger.TriggerType.MAP : activeTrigger.getType();
    }
}
