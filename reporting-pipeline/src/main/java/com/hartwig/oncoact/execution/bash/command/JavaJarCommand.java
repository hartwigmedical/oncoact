package com.hartwig.oncoact.execution.bash.command;

import java.util.List;

public class JavaJarCommand implements BashCommand {
    private final String jar;
    private final String maxHeapSize;
    private final List<String> arguments;

    public JavaJarCommand(final String jar, final String maxHeapSize, final List<String> arguments) {
        this.jar = jar;
        this.maxHeapSize = maxHeapSize;
        this.arguments = arguments;
    }

    @Override
    public String asBash() {
        return String.format("java -Xmx%s -jar %s %s", maxHeapSize, jar, String.join(" ", arguments));
    }
}
