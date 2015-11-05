package so.blacklight.vault;

import java.io.Serializable;

/**
 * Marker interface for all object that can be stored in a Vault.
 */
public interface Entry extends Serializable {

    /**
     * Return a user-readable alias (ie. a name) for this instance
     * @return entry alias
     */
    String getAlias();

}
