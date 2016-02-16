package so.blacklight.vault.crypto;

/**
 * Represents an encryption key used with AES ciphers.
 */
public class AESKey implements Credential {

    private byte[] key;

    public AESKey(final byte[] key) {
        this.key = key;
    }

    @Override
    public boolean isUserInput() {
        return false;
    }

    @Override
    public byte[] getBytes() {
        return key;
    }
}
