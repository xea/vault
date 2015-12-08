package so.blacklight.vault.cli;

import org.junit.Test;
import so.blacklight.vault.command.CLICommand;
import so.blacklight.vault.command.ShowHelp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class VaultCLITest {

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
        final Options o3 = cli.processArgs(new String[] { "-show-info", "-a", "alias" });

        final CLICommand cmd2 = cli.processRequest(o2);
        final CLICommand cmd3 = cli.processRequest(o3);

        assertTrue(cmd2 instanceof ShowHelp);
        assertTrue(cmd3 instanceof ShowHelp);
    }
}
