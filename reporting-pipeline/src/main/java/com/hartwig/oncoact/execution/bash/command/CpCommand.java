package com.hartwig.oncoact.execution.bash.command;

import static java.lang.String.format;

public class CpCommand implements BashCommand {
    private final String source;
    private final String destination;

    public CpCommand(final String source, final String destination) {
        this.source = source;
        this.destination = destination;
    }

    @Override
    public String asBash() {
        return format("cp %s %s", source, destination);
    }
}
