package com.hartwig.oncoact.common.linx;

import java.io.IOException;
import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.oncoact.common.drivercatalog.DriverCatalog;
import com.hartwig.oncoact.common.drivercatalog.DriverCatalogFile;
import com.hartwig.oncoact.common.drivercatalog.DriverType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public final class HomozygousDisruptionFactory
{
    private static final Logger LOGGER = LogManager.getLogger(HomozygousDisruptionFactory.class);

    private HomozygousDisruptionFactory()
    {
    }

    @NotNull
    public static List<HomozygousDisruption> extractFromLinxDriverCatalogTsv(@NotNull String linxDriverCatalogTsv)
            throws IOException
    {
        List<DriverCatalog> linxDriversCatalog = DriverCatalogFile.read(linxDriverCatalogTsv);
        LOGGER.debug(" Loaded {} linx driver catalog records from {}", linxDriversCatalog.size(), linxDriverCatalogTsv);

        List<HomozygousDisruption> homozygousDisruptions = extractHomozygousDisruptions(linxDriversCatalog);
        LOGGER.debug("  Extracted {} homozygous disruptions from linx drivers", homozygousDisruptions.size());
        return homozygousDisruptions;
    }

    private static List<HomozygousDisruption> extractHomozygousDisruptions(final List<DriverCatalog> driverCatalog)
    {
        List<HomozygousDisruption> homozygousDisruptions = Lists.newArrayList();

        for(DriverCatalog driver : driverCatalog)
        {
            if(driver.driver() == DriverType.HOM_DUP_DISRUPTION || driver.driver() == DriverType.HOM_DEL_DISRUPTION)
            {
                homozygousDisruptions.add(create(driver));
            }
        }

        return homozygousDisruptions;
    }

    private static HomozygousDisruption create(final DriverCatalog driverCatalog)
    {
        return ImmutableHomozygousDisruption.builder()
                .chromosome(driverCatalog.chromosome())
                .chromosomeBand(driverCatalog.chromosomeBand())
                .gene(driverCatalog.gene())
                .transcript(driverCatalog.transcript())
                .isCanonical(driverCatalog.isCanonical())
                .build();
    }
}
