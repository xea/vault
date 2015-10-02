package so.blacklight.vault.crypto;

import so.blacklight.vault.VaultSegment;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.spec.KeySpec;
import java.util.Arrays;

/**
 * 
 *
 */
public class VaultEncryptor {

    private static final String SEGMENT_CIPHER = "AES/CBC/PKCS5Padding";
    private static final String KEY_FACTORY = "PBKDF2WithHmacSHA256";
    private static final String KEY_ALGORITHM = "AES";
    private static final String DIGEST_ALGORITHM = "SHA-512";
    private static final int ITERATION_COUNT = 16384;
    private static final int KEY_LENGTH = 256;

    public SealedObject encryptSegment(final VaultSegment segment, final EncryptionParameters params) {
        return encryptSegment(segment, params.getKey(), params.getSalt(), params.getIv());
    }

    public SealedObject encryptSegment(final VaultSegment segment, final String password, final byte[] salt, final byte[] iv) {
        final SealedObject sealedObject;
        try {
            final SecretKey secretKey = generateKey(password, salt);

            final Cipher cipher = Cipher.getInstance(SEGMENT_CIPHER);
            final byte[] digest = MessageDigest.getInstance(DIGEST_ALGORITHM).digest(iv);
            final byte[] ivbytes = Arrays.copyOfRange(digest, 0, 16);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(ivbytes));
            sealedObject = new SealedObject(segment, cipher);
        } catch (final Exception e) {
            throw new RuntimeException("Couldn't encrypt segment: " + e.getMessage());
        }
        return sealedObject;
    }

    public VaultSegment decryptSegment(final SealedObject object, final EncryptionParameters params) throws EncryptionException {
        return decryptSegment(object, params.getKey(), params.getSalt(), params.getIv());
    }

    public VaultSegment decryptSegment(final SealedObject object, final String password, final byte[] salt, final byte[] iv) throws EncryptionException {
        final VaultSegment segment;

        try {
            final SecretKey secretKey = generateKey(password, salt);

            final Cipher cipher = Cipher.getInstance(SEGMENT_CIPHER);
            final byte[] digest = MessageDigest.getInstance(DIGEST_ALGORITHM).digest(iv);
            final byte[] ivbytes = Arrays.copyOfRange(digest, 0, 16);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(ivbytes));

            final Object result = object.getObject(cipher);

            if (result instanceof VaultSegment) {
                segment = (VaultSegment) result;
            } else {
                throw new RuntimeException("Encryptet object wasn't a segment");
            }
        } catch (final IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            throw new EncryptionException();
        } catch (final Exception e) {
            throw new RuntimeException("Couldn't decrypt segment: " + e.getMessage());
        }

        return segment;
    }

    protected SecretKey generateKey(final String password, final byte[] salt) {
        final SecretKey secretKey;
        try {
            final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_FACTORY);
            final KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
            final SecretKey tempKey = keyFactory.generateSecret(keySpec);
            secretKey = new SecretKeySpec(tempKey.getEncoded(), KEY_ALGORITHM);
        } catch (final Exception e) {
            // TODO Replace RuntimeException with a checked exception
            throw new RuntimeException("Couldn't generate secret key: " + e.getMessage());
        }

        return secretKey;
    }
}
