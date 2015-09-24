package so.blacklight.vault;

import javax.crypto.SealedObject;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A Vault is a collection of secrets organised into a flat folder-based hierarchy.
 *
 * Each vault has a primary segment where the complete database can be found. In addition to the primary segment,
 * a vault may have secondary segment containing less sensitive extracts from the primary segment.
 *
 * Secondary segments are used to provide recovery information in case any of the authentication data is lost/unavailable.
 */
public class Vault implements Serializable {

    public static final long serialVersionUID = 10010l;

    private final Instant createTime;

    private final Instant modifyTime;

    private SealedObject sealedPrimarySegment;

    private SealedObject sealedSecondarySegments;

    private transient VaultSegment primarySegment;

    private transient List<VaultSegment> secondarySegments;

    private transient VaultStatus status;

    public Vault() {
        createTime = Instant.now();
        modifyTime = Instant.now();
        primarySegment = new VaultSegment();
        secondarySegments = new CopyOnWriteArrayList<>();
        status = VaultStatus.UNLOCKED;
    }

    public void lock(final Credentials credentials) {
        if (status == VaultStatus.DEGRADED) {
            // Error: saving a degraded vault is not allowed
        } else if (status == VaultStatus.UNLOCKED) {
            final VaultEncryptor encryptor = new VaultEncryptor();
            final EncryptionParameters params = new EncryptionParameters(credentials);
            generateSecondarySegments();
            sealedPrimarySegment = encryptor.encryptSegment(primarySegment, params);
            sealedSecondarySegments = sealedPrimarySegment;
            primarySegment = null;
            secondarySegments.clear();
            status = VaultStatus.LOCKED;
        }
    }

    public void unlock(final Credentials credentials) {
        if (status != VaultStatus.UNLOCKED) {
            final VaultEncryptor encryptor = new VaultEncryptor();
            final EncryptionParameters params = new EncryptionParameters(credentials);
            primarySegment = encryptor.decryptSegment(sealedPrimarySegment, params);
            status = VaultStatus.UNLOCKED;
        }
    }

    public List<String> getFolderNames() {
        final List<String> folderNames;
        if (status == VaultStatus.LOCKED) {
            folderNames = new ArrayList<>();
        } else {
            // TODO actually return folder list
            folderNames = new ArrayList<>();
        }
        return folderNames;
    }

    public VaultStatus getStatus() {
        return status;
    }

    protected void generateSecondarySegments() {

    }

}
