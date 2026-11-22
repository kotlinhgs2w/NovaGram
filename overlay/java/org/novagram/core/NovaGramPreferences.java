package org.novagram.core;

import android.content.Context;
import android.content.SharedPreferences;

/** Local NovaGram appearance preferences. These settings do not alter Telegram protocol behavior. */
public final class NovaGramPreferences {
    private static final String FILE_NAME = "novagram_preferences";
    private static final String KEY_THEME = "theme_preset";
    private static final String KEY_REDUCED_MOTION = "reduced_motion";
    private static final String KEY_REDUCE_TRANSPARENCY = "reduce_transparency";
    private static final String KEY_COMPACT_CHATS = "compact_chats";

    private NovaGramPreferences() {}

    private static SharedPreferences store(Context context) {
        return context.getApplicationContext().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    public static String getThemePreset(Context context) {
        return store(context).getString(KEY_THEME, NovaGramTheme.PRESET_GLASS_SKY);
    }

    public static void setThemePreset(Context context, String preset) {
        if (!NovaGramTheme.isKnownPreset(preset)) {
            throw new IllegalArgumentException("Unknown NovaGram theme preset: " + preset);
        }
        store(context).edit().putString(KEY_THEME, preset).apply();
    }

    public static boolean isReducedMotionEnabled(Context context) {
        return store(context).getBoolean(KEY_REDUCED_MOTION, false);
    }

    public static void setReducedMotionEnabled(Context context, boolean enabled) {
        store(context).edit().putBoolean(KEY_REDUCED_MOTION, enabled).apply();
    }

    public static boolean isReduceTransparencyEnabled(Context context) {
        return store(context).getBoolean(KEY_REDUCE_TRANSPARENCY, false);
    }

    public static void setReduceTransparencyEnabled(Context context, boolean enabled) {
        store(context).edit().putBoolean(KEY_REDUCE_TRANSPARENCY, enabled).apply();
    }

    public static boolean isCompactChatsEnabled(Context context) {
        return store(context).getBoolean(KEY_COMPACT_CHATS, false);
    }

    public static void setCompactChatsEnabled(Context context, boolean enabled) {
        store(context).edit().putBoolean(KEY_COMPACT_CHATS, enabled).apply();
    }
}
