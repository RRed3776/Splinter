package me.rred.splinter.client.sets.gui;

import me.rred.splinter.Splinter;
import me.rred.splinter.client.SplinterClient;
import me.rred.splinter.client.keyboard.KeyInputHandler;
import me.rred.splinter.client.sets.SplinterSet;
import me.rred.splinter.client.utils.ScissorUtil;
import me.rred.splinter.client.utils.SplinterColors;
import me.rred.splinter.client.widgets.SplinterButton;
import me.rred.splinter.export.CsvExport;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ExportScreen extends Screen {
    private int screenTop, screenBottom, screenLeft, screenRight;
    private int listTop;
    private final int[] partitions = new int[3];
    private int partitionWidth;
    private final int borderWidth = 1;
    private static final int textColor = SplinterColors.TEXT;
    private final int tabHeight = 20;
    private final int buttonLen = 20;
    private ExportSetsListPanel setsListPanel;

    private List<SplinterSet> exportSets = new ArrayList<>();

    public ExportScreen() {
        super(new LiteralText("Export Data"));
    }

    @Override
    protected void init() {
        buttons.clear();
        children.clear();

        int offset = 25;
        screenTop = offset;
        screenBottom = height - (int)(offset * 1.8);
        screenLeft = offset * 3;
        screenRight = width - (offset * 3);


        listTop = screenTop + tabHeight;

        partitions[0] = screenLeft;
        partitionWidth = (screenRight - screenLeft) / 3;
        // sets list
        for (int i = 1; i <= 2; i++) {
            partitions[i] = partitions[i - 1] + partitionWidth + borderWidth;
        }

        // sets list panel
        int listHeight = screenBottom - listTop;
        List<SplinterSet> sets = SplinterClient.setManager.getAllSets();

        setsListPanel = new ExportSetsListPanel(screenLeft, listTop, partitionWidth, listHeight, sets,
                (set, button) -> {
                    if (button == 0 || button == 1) { // left or right click
                        // add the set to the export sets
                        if (exportSets.contains(set)) {
                            exportSets.remove(set);
                        } else {
                            exportSets.add(set);
                        }
                        setsListPanel.updateExportSets(exportSets);
                        init();
                    }
                }
        );

        int buttonHeight = 18;
        int buttonWidth = partitionWidth / 2;

        int cancelX = screenRight - buttonWidth - 5;
        // return to sets screen
        addButton(new SplinterButton(cancelX, screenBottom - buttonHeight - 5, buttonWidth, buttonHeight,
                new LiteralText("CANCEL"),
                () -> {
                    // close this screen, open Sets Screen
                    ExportScreen.toggle();
                    SetsScreen.toggle();
                }
        ));

        int deselectX = cancelX - buttonWidth - 5;
        // deselect all export selections
        addButton(new SplinterButton(deselectX, screenBottom - buttonHeight - 5, buttonWidth, buttonHeight,
                new LiteralText("DESELECT"),
                () -> {
                    // close this screen, open Sets Screen
                    exportSets.clear();
                    setsListPanel.updateExportSets(exportSets);
                }
        ));

        int exportX = deselectX - buttonWidth - 5;
        // deselect all export selections
        if (!(exportSets.isEmpty())) {
            addButton(new SplinterButton(exportX, screenBottom - buttonHeight - 5, buttonWidth, buttonHeight,
                    new LiteralText("EXPORT"),
                    () -> {
                        // export given exportSets
                        Splinter.LOGGER.info("exporting");

                        //https://stackoverflow.com/questions/63220762/how-to-get-minecraft-path-with-fabric
                        Path exportPath = FabricLoader.getInstance().getGameDir().resolve("splinter/exports");

                        String fileName = "test.csv";
                        Path out = exportPath.resolve(fileName);
                        String outString = out.toString();
                        Splinter.LOGGER.info("out: {}", outString);

                        try {
                            Files.createDirectories(exportPath);
                        } catch (IOException e) {
                            Splinter.LOGGER.error(e);
                        }

                        CsvExport.export(exportSets, out);

                        // clear the exportSets
                        exportSets.clear();
                        setsListPanel.updateExportSets(exportSets);

                        init();
                    }
            ));
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        drawCenteredText(matrixStack, textRenderer, title, width / 2, 10, textColor);

        int topPanelColor = SplinterColors.alpha(SplinterColors.TOP_PANEL, 0x95);;
        fill(matrixStack, screenLeft, screenTop, screenRight, screenTop + tabHeight, topPanelColor);

        int middlePanelColor = SplinterColors.alpha(SplinterColors.MIDDLE_PANEL, 0xE0); // 88% opacity
        fill(matrixStack, screenLeft, listTop, screenRight, screenBottom, middlePanelColor);

        // outer border, screen is inside the border
        int outerBorderColor = SplinterColors.BORDER;
        // top
        fill(matrixStack, screenLeft - borderWidth, screenTop - borderWidth, screenRight + borderWidth, screenTop, outerBorderColor);
        // bottom
        fill(matrixStack, screenLeft - borderWidth, screenBottom, screenRight + borderWidth, screenBottom + borderWidth, outerBorderColor);
        // left
        fill(matrixStack, screenLeft - borderWidth, screenTop, screenLeft, screenBottom, outerBorderColor);
        // right
        fill(matrixStack, screenRight, screenTop, screenRight + borderWidth, screenBottom, outerBorderColor);

        int verticalBorderColor = SplinterColors.BORDER;
        fill(matrixStack, partitions[1] - borderWidth, screenTop, partitions[1], screenBottom, verticalBorderColor);

        // headers
        int headerTextY = screenTop + (tabHeight - textRenderer.fontHeight + 1) / 2;
        String headerText = "Select Sets";
        int headersBorderColor = SplinterColors.BORDER_OTHER;
        int headerMiddleX = partitions[0] + partitionWidth / 2;
        int headerX = headerMiddleX - textRenderer.getWidth(headerText) / 2;

        DrawableHelper.fill(matrixStack, screenLeft, listTop, screenRight,  listTop + borderWidth, headersBorderColor);
        textRenderer.drawWithShadow(matrixStack, "Select Sets", headerX, headerTextY, textColor);

        int headerX2 = partitions[1] + 5;
        textRenderer.drawWithShadow(matrixStack, "Sets To Be Exported", headerX2, headerTextY, textColor);


        // sets list
        double scale = client.getWindow().getScaleFactor();
        int scissorWidth = screenRight - screenLeft;
        int scissorHeight = screenBottom - listTop;

        ScissorUtil.enable(scale, screenLeft, listTop + borderWidth, scissorWidth, scissorHeight);
        setsListPanel.render(matrixStack, textRenderer, mouseX, mouseY, true);
        ScissorUtil.disable();

        // selected sets text
        // probably a better way to do this but I cbb and it seems complicated
        int textOffset = 0;
        for (int i = 0; i < exportSets.size(); i++) {
            SplinterSet set = exportSets.get(i);
            textRenderer.drawWithShadow(matrixStack, set.getName(), partitions[1] + 5,
                    listTop + 5 + ((3 + textRenderer.fontHeight) * i), textColor);
        }

        // draw buttons
        super.render(matrixStack, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (setsListPanel.isMouseOver(mouseX, mouseY)) {
            setsListPanel.scroll(amount);
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(setsListPanel.handleClick(mouseX, mouseY, button)) return true;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // leave screen with Esc or specified hotkey
        if (keyCode == GLFW.GLFW_KEY_ESCAPE || KeyInputHandler.GUI_SETS_BIND.getKeyBinding().matchesKey(keyCode, scanCode)) {
            ExportScreen.toggle();
            SetsScreen.toggle();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }



    public List<SplinterSet> getExportSets() {
        return exportSets;
    }

    public static void toggle() {
        MinecraftClient client = MinecraftClient.getInstance();
        assert(client != null);
        if (client.currentScreen instanceof ExportScreen) {
            client.openScreen(null);
        }
        else {
            client.openScreen(new ExportScreen());
        }
    }
}
