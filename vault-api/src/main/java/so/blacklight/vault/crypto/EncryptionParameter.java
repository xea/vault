package so.blacklight.vault.crypto;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
/*
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.spec.SecretKeySpec;

import com.lambdaworks.crypto.SCrypt;

import so.blacklight.vault.Credential;
*/


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
