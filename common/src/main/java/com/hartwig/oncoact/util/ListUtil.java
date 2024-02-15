package com.hartwig.oncoact.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListUtil {

    private ListUtil() {
    }

    @SafeVarargs
    public static <T> List<T> mergeListsDistinct(Collection<T>... lists) {
        Set<T> result = new HashSet<>();
        for (Collection<T> list : lists) {
            if (list != null) {
                result.addAll(list);
            }
        }
        return new ArrayList<>(result);
    }
}