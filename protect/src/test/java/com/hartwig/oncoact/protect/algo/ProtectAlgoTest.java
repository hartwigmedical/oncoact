package com.hartwig.oncoact.protect.algo;

import static org.junit.Assert.assertNotNull;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hartwig.oncoact.doid.DoidParents;
import com.hartwig.hmftools.datamodel.TestOrangeFactory;
import com.hartwig.serve.datamodel.ActionableEvents;
import com.hartwig.serve.datamodel.ImmutableActionableEvents;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class ProtectAlgoTest {

    @Test
    public void canRunProtectAlgoOnMinimalTestData() {
        ProtectAlgo algo = createTestAlgo();

        assertNotNull(algo.run(TestOrangeFactory.createMinimalTestOrangeRecord()));
    }

    @Test
    public void canRunProtectAlgoOnProperTestData() {
        ProtectAlgo algo = createTestAlgo();

        assertNotNull(algo.run(TestOrangeFactory.createProperTestOrangeRecord()));
    }

    @NotNull
    private static ProtectAlgo createTestAlgo() {
        ActionableEvents events = ImmutableActionableEvents.builder().build();
        DoidParents doidParents = DoidParents.fromEdges(Lists.newArrayList());

        return ProtectAlgo.build(events, Sets.newHashSet(), Lists.newArrayList(), doidParents);
    }
}