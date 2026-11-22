package org.novagram.core;

import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

/** Shared animation defaults; callers should honor the user's reduced-motion preference. */
public final class NovaGramMotion {
    private static final long DEFAULT_DURATION_MS = 180L;

    private NovaGramMotion() {}

    public static long duration(boolean reducedMotion, long requestedDurationMs) {
        if (reducedMotion) {
            return 0L;
        }
        return requestedDurationMs > 0L ? requestedDurationMs : DEFAULT_DURATION_MS;
    }

    public static Interpolator interpolator() {
        return new DecelerateInterpolator(1.5f);
    }
}
