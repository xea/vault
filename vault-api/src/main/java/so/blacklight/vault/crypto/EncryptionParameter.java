package so.blacklight.vault.crypto;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Encapsulates the information necessary to encrypt/decrypt an object.
 *
 * Note: depending on the credential type, salt may not be used during key derivation
 */
public abstract class EncryptionParameter {

    private static final String RANDOM_ALG = "SHA1PRNG";

    abstract byte[] getEncoded();

    abstract Key getKey();

    protected byte[] generateRandomBytes(final int length) {
        final byte[] bytes = new byte[length];
        generateRandomBytes(bytes);
        return bytes;
    }

    protected void generateRandomBytes(final byte[] buffer) {
        SecureRandom random;

        try {
            random = SecureRandom.getInstance(RANDOM_ALG);
        } catch (NoSuchAlgorithmException e) {
            random = new SecureRandom();
        }

        random.nextBytes(buffer);
    }

    public static boolean supports(final byte[] bytes) {
        return false;
    }

}
