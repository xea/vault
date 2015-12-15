package so.blacklight.vault.command;

import fj.data.Either;
import so.blacklight.vault.*;
import so.blacklight.vault.cli.CredentialsBuilder;
import so.blacklight.vault.cli.Options;
import so.blacklight.vault.cli.Console;
import so.blacklight.vault.crypto.AESKey;
import so.blacklight.vault.crypto.KeyManager;
import so.blacklight.vault.crypto.Password;
import so.blacklight.vault.crypto.RSAPrivateKey;
import so.blacklight.vault.locale.I18n;
import so.blacklight.vault.locale.Message;
import so.blacklight.vault.store.StreamVaultStore;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.function.*;
import java.util.stream.Collectors;

public abstract class VaultCommand implements CLICommand {

    protected final static String CREDENTIAL_PASSWORD = "pw";
    protected final static String CREDENTIAL_AES_KEY = "aes";
    protected final static String CREDENTIAL_RSA_KEY = "rsa";
    protected final static String CREDENTIAL_RSA_PRIVATE_KEY = "rsa_private";
    protected final static String CREDENTIAL_RSA_PUBLIC_KEY = "rsa_public";
    protected final static String CREDENTIAL_HMAC_KEY = "hmac";

    protected final Options options;

    public static final File DEFAULT_VAULT = new File("vault.vlt");

    protected final Console console = new Console();

    protected final I18n i18n = new I18n();

    protected final CredentialsBuilder credentialsBuilder = new CredentialsBuilder();

    private final Map<String, Supplier<Either<String, Credential>>> authMapping = new HashMap<>();

    protected VaultCommand(final Options options) {
        this.options = options;

        authMapping.put(CREDENTIAL_PASSWORD, () -> credentialsBuilder.requirePassword());
        authMapping.put(CREDENTIAL_AES_KEY, () -> credentialsBuilder.requireAESKey());
        authMapping.put(CREDENTIAL_RSA_KEY, () -> credentialsBuilder.requireRSAPrivateKey());
        authMapping.put(CREDENTIAL_RSA_PRIVATE_KEY, () -> credentialsBuilder.requireRSAPrivateKey());
        authMapping.put(CREDENTIAL_RSA_PUBLIC_KEY, () -> credentialsBuilder.requireRSAPublicKey());
        authMapping.put(CREDENTIAL_HMAC_KEY, () -> credentialsBuilder.requireHMACToken());
    }

    /**
     * Attempt to perform a mutating action on the vault, if possible. If the vault cannot be opened in write mode
     * then the action will not be executed and an error message will be displayed.
     *
     * If the action is executed successfully then the vault will be saved to it's original place.
     *
     * @param options vault options
     * @param consumer action to perform
     * @throws VaultException
     */
    protected void doWriteAction(final Options options, final Consumer<Vault> consumer) throws VaultException {
        doAction(options, vault -> {
            if (vault.isWritable()) {
                consumer.accept(vault);
            } else {
                console.error(i18n.t(Message.ERROR_RESTRICTED_ACCESS));
            }
        }, true);
    }

    /**
     * Attempt to perform a read-only action on the vault, if possible. If the vault cannot be opened then the
     * action will not be executed and an error message will be displayed.
     *
     * Any changes will be silently ignored.
     *
     * @param options vault option
     * @param consumer action to perform
     * @throws VaultException
     */
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
                    console.error(i18n.t(Message.ERROR_CANNOT_LOAD, maybeVault.left().value()));
                }
            } catch (final IOException e) {
                throw new VaultException(i18n.t(Message.GENERIC_ERROR, e.getLocalizedMessage()));
            }
        } else {
            console.error(i18n.t(Message.ERROR_CANNOT_READ, vaultFile.getAbsolutePath()));
        }
    }

    protected void doFileAction(final Options options, final Consumer<File> consumer) {
        final File vaultFile = options.getVaultFile().orElse(DEFAULT_VAULT);

        if (vaultFile.exists() && vaultFile.canRead()) {
            consumer.accept(vaultFile);
        } else {
            console.error(i18n.t(Message.ERROR_CANNOT_READ, vaultFile.getAbsolutePath()));
        }
    }

    protected void setAuthMapping(final String authId, final Supplier<Either<String, Credential>> mapping) {
        authMapping.put(authId, mapping);
    }

    protected Credentials buildCredentials(final Options options) throws IOException {
        final Either<String, Credentials> reduceDefault = Either.right(new Credentials());

        final String defaultMessage = "ERROR";

        final Either<String, Credentials> reduced = options.getAuthOptions().stream()
                .map(opt -> authMapping
                    .getOrDefault(opt, () -> Either.left(defaultMessage)))
                    .map(f -> f.get())
                    .reduce(reduceDefault, (acc, currentItem) -> {
                        if (acc.isRight() && currentItem.isRight()) {
                            final Credentials credentials = acc.right().value();
                            credentials.add(currentItem.right().value());
                            return Either.right(credentials);
                       } else if (acc.isRight()) {
                            return Either.left(currentItem.left().value());
                        } else {
                            return acc;
                        }
                  }, (a, b) -> {
                        if (a.isRight() && b.isRight()) {
                            return b;
                        } else {
                            return a;
                        }
                 });
        if (reduced.isRight()) {
            return reduced.right().value();
        } else {
            throw new IOException(reduced.left().value());
        }
    }
}
