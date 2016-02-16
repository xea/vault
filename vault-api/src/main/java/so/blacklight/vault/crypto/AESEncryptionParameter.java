package so.blacklight.vault.crypto;

import com.lambdaworks.crypto.SCrypt;

import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.Arrays;

/**
 * Encryption parameter for AES ciphers.
 *
 * Supports both static keys and user pass phrases. In the case of user pass phrases
 * it uses the SCrypt key derivation algorithm.
 */
public class AESEncryptionParameter extends EncryptionParameter {

    public static final byte[] PREFIX = "A:".getBytes();
    private static final int DEFAULT_N = 16384;

    private static final String CRYPTO_ALG = "AES";
    private static final int IV_LENGTH = 16;
    private static final int SALT_LENGTH = 16;
    private static final int KEY_LENGTH = 16;

    /**
     * The number of hashing iterations it will perform during key derivation.
     *
     * Making this property mutable allows client code override this but also provide a reasonable
     * default setting.
     */
    public static int N = DEFAULT_N;

    private final byte[] iv;

    private final byte[] salt;

    private final Key key;

    public AESEncryptionParameter() {
        iv = generateRandomBytes(IV_LENGTH);
        salt = generateRandomBytes(SALT_LENGTH);
        key = generateKey(generateRandomBytes(KEY_LENGTH));
    }

    /**
     * Initialise a new parameter object with the given credential and random
     * IV and random salt.
     *
     * @param credential credential
     */
    public AESEncryptionParameter(final Credential credential) {
        iv = generateRandomBytes(IV_LENGTH);
        salt = generateRandomBytes(SALT_LENGTH);
        key = generateKey(credential);
    }

    /**
     * Initialise a new parameter object with the given credential, IV and salt.
     *
     * @param credential credential
     * @param iv initialising vector
     * @param salt password salt
     */
    public AESEncryptionParameter(final Credential credential, final byte[] iv, final byte[] salt) {
        this.iv = iv;
        this.salt = salt;
        key = generateKey(credential);
    }

    /**
     * Create a new AES encryption parameter using the key bytes only.
     *
     * This constructor should be typically used the encryption parameter is generated from a user input.
     *
     * @param passPhrase pass phrase
     */
    public AESEncryptionParameter(final char[] passPhrase) {
        iv = generateRandomBytes(IV_LENGTH);
        salt = generateRandomBytes(SALT_LENGTH);
        key = generateKey(passPhrase, salt);
    }

    public AESEncryptionParameter(final Credential credential, final byte[] paramBytes) throws IllegalArgumentException {
        if (paramBytes == null || paramBytes.length < PREFIX.length) {
            throw new IllegalArgumentException();
        }
        final ByteArrayInputStream bais = new ByteArrayInputStream(paramBytes);
        final DataInputStream dis = new DataInputStream(bais);

        try {
            final byte[] prefixBuffer = new byte[PREFIX.length];
            final byte[] ivBuffer = new byte[IV_LENGTH];
            final byte[] saltBuffer = new byte[SALT_LENGTH];

            dis.read(prefixBuffer);
            dis.read(ivBuffer);
            dis.read(saltBuffer);

            if (Arrays.equals(prefixBuffer, PREFIX)) {
                iv = ivBuffer;
                salt = saltBuffer;

                key = generateKey(credential);
            } else {
                throw new IllegalArgumentException();
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }

        // note: there's no need to close ByteArrayInputStreams
    }

    /**
     * Initialise a new parameter object with the given password characters, initialisation
     * vector and salt. This constructor should be typically used during decryption, with
     * a user-supplied password.
     *
     * @param passPhrase passphrase characters
     * @param iv initialisation vector bytes
     * @param salt salt bytes
     */
    public AESEncryptionParameter(final char[] passPhrase, final byte[] iv, final byte[] salt) {
        this.iv = iv;
        this.salt = salt;
        key = generateKey(passPhrase, salt);
    }

    /**
     * Initialise a new parameter object with the given secret key bytes,
     * initialisation vector and salt. This constructor should typically be used
     * during decryption with a raw decryption key.
     *
     * @param keyBytes secret key bytes
     * @param iv initialisation vector bytes
     * @param salt salt bytes
     */
    public AESEncryptionParameter(final byte[] keyBytes, final byte[] iv, final byte[] salt) {
        this.iv = iv;
        this.salt = salt;
        key = generateKey(keyBytes);
    }

    @Override
    public byte[] getEncoded() {
        final int bufferLength = PREFIX.length + iv.length + salt.length + key.getEncoded().length;
        final ByteBuffer buffer = ByteBuffer.allocate(bufferLength);

        buffer.put(PREFIX);
        buffer.put(iv);
        buffer.put(salt);
        buffer.put(key.getEncoded());

        return buffer.array();
    }

    @Override
    public Key getKey() {
        return key;
    }

    public byte[] getIv() {
        return iv;
    }

    public byte[] getSalt() {
        return salt;
    }

    protected Key generateKey(final char[] passphrase, final byte[] salt) {
        return new SecretKeySpec(deriveKey(passphrase, salt), CRYPTO_ALG);
    }

    protected Key generateKey(final byte[] keyBytes) {
        return new SecretKeySpec(keyBytes, CRYPTO_ALG);
    }

    protected Key generateKey(final Credential credential) {
        final Key newKey;

        if (credential.isUserInput()) {
            newKey = generateKey(deriveKey(credential.getBytes(), salt));
        } else {
            newKey = new SecretKeySpec(credential.getBytes(), CRYPTO_ALG);
        }

        return newKey;
    }

    private byte[] deriveKey(final char[] password, final byte[] salt) {
        final byte[] bytes = new String(password).getBytes(StandardCharsets.UTF_8);

        return deriveKey(bytes, salt);
    }

    private byte[] deriveKey(final byte[] password, final byte[] salt) {
        int r = 8;
        int p = 1;
        int derivedLength = KEY_LENGTH;

        try {
            byte[] derived = SCrypt.scrypt(password, salt, N, r, p, derivedLength);

            return derived;
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("JVM doesn\'t support SHA1PRNG or HMAC_SHA256?");
        }
    }

    /**
     * Indicate whether the given bytes represent a valid encoded AES key
     *
     * @param encodedBytes input bytes
     * @return <code>true</code> if the input bytes are a valid AES key, otherwise <code>false</code>
     */
    public static boolean supports(byte[] encodedBytes) {
        boolean result = false;

        if (encodedBytes != null && encodedBytes.length > PREFIX.length) {
            if (Arrays.equals(Arrays.copyOf(encodedBytes, PREFIX.length), PREFIX)) {
                result = true;
            }
        }

        return result;
    }

}
