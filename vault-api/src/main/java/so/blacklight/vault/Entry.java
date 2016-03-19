package so.blacklight.vault;

import java.io.Serializable;

/**
 * Represents a unit of data that can be stored in a vault. Implementations of this interface
 * can be eg. passwords, private keys, other sensitive information or just structural elements
 * like folders or groups.
 */
public interface Entry extends Serializable {

    /**
     * Return a user-readable identifies for this entry, eg. my-password-1
     *
     * @return alias
     */
    String getAlias();
}
