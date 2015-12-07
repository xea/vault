package so.blacklight.vault.command;

import so.blacklight.vault.cli.Options;
import so.blacklight.vault.VaultException;
import so.blacklight.vault.entry.Folder;
import so.blacklight.vault.entry.Metadata;
import so.blacklight.vault.entry.PasswordEntry;
import so.blacklight.vault.entry.SecretEntry;

import java.util.Optional;

public class ListEntries extends VaultCommand {

    public ListEntries(final Options options) {
        super(options);
    }

    @Override
    public boolean execute() throws VaultException {
        doAction(options, vault -> {
            if (!vault.isWritable()) {
                System.out.println("Warning: this vault is opened in restricted mode");
            }

            vault.getEntries().forEach(e -> {
                final Metadata m = e.getMetadata();

                final String line;

                if (e instanceof Folder) {
                    final Folder f = (Folder) e;
                    line = String.format("FOLDER: %s", m.getAlias());
                } else if (e instanceof SecretEntry) {
                    final SecretEntry se = (SecretEntry) e;
                    line = String.format("SECRET: %s / %s / %s", m.getAlias(), se.getRecoveryInfo(), m.getExpirationTime().toString());
                } else if (e instanceof PasswordEntry) {
                    final PasswordEntry pe = (PasswordEntry) e;
                    line = String.format("PASSWORD: %s / %s / %s / %s", m.getAlias(), pe.getId(), pe.getRecoveryInfo(), m.getExpirationTime().toString());
                } else {
                    line = "Unknown entry";
                }

                console.out(line);
            });
        });
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
