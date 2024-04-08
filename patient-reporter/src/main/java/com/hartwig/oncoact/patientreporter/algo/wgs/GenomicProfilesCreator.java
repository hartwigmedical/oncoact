package com.hartwig.oncoact.patientreporter.algo.wgs;

import com.hartwig.hmftools.datamodel.chord.ChordStatus;
import com.hartwig.hmftools.datamodel.purple.PurpleMicrosatelliteStatus;
import com.hartwig.hmftools.datamodel.purple.PurpleTumorMutationalStatus;
import com.hartwig.oncoact.patientreporter.model.GenomicProfiles;
import org.jetbrains.annotations.NotNull;

import static com.hartwig.oncoact.patientreporter.algo.wgs.HomologousRecombinationDeficiencyCreator.createHomologousRecombinationDeficiencyExtend;
import static com.hartwig.oncoact.patientreporter.algo.wgs.MicrosatelliteCreator.createMicrosatalliteExtend;
import static com.hartwig.oncoact.patientreporter.algo.wgs.TumorMutationalBurdenCreator.createTumorMutationalBurdenExtend;

class GenomicProfilesCreator {

    static GenomicProfiles createGenomicProfiles(
            double hrdValue,
            @NotNull ChordStatus hrdStatus,
            double microsatelliteIndelsPerMb,
            @NotNull PurpleMicrosatelliteStatus microsatelliteStatus,
            double tumorMutationalBurden,
            @NotNull PurpleTumorMutationalStatus tumorMutationalBurdenStatus,
            int tumorMutationalLoad,
            boolean hasReliablePurity) {

        return GenomicProfiles.builder()
                .tumorMutationalBurden(createTumorMutationalBurdenExtend(tumorMutationalBurden, tumorMutationalBurdenStatus, hasReliablePurity))
                .microsatellite(createMicrosatalliteExtend(microsatelliteIndelsPerMb, microsatelliteStatus, hasReliablePurity))
                .homologousRecombinationDeficiency(createHomologousRecombinationDeficiencyExtend(hrdValue, hrdStatus, hasReliablePurity))
                .tumorMutationalLoad(tumorMutationalLoad)
                .build();
    }
}