package so.blacklight.vault.command;

import so.blacklight.vault.*;
import so.blacklight.vault.cli.Options;
import so.blacklight.vault.store.StreamVaultStore;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class CreateVault extends VaultCommand {

    public CreateVault(final Options options) {
        super(options);
    }

    @Override
    public boolean execute() throws VaultException {
        final File vaultFile = options.getVaultFile().orElse(DEFAULT_VAULT);

        if (vaultFile.exists()) {
            System.out.println("ERROR: Vault file already exists: " + vaultFile.getAbsolutePath());
        } else {
            try {
                final Credentials credentials = buildCredentials(options);
                final VaultSettings settings = new VaultSettings(options.isGenerateRecovery(), options.isGenerateDegraded());
                final Vault vault = new Vault(settings);
                final VaultStore vaultStore = new StreamVaultStore();

                vaultStore.save(vault, credentials, vaultFile);
            } catch (final IOException e) {
                throw new VaultException("ERROR: " + e.getMessage());
            }
        }
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
