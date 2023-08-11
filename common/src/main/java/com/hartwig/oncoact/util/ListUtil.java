package com.hartwig.oncoact.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ListUtil {

    private ListUtil() {
    }

    public static <T> List<T> mergeLists(@NotNull Collection<T> list1, @Nullable Collection<T> list2) {
        ArrayList<T> result = new ArrayList<>(list1);
        if (list2 != null) {
            result.addAll(list2);
        }
        return result;
    }
}