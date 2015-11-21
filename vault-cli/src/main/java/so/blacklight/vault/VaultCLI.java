package so.blacklight.vault;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.github.jankroken.commandline.CommandLineParser;
import com.github.jankroken.commandline.OptionStyle;

import fj.data.Either;
import so.blacklight.vault.store.StreamVaultStore;

/**
 * Main executable class, intended to be called from the command line.
 */
public class VaultCLI {

    public static final File DEFAULT_VAULT = new File("vault.vlt");

    public static void main(final String[] args) {
        final VaultCLI cli = new VaultCLI();
        final Options options = cli.processArgs(args);

        cli.processRequest(options);
    }

    public Options processArgs(final String[] args) {
        try {
            final Options options = CommandLineParser.parse(Options.class, args, OptionStyle.SIMPLE);

            return options;
        } catch (IllegalAccessException e) {
            System.out.println("Illegal access exception: " + e.getMessage());
        } catch (InstantiationException e) {
            System.out.println("Instantiation exception: " + e.getMessage());
        } catch (InvocationTargetException e) {
            System.out.println("Invocation target exception: " + e.getMessage());
        }

        return new Options();
    }

    public void processRequest(final Options options) {
        try {
            if (!options.isValid()) {
                System.out.println("ERROR: Invalid parameters");
                showHelp(options.getAction());
            }
            else if (options.isHelpRequested()) {
                showHelp(options.getAction());
            } else {
                switch (options.getAction()) {
                    case CREATE_VAULT:
                        createVault(options);
                        break;
                    case LIST_ENTRIES:
                        listEntries(options);
                        break;
                    case DEFAULT_ACTION:
                    default:
                        showHelp(options.getAction());
                        break;
                }
            }
        } catch (final VaultException e) {
            System.out.println("Application error: " + e.getMessage());
        }
    }

    public void listEntries(final Options options) throws VaultException {
        System.out.println("LIST ENTRIES");

        final File vaultFile = options.getVaultFile().orElse(DEFAULT_VAULT);

        if (vaultFile.exists() && vaultFile.canRead()) {
            try {
                final Credentials credentials = buildCredentials(options);
                final VaultStore store = new StreamVaultStore();

                final Either<String, Vault> maybeVault = store.load(credentials, vaultFile);

                if (maybeVault.isRight()) {
                    final Vault vault = maybeVault.right().value();

                } else {
                    final String message = "ERROR: Could not load vault: ";
                    System.out.println(message + maybeVault.left().value());
                }
            } catch (final IOException e) {
                throw new VaultException("ERROR: " + e.getMessage());
            }
        } else {
            final String message = "ERROR: Vault file doesn't exist or isn't readable: ";
            System.out.println(message + vaultFile.getAbsolutePath());
        }
    }

    /**
     * Create a new, empty vault.
     *
     * @param options
     * @throws VaultException
     */
    public void createVault(final Options options) throws VaultException {
        System.out.println("CREATE VAULT");

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
    }

    public void showHelp(final Options.Action action) {
        if (action == Options.Action.DEFAULT_ACTION) {
            System.out.println("Usage: ");
            System.out.println("    vault -create -v <vault> [OPTS]         Create new vault");
            System.out.println("    vault -list -v <vault> [OPTS]           List vault entries");
            System.out.println();
            System.out.println("  Where OPTS are:");
            System.out.println("    -r                     Activate recovery segment");
            System.out.println("    -k <keyfile>           Use keyfile for encryption/decryption");
            System.out.println("    -d                     Activate degraded segment");
            System.out.println("    -m [METHODS]           Use specified authentication methods");
            System.out.println();
            System.out.println("  Where METHODS are:");
            System.out.println("    pw                     Use password-based encryption");
            System.out.println("    key                    Use key file-based encryption");
            System.out.println();
        } else {
            System.out.println("HELP MESSAGE ABOUT: " + action.name());
        }
    }

    protected Credentials buildCredentials(final Options options) throws IOException {
        final Credentials credentials = new Credentials();

        for (final String authOption : options.getAuthOptions()) {
            final Console console = System.console();

            if ("pw".equals(authOption)) {
                if (console != null) {
                    System.out.print("Enter password: ");
                    credentials.add(new Password(console.readPassword()));
                }
            } else if ("key".equals(authOption)) {
                if (console != null) {
                    System.out.print("Enter path to key file: ");
                    final File keyFile = new File(console.readLine());

                    if (keyFile.exists()) {
                        final Path keyPath = keyFile.toPath();
                        final byte[] bytes = Files.readAllBytes(keyPath);
                        credentials.add(new PrivateKey(bytes));
                    }
                }
            }
        }

        return credentials;
    }

}
