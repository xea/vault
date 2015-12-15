package so.blacklight.vault.cli;

import fj.data.Either;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import so.blacklight.vault.Credentials;
import so.blacklight.vault.Vault;
import so.blacklight.vault.VaultException;
import so.blacklight.vault.VaultStore;
import so.blacklight.vault.command.CLICommand;
import so.blacklight.vault.command.ShowHelp;
import so.blacklight.vault.crypto.KeyManager;
import so.blacklight.vault.crypto.Password;
import so.blacklight.vault.crypto.RSAPrivateKey;
import so.blacklight.vault.store.StreamVaultStore;

import java.io.*;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class VaultCLITest {

    private InputStream sysInStream = null;

    private PrintStream sysOutStream = null;

    private File randomFile = null;

    private final VaultStore store = new StreamVaultStore();

    @Before
    public void setup() throws IOException {
        sysInStream = System.in;
        sysOutStream = System.out;
        randomFile = File.createTempFile("junit", "vlt");
        randomFile.deleteOnExit();
        randomFile.delete();
    }

    @After
    public void tearDown() {
        System.setIn(sysInStream);
        System.setOut(sysOutStream);
    }

    @Test
    public void processArgsShouldRecogniseShortCommands() {
        final VaultCLI cli = new VaultCLI();

        final Options o1 = cli.processArgs(new String[] { "-q" });
        assertNotNull(o1);
        assertEquals(Options.Action.DEFAULT_ACTION, o1.getAction());

        final Options o2 = cli.processArgs(new String[] {});
        assertNotNull(o2);
        assertEquals(Options.Action.DEFAULT_ACTION, o2.getAction());

        final Options o3 = cli.processArgs(new String[] { "-c" });
        assertNotNull(o3);
        assertEquals(Options.Action.CREATE_VAULT, o3.getAction());
    }

    @Test
    public void processArgsShouldRecogniseCommandsAtAnyPosition() {
        final VaultCLI cli = new VaultCLI();

        final Options o1 = cli.processArgs(new String[] { "-l" });
        assertNotNull(o1.getAction());
        assertEquals(Options.Action.LIST_ENTRIES, o1.getAction());

        final Options o2 = cli.processArgs(new String[] { "-v", "vault.vlt", "-l" });
        assertNotNull(o2.getAction());
        assertEquals(Options.Action.LIST_ENTRIES, o2.getAction());
        final Options o3 = cli.processArgs(new String[] { "-v", "vault.vlt", "-l" , "-show-entry" });
        assertNotNull(o3.getAction());
        assertEquals(Options.Action.LIST_ENTRIES, o3.getAction());
    }

    @Test
    public void processArgsShouldNotAllowOverridingActions() {
        final VaultCLI cli = new VaultCLI();

        final Options o1 = cli.processArgs(new String[] { "-l", "-c" });
        assertNotNull(o1.getAction());
        assertEquals(Options.Action.LIST_ENTRIES, o1.getAction());
    }

    @Test
    public void runCommandDefaultsToDefaultActionWhenCommandIsInvalid() {
        final VaultCLI cli = new VaultCLI();

        // typo in command
        final Options o2 = cli.processArgs(new String[] { "-cre-eny" });
        // superfluous arguments
        final Options o3 = cli.processArgs(new String[] { "-info", "-a", "alias" });

        final CLICommand cmd2 = cli.processRequest(o2);
        final CLICommand cmd3 = cli.processRequest(o3);

        assertTrue(cmd2 instanceof ShowHelp);
        assertTrue(cmd3 instanceof ShowHelp);
    }
    
    @Test
    public void testSinglePasswordEncryption() throws IOException, VaultException {
        final String password1 = "Password1";
        updateInput(password1);
        final String[] args = new String[] { "-create-vault", "-v", randomFile.getAbsolutePath(), "-m", "pw" };

        VaultCLI.main(args);

        assertTrue(randomFile.exists());
        assertTrue(randomFile.length() > 0);

        final Credentials credentials = new Credentials(Arrays.asList(new Password(password1.toCharArray())));
        Either<String, Vault> load = store.load(credentials, randomFile);
        assertTrue(load.isRight());
    }

    @Test
    public void testSingleRSAKeyEncryption() throws IOException, VaultException, InvalidKeySpecException, NoSuchAlgorithmException {
        final File tmpPrivateKey = File.createTempFile("junit", "-private.vlt");
        final File tmpPublicKey = File.createTempFile("junit", "-public.vlt");
        tmpPrivateKey.deleteOnExit();
        tmpPublicKey.deleteOnExit();
        tmpPrivateKey.delete();
        tmpPublicKey.delete();

        final String[] genargs = new String[] { "-generate-key", "rsa" };
        updateInput(tmpPublicKey.getAbsolutePath(), tmpPrivateKey.getAbsolutePath());
        VaultCLI.main(genargs);

        final String[] args = new String[] { "-create-vault", "-v", randomFile.getAbsolutePath(), "-m", "rsa" };
        updateInput(tmpPublicKey.getAbsolutePath());
        VaultCLI.main(args);

        assertTrue(randomFile.exists());
        assertTrue(randomFile.length() > 0);

        final KeyManager keyManager = new KeyManager();
        final Credentials credentials = new Credentials(Arrays.asList(keyManager.loadRSAPrivateKey(tmpPrivateKey)));
        Either<String, Vault> load = store.load(credentials, randomFile);
        assertTrue(load.isRight());
    }

    private void updateInput(final String... args) {
        updateInput(Arrays.asList(args));
    }

    private void updateInput(final List<String> args) {
        String input = args.stream().map(a -> a + "\r\n").reduce((a, b) -> a + b).get();

        final InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        final OutputStream outputStream = new ByteArrayOutputStream();
        final PrintStream printStream = new PrintStream(outputStream);

        System.setIn(inputStream);
        System.setOut(printStream);
    }
}

