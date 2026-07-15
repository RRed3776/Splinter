package me.rred.splinter.client.routing.triggers;

import net.minecraft.util.math.BlockPos;

public interface PositionalTrigger {
    BlockPos getPos();
    void setPos(BlockPos pos);
}
