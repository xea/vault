package so.blacklight.vault;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;

import com.github.jankroken.commandline.CommandLineParser;
import com.github.jankroken.commandline.OptionStyle;

import fj.data.Either;
import so.blacklight.vault.entry.*;
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
            final Optional<String> validationResult = options.isValid();
            if (validationResult.isPresent()) {
                System.out.println("ERROR: " + validationResult.get());
                showHelp(options.getAction());
            }
            else if (options.isHelpRequested()) {
                showHelp(options.getAction());
            } else {
                switch (options.getAction()) {
                    case CREATE_VAULT:
                        createVault(options);
                        break;
                    case CREATE_ENTRY:
                        createEntry(options);
                        break;
                    case CREATE_FOLDER:
                        createFolder(options);
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

        doAction(options, vault -> {
            if (!vault.isWritable()) {
                System.out.println("Warning: this vault is opened in restricted mode");
            }

            vault.getEntries().forEach(e -> {
                final Metadata m = e.getMetadata();
                System.out.println(String.format("%s %s", m.getTitle(), m.getComment()));
            });
        });
    }

    public void createEntry(final Options options) throws VaultException {
        System.out.println("CREATE ENTRY");

        doWriteAction(options, vault -> {
            final String title = askInput("Title");
            final String userId = askInput("User id");
            final Optional<char[]> maybePassword = askPassword("Password");
            final Optional<char[]> maybePasswordConfirm = askPassword("Password again");

            if (maybePassword.isPresent() && maybePasswordConfirm.isPresent()) {
                if (Arrays.equals(maybePassword.get(), maybePasswordConfirm.get())) {
                    final Entry newEntry = new PasswordEntry(userId, maybePassword.toString(), title);

                    vault.getEntries().add(newEntry);
                } else {
                    error("Passwords are not the same");
                }
            } else {
                error("Empty password");
            }
        });
    }

    public void createFolder(final Options options) throws VaultException {
        System.out.println("CREATE FOLDER");

        doWriteAction(options, vault -> {
            if (options.getAlias().isPresent()) {
                final Folder newFolder = new FlatFolder(options.getAlias().get());

                vault.getEntries().add(newFolder);
            } else {
            }
        });
    }

    protected String askInput(final String prompt) {
        System.out.print(prompt + ": ");

        if (System.console() != null) {
            return System.console().readLine();
        } else {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            try {
                final String input = reader.readLine();

                return input;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    protected Optional<char[]> askPassword(final String prompt) {
        System.out.print(prompt);

        if (System.console() != null) {
            final char[] password = System.console().readPassword();

            return Optional.of(password);
        } else {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            try {
                final String input = reader.readLine();

                return Optional.of(input.toCharArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return Optional.empty();
    }

    protected void doWriteAction(final Options options, final Consumer<Vault> consumer) throws VaultException {
        doAction(options, vault -> {
            if (vault.isWritable()) {
                consumer.accept(vault);
            } else {
                System.out.println("ERROR: restricted mode doesn't allow changing data");
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
                    final String message = "ERROR: Could not load vault: ";
                    System.out.println(message + maybeVault.left().value());
                }
            } catch (final IOException e) {
                throw new VaultException("ERROR: " + e.getMessage());
            }
        } else {
            final String message = "Vault file doesn't exist or isn't readable: " + vaultFile.getAbsolutePath();
            error(message);
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
                final Entry testEntry = new PasswordEntry("test id", "test password", "test alias", "test comment", "test recovery");
                vault.getEntries().add(testEntry);
                final VaultStore vaultStore = new StreamVaultStore();

                vaultStore.save(vault, credentials, vaultFile);
            } catch (final IOException e) {
                throw new VaultException("ERROR: " + e.getMessage());
            }
        }
    }

    public void showHelp(final Options.Action action) {
        if (action == Options.Action.DEFAULT_ACTION) {
            final String[] messages = new String[] {
                "Usage: ",
                "    vault -create -v <vault> [OPTS]                                 Create new vault",
                "    vault -list -v <vault> [OPTS]                                   List vault entries",
                "    vault -create-entry -a <alias> -t <TYPE> -v <vault> [OPTS]      Create a new vault entry",
                "", "",
                "  Possible OPTS are:",
                "    -f <folder alias>      Specify the current folder",
                "    -r                     Activate recovery segment",
                "    -k <keyfile>           Use keyfile for encryption/decryption",
                "    -d                     Activate degraded segment",
                "    -m [METHODS]           Use specified authentication methods",
                "",
                "  Possible METHODS are:",
                "    pw                     Use password-based encryption",
                "    key                    Use key file-based encryption",
                "",
                "  Possible TYPEs are:",
                "    password"
            };

            out(messages);
        } else {
            out("HELP MESSAGE ABOUT: " + action.name());
        }
    }

    protected Credentials buildCredentials(final Options options) throws IOException {
        final Credentials credentials = new Credentials();

        for (final String authOption : options.getAuthOptions()) {
            final Console console = System.console();

            if ("pw".equals(authOption)) {
                final Optional<char[]> maybePassword = askPassword("Enter password");

                if (maybePassword.isPresent()) {
                    credentials.add(new Password(maybePassword.get()));
                } else {
                    error("Password encryption was selected but no password was provided");
                }
            } else if ("key".equals(authOption)) {
                if (console != null) {
                    out("Enter path to key file: ");
                    final File keyFile = new File(console.readLine());

                    if (keyFile.exists()) {
                        final Path keyPath = keyFile.toPath();
                        final byte[] bytes = Files.readAllBytes(keyPath);
                        credentials.add(new PrivateKey(bytes));
                    } else {
                        error("The specified keyfile does not exist");
                    }
                }
            } else {
                error("The selected authentication mode (" + authOption + ") is invalid, skipping");
            }
        }

        return credentials;
    }

    protected void out(final String... messages) {
        Arrays.asList(messages).forEach(System.out::println);
    }

    protected void error(final String... messages) {
        Arrays.asList(messages).forEach(msg -> System.out.println("ERROR: " + msg));
    }
}
