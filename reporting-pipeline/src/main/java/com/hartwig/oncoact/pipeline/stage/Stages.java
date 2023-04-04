package com.hartwig.oncoact.pipeline.stage;

import java.util.List;

import com.hartwig.oncoact.execution.bash.BashDetails;
import com.hartwig.oncoact.execution.bash.command.CpCommand;
import com.hartwig.oncoact.execution.bash.command.GroupCommand;
import com.hartwig.oncoact.execution.bash.command.JavaJarCommand;
import com.hartwig.oncoact.execution.bash.command.MkDirCommand;
import com.hartwig.oncoact.graph.IOResource;
import com.hartwig.oncoact.graph.Pipeline;
import com.hartwig.oncoact.graph.Stage;
import com.hartwig.oncoact.kubernetes.KubernetesStage;
import com.hartwig.oncoact.pipeline.Arguments;

public class Stages {

    public static Pipeline getStages(Arguments arguments) {
        return Pipeline.builder()
                .stages(List.of(roseStage(arguments), protectStage(), orangeInputStage(arguments), roseOutputStage()))
                .build();
    }

    static Stage orangeInputStage(Arguments arguments) {
        var bashDetails = BashDetails.builder()
                .command(new GroupCommand(new MkDirCommand("run/orange"), new CpCommand(arguments.orangePath(), "run/orange/orange.json")))
                .build();
        return Stage.builder()
                .name("orange-input")
                .executionDetails(List.of(bashDetails))
                .inputs(List.of())
                .outputs(List.of(IOResource.builder().name("orange.json").location("run/orange").build()))
                .build();
    }

    static Stage roseOutputStage() {
        var bashDetails = BashDetails.builder()
                .command(new GroupCommand(new MkDirCommand("run/out"), new CpCommand("run/rose/out/*", "run/out/")))
                .build();
        return Stage.builder()
                .name("rose-output")
                .executionDetails(List.of(bashDetails))
                .inputs(List.of(IOResource.builder().name("rose.tsv").location("run/rose/out").build()))
                .outputs(List.of())
                .build();
    }

    static Stage roseStage(Arguments arguments) {
        var name = "rose";
        var kubernetesExecutionDetails = KubernetesStage.builder().imageName(name).imageVersion("1.0.0-alpha.1").build();
        var bashDetails = BashDetails.builder()
                .command(new JavaJarCommand("rose/target/rose-2.0-jar-with-dependencies.jar",
                        "1G",
                        List.of("-properties_file", "rose/run-local.properties", "-patient_id", arguments.patientId())))
                .build();
        return Stage.builder()
                .name(name)
                .inputs(List.of(IOResource.builder().name("orange.json").location("run/rose/in").build()))
                .outputs(List.of(IOResource.builder().name("rose.tsv").location("run/rose/out").build()))
                .executionDetails(List.of(kubernetesExecutionDetails, bashDetails))
                .build();
    }

    static Stage protectStage() {
        var name = "protect";
        var kubernetesExecutionDetails = KubernetesStage.builder().imageName(name).imageVersion("1.0.0-alpha.1").build();
        var bashDetails = BashDetails.builder()
                .command(new JavaJarCommand("protect/target/protect-3.0-jar-with-dependencies.jar",
                        "1G",
                        List.of("-properties_file", "protect/run-local.properties")))
                .build();
        return Stage.builder()
                .name(name)
                .inputs(List.of(IOResource.builder().name("orange.json").location("run/protect/in").build()))
                .outputs(List.of(IOResource.builder().name("protect.tsv").location("run/protect/out").build()))
                .executionDetails(List.of(kubernetesExecutionDetails, bashDetails))
                .build();
    }
}
