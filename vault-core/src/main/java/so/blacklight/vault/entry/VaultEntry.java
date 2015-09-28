package so.blacklight.vault.entry;

import java.io.Serializable;
import java.time.Instant;
import java.util.Optional;

/**
 * Base class for entries that can be put into vault folders.
 */
public abstract class VaultEntry implements Serializable {

    private final Instant createTime;

    private final Optional<Instant> expirationTime;

    private final String recoveryInfo;

    public VaultEntry() {
        this(Optional.empty());
    }

    protected VaultEntry(final Optional<Instant> expirationTime) {
        this(expirationTime, "");
    }

    public VaultEntry(final Instant expirationTime, final String recoveryInfo) {
        this.createTime = Instant.now();
        this.recoveryInfo = recoveryInfo;

        if (expirationTime == null) {
            this.expirationTime = Optional.empty();
        } else {
            this.expirationTime = Optional.of(expirationTime);
        }
    }

    protected VaultEntry(final Optional<Instant> expirationTime, final String recoveryInfo) {
        this.createTime = Instant.now();
        this.expirationTime = expirationTime;
        this.recoveryInfo = recoveryInfo;
    }

    public VaultEntry(final VaultEntry copy) {
        createTime = copy.createTime;
        expirationTime = copy.expirationTime;
        recoveryInfo = copy.recoveryInfo;
    }

}
