package com.hartwig.oncoact.database;

import org.jetbrains.annotations.NotNull;

public final class Utils {

    private Utils() {
    }

    static boolean anyNull(@NotNull Object... arguments) {
        for (Object object : arguments) {
            if (object == null) {
                return true;
            }
        }
        return false;
    }
}
