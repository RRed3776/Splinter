package me.rred.splinter.client.sets.gui;

import me.rred.splinter.client.SplinterClient;
import me.rred.splinter.client.keyboard.KeyInputHandler;
import me.rred.splinter.client.routing.Route;
import me.rred.splinter.client.sets.gui.exports.ExportScreen;
import me.rred.splinter.client.utils.SplinterColors;
import me.rred.splinter.client.widgets.SplinterButton;
import me.rred.splinter.client.widgets.SplinterExitButton;
import me.rred.splinter.client.widgets.modals.ConfirmModal;
import me.rred.splinter.client.widgets.modals.InputModal;
import me.rred.splinter.client.widgets.modals.SplinterModal;
import me.rred.splinter.client.sets.SplinterSet;
import me.rred.splinter.client.utils.ScissorUtil;
import me.rred.splinter.client.utils.TimerFormatter;
import me.rred.splinter.client.utils.TruncateText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class SetsScreen extends Screen {

    // main screen fields
    private int screenTop, screenBottom, screenLeft, screenRight;
    private int headerBottom;
    private int menuBarTop, menuBarBottom;
    private int listTop, listBottom;
    private int headerTextY;
    private final int headerHeight = 20;
    private final int menuBarHeight = 15;
    private int headerButtonLen;
    private SplinterSet setA;
    private SplinterSet setB;
    private static final int textColor = SplinterColors.TEXT;

    // panel fields
    private SetsListPanel setsListPanel;
    private TimesListPanel timesListPanelA;
    private TimesListPanel timesListPanelB;
    private ContextMenu contextMenu = new ContextMenu();
    private final int borderWidth = 1;
    private final int[] partitions = new int[5];
    private int partitionWidth;
    private int lastClickX, lastClickY;
    private SplinterModal activeModal;
    private List<SplinterExitButton> exitButtons = new ArrayList<>();

    public SetsScreen() {
        super(new LiteralText("Splinter Sets"));
    }

    @Override
    protected void init() {
        buttons.clear();
        children.clear();

        List<SplinterSet> sets = SplinterClient.setManager.getAllSets();
        setA = SplinterClient.setManager.getDisplayedSetA();
        setB = SplinterClient.setManager.getDisplayedSetB();

        // sets UI screen dimensions
        int offset = 25;
        screenTop = offset;
        screenBottom = height - (int)(offset * 1.8) ;
        screenLeft = offset;
        screenRight = width - offset;

        headerBottom = screenTop + headerHeight;

//        menuBarTop = headerBottom + borderWidth;
//        menuBarBottom = menuBarTop + menuBarHeight;

        // middle section cutoff points
        listTop = headerBottom + borderWidth;
        listBottom = screenBottom;

        // list starting X coordinate (after border) list1 starts at screenLeft
        partitions[0] = screenLeft;
        partitionWidth = width / 6;
        // sets list
        partitions[1] = partitions[0] + partitionWidth + borderWidth;
        // times lists
        for (int i = 1; i <= 3; i++) {
            partitions[i] = partitions[i - 1] + partitionWidth + borderWidth;
        }

        // panels for middle section
        int listHeight = listBottom - listTop;
        setsListPanel = new SetsListPanel(screenLeft, listTop - borderWidth, partitionWidth, listHeight, sets,
                (set, button) -> {
                        if (button == 0) { // left click
                            // refresh edit session or send confirm message
                            SplinterSet currActiveSet = SplinterClient.setManager.getActiveSet();
                            if (currActiveSet != set) {
                                if (SplinterClient.ssm.isEditingWithChanges()) {
                                    // editing with changes
                                    client.player.sendMessage(new LiteralText("confirm or cancel changes in GUI")
                                            .styled(s -> s.withColor(Formatting.YELLOW)), false);
                                }
                                else if (SplinterClient.ssm.isEditing()) {
                                    // just editing, refresh edit session
                                    SplinterClient.setManager.setActiveSet(set);
                                    SplinterClient.ssm.refreshEditSession();
                                }
                                else if (SplinterClient.timer.isRunning()) {
                                    // timer is running, invalidate the run then switch
                                    SplinterClient.routeEngine.invalidateRun();
                                    SplinterClient.setManager.setActiveSet(set);
                                }
                                else {
                                    // swap set if it's not already active
                                    SplinterClient.setManager.setActiveSet(set);
                                }
                            }
                        }
                        else if (button == 1) { // right click logic
                            // RC + SHIFT displays, RC opens context menu
                            if (hasShiftDown() && setA == null && setB != set) {
                                SplinterClient.setManager.setDisplayedSetA(set);
                                init();
                            }
                            else if (hasShiftDown() && setB == null && setA != set) {
                                SplinterClient.setManager.setDisplayedSetB(set);
                                init();
                            }
                            else {
                                // context menu
                                contextMenu.open(lastClickX, lastClickY, height, set, List.of(
                                        new ContextMenu.Option("Set as A", () -> {
                                            SplinterClient.setManager.setDisplayedSetA(set);
                                            init();
                                        }, SplinterColors.TEXT,
                                                SplinterClient.setManager.getDisplayedSetA() != set),
                                        new ContextMenu.Option("Set as B", () -> {
                                            SplinterClient.setManager.setDisplayedSetB(set);
                                            init();
                                        }, SplinterColors.TEXT,
                                                SplinterClient.setManager.getDisplayedSetB() != set),
                                        new ContextMenu.Option("Rename", () -> {
                                            activeModal = new InputModal("Rename Set", () -> {
                                                if(activeModal instanceof InputModal im) {
                                                    String name = im.getTextInput();
                                                    if (name == null || name.isEmpty()) {
                                                        im.setPopUp(false);
                                                    }
                                                    else if (sets.contains(new SplinterSet(name, new Route()))) {
                                                        im.setPopUp(true);
                                                    } else {
                                                        set.renameSet(name);
                                                        activeModal.closeGuard = false;
                                                        activeModal = null;
                                                        init();
                                                    }
                                                }
                                            });
                                            String setName = set.getName();
                                            activeModal.setSubmessage(setName);
                                            activeModal.openModal(width, height);
                                        }, SplinterColors.TEXT, true),
                                        new ContextMenu.Option("Clear", () -> {
                                            activeModal = new ConfirmModal("Clear \"" + set.getName() + "\"?", () -> {
                                                set.clearSet();
                                                activeModal = null;
                                                init();
                                            });
                                            activeModal.openModal(width, height);
                                        }, SplinterColors.TEXT, !set.isEmpty()),
                                        new ContextMenu.Option("Copy", () -> {
                                            SplinterSet copy = new SplinterSet("Copy of " + set.getName(), set.getRoute());
                                            SplinterClient.setManager.addSet(copy);
                                            init();
                                            },
                                                SplinterColors.TEXT,
                                                true),
                                        new ContextMenu.Option("Delete", () -> {
                                            activeModal = new ConfirmModal("Delete \"" + set.getName() + "\"?", () -> {
                                                SplinterClient.setManager.deleteSet(set);
                                                activeModal = null;
                                                init();
                                            });
                                            activeModal.openModal(width, height);
                                        }, 0xFF5555, !(sets.size() == 1))
                                ));
                            }
                        }
                    }
                );

        timesListPanelA = new TimesListPanel(partitions[1], listTop - borderWidth, partitionWidth, listHeight, setA);
        timesListPanelB = new TimesListPanel(partitions[2], listTop - borderWidth, partitionWidth, listHeight, setB);

        // initialize buttons

        int buttonHeight = 20;

        addButton(new SplinterButton(screenLeft, screenTop, partitionWidth, buttonHeight,
                new LiteralText("NEW SET"),
                () -> {
                    activeModal = new InputModal("Choose Set Name", () -> {
                        if(activeModal instanceof InputModal im) {
                            String name = im.getTextInput();
                            if (name == null || name.isEmpty()) {
                                im.setPopUp(false);
                            }
                            else if (sets.contains(new SplinterSet(name, new Route()))) {
                                im.setPopUp(true);
                            } else {
                                SplinterClient.setManager.createSet(name);
                                activeModal.closeGuard = false;
                                activeModal = null;
                                init();
                            }
                        }
                    });
                    activeModal.openModal(width, height);
                }
        ));

        int exportButtonHeight = 18;

        // open export screen
        //         addButton(new SplinterButton(partitions[3] + 5, screenBottom - exportButtonHeight - 5, partitionWidth, exportButtonHeight,
        addButton(new SplinterButton(partitions[3] + 5, screenBottom - exportButtonHeight - 5, partitionWidth, exportButtonHeight,
                new LiteralText("EXPORT DATA"),
                () -> {
                    boolean hasTime = false;
                    SetsScreen.toggle();
                    ExportScreen.toggle();
                }
        ));

        // open hints overlay
        int  iButtonLen = 18;
        addButton(new SplinterButton(screenRight - iButtonLen - 5, screenBottom - iButtonLen - 5, iButtonLen, iButtonLen,
                new LiteralText("i"),
                () -> {
                    String keybind = KeyInputHandler.TOGGLE_EDIT_BIND.getKeyBinding().getBoundKeyLocalizedText().getString();
                    List<String> messages = new ArrayList<>();
                    messages.add("enter idle mode by pressing the \"■\" symbol");
                    messages.add("enter edit mode with " + "\" " + keybind + " \"");
                    messages.add("Shift + Right Mouse to quick display a set");

                    activeModal = new ConfirmModal(messages, () -> {
                        activeModal = null;
                        init();
                    });
                    activeModal.openModal(width, height);
                }
        ));


        if (activeModal != null) activeModal.openModal(width, height);

        // should replace this logic with the active checking later
        // dynamic header buttons to clear the specified displayed set
        int headerWidth = partitionWidth;
        headerButtonLen = 20;
        int startX = screenLeft + partitionWidth;

        if (setA != null) {
            SplinterExitButton newButton = new SplinterExitButton(startX + borderWidth, screenTop, headerButtonLen,
                     () -> {
                         SplinterClient.setManager.clearDisplayedSetA();
                         init();
                     });
            exitButtons.add(newButton);
        }

        if (setB != null) {
            SplinterExitButton newButton = new SplinterExitButton(startX + borderWidth, screenTop, headerButtonLen,
                    () -> {
                        SplinterClient.setManager.clearDisplayedSetB();
                        init();
                    });
            exitButtons.add(newButton);
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {

        // GUI title text
        drawCenteredText(matrixStack, textRenderer, title, width / 2, 10, textColor);

        // top panel (create button, headers)
        int topPanelColor = SplinterColors.alpha(SplinterColors.TOP_PANEL, 0xE0);;
        fill(matrixStack, screenLeft, screenTop, screenRight, headerBottom, topPanelColor);
        // headers
        headerTextY = screenTop + (headerHeight - textRenderer.fontHeight + 1) / 2;
        int setAX = partitions[1] + headerButtonLen + 3;
        int setBX = setAX + partitionWidth;
        int headerWidth = partitionWidth - headerButtonLen - 6;
        int headersBorderColor = SplinterColors.BORDER_OTHER;
        fill(matrixStack, screenLeft, headerBottom, screenRight,headerBottom + borderWidth, headersBorderColor);

//        // menu bar
//        int menuBarColor = SplinterColors.alpha(SplinterColors.MENU_BAR, 0xE0);;
//        fill(matrixStack, screenLeft, menuBarTop, screenRight, menuBarBottom, menuBarColor);
//        fill(matrixStack, screenLeft, menuBarBottom, screenRight,  menuBarBottom + borderWidth, headersBorderColor);

        // middle panel (sets, times, stats)
        int middlePanelColor = SplinterColors.alpha(SplinterColors.MIDDLE_PANEL, 0xE0); // 88% opacity
        fill(matrixStack, screenLeft, listTop, screenRight, listBottom, middlePanelColor);

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

        // vertical borders between columns
        int verticalBorderColor = SplinterColors.BORDER;
        fill(matrixStack, partitions[1] - borderWidth, listTop, partitions[1], listBottom, verticalBorderColor);
        fill(matrixStack, partitions[2] - borderWidth, listTop, partitions[2], listBottom, verticalBorderColor);
        fill(matrixStack, partitions[3] - borderWidth, listTop, partitions[3], listBottom, verticalBorderColor);

        if (setA != null) {
            textRenderer.drawWithShadow(matrixStack,
                    TruncateText.truncate(setA.getName(), headerWidth, textRenderer),
                    setAX, headerTextY, textColor);
        }

        if (setB != null) {
            textRenderer.drawWithShadow(matrixStack,
                    TruncateText.truncate(setB.getName(), headerWidth, textRenderer),
                    setBX, headerTextY, textColor);
        }

        // render middle ListPanels

        double scale = client.getWindow().getScaleFactor();
        int scissorWidth = screenRight - screenLeft;
        int scissorHeight = listBottom - listTop;

        ScissorUtil.enable(scale, screenLeft, menuBarBottom + borderWidth, scissorWidth, scissorHeight);
        boolean showSetsHover = !contextMenu.isVisible();
        setsListPanel.render(matrixStack, textRenderer, mouseX, mouseY, showSetsHover);
        timesListPanelA.render(matrixStack, textRenderer, mouseX, mouseY, false);
        timesListPanelB.render(matrixStack, textRenderer, mouseX, mouseY, false);
        ScissorUtil.disable();

        // render context menu
        if (contextMenu.isVisible()) {
            contextMenu.render(matrixStack, textRenderer, mouseX, mouseY);
        }

        // render stats and overlays
        renderStats(matrixStack);

        // modals render on top of everything
        if (activeModal != null) {
            activeModal.render(matrixStack, textRenderer, mouseX, mouseY);
        }

        for (SplinterExitButton exitButton : exitButtons) {
            exitButton.renderButton(matrixStack, mouseX, mouseY);
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

        if (timesListPanelA.isMouseOver(mouseX, mouseY)) {
            timesListPanelA.scroll(amount);
            return true;
        }

        if (timesListPanelB.isMouseOver(mouseX, mouseY)) {
            timesListPanelB.scroll(amount);
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, amount);
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        lastClickX = (int) mouseX;
        lastClickY = (int) mouseY;

        if (activeModal != null) {
            boolean pressed = activeModal.handleClick(mouseX, mouseY, button);
            if (activeModal != null && !activeModal.isVisible()) activeModal = null;
            return pressed;
        }

        // context menu gets priority over setlist
        if (contextMenu.isVisible()) {
            if (contextMenu.handleClick(mouseX, mouseY)) return true;
            contextMenu.close();
            return true;
        }

        if(setsListPanel.handleClick(mouseX, mouseY, button)) return true;
        if(timesListPanelA != null && timesListPanelA.handleClick(mouseX, mouseY, button)) return true;
        if(timesListPanelB != null && timesListPanelB.handleClick(mouseX, mouseY, button)) return true;
        return super.mouseClicked(mouseX, mouseY, button);
    }


    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // pass input to activeModal
        if (activeModal != null) {
            boolean pressed = activeModal.keyPressed(keyCode, scanCode, modifiers);
            if (!activeModal.isVisible()) activeModal = null;
            return pressed;
        }
        // leave screen with Esc or specified hotkey
        if (keyCode == GLFW.GLFW_KEY_ESCAPE || KeyInputHandler.GUI_SETS_BIND.getKeyBinding().matchesKey(keyCode, scanCode)) {
            SetsScreen.toggle();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int keyCode) {
        if (activeModal != null) return activeModal.charTyped(chr, keyCode);
        return super.charTyped(chr, keyCode);
    }

    private void renderStats(MatrixStack matrixStack) {
        int statsX = partitions[3];
        int headerWidth = screenRight - statsX;
        drawCenteredText(matrixStack, textRenderer, new LiteralText("Stats Panel"), statsX + headerWidth / 2, headerTextY, textColor);

        int panelWidth = screenRight - partitions[3];
        int panelX = partitions[3];

        int dividerColor = SplinterColors.BORDER_OTHER2;
        // beginning position of the next column, after the border
        int colWidth = panelWidth / 4;
        int stats2X = panelX + panelWidth / 6 + borderWidth;
        int stats3X = stats2X + colWidth + borderWidth;
        int stats4X = stats3X + colWidth + borderWidth;

        int rowHeight = 18;
        int padding = 5;
        int rowTop = listTop + rowHeight + padding;
        int rowBottom = listTop + rowHeight * 5;


        // vertical dividers
        fill(matrixStack, stats2X - borderWidth, listTop + borderWidth, stats2X, rowBottom, dividerColor);
        fill(matrixStack, stats3X - borderWidth, listTop + borderWidth, stats3X, rowBottom, dividerColor);
        fill(matrixStack, stats4X - borderWidth, listTop + borderWidth, stats4X, rowBottom, dividerColor);

        // info column (best, avg, SD)
        textRenderer.drawWithShadow(matrixStack, "BEST", panelX + padding, rowTop, textColor);
        textRenderer.drawWithShadow(matrixStack, "AVG", panelX + padding, rowTop + rowHeight, textColor);
        textRenderer.drawWithShadow(matrixStack, "SD", panelX + padding, rowTop + rowHeight * 2, textColor);
        textRenderer.drawWithShadow(matrixStack, "N", panelX + padding, rowTop + rowHeight * 3, textColor);

        // headers
        textRenderer.drawWithShadow(matrixStack, "Set A", stats2X + padding, listTop + padding, textColor);
        textRenderer.drawWithShadow(matrixStack, "Set B", stats3X + padding, listTop + padding, textColor);
        textRenderer.drawWithShadow(matrixStack, "Diff", stats4X + padding, listTop + padding, textColor);

        // data! setA col data (best, avg, SD), draw dashes if empty/null
        long[] setAStats = null;
        long[] setBStats = null;
        if (setA != null && !setA.getTimes().isEmpty()) {
            setAStats = new long[]{setA.getBest(), setA.getAverage(), setA.getStdDev(), (long) setA.getTimesSize()};
            for (int i = 0; i < 2; i++) {
                textRenderer.drawWithShadow(matrixStack, TimerFormatter.format(setAStats[i]), stats2X + padding, rowTop + (i * rowHeight), textColor);
            }
            String sdText = String.format("%.2fs", setAStats[2] / 1000.0);
            textRenderer.drawWithShadow(matrixStack, sdText, stats2X + padding, rowTop + (2 * rowHeight), textColor);

            String nText = String.valueOf(setAStats[3]);
            textRenderer.drawWithShadow(matrixStack, nText, stats2X + padding, rowTop + (3 * rowHeight), textColor);
        }
        else {
            for (int i = 0; i < 4; i++) {
                textRenderer.drawWithShadow(matrixStack, "-", stats2X + padding, rowTop + (i * rowHeight), textColor);
            }
        }

        // setB stats
        if (setB != null && !setB.getTimes().isEmpty()) {
            setBStats = new long[]{setB.getBest(), setB.getAverage(), setB.getStdDev(), (long) setB.getTimesSize()};
            for (int i = 0; i < 2; i++) {
                textRenderer.drawWithShadow(matrixStack, TimerFormatter.format(setBStats[i]), stats3X + padding, rowTop + (i* rowHeight), textColor);
            }
            String sdText = String.format("%.2fs", setBStats[2] / 1000.0);
            textRenderer.drawWithShadow(matrixStack, sdText, stats3X + padding, rowTop + (2 * rowHeight), textColor);

            String nText = String.valueOf(setBStats[3]);
            textRenderer.drawWithShadow(matrixStack, nText, stats3X + padding, rowTop + (3 * rowHeight), textColor);
        }
        else {
            for (int i = 0; i < 4; i++) {
                textRenderer.drawWithShadow(matrixStack, "-", stats3X + padding, rowTop + (i * rowHeight), textColor);
            }
        }

        // diff stats
        if (setAStats != null && setBStats != null) {
            for (int i = 0; i < 2; i++) {
                long diff = setAStats[i] - setBStats[i];
                String diffText = (diff > 0 ? "+" : "") + TimerFormatter.format(Math.abs(diff));
                int diffColor = diff > 0 ? 0xFF5555 : 0x55FF55; // red if A is slower, green if faster
                textRenderer.drawWithShadow(matrixStack, diffText, stats4X + padding, rowTop + (i * rowHeight), diffColor);
            }
        }
        else {
            for (int i = 0; i < 2; i++) {
                textRenderer.drawWithShadow(matrixStack, "-", stats4X + padding, rowTop + (i * rowHeight), textColor);
            }
        }

    }

    public static void toggle() {
        MinecraftClient client = MinecraftClient.getInstance();
        assert(client != null);
        if (client.currentScreen instanceof SetsScreen) {
            client.openScreen(null);
        }
        else {
            client.openScreen(new SetsScreen());
        }
    }
}
