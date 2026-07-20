package me.rred.splinter.client.routing.triggers;

import net.minecraft.item.TridentItem;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public abstract class Trigger {

    public enum TriggerType {
        MAP(List.of(
                "Start: triggers on LBP timer start",
                "(first tick of movement after entering)",
                "End: dropping pickaxe / dying"
        )),
        BLOCK_BREAK(List.of(
                "Starts/Stops on",
                "breaking specified blocks"
        )),
        POSITION(List.of(
                "Starts/Stops when",
                "the player position passes into the",
                "specified block position"
        )),
        TRADE_START(List.of(
                "Starts when a piglin barters",
                "in the specified block position"
        )),
        TRADE_END(List.of(
                "Stops when you reach",
                "a specified barter cap"
        ));

        private final List<String> description;

        TriggerType(List<String> description) {
            this.description = description;
        }

        public List<String> getDescription() {
            return description;
        }
    }
    public enum TriggerSlot { START, END }

    protected boolean triggered = false; // state
    protected TriggerSlot triggerSlot;

    public Trigger(TriggerSlot triggerSlot) {
        this.triggerSlot = triggerSlot;
    }

    public abstract TriggerType getType();

    public abstract Trigger copy();

    // called by RouteEngine each tick for poll-based events;
    public void tick() {}

    // called by mixins for push-based events
    public void onFired() {
        triggered = true;
    }

    public boolean isTriggered() {
        return triggered;
    }

    public void reset() {
        triggered = false;
    }

    public TriggerSlot getTriggerSlot() {
        return triggerSlot;
    }

    public void setTriggerSlot(TriggerSlot triggerSlot) {
        this.triggerSlot = triggerSlot;
    }

    public boolean isStart() {return triggerSlot == TriggerSlot.START;}

    public abstract boolean equals(Object obj);
}
