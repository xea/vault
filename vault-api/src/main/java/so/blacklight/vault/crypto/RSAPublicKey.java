package so.blacklight.vault.crypto;

import so.blacklight.vault.Credential;

public class RSAPublicKey implements Credential {

    private final byte[] bytes;

    public RSAPublicKey(final byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public boolean isUserInput() {
        return false;
    }

    @Override
    public byte[] getBytes() {
        return bytes;
    }
}
