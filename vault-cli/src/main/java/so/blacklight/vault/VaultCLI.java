package so.blacklight.vault;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;

import com.github.jankroken.commandline.CommandLineParser;
import com.github.jankroken.commandline.OptionStyle;

import fj.data.Either;
import fj.data.List;
import so.blacklight.vault.crypto.Password;
import so.blacklight.vault.crypto.AESKey;
import so.blacklight.vault.crypto.RSAPrivateKey;
import so.blacklight.vault.crypto.RSAPublicKey;
import so.blacklight.vault.entry.*;
import so.blacklight.vault.io.VaultInputStream;
import so.blacklight.vault.io.VaultRecord;
import so.blacklight.vault.store.Layout;
import so.blacklight.vault.store.StreamVaultStore;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

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
            error("Illegal access exception: " + e.getMessage());
        } catch (InstantiationException e) {
            error("Instantiation exception: " + e.getMessage());
        } catch (InvocationTargetException e) {
            error("Invocation target exception: " + e.getMessage());
        }

        return new Options();
    }

    public void processRequest(final Options options) {
        try {
            final Optional<String> validationResult = options.isValid();
            if (validationResult.isPresent()) {
                error(validationResult.get());
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
                    case SHOW_ENTRY:
                        showEntry(options);
                        break;
                    case SHOW_INFO:
                        showInfo(options);
                        break;
                    case GENERATE_KEY:
                        generateKey(options);
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

    public void showEntry(final Options options) throws VaultException {
        final String alias = options.getAlias().get();

        doAction(options, vault -> {
            final Optional<Entry> result = vault.findAlias(alias);

            if (result.isPresent()) {
                final EntryPrinter printer = new EntryPrinter();
                printer.print(result.get());
            } else {
                error("Can't find selected entry");
            }
        });
    }

    public void showInfo(Options options) throws VaultException {
        System.out.println("SHOW INFO");

        final File vaultFile = options.getVaultFile().get();

        if (vaultFile.exists() && vaultFile.canRead()) {
            try {
                final FileInputStream fis = new FileInputStream(vaultFile);
                final VaultInputStream vis = new VaultInputStream(fis);

                final Layout layout = vis.getLayout();

                out(String.format("Layout: %d %d %d", layout.getPrimaryLayers(), layout.getRecoveryLayers(), layout.getDegradedLayers()));

                final List<VaultRecord> vaultRecords = vis.readAll();

                out("Found " + vaultRecords.length() + " record(s)");

                vaultRecords.forEach(record -> {
                    out("Found record with " + record.getIvs().length + " IV(s) and " + record.getSalts().length + " salt(s) and block is " + record.getBlock().length + " bytes long");
                });

            } catch (FileNotFoundException e) {
                error(e.getMessage());
            } catch (IOException e) {
                error(e.getMessage());
            }
        } else {
            error("Can't open vault file " + vaultFile.getAbsolutePath());
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

                out(line);
            });
        });
    }

    public void createEntry(final Options options) throws VaultException {
        System.out.println("CREATE ENTRY");

        doWriteAction(options, vault -> {
            final String alias = options.getAlias().isPresent() ? options.getAlias().get() : askInput("Alias");
            final String userId = askInput("User id");
            final String recovery = askInput("Recovery info (optional)");
            final String comment = askInput("Comment (optional)");
            final Optional<char[]> maybePassword = askPassword("Password");
            final Optional<char[]> maybePasswordConfirm = askPassword("Password again");

            if (maybePassword.isPresent() && maybePasswordConfirm.isPresent()) {
                if (Arrays.equals(maybePassword.get(), maybePasswordConfirm.get())) {
                    final Entry newEntry = new PasswordEntry(userId, maybePassword.toString(), alias, comment, recovery);

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

    public void generateKey(final Options options) {
        System.out.println("GENERATE KEY");

        final String keyType = options.getKeyType().get().toLowerCase();

        if ("aes".equals(keyType)) {
            generateAESKey();
        } else if ("rsa".equals(keyType)) {
            generateRSAKeys();
        } else {
            error("Unknown key type: " + keyType);
        }
    }

    private void generateRSAKeys() {
        try {
            final String publicPath = askInput("Public key save path");
            final String privatePath = askInput("Private key save path");

            final File publicFile = new File(publicPath);
            final File privateFile = new File(privatePath);

            if (publicFile.exists()) {
                error("File already exists: " + publicFile.getAbsolutePath());
            } else if (privateFile.exists()) {
                error("File already exists: " + privateFile.getAbsolutePath());
            } else {
                final KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
                generator.initialize(4096);

                final KeyPair keyPair = generator.generateKeyPair();

                Files.write(privateFile.toPath(), keyPair.getPrivate().getEncoded());
                Files.write(publicFile.toPath(), keyPair.getPublic().getEncoded());

                out("Keys have been created");
            }
        } catch (NoSuchAlgorithmException e) {
            error(e.getMessage());
        } catch (IOException e) {
            error(e.getMessage());
        }
    }

    private void generateAESKey() {
        try {
            final String keyPath = askInput("Key save path");
            final File keyFile = new File(keyPath);

            if (keyFile.exists()) {
                error("File already exists: " + keyFile.getAbsolutePath());
            } else {
                final KeyGenerator generator = KeyGenerator.getInstance("AES");
                generator.init(256);
                final SecretKey secretKey = generator.generateKey();

                Files.write(keyFile.toPath(), secretKey.getEncoded());
            }
        } catch (NoSuchAlgorithmException e) {
            error(e.getMessage());
        } catch (IOException e) {
            error(e.getMessage());
        }
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
        System.out.print(prompt + ": ");

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
                    final String message = "Could not load vault: ";
                    error(message + maybeVault.left().value());
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
                "    vault -info -v <vault>                                          Show information about the selected vault",
                "    vault -show-entry -a <alias> -v <vault>                         Show the specified entry",
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
                "    aes                    Use AES key file-based encryption",
                "    rsa                    Use RSA key file-based encryption",
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

                if (maybePassword.isPresent() && maybePassword.get().length > 0) {
                    credentials.add(new Password(maybePassword.get()));
                } else {
                    error("Password encryption was selected but no password was provided");
                }
            } else if ("aes".equals(authOption)) {
                if (console != null) {
                    out("Enter path to key file: ");
                    final File keyFile = new File(console.readLine());

                    if (keyFile.exists()) {
                        final Path keyPath = keyFile.toPath();
                        final byte[] bytes = Files.readAllBytes(keyPath);
                        credentials.add(new AESKey(bytes));
                    } else {
                        error("The specified keyfile does not exist");
                    }
                }
            } else if ("rsa".equals(authOption)) {
                final String keyPath = askInput("Enter path to public key file");
                final File keyFile = new File(keyPath);

                if (keyFile.exists()) {
                    final byte[] bytes = Files.readAllBytes(keyFile.toPath());
                    credentials.add(new RSAPublicKey(bytes));
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
