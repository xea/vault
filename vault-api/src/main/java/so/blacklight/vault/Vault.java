package so.blacklight.vault;

import so.blacklight.vault.entry.Entry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Vault implements Serializable {

    public static final long serialVersionUID = -7671254481L;

    private UUID uuid;

    private final VaultSettings settings;

    private final List<Entry> entries;

    public Vault() {
        uuid = UUID.randomUUID();
        settings = new VaultSettings();
        entries = new ArrayList<>();
    }

    public Vault(final VaultSettings settings) {
        uuid = UUID.randomUUID();
        this.settings = settings;
        entries = new ArrayList<>();
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

    public List<Entry> getEntries() {
        return entries;
    }
}
