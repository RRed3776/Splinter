package me.rred.splinter.client.keyboard;

import me.rred.splinter.client.rendering.StateHud;
import me.rred.splinter.client.edit.EditSession;
import me.rred.splinter.client.SplinterClient;
import me.rred.splinter.client.SplinterStateMachine;
import me.rred.splinter.client.edit.gui.EditScreen;
import me.rred.splinter.client.sets.gui.SetsScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

public class KeyInputHandler {
    public static final String KEY_CATEGORY = "key.category.splinter";

    public static final String TOGGLE_TIMER = "key.splinter.toggle_timer";
    public static final String GUI_SETS = "key.splinter.gui_sets";
    public static final String TOGGLE_EDIT = "key.splinter.toggle_edit";
    public static final String GUI_EDIT = "key.splinter.gui_edit";
    public static final String EDIT_SELECT = "key.splinter.edit_select";

    public static KeyBind GUI_SETS_BIND;
    public static KeyBind GUI_EDIT_BIND;
    public static KeyBind TOGGLE_EDIT_BIND;
    public static KeyBind EDIT_SELECT_BIND;


    public static void register() {
        GUI_SETS_BIND = new KeyBind(GUI_SETS, GLFW.GLFW_KEY_B, () -> {
            StateHud.setHintConsumed(true);
            SetsScreen.toggle();
        });
        GUI_EDIT_BIND = new KeyBind(GUI_EDIT, GLFW.GLFW_KEY_N, () -> {
            if (SplinterClient.ssm.getState() == SplinterStateMachine.State.EDIT) {
                EditSession edit = SplinterClient.ssm.getEditSession();
                if (edit != null) {
                    EditScreen.toggle();
                }
            } else {
                MinecraftClient client = MinecraftClient.getInstance();
                if (client.player == null || client.world == null) return;
                client.player.sendMessage( new LiteralText("cannot open the edit GUI in active mode. see \"i\" in sets GUI")
                        .styled(s -> s.withColor(Formatting.RED)), false);
            }
        });
        TOGGLE_EDIT_BIND = new KeyBind(TOGGLE_EDIT, GLFW.GLFW_KEY_J, () -> {

            if (SplinterClient.ssm.getState() == SplinterStateMachine.State.EDIT) {
                SplinterClient.ssm.setIdle();
            } else if (SplinterClient.ssm.getState() != SplinterStateMachine.State.EDIT) {
                SplinterClient.ssm.setEdit();
            }
        });
        EDIT_SELECT_BIND = new KeyBind(EDIT_SELECT, GLFW.GLFW_KEY_M, () -> {
            if (SplinterClient.ssm.getState() == SplinterStateMachine.State.EDIT) {
                EditSession edit = SplinterClient.ssm.getEditSession();
                if (edit != null) {
                    edit.selectActive();
                }
            } else {
                MinecraftClient client = MinecraftClient.getInstance();
                if (client.player == null || client.world == null) return;
                client.player.sendMessage( new LiteralText("cannot select and edit in active mode. see \"i\" in sets GUI")
                        .styled(s -> s.withColor(Formatting.RED)), false);
            }
        });

        KeyBind[] keyBinds = new KeyBind[] {
                // bastion helper uses O I P K G
                new KeyBind(TOGGLE_TIMER, GLFW.GLFW_KEY_SEMICOLON, SplinterClient.routeEngine::toggleTimer),

                GUI_SETS_BIND,
                GUI_EDIT_BIND,
                TOGGLE_EDIT_BIND,
                EDIT_SELECT_BIND
        };

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;

            for (KeyBind keyBind : keyBinds) {
                keyBind.update();
            }
        });
    }
}
