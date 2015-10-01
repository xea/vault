package so.blacklight.vault.cli;

import com.github.jankroken.commandline.annotations.*;

import java.util.Optional;

/**
 * Encapsulates user-supplied arguments
 */
public class CLIOptions {

    private String vaultPath = VaultCLI.DEFAULT_VAULT_PATH;

    private VaultAction action = VaultAction.NONE;

    @Option
    @LongSwitch("action")
    @ShortSwitch("a")
    @SingleArgument
    public void setAction(final String selectedAction) {
        action = VaultAction.SHOW_USAGE;

        if (selectedAction == null) {
            action = VaultAction.SHOW_USAGE;
        } else {
            switch (selectedAction.toLowerCase()) {
                case "create-vault":
                    action = VaultAction.CREATE_VAULT;
                    break;
                case "create-folder":
                    action = VaultAction.CREATE_FOLDER;
                    break;
                case "create-entry":
                    action = VaultAction.CREATE_ENTRY;
                    break;
                case "delete-folder":
                    action = VaultAction.DELETE_FOLDER;
                    break;
                case "delete-entry":
                    action = VaultAction.CREATE_ENTRY;
                    break;
            }
        }
    }

    public VaultAction getAction() {
        return action;
    }

    @Option
    @LongSwitch("vault")
    @ShortSwitch("v")
    @SingleArgument
    public void setVaultPath(final String vaultPath) {
        this.vaultPath = vaultPath;
    }

    public String getVaultPath() {
        return vaultPath;
    }
}
