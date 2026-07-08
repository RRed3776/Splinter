package me.rred.splinter.client.routing.gui;

import me.rred.splinter.client.routing.Route;
import me.rred.splinter.client.sets.gui.ListPanel;
import me.rred.splinter.client.utils.SplinterColors;
import me.rred.splinter.client.utils.TruncateText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

import java.util.List;
import java.util.function.BiConsumer;

public class RoutesListPanel extends ListPanel {
    private final List<Route> routes;
    private final BiConsumer<Route, Integer> onClick;
    private Route selectedRoute;
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
        hoveredIndex = -1;
        for (int i = 0; i < getItemCount(); i++) {
            Route route = routes.get(i);
            String routeName = route.getName();

            int itemY = y + (i * LINE_HEIGHT) - scrollOffset + i + 1;
            if (itemY + LINE_HEIGHT < y || itemY > y + height) continue; // skip off-screen lines

            boolean isHovered = (
                    showHover &&
                            mouseX >= x && mouseX <= x + width &&
                            mouseY >= itemY && mouseY <= itemY + LINE_HEIGHT
            );

            if (isHovered) {
                hoveredIndex = i;
            }


            boolean isSelected = selectedRoute != null && selectedRoute.equals(route);

            // background color based on hover
            int bgColor = 0x00000000;

            if (isSelected)  {
                bgColor = SplinterColors.alpha(SplinterColors.TEAL_DEEP, 0x90);
            } else if (isHovered) {
                bgColor = SplinterColors.alpha(SplinterColors.SOFT_BLUE, 0x90);
            }

            // draw background
            DrawableHelper.fill(matrixStack, x, itemY, x + width, itemY + LINE_HEIGHT, bgColor);

            // draw bottom border for each record
            int border = SplinterColors.BORDER_OTHER;
            DrawableHelper.fill(matrixStack, x, itemY + LINE_HEIGHT, x + width, itemY + LINE_HEIGHT + 1, border);

            // draw text
            int textY = itemY + (ITEM_HEIGHT - textRenderer.fontHeight ) / 2;
            int textColor = SplinterColors.TEXT;

            textRenderer.drawWithShadow(matrixStack,
                    TruncateText.truncate(routeName, width, textRenderer),
                    x + 3, textY, textColor);
        }
    }

    public void updateSelectedRoute(Route selected) {
        this.selectedRoute = selected;
    }

    public boolean handleClick(double mouseX, double mouseY, int button) {
        if (!isMouseOver(mouseX, mouseY)) return false;
        if (onClick == null) return false;
        if (hoveredIndex < 0 || hoveredIndex >= routes.size()) return false;

        onClick.accept(routes.get(hoveredIndex), button);
        return true;
    }
}
