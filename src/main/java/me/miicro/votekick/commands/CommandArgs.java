package me.miicro.votekick.commands;

public enum CommandArgs {
    YES("yes"),
    NO("no"),
    STOP("stop"),
    RELOAD("reload");

    public final String value;

    private CommandArgs(String value) {
        this.value = value;
    }
}
