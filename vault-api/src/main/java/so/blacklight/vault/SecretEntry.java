package so.blacklight.vault;

import so.blacklight.crypto.EncryptionParams;

import javax.crypto.SealedObject;
import java.time.Instant;

public class SecretEntry extends BaseEntry {

    public static final long serialVersionUID = -628232743235L;

    private EncryptionParams params;

    private Class<? extends Entry> secretClass;

    private SealedObject secret;

    private String alias;

    public SecretEntry(final String alias, final SealedObject secret) {
        this(alias, secret, null);
    }

    public SecretEntry(final String alias, final SealedObject secret, final EncryptionParams params) {
        this.alias = alias;
        this.secret = secret;
        this.params = params;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public Instant getCreateTime() {
        return null;
    }

    @Override
    public Instant getModifyTime() {
        return null;
    }

    @Override
    public Instant getAccessTime() {
        return null;
    }

    @Override
    public String getComment() {
        return null;
    }

    public EncryptionParams getParams() {
        return params;
    }

    public SealedObject getSecret() {
        return secret;
    }

    public Class<? extends Entry> getSecretClass() {
        return secretClass;
    }
}
