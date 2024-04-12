package com.hartwig.oncoact.patientreporter.algo.wgs;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hartwig.hmftools.datamodel.orange.OrangeRecord;
import com.hartwig.hmftools.datamodel.peach.PeachGenotype;
import com.hartwig.hmftools.datamodel.purple.PurpleQCStatus;
import com.hartwig.oncoact.hla.HlaAllelesReportingData;
import com.hartwig.oncoact.hla.HlaAllelesReportingFactory;
import com.hartwig.oncoact.orange.OrangeJson;
import com.hartwig.oncoact.patientreporter.model.FailGenomic;
import com.hartwig.oncoact.patientreporter.qcfail.QCFailReason;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.hartwig.oncoact.patientreporter.algo.wgs.HlaAllelesCreator.createHlaAllelesFailed;
import static com.hartwig.oncoact.patientreporter.algo.wgs.PharmacogeneticsCreator.createPharmacogeneticsGenotype;

class FailGenomicCreator {
    private static final Logger LOGGER = LogManager.getLogger(FailGenomicCreator.class);

    static FailGenomic createFailGenomic(
            QCFailReason reason,
            @NotNull String orangeJson
    ) throws IOException {

        Map<String, List<PeachGenotype>> pharmacogeneticsMap = Maps.newHashMap();

        OrangeRecord orange = OrangeJson.read(orangeJson);

        String formattedPurity = new DecimalFormat("#'%'").format(orange.purple().fit().purity() * 100);
        boolean hasReliablePurity = orange.purple().fit().containsTumorCells();

        String wgsPurityString = hasReliablePurity ? formattedPurity : "N/A";
        Set<PurpleQCStatus> purpleQc = orange.purple().fit().qc().status();

        LOGGER.info("  QC status: {}", Objects.toString(purpleQc));

        Set<PeachGenotype> pharmacogeneticsGenotypes = Sets.newHashSet();
        if (reason.isDeepWGSDataAvailable() && !purpleQc.contains(PurpleQCStatus.FAIL_CONTAMINATION)) {
            pharmacogeneticsGenotypes = orange.peach();
        }

        for (PeachGenotype pharmacogenetics : pharmacogeneticsGenotypes) {
            if (pharmacogeneticsMap.containsKey(pharmacogenetics.gene())) {
                List<PeachGenotype> curent = pharmacogeneticsMap.get(pharmacogenetics.gene());
                curent.add(pharmacogenetics);
                pharmacogeneticsMap.put(pharmacogenetics.gene(), curent);
            } else {
                pharmacogeneticsMap.put(pharmacogenetics.gene(), Lists.newArrayList(pharmacogenetics));
            }
        }
        HlaAllelesReportingData hlaReportingData = HlaAllelesReportingFactory.convertToReportData(orange.lilac(), hasReliablePurity, purpleQc);

        purpleQc = orange.purple().fit().qc().status();

        return FailGenomic.builder()
                .purityString(wgsPurityString)
                .purpleQC(purpleQc)
                .pharmacogenetics(createPharmacogeneticsGenotype(pharmacogeneticsMap))
                .hlaAlleles(createHlaAllelesFailed(hlaReportingData, hasReliablePurity))
                .hlaQc(hlaReportingData.hlaQC())
                .build();
    }
}