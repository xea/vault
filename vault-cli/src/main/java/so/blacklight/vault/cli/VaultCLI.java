package so.blacklight.vault.cli;

import com.github.jankroken.commandline.CommandLineParser;
import com.github.jankroken.commandline.OptionStyle;
import so.blacklight.vault.Credentials;
import so.blacklight.vault.Vault;
import so.blacklight.vault.VaultStore;
import so.blacklight.vault.entry.PasswordEntry;

import java.io.*;

public class VaultCLI {

    public static final String DEFAULT_VAULT_PATH = "vault.vlt";

    private final CommandLineOptions opts;

    private final Vault vault;

    public VaultCLI(final CommandLineOptions opts) throws IOException, ClassNotFoundException {
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
        final CommandLineOptions cliOpts = CommandLineParser.parse(CommandLineOptions.class, args, OptionStyle.SIMPLE);

        final VaultCLI cli = new VaultCLI(cliOpts);
        cli.processCommand();
    }

}
