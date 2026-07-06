package me.rred.splinter.client.routing.triggers;

import net.minecraft.util.math.BlockPos;

public class TradeStartTrigger extends Trigger {
    private BlockPos pos;

    public TradeStartTrigger(TriggerSlot triggerSlot, BlockPos pos) {
        super(triggerSlot);
        this.pos = pos;
    }

    @Override
    public Trigger copy() {
        BlockPos posCopy = pos != null ? pos.mutableCopy() : null;
        return new TradeStartTrigger(triggerSlot, posCopy);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TradeStartTrigger other)) return false;
        if (pos != null && other.pos != null) {
            return pos.equals(other.pos);
        } else {
            return pos == null && other.pos == null;
        }
    }

    public boolean matches(BlockPos piglinPos) {
        return pos != null && pos.equals(piglinPos);
    }

    public TriggerType getType() { return TriggerType.TRADE_START;}

    public BlockPos getPos() { return pos; }
}
