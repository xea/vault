package so.blacklight.vault.entry;

import java.io.Serializable;

/**
 * Instances of classes implementing Entry are object that are stored in Folders and
 * holding end-user data, eg. passwords, secret keys, etc.
 */
public interface Entry extends Serializable {

    /**
     * Provide metadata about the current entry
     *
     * @return metadata
     */
    Metadata getMetadata();

}
