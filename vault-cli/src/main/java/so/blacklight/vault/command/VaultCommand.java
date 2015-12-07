package so.blacklight.vault.command;

import fj.data.Either;
import so.blacklight.vault.*;
import so.blacklight.vault.cli.Options;
import so.blacklight.vault.cli.Console;
import so.blacklight.vault.crypto.AESKey;
import so.blacklight.vault.crypto.Password;
import so.blacklight.vault.crypto.RSAPrivateKey;
import so.blacklight.vault.store.StreamVaultStore;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class VaultCommand implements CLICommand {

    protected final Options options;

    protected VaultCommand(final Options options) {
        this.options = options;
    }

    public static final File DEFAULT_VAULT = new File("vault.vlt");

    protected final Console console = new Console();

    protected void doWriteAction(final Options options, final Consumer<Vault> consumer) throws VaultException {
        doAction(options, vault -> {
            if (vault.isWritable()) {
                consumer.accept(vault);
            } else {
                console.error("restricted mode doesn't allow changing data");
            }
        }, true);
    }

    protected void doAction(final Options options, final Consumer<Vault> consumer) throws VaultException {
        doAction(options, consumer, false);
    }

    protected void doAction(final Options options, final Consumer<Vault> consumer, final boolean updateVault) throws VaultException {
        final File vaultFile = options.getVaultFile().orElse(DEFAULT_VAULT);

        if (vaultFile.exists() && vaultFile.canRead()) {
            try {
                final Credentials credentials = buildCredentials(options);
                final VaultStore store = new StreamVaultStore();

                final Either<String, Vault> maybeVault = store.load(credentials, vaultFile);

                if (maybeVault.isRight()) {
                    final Vault vault = maybeVault.right().value();

                    consumer.accept(vault);

                    if (vault.isWritable() && updateVault) {
                        store.save(vault, credentials, vaultFile);
                    }
                } else {
                    final String message = "Could not load vault: ";
                    console.error(message + maybeVault.left().value());
                }
            } catch (final IOException e) {
                throw new VaultException("ERROR: " + e.getMessage());
            }
        } else {
            final String message = "Vault file doesn't exist or isn't readable: " + vaultFile.getAbsolutePath();
            console.error(message);
        }
    }

    protected Credentials buildCredentials(final Options options) throws IOException {
        final Credentials credentials = new Credentials();

        for (final String authOption : options.getAuthOptions()) {

            if ("pw".equals(authOption)) {
                final Optional<char[]> maybePassword = console.askPassword("Enter password");

                if (maybePassword.isPresent() && maybePassword.get().length > 0) {
                    credentials.add(new Password(maybePassword.get()));
                } else {
                    console.error("Password encryption was selected but no password was provided");
                }
            } else if ("aes".equals(authOption)) {
                final String privateKeyPath = console.askInput("Enter path to key file");
                final File keyFile = new File(privateKeyPath);

                if (keyFile.exists()) {
                    final Path keyPath = keyFile.toPath();
                    final byte[] bytes = Files.readAllBytes(keyPath);
                    credentials.add(new AESKey(bytes));
                } else {
                    console.error("The specified keyfile does not exist");
                }
            } else if ("rsa".equals(authOption)) {
                final String keyPath = console.askInput("Enter path to private key file");
                final File keyFile = new File(keyPath);

                if (keyFile.exists()) {
                    final byte[] bytes = Base64.getDecoder().decode(Files.readAllBytes(keyFile.toPath()));
                    credentials.add(new RSAPrivateKey(bytes));
                }
            } else {
                console.error("The selected authentication mode (" + authOption + ") is invalid, skipping");
            }
        }

        return credentials;
    }
}
