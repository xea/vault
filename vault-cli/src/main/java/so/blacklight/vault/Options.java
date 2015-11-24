package so.blacklight.vault;

import com.github.jankroken.commandline.annotations.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    private List<String> authOptions = new ArrayList<>();

    private boolean generateRecovery = false;

    private boolean generateDegraded = false;

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

    @Option
    @ShortSwitch("ce")
    @LongSwitch("create-entry")
    @Toggle(false)
    public void requestCreateEntry(boolean value) {
        if (action == Action.DEFAULT_ACTION) {
            action = Action.CREATE_ENTRY;
        }
    }

    @Option
    @ShortSwitch("r")
    @LongSwitch("recovery")
    @Toggle(false)
    public void requestGenerateRecovery(boolean value) {
        this.generateRecovery = value;
    }

    @Option
    @ShortSwitch("d")
    @LongSwitch("degraded")
    @Toggle(false)
    public void requestGenerateDegraded(boolean value) {
        this.generateDegraded = value;
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

    @Option
    @ShortSwitch("m")
    @LongSwitch("methods")
    @SingleArgument
    public void setAuthenticationMethod(String method) {
        authOptions = Arrays.asList(method.split("[\\s]*,[\\s]*"));
    }

    public List<String> getAuthOptions() {
        return authOptions;
    }

    public boolean isGenerateRecovery() {
        return generateRecovery;
    }

    public boolean isGenerateDegraded() {
        return generateDegraded;
    }

    public Optional<String> isValid() {
        boolean valid = false;

        String errorMsg = "DEFAULT ERROR";

        if (action == Action.CREATE_VAULT) {
            if (!vaultFile.isPresent()) {
                errorMsg = "No vault file was specified";
            } else if (authOptions.size() == 0) {
                errorMsg = "No encryption method was specified";
            } else {
                valid = true;
            }
        } else if (action == Action.CREATE_FOLDER) {
            if (!alias.isPresent()) {
                errorMsg = "Missing folder alias";
            } else {
                valid = true;
            }
        } else if (action == Action.CREATE_ENTRY) {
            if (!alias.isPresent()) {
                errorMsg = "Missing entry alias";
            } else {
                valid = true;
            }
        } else if (action == Action.LIST_ENTRIES) {
            if (!vaultFile.isPresent()) {
                errorMsg = "No vault file was specified";
            } else {
                valid = true;
            }
        }

        if (valid) {
            return Optional.empty();
        } else {
            return Optional.of(errorMsg);
        }
    }

    /**
     * Actions that the CLI program can perform
     */
    enum Action {
        DEFAULT_ACTION,
        CREATE_VAULT,
        CREATE_FOLDER,
        CREATE_ENTRY,
        LIST_ENTRIES
    }
}
