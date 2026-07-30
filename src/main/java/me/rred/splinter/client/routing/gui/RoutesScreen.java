package me.rred.splinter.client.routing.gui;

import me.rred.splinter.client.SplinterClient;
import me.rred.splinter.client.keyboard.KeyInputHandler;
import me.rred.splinter.client.routing.Route;
import me.rred.splinter.client.sets.SplinterSet;
import me.rred.splinter.client.sets.gui.SetsScreen;
import me.rred.splinter.client.sets.gui.exports.ExportScreen;
import me.rred.splinter.client.utils.ScissorUtil;
import me.rred.splinter.client.utils.SplinterColors;
import me.rred.splinter.client.widgets.SplinterButton;
import me.rred.splinter.client.widgets.modals.InputModal;
import me.rred.splinter.client.widgets.modals.SplinterModal;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class RoutesScreen extends Screen {
    private int screenTop, screenBottom, screenLeft, screenRight;
    private int listTop, headersBottom;
    private final int[] partitions = new int[3];
    private int partitionWidth;
    private final int borderWidth = 1;
    private static final int textColor = SplinterColors.TEXT;
    private final int headerHeight = 20;
    private RoutesListPanel routesListPanel;
    private SplinterModal activeModal;
    private SplinterButton renameButton;
    private SplinterButton editButton;

    private List<Route> routes = new ArrayList<>();
    private Route selectedRoute = null;
    private SplinterSet set = null;
    private Route currentRoute;
    private boolean swapMode;

    public RoutesScreen() {
        super(new LiteralText("Routes Menu"));
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

        partitions[0] = screenLeft;
        partitionWidth = (screenRight - screenLeft) / 3;

        for (int i = 1; i <= 2; i++) {
            partitions[i] = partitions[i - 1] + partitionWidth + borderWidth;
        }

        routes = SplinterClient.routeRegistry.getAllRoutes();

        int buttonHeight = 18;
        int buttonWidth = partitionWidth / 2;

        // top of list is after the buttons
        headersBottom = screenTop + headerHeight;
        listTop = headersBottom + borderWidth;

        int listHeight = screenBottom - listTop;
        routesListPanel = new RoutesListPanel(screenLeft, listTop - borderWidth, partitionWidth, listHeight, routes,
                (route, button) -> {
                    if (button == 0 || button == 1) {
                        if (selectedRoute == route) {
                            selectedRoute = null;
                        } else {
                            selectedRoute = route;
                        }
                        routesListPanel.updateSelectedRoute(selectedRoute);
                    }
                }
        );
        routesListPanel.updateSelectedRoute(selectedRoute);

        int buttonsX = screenRight - buttonWidth - 5;
        int cancelY = screenBottom - buttonHeight - 5;

        // return to sets screen
        addButton(new SplinterButton(buttonsX, cancelY, buttonWidth, buttonHeight,
                new LiteralText("EXIT"),
                () -> {
                    // close this screen, open Sets Screen
                    RoutesScreen.toggle();
                    SetsScreen.toggle();
                }
        ));

        int renameY = cancelY - buttonHeight - 5;
        addButton(renameButton = new SplinterButton(buttonsX, renameY, buttonWidth, buttonHeight,
            new LiteralText("RENAME"),
                () -> {
                    activeModal = new InputModal("Rename Route", () -> {
                        if(activeModal instanceof InputModal im) {
                            String name = im.getTextInput();
                            if (name == null || name.isEmpty()) {
                                im.setPopUp(false);
                            }
                            else if (routes.contains(new Route(name))) {
                                im.setPopUp(true);
                            } else {
                                selectedRoute.setName(name);
                                activeModal.closeGuard = false;
                                activeModal = null;
                                init();
                            }
                        }
                    });
                    String routeName = selectedRoute.getName();
                    activeModal.setSubmessage(routeName);
                    activeModal.openModal(width, height);
                }
        ));

        int editY = renameY - buttonHeight - 5;
        addButton(editButton = new SplinterButton(buttonsX, editY, buttonWidth, buttonHeight,
                new LiteralText("EDIT"),
                () -> {
                    SplinterClient.ssm.setEdit(selectedRoute);
                    RoutesScreen.toggle();
                }
        ));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        drawCenteredText(matrixStack, textRenderer, title, width / 2, 10, textColor);

        int topPanelColor = SplinterColors.alpha(SplinterColors.TOP_PANEL, 0x95);;
        fill(matrixStack, screenLeft, screenTop, screenRight, screenTop + headerHeight, topPanelColor);

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
        int headerTextY = screenTop + (headerHeight - textRenderer.fontHeight + 1) / 2;
        String headerText = "Routes";
        int headersBorderColor = SplinterColors.BORDER_OTHER;
        int headerMiddleX = partitions[0] + partitionWidth / 2;
        int headerX = headerMiddleX - textRenderer.getWidth(headerText) / 2;

        textRenderer.drawWithShadow(matrixStack, "Routes", headerX, headerTextY, textColor);

        // route info text
        int headerX2 = partitions[1] + 5;
        textRenderer.drawWithShadow(matrixStack, "Route Info", headerX2, headerTextY, textColor);
        DrawableHelper.fill(matrixStack, screenLeft, headersBottom, screenRight, listTop, headersBorderColor);

        int routeInfoY = listTop + 5;
        textRenderer.drawWithShadow(matrixStack, "Nothing to see here!", headerX2, routeInfoY, textColor);

        // sets list
        double scale = client.getWindow().getScaleFactor();
        int scissorWidth = screenRight - screenLeft;
        int scissorHeight = screenBottom - listTop;

        ScissorUtil.enable(scale, screenLeft, listTop, scissorWidth, scissorHeight);
        routesListPanel.render(matrixStack, textRenderer, mouseX, mouseY, true);
        ScissorUtil.disable();

        if (activeModal != null && selectedRoute != null) {
            activeModal.render(matrixStack, textRenderer, mouseX, mouseY);
        }

        renameButton.active = selectedRoute != null;
        editButton.active = selectedRoute != null;

        if (selectedRoute != null && !selectedRoute.isDefault()) {
            renameButton.active = true;
            editButton.active = true;
        } else {
            renameButton.active = false;
            editButton.active = false;
        }

        super.render(matrixStack, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (routesListPanel.isMouseOver(mouseX, mouseY)) {
            routesListPanel.scroll(amount);
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (activeModal != null) {
            boolean pressed = activeModal.handleClick(mouseX, mouseY, button);
            if (activeModal != null && !activeModal.isVisible()) activeModal = null;
            return pressed;
        }

        if(routesListPanel.handleClick(mouseX, mouseY, button)) {
            return true;
        }
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
            ExportScreen.toggle();
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

    public static void toggle() {
        MinecraftClient client = MinecraftClient.getInstance();
        assert(client != null);
        if (client.currentScreen instanceof RoutesScreen) {
            client.openScreen(null);
        }
        else {
            client.openScreen(new RoutesScreen());
        }
    }
 }
