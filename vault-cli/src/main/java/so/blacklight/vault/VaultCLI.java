package so.blacklight.vault;

import com.github.jankroken.commandline.CommandLineParser;
import com.github.jankroken.commandline.OptionStyle;
import fj.data.Either;
import so.blacklight.crypto.Credentials;
import so.blacklight.crypto.Encryptor;
import so.blacklight.io.VaultStore;
import so.blacklight.vault.crypto.CryptoService;
import so.blacklight.vault.crypto.PasswordCredentials;
import so.blacklight.vault.io.DefaultVaultStoreImpl;

import java.io.*;
import java.lang.reflect.InvocationTargetException;

public class VaultCLI {

    public static void main(final String[] args) throws IllegalAccessException, InvocationTargetException, InstantiationException, IOException, ClassNotFoundException {
        final VaultCLI cli = new VaultCLI();
        final CLIArguments arguments = CommandLineParser.parse(CLIArguments.class, args, OptionStyle.SIMPLE);

        cli.processCommand(arguments);
    }

    public void processCommand(CLIArguments arguments) throws IOException, ClassNotFoundException {
        /*
        final VaultStore store = new DefaultVaultStoreImpl();

        final Credentials credentials = new PasswordCredentials("testStuff".toCharArray());

        final File vaultFile = new File("lofasz.vault");

        final Vault vault;
        if (vaultFile.exists()) {
            final InputStream is = new FileInputStream(vaultFile);
            Either<String, Vault> load = store.load(is, credentials);
            is.close();
            vault = load.right().toOption().orSome(new DefaultVault());
        } else {
            vault = new DefaultVault();
        }

        vault.getEntries().stream().forEach(e -> System.out.println(e.getAlias()));

        final Entry entry = new PasswordEntry("alias3", "username".getBytes(), "password".getBytes(), "url".getBytes());
        final Encryptor<Entry> encryptor = new CryptoService<>();

        vault.addEntry(encryptor.encrypt(entry, credentials));
        final OutputStream os = new FileOutputStream("lofasz.vault");
        store.save(os, vault, credentials);
        os.close();
        */
    }

}
