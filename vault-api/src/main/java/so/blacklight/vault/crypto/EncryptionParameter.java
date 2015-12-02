package so.blacklight.vault.crypto;

import com.lambdaworks.crypto.SCrypt;
import so.blacklight.vault.Credential;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * Encapsulates the information necessary to encrypt/decrypt an object.
 *
 * Note: depending on the credential type, salt may not be used during key derivation
 */
public class EncryptionParameter {

    private static final String CRYPTO_ALG = "AES";
    private static final String RANDOM_ALG = "SHA1PRNG";
    public static final int IV_LENGTH = 16;
    public static final int SALT_LENGTH = 16;

    // N holds the number of rounds for password-based key derivation
    public static int N = 16384;

    private byte[] iv;

    private byte[] salt;

    private Key key;

    /**
     * Initialise a new parameter object with the given credentials and random
     * IV and random salt.
     *
     * @param credentials credentials
     */
    public EncryptionParameter(final Credential credentials) {
        iv = generateRandom(IV_LENGTH);
        salt = generateRandom(SALT_LENGTH);
        key = generateKey(credentials, salt);
    }

    /**
     * Initialise a new parameter object with the given credentials, IV and salt.
     *
     * @param credentials credentials
     * @param iv initialising vector
     * @param salt password salt
     */
    public EncryptionParameter(final Credential credentials, final byte[] iv, final byte[] salt) {
        this.iv = iv;
        this.salt = salt;
        this.key = generateKey(credentials, salt);
    }

    /**
     * Initialise a new parameter object with the given secret key bytes. This
     * constructor should be typically used when loading a raw encryption key from
     * a file.
     *
     * An additional IV and salt will be generated randomly.
     *
     * @param key secret key bytes
     */
    public EncryptionParameter(final byte[] key) {
        this.key = new SecretKeySpec(key, CRYPTO_ALG);
        this.salt = generateRandom(SALT_LENGTH);
        this.iv = generateRandom(IV_LENGTH);
    }

    /**
     * Initialise a new parameter object with the given secret key bytes,
     * initialisation vector and salt. This constructor should typically be used
     * during decryption with a raw decryption key.
     *
     * @param key secret key bytes
     * @param iv initialisation vector bytes
     * @param salt salt bytes
     */
    public EncryptionParameter(final byte[] key, final byte[] iv, final byte[] salt) {
        this.key = new SecretKeySpec(key, CRYPTO_ALG);
        this.salt = salt;
        this.iv = iv;
    }

    /**
     * Initialise a new parameter  object with the given password characters.
     * Additional initialisation vector and salt will be generated randomly.
     * This constructor should be typically used during encryption, with a user-supplied
     * password.
     *
     * @param password password characters
     */
    public EncryptionParameter(final char[] password) {
        iv = generateRandom(IV_LENGTH);
        salt = generateRandom(SALT_LENGTH);
        key = new SecretKeySpec(deriveKey(password, salt), CRYPTO_ALG);
    }

    /**
     * Initialise a new parameter object with the given password characters, initialisation
     * vector and salt. This constructor should be typically used during decryption, with
     * a user-supplied password.
     *
     * @param password password characters
     * @param iv initialisation vector bytes
     * @param salt salt bytes
     */
    public EncryptionParameter(final char[] password, final byte[] iv, final byte[] salt) {
        this.iv = iv;
        this.salt = salt;
        key = new SecretKeySpec(deriveKey(password, salt), CRYPTO_ALG);
    }

    public EncryptionParameter(final Key key) {
        this.key = key;
        iv = generateRandom(IV_LENGTH);
        salt = generateRandom(SALT_LENGTH);
    }

    /**
     * Return the initialisation vector used with this encryption key
     *
     * @return initialisation vector
     */
    public byte[] getIv() {
        return iv;
    }

    /**
     * Return the salt used with this encryption key.
     *
     * @return salt
     */
    public byte[] getSalt() {
        return salt;
    }

    /**
     * Secret key used for encryption/decryption
     *
     * @return secret key
     */
    public Key getKey() {
        return key;
    }

    private byte[] deriveKey(final char[] password, final byte[] salt) {
        final byte[] bytes = new String(password).getBytes(StandardCharsets.UTF_8);

        return deriveKey(bytes, salt);
    }

    private byte[] deriveKey(final byte[] password, final byte[] salt) {
        int r = 8;
        int p = 1;
        int derivedLength = 16;

        try {
            byte[] derived = SCrypt.scrypt(password, salt, N, r, p, derivedLength);

            return derived;
        } catch (GeneralSecurityException e) {
            //throw new IllegalStateException("JVM doesn\'t support SHA1PRNG or HMAC_SHA256?");
        }

        return null;
    }

    private byte[] generateRandom(final int length) {
        final byte[] bytes = new byte[length];
        generateRandom(bytes);
        return bytes;
    }


    private void generateRandom(final byte[] bytes) {
        SecureRandom random;

        try {
            random = SecureRandom.getInstance(RANDOM_ALG);
        } catch (NoSuchAlgorithmException e) {
            random = new SecureRandom();
        }

        random.nextBytes(bytes);
    }

    private Key generateKey(Credential credentials, byte[] salt) {
        final Key secretKey;

        if (credentials.isUserInput()) {
            secretKey = new SecretKeySpec(deriveKey(credentials.getBytes(), salt), CRYPTO_ALG);
        } else if (credentials instanceof RSAPrivateKey) {
            secretKey = new SecretKeySpec(credentials.getBytes(), "RSA");
        } else if (credentials instanceof RSAPublicKey) {
            secretKey = new SecretKeySpec(credentials.getBytes(), "RSA");
        } else {
            secretKey = new SecretKeySpec(credentials.getBytes(), CRYPTO_ALG);
        }

        return secretKey;
    }
}
