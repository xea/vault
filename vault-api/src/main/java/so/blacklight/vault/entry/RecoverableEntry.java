package so.blacklight.vault.entry;

/**
 * A recoverable entity is one that is kept in several copies with different grades of
 * encryption so that the recovery information defined in these instances can be retrieved
 * in the event of loss of credentials
 */
public interface RecoverableEntry extends Entry {

    String DEFAULT_RECOVERY_INFO = null;

    /**
     * Return recovery information that can be displayed to the user in case
     * not all the required secrets are available in order to decrypt the whole
     * entry. Although this information isn't secret by itself, it should be handled
     * with care as it may contain information referring to secrets.
     *
     * @return recovery information
     */
    String getRecoveryInfo();

    RecoverableEntry setRecoveryInfo(String recoveryInfo);
}
