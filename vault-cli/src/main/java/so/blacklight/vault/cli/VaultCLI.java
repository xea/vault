package so.blacklight.vault.cli;

import com.github.jankroken.commandline.CommandLineParser;
import com.github.jankroken.commandline.OptionStyle;
import so.blacklight.vault.Credentials;
import so.blacklight.vault.Vault;
import so.blacklight.vault.VaultStore;

import java.io.Console;
import java.io.File;
import java.io.IOException;

/**
 * Provides a command line utility for accessing secure vaults.
 */
public class VaultCLI {

    public static final String DEFAULT_VAULT_PATH = "vault.vlt";

    public static void main(final String[] args) throws Exception {
        final CLIOptions cliOpts = CommandLineParser.parse(CLIOptions.class, args, OptionStyle.SIMPLE);
        final VaultCLI cli = new VaultCLI();

        cli.processRequest(cliOpts);
    }

    /**
     * Attempt to execute user request
     * @param opts command line options
     */
    public void processRequest(final CLIOptions opts) throws IOException {
        switch (opts.getAction()) {
            case CREATE_VAULT:
                createVault(opts);
                break;
            default:
                showUsage();
                break;
        }
    }

    /**
     * Creates a new, empty vault.
     * @param opts
     */
    protected void createVault(final CLIOptions opts) throws IOException {
        final File vaultFile = new File(opts.getVaultPath());

        if (vaultFile.exists()) {
            System.out.println("ERROR: File " + vaultFile.getAbsolutePath() + " already exists");
        } else {
            final Console console = System.console();

            if (console == null) {
                System.out.println("ERROR: Could not open console");
            } else {
                System.out.print("Enter vault password: ");
                final String password1 = String.valueOf(console.readPassword());
                System.out.print("Enter vault password again: ");
                final String password2 = String.valueOf(console.readPassword());

                if (password1.equals(password2)) {
                    System.out.print("Enter one-time password: ");
                    final char[] otp = System.console().readPassword();

                    final Vault vault = new Vault();
                    final Credentials credentials = new Credentials(String.valueOf(password1), String.valueOf(otp));

                    vault.lock(credentials);
                    new VaultStore().save(vault, vaultFile);
                } else {
                    System.out.println("ERROR: Passwords are different");
                }
            }
        }
        //final StringSelection ss = new StringSelection("valami");
        //Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, ss);
    }

    /**
     * Display a helpful message explaining how to use this program to the screen
     */
    protected void showUsage() {
        System.out.println("Usage: ");
        // TODO add extremely useful message here
    }

    /**
    private final CLIOptions opts;

    private final Vault vault;

    public VaultCLI(final CLIOptions opts) throws IOException, ClassNotFoundException {
        this.opts = opts;

        final File vaultFile = new File(opts.getVaultPath().orElse(DEFAULT_VAULT_PATH));

        if (vaultFile.exists()) {
            vault = new VaultStore().load(vaultFile).get();
        } else {
            if (opts.isCreateVault()) {
                vault = new Vault();
            } else {
                throw new FileNotFoundException(vaultFile.getAbsolutePath());
            }
        }
    }

    public void processCommand() {

        if (opts.isCreateVault()) {
            final File file = new File(opts.getVaultPath().orElse(DEFAULT_VAULT_PATH));
            createVault(file);
        } else if (opts.getListFolder().isPresent()) {
            listEntries();
        }
    }

    protected void createVault(final File outputFile) {
        try {
            vault.createFolder("test folder").addEntry(new PasswordEntry("user", "pass", "info"));

            new VaultStore().save(vault, outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    protected void listEntries() {
        System.out.print("Passphrase: ");
        final char[] passphrase = System.console().readPassword();
        System.out.print("OTP: ");
        final char[] otp = System.console().readPassword();
        final String p1 = new String(passphrase);
        final String p2 = new String(otp);

        final Credentials credentials = new Credentials(p1, p2);
        vault.unlock(credentials);
        vault.getFolderNames().forEach(name -> {
            vault.getFolder(name).get().getEntries().forEach(System.out::println);
        });
    }

    public static final void main(final String[] args) throws Exception {
        final CLIOptions cliOpts = CommandLineParser.parse(CLIOptions.class, args, OptionStyle.SIMPLE);

        final VaultCLI cli = new VaultCLI(cliOpts);
        cli.processCommand();
    }

     */
}
