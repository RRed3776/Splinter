package me.rred.splinter.client.network;

import me.rred.splinter.Splinter;
import me.rred.splinter.client.SplinterClient;
import me.rred.splinter.network.PacketIds;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

// referencing BastionHelper for packet implementation
public class ClientEventReceiver {
    public static void register() {
        ClientSidePacketRegistry.INSTANCE.register(PacketIds.PIGLIN_BARTER, (context, buf) -> {
            ItemStack barteredStack = buf.readItemStack();
            BlockPos barterPos = new BlockPos(buf.readDouble(), buf.readDouble(), buf.readDouble());
            context.getTaskQueue().execute(() -> {
                SplinterClient.barterTracker.updateBarterData(barteredStack);
                SplinterClient.routeEngine.onBarter(barterPos);
                Splinter.LOGGER.info("barterpos: {}", barterPos.toString());
            });
        });
    }
}
