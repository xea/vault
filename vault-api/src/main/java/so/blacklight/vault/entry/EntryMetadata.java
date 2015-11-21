package so.blacklight.vault.entry;

import java.io.Serializable;
import java.time.Instant;

/**
 * Provides supplementary information about a vault entry, mostly for administrative reasons or
 * to be displayed on the user interface.
 *
 * Note that this class is immutable, the setter methods return new instances of this class instead
 * of modifying the current instance.
 */
public class EntryMetadata implements Serializable {

    public static final long serialVersionUID = 54402L;

    public static final String DEFAULT_TITLE = "Untitled Entry";

    public static final String DEFAULT_COMMENT = null;

    public static final Instant DEFAULT_EXPIRATION_TIME = Instant.MAX;

    private final Instant createTime;

    private final Instant modifyTime;

    private final Instant expirationTime;

    private final String comment;

    private final String title;

    /**
     * Create a new metadata object with a title only. Every other fields are set to reasonable defaults.
     * Note: this entry will never expire.
     *
     * @param title displayable title of this entry
     */
    public EntryMetadata(final String title) {
        this(title, null);
    }

    /**
     * Create a new metadata object with a title and comment but without an expiration date so this
     * entry will never expire.
     *
     * @param title displayable title of this entry
     * @param comment displayable comment for this entry
     */
    public EntryMetadata(final String title, final String comment) {
        this(title, comment, DEFAULT_EXPIRATION_TIME);
    }

    /**
     * Create a new metadata object with the specified title, comment and expiration time.
     *
     * @param title displayable title of this entry
     * @param comment displayable comment for this entry
     * @param expirationTime point in time when this entry is considered expired
     */
    public EntryMetadata(final String title, final String comment, final Instant expirationTime) {
        this(title, comment, expirationTime, Instant.now());
    }

    private EntryMetadata(final String title, final String comment, final Instant expirationTime, final Instant modifyTime) {
        createTime = Instant.now();
        this.modifyTime = modifyTime;
        this.comment = comment;
        this.title = title;
        this.expirationTime = expirationTime;
    }

    /**
     * Return the title of this entry. Titles should be user-readable string intended
     * for displaying to the user.
     *
     * @return entry title
     */
    public String getTitle() {
        return title;
    }


    /**
     * Return the comment belonging to this entry. Comments should contain public information,
     * and never sensitive data, like passwords.
     *
     * @return comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * Return the time this entry was created
     *
     * @return time of creation
     */
    public Instant getCreateTime() {
        return createTime;
    }

    /**
     * Return the time this entry was last modified
     *
     * @return time of last modification
     */
    public Instant getModifyTime() {
        return modifyTime;
    }

    /**
     * Return the instant when this entry becomes expired
     *
     * @return time of expiration
     */
    public Instant getExpirationTime() {
        return expirationTime;
    }

    public EntryMetadata setModifyTime(Instant newModifyTime) {
        return new EntryMetadata(title, comment, newModifyTime);
    }

    public EntryMetadata setComment(String newComment) {
        return new EntryMetadata(title, newComment, expirationTime, Instant.now());
    }

    public EntryMetadata setTitle(String title) {
        return new EntryMetadata(title, comment, expirationTime, Instant.now());
    }

    public EntryMetadata setExpirationTime(final Instant newExpirationTime) {
        return new EntryMetadata(title, comment, newExpirationTime, Instant.now());
    }
}
