package so.blacklight.vault.cli;

import com.github.jankroken.commandline.annotations.LongSwitch;
import com.github.jankroken.commandline.annotations.Option;
import com.github.jankroken.commandline.annotations.ShortSwitch;
import com.github.jankroken.commandline.annotations.SingleArgument;
import com.github.jankroken.commandline.annotations.Toggle;

/**
 * Encapsulates user-supplied arguments
 */
public class CLIOptions {

    private String vaultPath = VaultCLI.DEFAULT_VAULT_PATH;

    private String folderName = null;

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
                case "debug":
                    action = VaultAction.DEBUG;
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

    @Option
    @LongSwitch("help")
    @ShortSwitch("h")
    @Toggle(false)
    public void showHelp(final boolean showHelp) {
        action = VaultAction.SHOW_USAGE;
    }

    public String getFolderName() {
        return folderName;
    }

    @Option
    @LongSwitch("folder")
    @ShortSwitch("f")
    @SingleArgument
    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }
}
