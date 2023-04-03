package com.hartwig.oncoact.execution.bash.command;

import java.util.List;
import java.util.stream.Collectors;

public class GroupCommand implements BashCommand {
    private final List<BashCommand> bashCommands;

    public GroupCommand(BashCommand... bashCommands) {
        this.bashCommands = List.of(bashCommands);
    }

    @Override
    public String asBash() {
        var commands = bashCommands.stream().map(BashCommand::asBash).collect(Collectors.joining("; "));
        return "set -e; " + commands;
    }
}
