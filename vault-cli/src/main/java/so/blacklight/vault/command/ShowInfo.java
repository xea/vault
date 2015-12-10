package so.blacklight.vault.command;

import fj.data.List;
import so.blacklight.vault.cli.Options;
import so.blacklight.vault.io.VaultInputStream;
import so.blacklight.vault.io.VaultRecord;
import so.blacklight.vault.store.Layout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

public class ShowInfo extends VaultCommand {

    public ShowInfo(final Options options) {
        super(options);
    }

    @Override
    public boolean execute() {
        final File vaultFile = options.getVaultFile().get();

        if (vaultFile.exists() && vaultFile.canRead()) {
            try {
                final FileInputStream fis = new FileInputStream(vaultFile);
                final VaultInputStream vis = new VaultInputStream(fis);

                final Layout layout = vis.getLayout();

                console.out(String.format("Layout: %d %d %d", layout.getPrimaryLayers(), layout.getRecoveryLayers(), layout.getDegradedLayers()));

                final List<VaultRecord> vaultRecords = vis.readAll();

                console.out("Found " + vaultRecords.length() + " record(s)");

                vaultRecords.forEach(record -> {
                    console.out("Found record with " + record.getIvs().length + " IV(s) and " + record.getSalts().length + " salt(s) and block is " + record.getBlock().length + " bytes long");
                });

                vis.close();
            } catch (FileNotFoundException e) {
                console.error(e.getMessage());
            } catch (IOException e) {
                console.error(e.getMessage());
            }
        } else {
            console.error("Can't open vault file " + vaultFile.getAbsolutePath());
        }

        return false;
    }

    @Override
    public boolean undo() {
        return false;
    }

    @Override
    public boolean isUndoable() {
        return false;
    }

    @Override
    public Optional<String> validate() {
        return Optional.empty();
    }

}
