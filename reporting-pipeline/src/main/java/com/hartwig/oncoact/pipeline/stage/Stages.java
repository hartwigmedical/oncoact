package com.hartwig.oncoact.pipeline.stage;

import java.util.List;

import com.hartwig.oncoact.execution.bash.BashDetails;
import com.hartwig.oncoact.execution.bash.command.GroupCommand;
import com.hartwig.oncoact.execution.bash.command.JavaJarCommand;
import com.hartwig.oncoact.execution.bash.command.MkDirCommand;
import com.hartwig.oncoact.graph.FileResource;
import com.hartwig.oncoact.graph.Pipeline;
import com.hartwig.oncoact.graph.Stage;
import com.hartwig.oncoact.kubernetes.KubernetesStage;
import com.hartwig.oncoact.pipeline.Arguments;

public class Stages {

    public static Pipeline getStages(Arguments arguments) {
        return Pipeline.builder()
                .stages(List.of(roseStage(arguments), protectStage(arguments)))
                .inputs(List.of(orangeResource(arguments)))
                .outputs(List.of(roseResource(), protectResource()))
                .build();
    }

    static FileResource orangeResource(Arguments arguments) {
        return FileResource.builder().path(arguments.orangePath()).name("orange.json").executionDetails(List.of()).build();
    }

    static FileResource roseResource() {
        return FileResource.builder().name("rose.tsv").executionDetails(List.of()).build();
    }

    static FileResource protectResource() {
        return FileResource.builder().name("protect.tsv").executionDetails(List.of()).build();
    }

    static Stage roseStage(Arguments arguments) {
        var name = "rose";
        var kubernetesExecutionDetails = KubernetesStage.builder().imageName(name).imageVersion("1.0.0-alpha.1").build();
        var bashDetails = BashDetails.builder()
                .command(new GroupCommand(new MkDirCommand("rose-out"),
                        new JavaJarCommand("rose/target/rose-2.0-jar-with-dependencies.jar",
                                "1G",
                                List.of("-properties_file", "rose/run-local.properties", "-patient_id", arguments.patientId()))))
                .build();
        return Stage.builder()
                .name(name)
                .inputs(List.of(orangeResource(arguments)))
                .outputs(List.of(roseResource()))
                .executionDetails(List.of(kubernetesExecutionDetails, bashDetails))
                .build();
    }

    static Stage protectStage(Arguments arguments) {
        var name = "protect";
        var kubernetesExecutionDetails = KubernetesStage.builder().imageName(name).imageVersion("1.0.0-alpha.1").build();
        var bashDetails = BashDetails.builder()
                .command(new GroupCommand(new MkDirCommand("protect-out"),
                        new JavaJarCommand("protect/target/protect-3.0-jar-with-dependencies.jar",
                                "1G",
                                List.of("-properties_file", "protect/run-local.properties"))))
                .build();
        return Stage.builder()
                .name(name)
                .inputs(List.of(orangeResource(arguments)))
                .outputs(List.of(protectResource()))
                .executionDetails(List.of(kubernetesExecutionDetails, bashDetails))
                .build();
    }
}
