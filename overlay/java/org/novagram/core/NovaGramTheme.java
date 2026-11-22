package org.novagram.core;

/**
 * NovaGram's soft Liquid Glass palette.
 *
 * Colors intentionally use translucent fills and gentle cool accents. These are
 * design tokens; the Telegram screens still need to opt into them explicitly.
 */
public final class NovaGramTheme {
    public static final String PRESET_GLASS_SKY = "glass_sky";
    public static final String PRESET_GLASS_LILAC = "glass_lilac";
    public static final String PRESET_GLASS_PEARL = "glass_pearl";
    public static final String PRESET_GLASS_NIGHT = "glass_night";

    public final int window;
    public final int surface;
    public final int primaryText;
    public final int secondaryText;
    public final int accent;
    public final int outgoingBubble;
    public final int incomingBubble;
    public final int glassFill;
    public final int glassStroke;
    public final int glassHighlight;

    private NovaGramTheme(int window, int surface, int primaryText, int secondaryText,
                          int accent, int outgoingBubble, int incomingBubble,
                          int glassFill, int glassStroke, int glassHighlight) {
        this.window = window;
        this.surface = surface;
        this.primaryText = primaryText;
        this.secondaryText = secondaryText;
        this.accent = accent;
        this.outgoingBubble = outgoingBubble;
        this.incomingBubble = incomingBubble;
        this.glassFill = glassFill;
        this.glassStroke = glassStroke;
        this.glassHighlight = glassHighlight;
    }

    public static boolean isKnownPreset(String preset) {
        return PRESET_GLASS_SKY.equals(preset)
                || PRESET_GLASS_LILAC.equals(preset)
                || PRESET_GLASS_PEARL.equals(preset)
                || PRESET_GLASS_NIGHT.equals(preset);
    }

    public static NovaGramTheme fromPreset(String preset) {
        if (PRESET_GLASS_LILAC.equals(preset)) {
            return new NovaGramTheme(
                    0xFFF5F1FA, 0xFFF9F6FC, 0xFF292438, 0xFF817A91,
                    0xFF9B83D5, 0xFFE9DFFA, 0xFFFDFBFF,
                    0xC9FFFFFF, 0x99FFFFFF, 0x70FFFFFF);
        }
        if (PRESET_GLASS_PEARL.equals(preset)) {
            return new NovaGramTheme(
                    0xFFF4F7F8, 0xFFFBFCFD, 0xFF26333B, 0xFF7C8990,
                    0xFF78AFC2, 0xFFDCEEF2, 0xFFFFFFFF,
                    0xD9FFFFFF, 0xA6FFFFFF, 0x80FFFFFF);
        }
        if (PRESET_GLASS_NIGHT.equals(preset)) {
            return new NovaGramTheme(
                    0xFF171A24, 0xFF202432, 0xFFF4F3FA, 0xFFAAA9BA,
                    0xFF9B91E8, 0xFF3A3859, 0xFF292E3D,
                    0xB82B3042, 0x66FFFFFF, 0x24FFFFFF);
        }
        // Default: calm sky blue with cool white glass.
        return new NovaGramTheme(
                0xFFF0F6FA, 0xFFF8FBFD, 0xFF243440, 0xFF748895,
                0xFF70B8DE, 0xFFDCEFFA, 0xFFFFFFFF,
                0xCCFFFFFF, 0x99FFFFFF, 0x80FFFFFF);
    }
}
