package org.novagram.core;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

/**
 * Reusable soft-glass surfaces for NovaGram screens.
 *
 * Android's regular view background cannot sample and blur arbitrary content
 * behind it on every supported device. This helper therefore creates a
 * lightweight translucent gradient with a fine highlight stroke; it avoids
 * claiming a true backdrop blur where the platform cannot provide one.
 */
public final class NovaGramGlass {
    private NovaGramGlass() {}

    public static GradientDrawable panel(Context context, boolean dark, boolean reduceTransparency) {
        int[] colors;
        int strokeColor;
        if (dark) {
            colors = reduceTransparency
                    ? new int[] {0xFF303649, 0xFF252B3B}
                    : new int[] {0xD9363D52, 0xB82A3042};
            strokeColor = 0x66FFFFFF;
        } else {
            colors = reduceTransparency
                    ? new int[] {0xFFFDFEFF, 0xFFF0F5F9}
                    : new int[] {0xEFFFFFFF, 0xBFFFFFFF};
            strokeColor = 0xB3FFFFFF;
        }
        GradientDrawable drawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, colors);
        drawable.setCornerRadius(dp(context, 24));
        drawable.setStroke(dpInt(context, 1), strokeColor);
        return drawable;
    }

    public static GradientDrawable pill(Context context, boolean dark, boolean reduceTransparency) {
        GradientDrawable drawable = panel(context, dark, reduceTransparency);
        drawable.setCornerRadius(dp(context, 999));
        return drawable;
    }

    public static void applyPanel(View view, boolean dark, boolean reduceTransparency) {
        view.setBackground(panel(view.getContext(), dark, reduceTransparency));
        view.setElevation(dp(view.getContext(), 5));
    }

    private static float dp(Context context, float value) {
        return value * context.getResources().getDisplayMetrics().density;
    }

    private static int dpInt(Context context, float value) {
        return Math.round(dp(context, value));
    }
}
