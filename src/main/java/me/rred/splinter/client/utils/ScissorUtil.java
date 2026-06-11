package me.rred.splinter.client.utils;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.opengl.GL11;

public class ScissorUtil {
    public static void enable(double scaleFactor, int x, int y, int width, int height) {
        int scissorX = (int)(x * scaleFactor);
        int scissorW = (int)(width * scaleFactor);
        int scissorH = (int)(height * scaleFactor);

        // convert from top-left origin to bottom left origin
        int windowHeight = MinecraftClient.getInstance().getWindow().getHeight();
        int scissorY = (int)(windowHeight - (y + height) * scaleFactor);

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(scissorX, scissorY, scissorW, scissorH);
    }

    public static void disable() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }
}
