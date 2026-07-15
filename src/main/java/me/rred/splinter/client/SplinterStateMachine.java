package me.rred.splinter.client;

import me.rred.splinter.client.edit.EditSession;
import me.rred.splinter.client.network.ClientEventEmitter;
import me.rred.splinter.client.rendering.StateHud;
import me.rred.splinter.client.routing.Route;
import me.rred.splinter.client.routing.triggers.MapTrigger;
import me.rred.splinter.client.routing.triggers.Trigger;
import me.rred.splinter.client.sets.SplinterSet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

public class SplinterStateMachine {
    public enum State {
        IDLE, // transitional state, !ACTIVE && !EDIT
        ACTIVE,
        EDIT
    }

    private State state = State.IDLE;
    private boolean inMap = false;
    private EditSession editSession = null;

    public State getState() {
        return state;
    }

    public void setActive() {
        if (state == State.IDLE) {
            state = State.ACTIVE;
            SplinterClient.routeEngine.resetFired();
            // begin listening for events
        }
        // eventually allow active -> edit possibly
    }

    public void setIdle() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return;

        if (state != State.IDLE) {
            if (state == State.EDIT && editSession.hasChanges()) {
                client.player.sendMessage( new LiteralText("confirm or cancel changes in GUI")
                        .styled(s -> s.withColor(Formatting.YELLOW)), false);
                return;
            }
            state = State.IDLE;
            SplinterClient.timer.clear();
            editSession = null;
        }
        // stop listening, clear highlights
    }

    public void setEdit() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return;
//        if (!StateHud.isHintConsumed()) {
//            StateHud.setHintConsumed(true);
//        }
        SplinterClient.timer.clear();

        state = State.EDIT;
        SplinterSet set = SplinterClient.setManager.getActiveSet();
        Route route = set.getEditableRoute();
        editSession = new EditSession(set, route);
        ClientEventEmitter.setCreative();
        // display set UI
    }

    public void refreshEditSession() {
        if (state == State.EDIT) {
            SplinterSet set = SplinterClient.setManager.getActiveSet();
            Route route = set.getEditableRoute();
            editSession = new EditSession(set, route);
        }
    }

    public EditSession getEditSession() {
        return editSession;
    }

    public boolean isActive() {
        return state == State.ACTIVE;
    }

    public boolean isIdle() {return state == State.IDLE;}

    public boolean isEditing() {return state == State.EDIT;}

    public boolean isEditingWithChanges() { return state == State.EDIT && editSession.hasChanges(); }

    public void setInMap(boolean inMap) {
        this.inMap = inMap;
        if (!inMap) {
            if (!SplinterClient.timer.isStopped()) {
                SplinterClient.timer.clear();
            }
            Route route = SplinterClient.setManager.getActiveSet().getRoute();
            route.getStartTrigger().reset();
            route.getEndTrigger().reset();
        } else {
            if (isEditing()) {
                ClientEventEmitter.setCreative();
            }
        }
    }

    public boolean isInMap() {
        return inMap;
    }
}
