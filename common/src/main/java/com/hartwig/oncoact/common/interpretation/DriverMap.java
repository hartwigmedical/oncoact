package com.hartwig.oncoact.common.interpretation;

import java.util.Map;

import com.google.common.collect.Maps;
import com.hartwig.oncoact.common.datamodel.DriverKey;
import com.hartwig.oncoact.common.orange.datamodel.purple.PurpleDriver;

import org.jetbrains.annotations.NotNull;

public final class DriverMap {

    private DriverMap() {
    }

    @NotNull
    public static Map<DriverKey, PurpleDriver> toDriverMap(@NotNull Iterable<PurpleDriver> drivers) {
        Map<DriverKey, PurpleDriver> map = Maps.newHashMap();
        for (PurpleDriver driver : drivers) {
            DriverKey key = DriverKey.create(driver.gene(), driver.transcript());
            map.put(key, driver);
        }
        return map;
    }
}
