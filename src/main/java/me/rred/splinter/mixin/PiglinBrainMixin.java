package me.rred.splinter.mixin;

import io.netty.buffer.Unpooled;
import me.rred.splinter.network.PacketIds;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(PiglinBrain.class)
public class PiglinBrainMixin {
    @Inject(method = "getBarteredItem", at = @At("RETURN"))
    private static void onBarterResult(PiglinEntity piglin, CallbackInfoReturnable<List<ItemStack>> cir) {
        List<ItemStack> result = cir.getReturnValue();
        if (result.isEmpty()) {
            return;
        }
        ItemStack barteredStack = result.get(0);

        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeItemStack(barteredStack);

        PlayerStream.watching(piglin).forEach(player -> {
            ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, PacketIds.PIGLIN_BARTER, buf);
        });
    }
}
