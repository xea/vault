package so.blacklight.vault.cli;

import com.github.jankroken.commandline.CommandLineParser;
import com.github.jankroken.commandline.OptionStyle;
import so.blacklight.vault.Credentials;
import so.blacklight.vault.Folder;
import so.blacklight.vault.Vault;
import so.blacklight.vault.VaultStore;

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

    /**
     * Attempt to execute user request
     * @param opts command line options
     */
    public void processRequest(final CLIOptions opts) throws IOException, ClassNotFoundException {
        switch (opts.getAction()) {
            case CREATE_VAULT:
                createVault(opts);
                break;
            case CREATE_FOLDER:
                createFolder(opts);
            case SHOW_USAGE:
                showUsage();
                break;
            case DEBUG:
                showDebugInfo();
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
            final Optional<Credentials> credentials = readCredentials();

            if (credentials.isPresent()) {
                final Vault vault = new Vault();
                vault.lock(credentials.get());
                new VaultStore().save(vault, vaultFile);
            } else {
                System.out.println("ERROR: Could not read credentials");
            }
        }
        //final StringSelection ss = new StringSelection("valami");
        //Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, ss);
    }
    
    protected void createFolder(final CLIOptions opts) throws IOException, ClassNotFoundException {
    	final File vaultFile = new File(opts.getVaultPath());
    	
    	if (vaultFile.exists()) {
    		
    		final VaultStore store = new VaultStore();
    		final Optional<Vault> loadedVault = store.load(vaultFile);
    			
    		if (loadedVault.isPresent() && opts.getFolderName() != null) {
    			final Optional<Credentials> credentials = readCredentials();
    			
    			if (credentials.isPresent()) {
    				final Vault vault = loadedVault.get();
    				vault.unlock(credentials.get());
                    vault.createFolder(opts.getFolderName());
                    vault.lock(credentials.get());
                    store.save(vault, vaultFile);
    			}
    				
    		} else {
                System.out.println("ERROR: Couldn't find selected folder");
            }
    	} else {
    		System.out.println("ERROR: Vault file does not exist: " + vaultFile.getAbsolutePath());
    	}
    }

    protected void createEntry(final CLIOptions opts) throws Exception {

    }

    protected Optional<Folder> loadFolder(final CLIOptions opts) throws IOException, ClassNotFoundException {
        final File vaultFile = new File(opts.getVaultPath());

        if (vaultFile.exists()) {
            final VaultStore store = new VaultStore();
            final Optional<Vault> loadedVault = store.load(vaultFile);

            if (loadedVault.isPresent() && opts.getFolderName() != null) {
                final Optional<Credentials> credentials = readCredentials();

                if (credentials.isPresent()) {
                    final Vault vault = loadedVault.get();
                    vault.unlock(credentials.get());
                    final Optional<Folder> folder = vault.getFolder(opts.getFolderName());

                    return folder;
                }
            }
        }

        return Optional.empty();
    }

    protected Optional<Credentials> readCredentials() {
        final Console console = System.console();

        if (console == null) {
            // TODO provide fallback method of entering credentials
            return Optional.empty();
        } else {
            System.out.print("Enter vault password: ");
            final String password1 = String.valueOf(console.readPassword());
            System.out.print("Enter vault password again: ");
            final String password2 = String.valueOf(console.readPassword());

            if (password1.equals(password2)) {
                System.out.print("Enter one-time password: ");
                final char[] otp = System.console().readPassword();

                final Credentials credentials = new Credentials(String.valueOf(password1), String.valueOf(otp));

                return Optional.of(credentials);
            } else {
                return Optional.empty();
            }
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
