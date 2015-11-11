package so.blacklight.vault;

import com.lambdaworks.crypto.SCrypt;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class EncryptionParameters {

    private static final String CRYPTO_ALG = "AES";

    private byte[] iv;

    private byte[] salt;

    private SecretKey key;

    public EncryptionParameters(final Credential credentials) {
        salt = generateRandom(16);
        iv = generateRandom(16);
        key = generateKey(credentials, salt);

    }

    public EncryptionParameters(final Credential credentials, final byte[] iv, final byte[] salt) {
        this.iv = iv;
        this.salt = salt;
        this.key = generateKey(credentials, salt);
    }

    public EncryptionParameters(final byte[] key) {
        this.key = new SecretKeySpec(key, CRYPTO_ALG);
        this.salt = generateRandom(16);
        this.iv = generateRandom(16);
    }

    public EncryptionParameters(final byte[] key, final byte[] iv, final byte[] salt) {
        this.key = new SecretKeySpec(key, CRYPTO_ALG);
        this.salt = salt;
        this.iv = iv;
    }

    public EncryptionParameters(final char[] password) {
        salt = generateRandom(16);
        iv = generateRandom(16);
        key = new SecretKeySpec(deriveKey(password, salt), CRYPTO_ALG);
    }

    public EncryptionParameters(final char[] password, final byte[] iv, final byte[] salt) {
        this.iv = iv;
        this.salt = salt;
        key = new SecretKeySpec(deriveKey(password, salt), CRYPTO_ALG);
    }

    public byte[] getIv() {
        return iv;
    }

    public byte[] getSalt() {
        return salt;
    }

    public SecretKey getKey() {
        return key;
    }

    private byte[] deriveKey(final char[] password, final byte[] salt) {
        final byte[] bytes = new String(password).getBytes(StandardCharsets.UTF_8);

        return deriveKey(bytes, salt);
    }

    private byte[] deriveKey(final byte[] password, final byte[] salt) {
        int N = 16384;
        int r = 8;
        int p = 1;
        int derivedLength = 16;

        try {
            byte[] derived = SCrypt.scrypt(password, salt, N, r, p, derivedLength);

            return derived;
        } catch (GeneralSecurityException e) {
            // TODO Throw shit
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
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            random = new SecureRandom();
        }

        random.nextBytes(bytes);
    }

    private SecretKey generateKey(Credential credentials, byte[] salt) {
        final SecretKey tkey;

        if (credentials.isUserInput()) {
            tkey = new SecretKeySpec(deriveKey(credentials.getBytes(), salt), CRYPTO_ALG);
        } else {
            tkey = new SecretKeySpec(credentials.getBytes(), CRYPTO_ALG);
        }

        return tkey;
    }
}
