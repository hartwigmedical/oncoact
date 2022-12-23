package com.hartwig.oncoact.io;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import org.jetbrains.annotations.NotNull;

public final class FileReaderUtils {

    private FileReaderUtils() {
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

    // TODO Remove
    public static String getValue(final List<String> lines, final String field, final String defaultValue, final String delimiter) {
        for (String line : lines) {
            String[] values = line.split(delimiter, -1);
            if (values[0].equals(field)) {
                return values[1];
            }
        }

        return defaultValue;
    }

}
