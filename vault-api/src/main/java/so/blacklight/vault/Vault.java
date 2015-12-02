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

/**
 * Database for private, confidential or secret information such as passwords, private keys, etc.
 *
 */
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

    /**
     * Indicates if this vault is writable.
     * @return <code>true</code> if the current vault is writable, otherwise <code>false</code>
     */
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

    /**
     * Return the entries in the current vault
     *
     * @return list of entries
     */
    public List<Entry> getEntries() {
        return entries;
    }

    /**
     * Return a recovery version of the current vault.
     *
     * @return recovery vault
     */
    protected Vault generateRecovery() {
        final Vault recoveryVault = new Vault(this, this::stripRecovery);

        return recoveryVault;
    }

    /**
     * Return a degraded version of the current vault.
     *
     * @return degraded vault
     */
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

    public Optional<Entry> findAlias(String alias) {
        return findAlias(alias, entries);
    }

    protected Optional<Entry> findAlias(final String alias, final List<Entry> entryList) {
        final Optional<Entry> directResult = entryList.parallelStream().filter(e -> e.getMetadata().getAlias().equals(alias)).findFirst();

        if (!directResult.isPresent()) {
            return entryList.parallelStream().filter(e ->
                    e instanceof  Folder).filter(e ->
                    findAlias(alias, ((Folder) e).getEntries()).isPresent()).findFirst();
        } else {
            return directResult;
        }
    }
}
