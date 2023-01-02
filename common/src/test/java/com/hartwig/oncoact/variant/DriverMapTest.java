package com.hartwig.oncoact.variant;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.hartwig.oncoact.orange.purple.ImmutablePurpleDriver;
import com.hartwig.oncoact.orange.purple.PurpleDriver;
import com.hartwig.oncoact.orange.purple.PurpleDriverType;
import com.hartwig.oncoact.orange.purple.TestPurpleFactory;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class DriverMapTest {

    @Test
    public void canMapDriverCatalog() {
        String gene1 = "CDKN2A";
        String gene2 = "BRAF";

        PurpleDriver driver1 = mutationBuilder().gene(gene1).transcript("transcript 1").build();
        PurpleDriver driver2 = mutationBuilder().gene(gene1).transcript("transcript 2").build();
        PurpleDriver driver3 = mutationBuilder().gene(gene2).transcript("transcript 3").build();
        List<PurpleDriver> drivers = Lists.newArrayList(driver1, driver2, driver3);

        Map<DriverKey, PurpleDriver> driverMap = DriverMap.create(drivers);

        assertEquals(driver1, driverMap.get(DriverKey.create(gene1, "transcript 1")));
        assertEquals(driver2, driverMap.get(DriverKey.create(gene1, "transcript 2")));
        assertEquals(driver3, driverMap.get(DriverKey.create(gene2, "transcript 3")));
    }

    @NotNull
    private static ImmutablePurpleDriver.Builder mutationBuilder() {
        return TestPurpleFactory.driverBuilder().type(PurpleDriverType.MUTATION);
    }
}