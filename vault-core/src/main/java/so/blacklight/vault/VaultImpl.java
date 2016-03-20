package so.blacklight.vault;

import java.util.UUID;

/**
 * Vault implementation that doesn't allow hierarchical structuring, every entry is stored in a flat structure instead.
 */
public class VaultImpl implements Vault {

    @Override
    public boolean isWritable() {
        return false;
    }

    @Override
    public UUID getUuid() {
        return null;
    }
}
