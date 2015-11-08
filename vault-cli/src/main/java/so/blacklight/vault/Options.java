package so.blacklight.vault;

import com.github.jankroken.commandline.annotations.*;

import java.io.File;
import java.util.Optional;

/**
 * Represents the user-supplied arguments in a consumable form.
 */
public class Options {

    private Action action = Action.DEFAULT_ACTION;

    private boolean helpRequested = false;

    private Optional<File> keyFile = Optional.empty();

    private Optional<File> vaultFile = Optional.empty();

    private Optional<String> alias = Optional.empty();

    @Option
    @ShortSwitch("h")
    @LongSwitch("help")
    @Toggle(true)
    public void requestShowHelp(boolean value) {
        helpRequested = value;
    }

    @Option
    @ShortSwitch("l")
    @LongSwitch("list")
    @Toggle(false)
    public void requestListEntries(boolean value) {
        if (action == Action.DEFAULT_ACTION) {
            action = Action.LIST_ENTRIES;
        }
    }

    @Option
    @ShortSwitch("c")
    @LongSwitch("create")
    @Toggle(false)
    public void requestCreateVault(boolean value) {
        if (action == Action.DEFAULT_ACTION) {
            action = Action.CREATE_VAULT;
        }
    }

    public Action getAction() {
        return action;
    }

    public boolean isHelpRequested() {
        return helpRequested;
    }

    public Optional<File> getKeyFile() {
        return keyFile;
    }

    @Option
    @ShortSwitch("k")
    @LongSwitch("key")
    @SingleArgument
    public void setKeyFile(String keyPath) {
        this.keyFile = Optional.of(new File(keyPath));
    }

    public Optional<File> getVaultFile() {
        return vaultFile;
    }

    @Option
    @ShortSwitch("v")
    @LongSwitch("vault")
    @SingleArgument
    public void setVaultFile(String vaultPath) {
        vaultFile = Optional.of(new File(vaultPath));
    }

    @Option
    @ShortSwitch("a")
    @LongSwitch("alias")
    @SingleArgument
    public void setAlias(String alias) {
        if (alias == null) {
            this.alias = Optional.empty();
        } else {
            this.alias = Optional.of(alias);
        }
    }

    public Optional<String> getAlias() {
        return alias;
    }

    public boolean isValid() {
        boolean valid = false;

        if (action == Action.CREATE_VAULT) {
            if (keyFile.isPresent() && vaultFile.isPresent()) {
                valid = true;
            }
        } else if (action == Action.LIST_ENTRIES) {
            if (vaultFile.isPresent()) {
                valid = true;
            }
        }

        return valid;
    }

    /**
     * Actions that the CLI program can perform
     */
    enum Action {
        CREATE_VAULT,
        DEFAULT_ACTION,
        LIST_ENTRIES
    }
}
