package so.blacklight.vault.entry;

import java.time.Instant;

/**
 * Holds a regular username/email-password pair.
 */
public class PasswordEntry implements RecoverableEntry {

    private final String id;

    private final String password;

    private final String recoveryInfo;

    private final EntryMetadata metadata;

    public PasswordEntry(final String id, final String password) {
        this(id, password, EntryMetadata.DEFAULT_TITLE);
    }

    public PasswordEntry(final String id, final String password, final String title) {
        this(id, password, title, null, RecoverableEntry.DEFAULT_RECOVERY_INFO);
    }

    public PasswordEntry(final String id, final String password, final String title, final String comment, final String recoveryInfo) {
        this(id, password, title, comment, recoveryInfo, EntryMetadata.DEFAULT_EXPIRATION_TIME);
    }

    public PasswordEntry(final String id, final String password, final String title, final String comment, final String recoveryInfo, final Instant expirationTime) {
        this.id = id;
        this.password = password;
        this.recoveryInfo = recoveryInfo;
        this.metadata = new EntryMetadata(title, comment, expirationTime, Instant.now());
    }

    protected PasswordEntry(final PasswordEntry copy, final EntryMetadata metadata) {
        this.id = copy.id;
        this.password = copy.password;
        this.recoveryInfo = copy.recoveryInfo;
        this.metadata = metadata;
    }

    protected PasswordEntry(final String id, final String password, final String recoveryInfo, final EntryMetadata metadata) {
        this.id = id;
        this.password = password;
        this.recoveryInfo = recoveryInfo;
        this.metadata = metadata;
    }

    public String getId() {
        return id;
    }

    public PasswordEntry setId(final String newId) {
        final EntryMetadata newMetadata = metadata.setModifyTime(Instant.now());
        return new PasswordEntry(newId, password, recoveryInfo, newMetadata);
    }

    public String getPassword() {
        return password;
    }

    public PasswordEntry setPassword(final String newPassword) {
        final EntryMetadata newMetadata = metadata.setModifyTime(Instant.now());
        return new PasswordEntry(id, newPassword, recoveryInfo, newMetadata);
    }

    @Override
    public String getRecoveryInfo() {
        return recoveryInfo;
    }

    public PasswordEntry setRecoveryInfo(final String newRecoveryInfo) {
        final EntryMetadata newMetadata = metadata.setModifyTime(Instant.now());
        return new PasswordEntry(id, password, newRecoveryInfo, newMetadata);
    }

    @Override
    public EntryMetadata getMetadata() {
        return metadata;
    }

}
