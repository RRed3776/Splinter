package me.rred.splinter.client.routing.gui;

import me.rred.splinter.client.routing.Route;
import me.rred.splinter.client.sets.gui.ListPanel;
import me.rred.splinter.client.widgets.SplinterExitButton;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;

import java.util.List;
import java.util.function.BiConsumer;

public class RoutesListPanel extends ListPanel {
    private final List<Route> routes;
    private final BiConsumer<Route, Integer> onClick;
    private int hoveredIndex = -1;

    public RoutesListPanel(int x, int y, int width, int height,
                           List<Route> routes, BiConsumer<Route, Integer> onClick) {
        super(x, y, width, height);
        this.routes = routes;
        this.onClick = onClick;
    }

    @Override
    public int getItemCount() { return routes.size();}

    @Override
    public void render(MatrixStack matrixStack, TextRenderer textRenderer,
                       int mouseX, int mouseY, boolean showHover) {

    }

}
