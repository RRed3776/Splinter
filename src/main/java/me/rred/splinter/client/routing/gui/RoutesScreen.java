package me.rred.splinter.client.routing.gui;

import me.rred.splinter.client.SplinterClient;
import me.rred.splinter.client.routing.Route;
import me.rred.splinter.client.sets.SplinterSet;
import me.rred.splinter.client.sets.gui.SetsScreen;
import me.rred.splinter.client.sets.gui.exports.ExportScreen;
import me.rred.splinter.client.utils.SplinterColors;
import me.rred.splinter.client.widgets.SplinterButton;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

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

    private List<Route> routes = new ArrayList<>();
    private SplinterSet set = null;

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

        int cancelX = screenRight - buttonWidth - 5;
        // return to sets screen
        addButton(new SplinterButton(cancelX, screenBottom - buttonHeight - 5, buttonWidth, buttonHeight,
                new LiteralText("EXIT"),
                () -> {
                    // close this screen, open Sets Screen
                    RoutesScreen.toggle();
                    SetsScreen.toggle();
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

        int headerX2 = partitions[1] + 5;
        textRenderer.drawWithShadow(matrixStack, "Route Info", headerX2, headerTextY, textColor);

        DrawableHelper.fill(matrixStack, screenLeft, headersBottom, screenRight,  listTop, headersBorderColor);

        // sets list
        double scale = client.getWindow().getScaleFactor();
        int scissorWidth = screenRight - screenLeft;
        int scissorHeight = screenBottom - listTop - borderWidth;

        super.render(matrixStack, mouseX, mouseY, delta);
    }

    public void selectSet(SplinterSet set) {
        this.set = set;
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
