package org.novagram.core;

/** Small, dependency-free palette model for NovaGram UI integration. */
public final class NovaGramTheme {
    public static final String PRESET_TELEGRAM = "telegram";
    public static final String PRESET_MIDNIGHT = "midnight";
    public static final String PRESET_MINT = "mint";

    public final int window;
    public final int surface;
    public final int primaryText;
    public final int secondaryText;
    public final int accent;
    public final int outgoingBubble;
    public final int incomingBubble;

    private NovaGramTheme(int window, int surface, int primaryText, int secondaryText,
                          int accent, int outgoingBubble, int incomingBubble) {
        this.window = window;
        this.surface = surface;
        this.primaryText = primaryText;
        this.secondaryText = secondaryText;
        this.accent = accent;
        this.outgoingBubble = outgoingBubble;
        this.incomingBubble = incomingBubble;
    }

    public static boolean isKnownPreset(String preset) {
        return PRESET_TELEGRAM.equals(preset)
                || PRESET_MIDNIGHT.equals(preset)
                || PRESET_MINT.equals(preset);
    }

    public static NovaGramTheme fromPreset(String preset) {
        if (PRESET_MIDNIGHT.equals(preset)) {
            return new NovaGramTheme(0xFF101820, 0xFF18232D, 0xFFF4F7FA,
                    0xFF9EADB9, 0xFF58A6E7, 0xFF24445B, 0xFF202E38);
        }
        if (PRESET_MINT.equals(preset)) {
            return new NovaGramTheme(0xFFF2F8F5, 0xFFFFFFFF, 0xFF172B24,
                    0xFF65776F, 0xFF26966D, 0xFFD7F4E7, 0xFFFFFFFF);
        }
        return new NovaGramTheme(0xFFF1F3F5, 0xFFFFFFFF, 0xFF17212B,
                0xFF708499, 0xFF3390EC, 0xFFE3FFC9, 0xFFFFFFFF);
    }
}
