package me.rred.splinter.client.network;

import me.rred.splinter.client.SplinterClient;
import me.rred.splinter.network.PacketIds;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.item.ItemStack;

// referencing BastionHelper for packet implementation
public class ClientEventReceiver {
    public static void register() {
        ClientSidePacketRegistry.INSTANCE.register(PacketIds.PIGLIN_BARTER, (context, buf) -> {
            ItemStack barteredStack = buf.readItemStack();

            context.getTaskQueue().execute(() -> {
                SplinterClient.barterTracker.updateBarterData(barteredStack);
            });
        });
    }
}
