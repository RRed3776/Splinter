package me.rred.splinter.client.sets.gui.exports;

import me.rred.splinter.client.sets.SplinterSet;
import me.rred.splinter.client.sets.gui.ListPanel;
import me.rred.splinter.client.utils.SplinterColors;
import me.rred.splinter.client.utils.TruncateText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

import java.util.List;
import java.util.function.BiConsumer;

public class ExportSetsListPanel extends ListPanel {
    private List<SplinterSet> sets;
    private List<SplinterSet> exportSets;
    private BiConsumer<SplinterSet, Integer> onClick;
    private boolean isHovered = false;
    private int hoveredIndex = -1;

    public ExportSetsListPanel(int x, int y, int width, int height, List<SplinterSet> sets, BiConsumer<SplinterSet, Integer> onClick) {
        super(x, y, width, height);
        this.sets = sets;
        this.onClick = onClick;
    }

    @Override
    public int getItemCount() {
        return sets.size();
    }

    @Override
    public void render(MatrixStack matrixStack, TextRenderer textRenderer, int mouseX, int mouseY, boolean showHover) {
        if (getItemCount() == 0) {
            String text = "No Data!";
            int textX = x + (width - textRenderer.getWidth(text)) / 2;
            textRenderer.drawWithShadow(matrixStack, text, textX, y + 5, SplinterColors.TEXT);
            return;
        }

        hoveredIndex = -1;

        for (int i = 0; i < getItemCount(); i++) {
            SplinterSet set = sets.get(i);
            String setName = set.getName();

            int itemY = y + (i * LINE_HEIGHT) - scrollOffset + i + 1;
            if (itemY + LINE_HEIGHT < y || itemY > y + height) continue; // skip off-screen lines

            isHovered = (
                    showHover &&
                            mouseX >= x && mouseX <= x + width &&
                            mouseY >= itemY && mouseY <= itemY + LINE_HEIGHT
            );

            if (isHovered) {
                hoveredIndex = i;
            }

            boolean isSelected = !(exportSets == null) && exportSets.contains(set);

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
                    TruncateText.truncate(setName, width, textRenderer),
                    x + 3, textY, textColor);
        }
    }

    public void updateExportSets(List<SplinterSet> exportSets) {
        this.exportSets = exportSets;
    }

    public boolean handleClick(double mouseX, double mouseY, int button) {
        if (!isMouseOver(mouseX, mouseY)) return false;
        if (onClick == null) return false;
        if (hoveredIndex < 0 || hoveredIndex >= sets.size()) return false;

        onClick.accept(sets.get(hoveredIndex), button);
        return true;
    }
}
