package so.blacklight.vault.cli;

import com.github.jankroken.commandline.CommandLineParser;
import com.github.jankroken.commandline.OptionStyle;
import so.blacklight.vault.Credentials;
import so.blacklight.vault.Folder;
import so.blacklight.vault.Vault;
import so.blacklight.vault.VaultStore;
import so.blacklight.vault.entry.PasswordEntry;
import so.blacklight.vault.entry.VaultEntry;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.security.Provider;
import java.security.Security;
import java.util.Optional;

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

    protected void processRequest(final CLIOptions options) throws VaultException, IOException {
        switch (options.getAction()) {
            case CREATE_VAULT:
                createVault(options);
                break;
            case CREATE_FOLDER:
                createFolder(options);
                break;
            case CREATE_ENTRY:
                createEntry(options);
                break;
            case SHOW_FOLDERS:
                showFolders(options);
                break;
        }
    }

    protected void createVault(final CLIOptions options) throws VaultException, IOException {
        final Credentials credentials = readCredentials(true);
        final Vault vault = new Vault();
        vault.lock(credentials);
        saveVault(vault, options.getVaultPath());
    }

    protected void createFolder(final  CLIOptions options) throws VaultException, IOException {
        final Credentials credentials = readCredentials(false);
        final Vault vault = loadVault(options.getVaultPath(), credentials);

        if (vault.getFolderNames().contains(options.getFolderName())) {
            System.out.println("ERROR: Folder already exists");
        } else {
            vault.createFolder(options.getFolderName());
            vault.lock(credentials);
            saveVault(vault, options.getVaultPath());
            System.out.println("Folder saved");
        }
    }

    protected void createEntry(final CLIOptions options) throws VaultException, IOException {
        final Credentials credentials = readCredentials(false);
        final Vault vault = loadVault(options.getVaultPath(), credentials);
        final Optional<Folder> maybeFolder = vault.getFolder(options.getFolderName());

        if (maybeFolder.isPresent()) {
            final Folder folder = maybeFolder.get();
            final VaultEntry entry = readEntryDetails();
            folder.addEntry(entry);
            vault.lock(credentials);
            saveVault(vault, options.getVaultPath());
        } else {
            System.out.println("ERROR: Selected folder does not exist: " + options.getFolderName());
        }
    }

    protected void showFolders(final CLIOptions options) throws VaultException {
        final Credentials credentials = readCredentials(false);
        final Vault vault = loadVault(options.getVaultPath(), credentials);

        vault.getFolderNames().forEach(System.out::println);
    }

    protected void saveVault(final Vault vault, final String vaultPath) throws IOException {
        final VaultStore store = new VaultStore();
        store.save(vault, new File(vaultPath));
    }

    protected VaultEntry readEntryDetails() throws VaultException {
        final Console console = System.console();

        if (console == null) {
            throw new VaultException();
        } else {
            System.out.print("Username: ");
            final String username = console.readLine();
            System.out.print("Password: ");
            final String password1 = String.valueOf(console.readPassword());
            System.out.print("Password again: ");
            final String password2 = String.valueOf(console.readPassword());
            System.out.print("Recovery info (optional): ");
            final String recoveryInfo = console.readLine();

            if (password1.equals(password2)) {
                final VaultEntry entry = new PasswordEntry(username, password1, recoveryInfo);
                return entry;
            } else {
                throw new VaultException();
            }
        }
    }

    protected Vault loadVault(final String path, final Credentials credentials) throws VaultException {
        final File vaultFile = new File(path);

        if (vaultFile.exists()) {
            try {
                final VaultStore store = new VaultStore();
                final Optional<Vault> maybeVault = store.load(vaultFile);

                if (maybeVault.isPresent()) {
                    final Vault vault = maybeVault.get();
                    vault.unlock(credentials);
                    return vault;
                } else {
                    throw new VaultException();
                }
            } catch (final Exception e) {
                throw new VaultException();
            }
        } else {
            throw new VaultException();
        }
    }

    protected Credentials readCredentials(final boolean confirmPassword) throws VaultException {
        final Console console = System.console();

        if (console == null) {
            System.out.println("ERROR: Couldn't open system console");
            throw new VaultException();
        } else {
            System.out.print("Enter password: ");
            final String password1 = String.valueOf(console.readPassword());

            if (confirmPassword) {
                System.out.print("Confirm password: ");
                final String password2 = String.valueOf(console.readPassword());

                if (!password1.equals(password2)) {
                    throw new VaultException();
                }
            }

            System.out.print("Enter one-time password: ");
            final String otp = String.valueOf(console.readPassword());

            return new Credentials(password1, otp);
        }
    }

    /**
     * Display a helpful message explaining how to use this program to the screen
     */
    protected void showUsage() {
        final String fmt = "    %-20s %s";
        System.out.println("Usage: java -jar vault.jar -a <action> -v <vault> [-f <folder>]");
        System.out.println("  where <action> is one of the following:");
        System.out.println(String.format(fmt, "create-vault", "Create new vault"));
        System.out.println(String.format(fmt, "create-folder", "Create new folder"));
        System.out.println(String.format(fmt, "create-entry", "Create new entry within the specified folder"));
    }

    protected void showDebugInfo() {
        final Provider[] providers = Security.getProviders();
        for (final Provider p : providers)
        {
            System.out.format("%s %s%s", p.getName(), p.getVersion(), System.getProperty("line.separator"));
            for (final Object o : p.keySet())
            {
                System.out.format("\t%s : %s%s", o, p.getProperty((String)o), System.getProperty("line.separator"));
            }
        }
    }

}
