package so.blacklight.vault;

import so.blacklight.vault.entry.Entry;
import so.blacklight.vault.entry.Folder;
import so.blacklight.vault.entry.RecoverableEntry;
import so.blacklight.vault.entry.SecretContainer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class Vault implements Serializable {

    public static final long serialVersionUID = -7671254481L;

    private UUID uuid;

    private final VaultSettings settings;

    private final List<Entry> entries;

    private boolean masterCopy;

    public Vault() {
        uuid = UUID.randomUUID();
        settings = new VaultSettings();
        entries = new ArrayList<>();
        masterCopy = true;
    }

    public Vault(final VaultSettings settings) {
        uuid = UUID.randomUUID();
        this.settings = settings;
        entries = new ArrayList<>();
        masterCopy = true;
    }

    protected Vault(final Vault otherVault, final UnaryOperator<List<Entry>> filter) {
        uuid = otherVault.getUuid();
        masterCopy = false;
        entries = new ArrayList<>(filter.apply(otherVault.getEntries()));
        settings = otherVault.settings;
    }

    public boolean isWritable() {
        return masterCopy;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Optional<Vault> getRecoverySegment() {
        if (settings.isGenerateRecovery()) {
            return Optional.of(generateRecovery());
        } else {
            return Optional.empty();
        }
    }

    public Optional<Vault> getDegradedSegment() {
        if (settings.isGenerateDegraded()) {
            return Optional.of(generateDegraded());
        } else {
            return Optional.empty();
        }
    }

    public List<Entry> getEntries() {
        return entries;
    }

    protected Vault generateRecovery() {
        final Vault recoveryVault = new Vault(this, this::stripRecovery);

        return recoveryVault;
    }

    protected Vault generateDegraded() {
        final Vault degradedVault = new Vault(this, this::stripDegraded);
        
        return degradedVault;
    }

    private List<Entry> stripRecovery(final List<Entry> entries) {
        final List<Entry> collect = entries.stream().map(e -> {
            if (e instanceof Folder) {
                final Folder f = (Folder) e;
                return f.updateEntries(stripRecovery(f.getEntries()));
            } else if (e instanceof SecretContainer) {
                final SecretContainer s = (SecretContainer) e;
                return s.clearSecret();
            } else {
                return e;
            }
        }).collect(Collectors.toList());

        return collect;
    }

    private List<Entry> stripDegraded(final List<Entry> entries) {
        final List<Entry> collect = entries.stream().map(e -> {
            if (e instanceof Folder) {
                final Folder f = (Folder) e;
                return f.updateEntries(stripDegraded(f.getEntries()));
            } else if (e instanceof SecretContainer) {
                final SecretContainer s = (SecretContainer) e;
                final SecretContainer cleared = s.clearSecret();

                if (cleared instanceof RecoverableEntry) {
                    final RecoverableEntry r = (RecoverableEntry) cleared;

                    return r.setRecoveryInfo(null);
                } else {
                    return cleared;
                }
            } else if (e instanceof RecoverableEntry) {
                final RecoverableEntry r = (RecoverableEntry) e;

                return r.setRecoveryInfo(null);
            } else {
                return e;
            }
        }).collect(Collectors.toList());

        return collect;
    }


}
