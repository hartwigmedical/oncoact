package com.hartwig.oncoact.util;

import java.util.Map;

import com.google.common.collect.Maps;

import org.jetbrains.annotations.NotNull;

public final class FileReader {

    private FileReader() {
    }

    @NotNull
    public static Map<String, Integer> createFields(@NotNull String fieldsHeader, @NotNull String delimiter) {
        String[] items = fieldsHeader.split(delimiter, -1);
        Map<String, Integer> fieldsIndexMap = Maps.newLinkedHashMap();

        for (int i = 0; i < items.length; ++i) {
            fieldsIndexMap.put(items[i], i);
        }

        return fieldsIndexMap;
    }
}
