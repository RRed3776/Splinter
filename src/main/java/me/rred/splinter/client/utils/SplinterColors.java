package me.rred.splinter.client.utils;

public class SplinterColors {
    // raw Splinter palette
    public static int WHITE = 0xFFFFFFFF;
    public static int LIGHT_GRAY = 0xFF383840;
    public static int CHARCOAL = 0xFF1C1F2A;
    public static int CHARCOAL_BLUE = 0xFF252838;
    public static int DEEP_NAVY = 0xFF2E3248;
    public static int SLATE_NAVY  = 0xFF3A3E55;
    public static int MUTED_SLATE = 0xFF474C65;
    public static int GRAY_BLUE  = 0xFF6B7080;
    public static int SOFT_BLUE = 0xFFABB0BF;
    public static int LAVENDER_GRAY = 0xFFC8CCD8;

    // default colors
    public static int BORDER = GRAY_BLUE;
    public static int BORDER_OTHER = SOFT_BLUE;
    public static int BORDER_HOVER = LAVENDER_GRAY;
    public static int TEXT = WHITE;
    public static int SUB_TEXT = SOFT_BLUE;

    public static int BUTTON_FILL = GRAY_BLUE;
    public static int BUTTON_BORDER = CHARCOAL;

    public static int MODAL_BG = CHARCOAL_BLUE;
    public static int MIDDLE_PANEL = LIGHT_GRAY;
    public static int TOP_PANEL = CHARCOAL_BLUE;
    public static int CONTEXT_MENU = CHARCOAL;


    public static int alpha(int color, int alpha) {
        // alpha will be of the form 0xFF or 0xCC
        // 0xFF -> 0x00000FF
        int mask = 0x00FFFFFF;
        return (alpha << 24) | (color & mask);
    }

    public static int pulse(int color, float speed) {
        float time = (System.currentTimeMillis() % (long)(1000 / speed)) / (1000f / speed);
        float brightness = 0.85f + 0.15f * (float) Math.sin(time * 2 * Math.PI);
        // brightness between 0.7 and 1.0

        int r = (int)(((color >> 16) & 0xFF) * brightness);
        int g = (int)(((color >> 8) & 0xFF) * brightness);
        int b = (int)((color & 0xFF) * brightness);

        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }
}
