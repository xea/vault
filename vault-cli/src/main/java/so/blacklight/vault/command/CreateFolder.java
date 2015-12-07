package so.blacklight.vault.command;

import so.blacklight.vault.cli.Options;
import so.blacklight.vault.VaultException;
import so.blacklight.vault.entry.FlatFolder;
import so.blacklight.vault.entry.Folder;

import java.util.Optional;

public class CreateFolder extends VaultCommand {

    public CreateFolder(Options options) {
        super(options);
    }

    @Override
    public boolean execute() throws VaultException {
        doWriteAction(options, vault -> {
            final Folder newFolder = new FlatFolder(options.getAlias().get());

            vault.getEntries().add(newFolder);
        });

        return true;
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
        Optional<String> result = Optional.empty();

        if (!options.getAlias().isPresent()) {
            result = Optional.of("Alias is not specified");
        }

        return result;
    }
}
