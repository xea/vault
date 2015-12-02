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
public class Metadata implements Serializable {


	private static final long serialVersionUID = -7717838248323617420L;

	public static final String DEFAULT_ALIAS = "Untitled Entry";

    public static final String DEFAULT_COMMENT = null;

    public static final Instant DEFAULT_EXPIRATION_TIME = Instant.MAX;

    private final Instant createTime;

    private final Instant modifyTime;

    private final Instant expirationTime;

    private final String comment;

    private final String alias;

    /**
     * Create a new metadata object with a alias only. Every other fields are set to reasonable defaults.
     * Note: this entry will never expire.
     *
     * @param alias displayable alias of this entry
     */
    public Metadata(final String alias) {
        this(alias, null);
    }

    /**
     * Create a new metadata object with a alias and comment but without an expiration date so this
     * entry will never expire.
     *
     * @param alias displayable alias of this entry
     * @param comment displayable comment for this entry
     */
    public Metadata(final String alias, final String comment) {
        this(alias, comment, DEFAULT_EXPIRATION_TIME);
    }

    /**
     * Create a new metadata object with the specified alias, comment and expiration time.
     *
     * @param alias displayable alias of this entry
     * @param comment displayable comment for this entry
     * @param expirationTime point in time when this entry is considered expired
     */
    public Metadata(final String alias, final String comment, final Instant expirationTime) {
        this(alias, comment, expirationTime, Instant.now());
    }

    private Metadata(final String alias, final String comment, final Instant expirationTime, final Instant modifyTime) {
        createTime = Instant.now();
        this.modifyTime = modifyTime;
        this.comment = comment;
        this.alias = alias;
        this.expirationTime = expirationTime;
    }

    /**
     * Return the alias of this entry. Aliases should be user-readable string intended
     * for displaying to the user.
     *
     * @return entry alias
     */
    public String getAlias() {
        return alias;
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

    public Metadata setModifyTime(Instant newModifyTime) {
        return new Metadata(alias, comment, newModifyTime);
    }

    public Metadata setComment(String newComment) {
        return new Metadata(alias, newComment, expirationTime, Instant.now());
    }

    public Metadata setAlias(String alias) {
        return new Metadata(alias, comment, expirationTime, Instant.now());
    }

    public Metadata setExpirationTime(final Instant newExpirationTime) {
        return new Metadata(alias, comment, newExpirationTime, Instant.now());
    }
}
