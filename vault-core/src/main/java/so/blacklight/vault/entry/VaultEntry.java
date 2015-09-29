package so.blacklight.vault.entry;

import java.io.Serializable;
import java.time.Instant;
import java.util.Optional;

/**
 * Base class for entries that can be put into vault folders.
 */
public abstract class VaultEntry implements Serializable {

    private final Instant createTime;

    private final Instant expirationTime;

    private final String recoveryInfo;

    public VaultEntry(final Instant expirationTime, final String recoveryInfo) {
        this.createTime = Instant.now();
        this.recoveryInfo = recoveryInfo;
        this.expirationTime = expirationTime;
    }

    public VaultEntry(final VaultEntry copy) {
        createTime = copy.createTime;
        expirationTime = copy.expirationTime;
        recoveryInfo = copy.recoveryInfo;
    }

    public Instant getCreateTime() {
        return createTime;
    }

    public Optional<Instant> getExpirationTime() {
        if (expirationTime == null) {
            return Optional.empty();
        } else {
            return Optional.of(expirationTime);
        }
    }

    public String getRecoveryInfo() {
        return getRecoveryInfo();
    }
}
