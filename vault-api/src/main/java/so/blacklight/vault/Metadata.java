package so.blacklight.vault;

import java.time.Instant;

/**
 * Provides audit metadata about the implementing class.
 */
public interface Metadata {

    /**
     * Return time when this item was created
     *
     * @return create time
     */
    Instant getCreateTime();

    /**
     * Return time when this item was last modified. This does not include read-only
     * accesses.
     *
     * @return time of last modification
     */
    Instant getModifyTime();

    /**
     * Return time when this item was last accessed. This includes modifications.
     *
     * @return time of last access
     */
    Instant getAccessTime();

    /**
     * Return a human-readable description explaining the purpose/origins/etc of this entry
     *
     * @return human-readable comment
     */
    String getComment();

}
