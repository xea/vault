package so.blacklight.vault.crypto;

import so.blacklight.vault.Credentials;

public class EncryptionParameters {

    private final transient String compositeKey;

    public EncryptionParameters(final Credentials credentials) {
        this.compositeKey = credentials.toString();
    }

    public String getKey() {
        return compositeKey;
    }

    public byte[] getSalt() {
        return compositeKey.getBytes();
    }

    public byte[] getIv() {
        return compositeKey.getBytes();

    }
}
