package me.rred.splinter.client.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.network.PacketByteBuf;

import static me.rred.splinter.network.PacketIds.SET_CREATIVE;

// referencing BastionHelper
public class ClientEventEmitter {
    public static void setCreative() {
        ClientSidePacketRegistry.INSTANCE.sendToServer(SET_CREATIVE, new PacketByteBuf(Unpooled.buffer()));
    }
}
