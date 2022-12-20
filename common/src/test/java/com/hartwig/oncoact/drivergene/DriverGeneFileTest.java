package com.hartwig.oncoact.drivergene;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import com.google.common.io.Resources;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class DriverGeneFileTest {

    private static final String DRIVER_GENE_FILE = Resources.getResource("driver_genes/driver_gene_panel.tsv").getPath();

    @Test
    public void canReadDriverGeneFile() throws IOException {
        List<DriverGene> driverGenes = DriverGeneFile.read(DRIVER_GENE_FILE);

        assertEquals(2, driverGenes.size());

        DriverGene gene = findByGene(driverGenes, "B");
        assertEquals(2, gene.additionalReportedTranscripts().size());
        assertTrue(gene.additionalReportedTranscripts().contains("trans1"));
        assertTrue(gene.additionalReportedTranscripts().contains("trans2"));

        // TODO Expand test to cover all fields.
    }

    @NotNull
    private static DriverGene findByGene(@NotNull List<DriverGene> driverGenes, @NotNull String geneToFind) {
        for (DriverGene driverGene : driverGenes) {
            if (driverGene.gene().equals(geneToFind)) {
                return driverGene;
            }
        }

        throw new IllegalStateException("Could not find driver gene for gene: " + geneToFind);
    }

}