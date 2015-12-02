package so.blacklight.vault.entry;

import so.blacklight.vault.crypto.EncryptionParameter;

import javax.crypto.SealedObject;
import java.time.Instant;

/**
 * Secret entries don't hold data by themselves but they contain encrypted entries
 * along with their public parameters.
 */
public class SecretEntry implements RecoverableEntry {

	private static final long serialVersionUID = 84099412848640782L;

	private final Metadata metadata;
    private final String recoveryInfo;
    private final EncryptionParameter param;
    private final SealedObject sealedSecret;

    public SecretEntry(final SealedObject secret, final EncryptionParameter param, final String alias, final String comment, final String recoveryInfo) {
        this.sealedSecret = secret;
        this.recoveryInfo = recoveryInfo;
        this.param = param;
        this.metadata = new Metadata(alias, comment);
    }

    protected SecretEntry(final SealedObject secret, final EncryptionParameter param, final String recoveryInfo, final Metadata metadata) {
        this.sealedSecret = secret;
        this.recoveryInfo = recoveryInfo;
        this.metadata = metadata;
        this.param = param;
    }

    @Override
    public String getRecoveryInfo() {
        return recoveryInfo;
    }

    public SecretEntry setRecoveryInfo(final String recoveryInfo) {
        final Metadata newMetadata = metadata.setModifyTime(Instant.now());
        return new SecretEntry(sealedSecret, param, recoveryInfo, newMetadata);
    }

    @Override
    public Metadata getMetadata() {
        return metadata;
    }

    public SealedObject getSealedSecret() {
        return sealedSecret;
    }

    public EncryptionParameter getEncryptionParameter() {
        return param;
    }
}
