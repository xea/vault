package so.blacklight.vault.cli;

import com.github.jankroken.commandline.annotations.*;

import java.util.Optional;

public class CommandLineOptions {

    private Optional<String> vaultPath = Optional.empty();

    private boolean createVault = false;

    private Optional<String> listFolder = Optional.empty();

    public Optional<String> getVaultPath() {
        return vaultPath;
    }

    @Option
    @LongSwitch("file")
    @ShortSwitch("f")
    @SingleArgument
    public void setVaultPath(String vaultPath) {
        if (vaultPath == null) {
            this.vaultPath = Optional.empty();
        } else {
            this.vaultPath = Optional.of(vaultPath);
        }
    }

    public boolean isCreateVault() {
        return createVault;
    }

    @Option
    @LongSwitch("create")
    @ShortSwitch("c")
    @Toggle(false)
    public void setCreateVault(boolean createVault) {
        this.createVault = createVault;
    }

    public Optional<String> getListFolder() {
        return listFolder;
    }

    @Option
    @LongSwitch("list")
    @ShortSwitch("l")
    @SingleArgument
    public void setListFolder(String listFolder) {
        if (listFolder == null) {
            this.listFolder = Optional.empty();
        } else {
            this.listFolder = Optional.of(listFolder);
        }
    }
}
