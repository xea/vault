package so.blacklight.vault;

import so.blacklight.vault.crypto.EncryptionException;
import so.blacklight.vault.crypto.EncryptionParameters;
import so.blacklight.vault.crypto.VaultEncryptor;

import javax.crypto.SealedObject;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

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
            try {
                primarySegment = encryptor.decryptSegment(sealedPrimarySegment, params);
                status = VaultStatus.UNLOCKED;
            } catch (final EncryptionException e) {
                // TODO error handling
            }
        }
    }

    public List<String> getFolderNames() {
        final List<String> folderNames = getFolders().stream().map(Folder::getName).collect(Collectors.toList());

        return folderNames;
    }

    protected List<Folder> getFolders() {
        final List<Folder> folders = new CopyOnWriteArrayList<>();

        if (status == VaultStatus.UNLOCKED) {
            folders.addAll(primarySegment.getFolders());
        } else if (status == VaultStatus.DEGRADED) {
            folders.addAll(resolveSecondarySegment().getFolders());
        }

        return folders;
    }

    protected VaultSegment resolveSecondarySegment() {
        return primarySegment;
    }

    public VaultStatus getStatus() {
        return status;
    }

    protected void generateSecondarySegments() {
        if (status == VaultStatus.UNLOCKED) {

        }
    }

    public Folder createFolder(final String folderName) {
        if (status == VaultStatus.UNLOCKED) {
            final Folder folder = new Folder(folderName);

            primarySegment.getFolders().add(folder);

            return folder;
        }

        return null;
    }

    public Optional<Folder> getFolder(final String folderName) {
        if (status == VaultStatus.UNLOCKED) {
            return primarySegment.getFolders().stream().filter(folder -> folder.getName().equals(folderName)).findFirst();
        } else {
            return Optional.empty();
        }
    }


    public Folder deleteFolder(final String folderName) {
        return null;
    }

}
