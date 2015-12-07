package so.blacklight.vault.command;

import so.blacklight.vault.cli.Options;
import so.blacklight.vault.VaultException;
import so.blacklight.vault.entry.Entry;
import so.blacklight.vault.entry.PasswordEntry;

import java.util.Arrays;
import java.util.Optional;

public class CreateEntry extends VaultCommand {

    public CreateEntry(final Options options) {
        super(options);
    }

    @Override
    public boolean execute() throws VaultException {
        doWriteAction(options, vault -> {
            final String alias = options.getAlias().isPresent() ? options.getAlias().get() : console.askInput("Alias");
            final String userId = console.askInput("User id");
            final String recovery = console.askInput("Recovery info (optional)");
            final String comment = console.askInput("Comment (optional)");
            final Optional<char[]> maybePassword = console.askPassword("Password");
            final Optional<char[]> maybePasswordConfirm = console.askPassword("Password again");

            if (maybePassword.isPresent() && maybePasswordConfirm.isPresent()) {
                if (Arrays.equals(maybePassword.get(), maybePasswordConfirm.get())) {
                    final Entry newEntry = new PasswordEntry(userId, maybePassword.toString(), alias, comment, recovery);

                    vault.getEntries().add(newEntry);
                } else {
                    console.error("Passwords are not the same");
                }
            } else {
                console.error("Empty password");
            }
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
        Optional<String> result = Optional.empty();

        return result;
    }

}
