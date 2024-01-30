package com.hartwig.oncoact.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ListUtil {

    private ListUtil() {
    }

    public static <T> List<T> mergeLists(@NotNull Collection<T> list1, @Nullable Collection<T> list2) {
        Set<T> result = new HashSet<>(list1);
        if (list2 != null) {
            result.addAll(list2);
        }
        return new ArrayList<>(result);
    }

    public static <T> List<T> mergeLists(@NotNull Collection<T> list1, @Nullable Collection<T> list2, @Nullable Collection<T> list3) {
        Set<T> result = new HashSet<>(list1);
        if (list2 != null) {
            result.addAll(list2);
        }
        if (list3 != null) {
            result.addAll(list3);
        }
        return new ArrayList<>(result);
    }
}