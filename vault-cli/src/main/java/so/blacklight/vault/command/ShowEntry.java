package so.blacklight.vault.command;

import so.blacklight.vault.cli.Options;

import java.util.Optional;

public class ShowEntry extends VaultCommand {

    public ShowEntry(final Options options) {
        super(options);
    }

    @Override
    public boolean execute() {
        return false;
    }

    @Override
    public boolean undo() {
        return false;
    }

    @Override
    public boolean isUndoable() {
        return false;
    }

    @Override
    public Optional<String> validate() {
        return Optional.empty();
    }

}
