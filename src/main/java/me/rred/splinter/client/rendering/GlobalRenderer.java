package me.rred.splinter.client.rendering;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.rred.splinter.client.edit.EditSession;
import me.rred.splinter.client.SplinterClient;
import me.rred.splinter.client.SplinterStateMachine;
import me.rred.splinter.client.edit.gui.EditOutlines;
import net.minecraft.client.util.math.MatrixStack;

/*
 * Most of this class is adapted from
 * https://github.com/LeSaRXD/BastionHelper
 */

public class GlobalRenderer {
    public static final GlobalRenderer INSTANCE = new GlobalRenderer();
    private MatrixStack matrixStack = null;

    public void setMatrixStack(MatrixStack value) {
        matrixStack = value;
    }

    public void clearMatrixStack() {
        matrixStack = null;
    }

    public void render() {
        if (matrixStack == null) return;

        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(matrixStack.peek().getModel());
        GlStateManager.disableTexture();
        GlStateManager.disableDepthTest();
        GlStateManager.disableCull();

        SplinterClient.routeHandler.render();

        if (SplinterClient.ssm.getState() == SplinterStateMachine.State.EDIT) {
            EditSession session = SplinterClient.ssm.getEditSession();
            if (session != null) EditOutlines.render(session);
        }

        GlStateManager.enableDepthTest();
        GlStateManager.enableCull();
        GlStateManager.enableTexture();
        RenderSystem.popMatrix();
    }
}
