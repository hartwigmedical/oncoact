package com.hartwig.oncoact.patientreporter.cfreport.data;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class EvidenceItemsTest {

    @Test
    public void canShortenTitleName() {
        assertEquals("DRUP", EvidenceItems.shortenTrialName("DRUP"));
        assertEquals(
                "A Study of Imatinib Versus Nilotinib in Adult Patients With Newly Diagnosed ... Positive (Ph+) Chronic Myelogenous Leukemia in Chronic Phase (CML-CP) (ENESTnd)",
                EvidenceItems.shortenTrialName(
                        "A Study of Imatinib Versus Nilotinib in Adult Patients With Newly Diagnosed Philadelphia Chromosome Positive (Ph+) Chronic Myelogenous Leukemia in Chronic Phase (CML-CP) (ENESTnd)"));
    }

}