package so.blacklight.vault.entry;

import javax.crypto.SealedObject;
import java.time.Instant;

public class SecretEntry implements RecoverableEntry {

	private static final long serialVersionUID = 84040782L;

	private final Metadata metadata;
    private final String recoveryInfo;
    private final SealedObject sealedSecret;

    public SecretEntry(final SealedObject secret, final String title, final String comment, final String recoveryInfo) {
        this.sealedSecret = secret;
        this.recoveryInfo = recoveryInfo;
        this.metadata = new Metadata(title, comment);
    }

    protected SecretEntry(final SealedObject secret, final String recoveryInfo, final Metadata metadata) {
        this.sealedSecret = secret;
        this.recoveryInfo = recoveryInfo;
        this.metadata = metadata;
    }

    @Override
    public String getRecoveryInfo() {
        return recoveryInfo;
    }

    public SecretEntry setRecoveryInfo(final String recoveryInfo) {
        final Metadata newMetadata = metadata.setModifyTime(Instant.now());
        return new SecretEntry(sealedSecret, recoveryInfo, newMetadata);
    }

    @Override
    public Metadata getMetadata() {
        return metadata;
    }
}