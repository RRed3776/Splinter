package me.rred.splinter.client.routing.triggers;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

public class BlockBreakTrigger extends Trigger{
    private BlockPos pos;

    public BlockBreakTrigger(TriggerSlot triggerSlot, BlockPos pos) {
        super(triggerSlot);
        this.pos = pos;
    }

    @Override
    public Trigger copy() {
        BlockPos posCopy = pos != null ? pos.mutableCopy() : null;
        return new BlockBreakTrigger(triggerSlot, posCopy);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BlockBreakTrigger other)) return false;
        if (pos != null && other.pos != null) {
            return pos.equals(other.pos);
        } else {
            return pos == null && other.pos == null;
        }
    }

    public TriggerType getType() {
        return TriggerType.BLOCK_BREAK;
    }

    public boolean matches(BlockPos broken) {
        return pos != null && pos.equals(broken);
    }

    public BlockPos getPos() {
        return pos;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }
}
