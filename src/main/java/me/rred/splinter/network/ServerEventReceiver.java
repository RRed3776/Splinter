package me.rred.splinter.network;

import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;

// referencing BastionHelper
public class ServerEventReceiver {
    public static void register() {
        ServerSidePacketRegistry.INSTANCE.register(PacketIds.SET_CREATIVE,
                (context, buf) -> context.getTaskQueue().execute(() -> {
                    ServerPlayerEntity player = (ServerPlayerEntity)context.getPlayer();
                    player.setGameMode(GameMode.CREATIVE);
                }));
    }
}
