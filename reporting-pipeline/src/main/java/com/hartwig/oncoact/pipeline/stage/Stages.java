package com.hartwig.oncoact.pipeline.stage;

import java.util.Collection;
import java.util.List;

import com.hartwig.oncoact.graph.FileResource;
import com.hartwig.oncoact.graph.ImmutableParameterResource;
import com.hartwig.oncoact.graph.ParameterResource;
import com.hartwig.oncoact.graph.Pipeline;
import com.hartwig.oncoact.graph.Stage;
import com.hartwig.oncoact.kubernetes.KubernetesStage;
import com.hartwig.oncoact.pipeline.Arguments;

public class Stages {

    public static Pipeline getStages(Arguments arguments) {
        return Pipeline.builder()
                .stages(List.of(roseStage(arguments), protectStage(arguments)))
                .inputs(List.of(orangeResource(), patientIdResource(arguments), tumorDoidsResource(arguments)))
                .outputs(List.of(roseResource(), protectResource()))
                .build();
    }

    static FileResource orangeResource() {
        return FileResource.builder().name("orange.json").build();
    }

    static FileResource roseResource() {
        return FileResource.builder().name("rose.tsv").build();
    }

    static FileResource protectResource() {
        return FileResource.builder().name("protect.tsv").build();
    }

    private static ImmutableParameterResource patientIdResource(final Arguments arguments) {
        return ParameterResource.builder().name("patient_id").value(arguments.patientId()).build();
    }

    private static ImmutableParameterResource tumorDoidsResource(final Arguments arguments) {
        return ParameterResource.builder().name("primary_tumor_doids").value(arguments.primaryTumorDoids()).build();
    }

    static Stage roseStage(Arguments arguments) {
        var name = "rose";
        var kubernetesExecutionDetails = KubernetesStage.builder().imageName(name).imageVersion("1.0.0-alpha.1").build();
        var patientId = patientIdResource(arguments);
        return Stage.builder()
                .name(name)
                .inputs(List.of(orangeResource(), patientId))
                .outputs(List.of(roseResource()))
                .executionDetails(List.of(kubernetesExecutionDetails))
                .build();
    }

    static Stage protectStage(Arguments arguments) {
        var name = "protect";
        var kubernetesExecutionDetails = KubernetesStage.builder().imageName(name).imageVersion("1.0.0-alpha.1").build();
        var tumorDoids = tumorDoidsResource(arguments);
        return Stage.builder()
                .name(name)
                .inputs(List.of(orangeResource(), tumorDoids))
                .outputs(List.of(protectResource()))
                .executionDetails(List.of(kubernetesExecutionDetails))
                .build();
    }
}
