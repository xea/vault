package so.blacklight.vault;

import com.github.jankroken.commandline.CommandLineParser;
import com.github.jankroken.commandline.OptionStyle;
import fj.data.Either;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

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
            final Credentials credentials = buildCredentials(options);
            final VaultStore store = new VaultStoreImpl();

            final Either<String, Vault> maybeVault = store.load(credentials, vaultFile);

            if (maybeVault.isRight()) {
                final Vault vault = maybeVault.right().value();

                // TODO list entries
            } else {
                final String message = "ERROR: Could not load vault: ";
                System.out.println(message + maybeVault.left().value());
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
            final Credentials credentials = buildCredentials(options);
            final Vault vault = new Vault();
            final VaultStore vaultStore = new VaultStoreImpl();

            vaultStore.save(vault, credentials, vaultFile);
        }
    }

    public void showHelp(final Options.Action action) {
        System.out.println("HELP MESSAGE ABOUT: " + action.name());
        System.out.println("Usage: ");
        System.out.println("\tvault -c -v <vault> -k <keyfile>");
    }

    protected Credentials buildCredentials(final Options options) {
        final char[] password;

        System.out.print("Password: ");
        if (System.console() == null) {
            // TODO Try reading password from file when console is not available
            password = "asdf".toCharArray();
        } else {
            password = System.console().readPassword();
        }

        final char[] staticKey;
        System.out.print("Static key: ");
        if (System.console() == null) {
            staticKey = null;
        } else {
            staticKey = System.console().readPassword();
        }

        final char[] otp;

        System.out.print("One-time password: ");
        if (System.console() == null) {
            otp = null;
        } else {
            otp = System.console().readPassword();
        }

        final Credentials credentials = new Credentials();
        credentials.add(new Password(password));
        credentials.add(new Password(otp));
        //credentials.add(new PrivateKey(new String(staticKey).getBytes()));

        return credentials;
    }

}
