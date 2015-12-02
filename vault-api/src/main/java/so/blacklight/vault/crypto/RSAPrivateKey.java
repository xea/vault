package so.blacklight.vault.crypto;

import so.blacklight.vault.Credential;

public class RSAPrivateKey implements Credential {

    private final byte[] bytes;

    public RSAPrivateKey(final byte[] bytes) {
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
