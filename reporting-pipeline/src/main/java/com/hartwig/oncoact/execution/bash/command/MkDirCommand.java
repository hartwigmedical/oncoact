package com.hartwig.oncoact.execution.bash.command;

public class MkDirCommand implements BashCommand {

    private final String directoryName;

    public MkDirCommand(final String directoryName) {
        this.directoryName = directoryName;
    }

    @Override
    public String asBash() {
        return String.format("mkdir -p %s", directoryName);
    }
}
