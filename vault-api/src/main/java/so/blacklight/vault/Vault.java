package so.blacklight.vault;

import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

public class Vault implements Serializable {

    public static final long serialVersionUID = -7671254481L;

    private UUID uuid;

    private VaultSettings settings;

    public Vault() {
        uuid = UUID.randomUUID();
        settings = new VaultSettings();
    }

    public Vault(final VaultSettings settings) {

    }

    public boolean isWritable() {
        return true;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Optional<Vault> getRecoverySegment() {
        return Optional.of(this);
    }

    public Optional<Vault> getDegradedSegment() {
        return Optional.empty();
    }

}
