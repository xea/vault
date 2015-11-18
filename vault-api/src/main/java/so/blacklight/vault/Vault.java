package so.blacklight.vault;

import fj.data.List;

import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

public class Vault implements Serializable {

    public static final long serialVersionUID = -7671254481L;

    private UUID uuid;

    private final VaultSettings settings;

    public Vault() {
        uuid = UUID.randomUUID();
        settings = new VaultSettings();
    }

    public Vault(final VaultSettings settings) {
        uuid = UUID.randomUUID();
        this.settings = settings;
    }

    public boolean isWritable() {
        return true;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Optional<Vault> getRecoverySegment() {
        if (settings.isGenerateRecovery()) {
            return Optional.of(new Vault());
        } else {
            return Optional.empty();
        }
    }

    public Optional<Vault> getDegradedSegment() {
        if (settings.isGenerateDegraded()) {
            return Optional.of(new Vault());
        } else {
            return Optional.empty();
        }
    }
}
