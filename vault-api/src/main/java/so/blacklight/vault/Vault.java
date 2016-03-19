package so.blacklight.vault;

import java.util.UUID;

public interface Vault {

    /**
     * Indicate if this vault instance can be modified. Read-only vaults throw exceptions upon mutation.
     *
     * @return <code>true</code> if this value is writable, otherwise <code>false</code>
     */
    boolean isWritable();

    /**
     * Return a globally unique identifier.
     *
     * @return UUID
     */
    UUID getUuid();


}
