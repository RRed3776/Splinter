package me.rred.splinter.client.sets.gui.exports;

import me.rred.splinter.Splinter;
import me.rred.splinter.client.SplinterClient;
import me.rred.splinter.client.keyboard.KeyInputHandler;
import me.rred.splinter.client.sets.SplinterSet;
import me.rred.splinter.client.sets.gui.SetsScreen;
import me.rred.splinter.client.utils.ScissorUtil;
import me.rred.splinter.client.utils.SplinterColors;
import me.rred.splinter.client.widgets.SplinterButton;
import me.rred.splinter.client.widgets.modals.InputModal;
import me.rred.splinter.export.CsvExport;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExportScreen extends Screen {
    private int screenTop, screenBottom, screenLeft, screenRight;
    private int listTop, buttonsTop;
    private final int[] partitions = new int[3];
    private int partitionWidth;
    private final int borderWidth = 1;
    private static final int textColor = SplinterColors.TEXT;
    private final int tabHeight = 20;
    private ExportSetsListPanel setsListPanel;
    private SplinterButton exportButton;
    private InputModal exportModal;

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

        List<SplinterSet> totalSets = SplinterClient.setManager.getAllSets();
        List<SplinterSet> sets = new ArrayList<>();
        for (SplinterSet set : totalSets) {
            if (set.getTimesSize() == 0) continue;
            sets.add(set);
        }

        partitions[0] = screenLeft;
        partitionWidth = (screenRight - screenLeft) / 3;

        for (int i = 1; i <= 2; i++) {
            partitions[i] = partitions[i - 1] + partitionWidth + borderWidth;
        }

        int buttonHeight = 18;
        int buttonWidth = partitionWidth / 2;

        // top of list is after the buttons
        buttonsTop = screenTop + tabHeight + borderWidth;
        listTop = buttonsTop + buttonHeight;

        // sets list panel
        int listHeight = screenBottom - listTop;

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
                    }
                }
        );

        // deselect all export selections
        addButton(new SplinterButton(screenLeft, screenTop + tabHeight + borderWidth, buttonWidth, buttonHeight,
                new LiteralText("CLEAR"),
                () -> {
                    // close this screen, open Sets Screen
                    exportSets.clear();
                    setsListPanel.updateExportSets(exportSets);
                }
        ));

        addButton(new SplinterButton(screenLeft + buttonWidth, screenTop + tabHeight + + borderWidth, buttonWidth, buttonHeight,
                new LiteralText("ALL"),
                () -> {
                    // close this screen, open Sets Screen
                    exportSets = new ArrayList<>(sets);
                    setsListPanel.updateExportSets(exportSets);
                }
        ));

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

        // open exports folder
        int exportFolderEndX = cancelX - 5;
        int exportX = partitions[1] + 5;
        int exportFolderWidth = exportFolderEndX - (exportX + buttonWidth + 5);

        int exportFolderX = exportFolderEndX - exportFolderWidth;

        // open exports folder
        addButton(new SplinterButton(exportFolderX, screenBottom - buttonHeight - 5, exportFolderWidth, buttonHeight,
                new LiteralText("OPEN FOLDER"),
                () -> {
                    Path exportPath = FabricLoader.getInstance().getGameDir().resolve("splinter/exports");
                    // taken from AbstractPackScreen class
                    Util.getOperatingSystem().open(exportPath.toFile());
                }
        ));

        // deselect all export selections
        addButton(exportButton = new SplinterButton(exportX, screenBottom - buttonHeight - 5, buttonWidth, buttonHeight,
                new LiteralText("EXPORT"),
                () -> {
                        //https://stackoverflow.com/questions/63220762/how-to-get-minecraft-path-with-fabric
                        Path exportPath = FabricLoader.getInstance().getGameDir().resolve("splinter/exports");

                        //https://stackoverflow.com/a/23068695
                        // Get the current date and time
                        LocalDateTime now = LocalDateTime.now();

                        // Define the format
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                        // Format the current date and time
                        String timestamp = now.format(formatter);

                        // check if files with timestamp already exist and add -n+1 after

                        List<String> names = readPathFileNames(exportPath);
                        int fileNumber = 0;
                        if (names != null) {
                            fileNumber = countTSFileAmount(timestamp, names);
                        }

                        String fileNumberString = "";
                        if (fileNumber != 0) {
                            fileNumberString = "_(" + String.valueOf(fileNumber) + ")";
                        }

                        String fileName = "splinter_export_" + timestamp + fileNumberString + ".csv";
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
                }
        ));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        drawCenteredText(matrixStack, textRenderer, title, width / 2, 10, textColor);

        int topPanelColor = SplinterColors.alpha(SplinterColors.TOP_PANEL, 0x95);;
        fill(matrixStack, screenLeft, screenTop, screenRight, screenTop + tabHeight, topPanelColor);

        int middlePanelColor = SplinterColors.alpha(SplinterColors.MIDDLE_PANEL, 0xE0); // 88% opacity
        fill(matrixStack, screenLeft, buttonsTop, screenRight, screenBottom, middlePanelColor);

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

        DrawableHelper.fill(matrixStack, screenLeft, buttonsTop - borderWidth, screenRight,  buttonsTop, headersBorderColor);
        textRenderer.drawWithShadow(matrixStack, "Select Sets", headerX, headerTextY, textColor);

        int headerX2 = partitions[1] + 5;
        textRenderer.drawWithShadow(matrixStack, "Selected", headerX2, headerTextY, textColor);

        int headerX3 = partitions[2];
        textRenderer.drawWithShadow(matrixStack, "Trial Amount", headerX3, headerTextY, textColor);

        // top border of sets list
        DrawableHelper.fill(matrixStack, screenLeft, listTop, partitions[1] - borderWidth,  listTop + borderWidth, headersBorderColor);

        // sets list
        double scale = client.getWindow().getScaleFactor();
        int scissorWidth = screenRight - screenLeft;
        int scissorHeight = screenBottom - listTop - borderWidth;

        ScissorUtil.enable(scale, screenLeft, listTop + borderWidth, scissorWidth, scissorHeight);
        setsListPanel.render(matrixStack, textRenderer, mouseX, mouseY, true);
        ScissorUtil.disable();

        // selected sets text
        // probably a better way to do this but I cbb and it seems complicated
        int textOffset = 0;
        for (int i = 0; i < exportSets.size(); i++) {
            SplinterSet set = exportSets.get(i);
            textRenderer.drawWithShadow(matrixStack, set.getName(), partitions[1] + 5,
                    buttonsTop + 5 + ((3 + textRenderer.fontHeight) * i), textColor);
            // # of trials
            String nText = String.valueOf(set.getTimesSize());
            textRenderer.drawWithShadow(matrixStack, nText, partitions[2],
                    buttonsTop + 5 + ((3 + textRenderer.fontHeight) * i), textColor);
        }

        // draw buttons
        exportButton.active = !exportSets.isEmpty();

        if (exportModal != null) {
            exportModal.render(matrixStack, textRenderer, mouseX, mouseY);
        }

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
        if (exportModal != null) {
            boolean pressed = exportModal.handleClick(mouseX, mouseY, button);
            if (exportModal != null && !exportModal.isVisible()) exportModal = null;
            return pressed;
        }

        if(setsListPanel.handleClick(mouseX, mouseY, button)) return true;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // pass input to activeModal
        if (exportModal != null) {
            boolean pressed = exportModal.keyPressed(keyCode, scanCode, modifiers);
            if (!exportModal.isVisible()) exportModal = null;
            return pressed;
        }
        // leave screen with Esc or specified hotkey
        if (keyCode == GLFW.GLFW_KEY_ESCAPE || KeyInputHandler.GUI_SETS_BIND.getKeyBinding().matchesKey(keyCode, scanCode)) {
            ExportScreen.toggle();
            SetsScreen.toggle();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int keyCode) {
        if (exportModal != null) return exportModal.charTyped(chr, keyCode);
        return super.charTyped(chr, keyCode);
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

    private List<String> readPathFileNames(Path path) {
        // https://www.youtube.com/watch?v=_159xnyO1to
        List<String> fileNames;
        try (Stream<Path> stream = Files.walk(path, 1)){
            fileNames = stream.filter(Files::isRegularFile)
                    // parse out .csv and get just the filename
                    .map(p -> p.getFileName().toString().replace(".csv", ""))
                    .sorted()
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return null;
        }
        return fileNames;
    }

    private int countTSFileAmount(String timestamp, List<String> names) {
        int count = 0;
        for (String name : names) {
            // only capture thte timestamp between splinter_export_ and _(n)
            String nameTimestamp = name.substring(16, 26);
            Splinter.LOGGER.info("name: {}", nameTimestamp);
            if (nameTimestamp.equals(timestamp)) {
                count++;
            } else if (count > 0) {
                break; // already passed all matching entries
            }
        }
        return count;
    }
}
