package me.rred.splinter.client.bartertracking;

import me.rred.splinter.Splinter;
import me.rred.splinter.client.SplinterClient;
import me.rred.splinter.client.utils.SplinterColors;
import me.rred.splinter.client.utils.TimerFormatter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

public class BarterHud {
    public static void render(MatrixStack matrixStack, TextRenderer textRenderer) {
        if (!SplinterClient.barterTracker.isTradeEnd()) return;
        if (!(SplinterClient.timer.isStopped() || SplinterClient.timer.isRunning())) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        float scaledHeight = client.getWindow().getScaledHeight();
        float y = scaledHeight / 2;

        int color = SplinterColors.TEXT;

        String totalBarters = String.valueOf(SplinterClient.barterTracker.getTotalBarters());
        String barterCap = String.valueOf(SplinterClient.barterTracker.getBarterCap());

        String barterText = "Total Barters: " + totalBarters + "/" + barterCap;
//        String pearls = "Pearls: " + SplinterClient.barterTracker.getPearls();
//        String strings = "String: " + SplinterClient.barterTracker.getStrings();
//        String glowstone = "Glowstone: " + SplinterClient.barterTracker.getGlowstone();
//        String obsidian = "Obsidian: " + SplinterClient.barterTracker.getObsidian();

        textRenderer.drawWithShadow(matrixStack, new LiteralText(barterText), 10, y + textRenderer.fontHeight + 3, color);
//        textRenderer.drawWithShadow(matrixStack, new LiteralText(pearls), 10, y + (textRenderer.fontHeight + 3) * 2, color);
//        textRenderer.drawWithShadow(matrixStack, new LiteralText(strings), 10, y + (textRenderer.fontHeight + 3) * 3, color);
//        textRenderer.drawWithShadow(matrixStack, new LiteralText(glowstone), 10, y + (textRenderer.fontHeight + 3) * 4, color);
//        textRenderer.drawWithShadow(matrixStack, new LiteralText(obsidian), 10, y + (textRenderer.fontHeight + 3) * 5, color);

    }
}
