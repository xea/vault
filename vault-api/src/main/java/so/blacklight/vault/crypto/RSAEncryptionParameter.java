package so.blacklight.vault.crypto;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

/**
 * Encryption parameter for RSA ciphers.
 *
 * This implementation default to using 4096 bit keys because it favours
 * security over performance.
 */
public class RSAEncryptionParameter extends EncryptionParameter {

    protected static final byte[] PRIVATE_PREFIX = "R:".getBytes();
    protected static final byte[] PUBLIC_PREFIX = "P:".getBytes();
    protected static final int PREFIX_LENGTH = 2;
    private static final String CRYPTO_ALG = "RSA";

    private final Key key;

    public RSAEncryptionParameter(final Credential credential) {
        try {
            if (credential instanceof RSAPrivateKey) {
                key = KeyFactory.getInstance(CRYPTO_ALG).generatePrivate(new PKCS8EncodedKeySpec(credential.getBytes()));
            } else if (credential instanceof RSAPublicKey) {
                key = KeyFactory.getInstance(CRYPTO_ALG).generatePublic(new X509EncodedKeySpec(credential.getBytes()));
            } else {
                throw new IllegalArgumentException();
            }
        } catch (final Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public RSAEncryptionParameter(final PrivateKey privateKey) {
        key = privateKey;
    }

    public RSAEncryptionParameter(final PublicKey publicKey) {
        key = publicKey;
    }

    /**
     * Construct a new RSA Encryption parameter from a previously encoded byte array.
     * The byte array should have either a private or public key prefix.
     *
     * @param keyBytes encoded key bytes
     */
    public RSAEncryptionParameter(final byte[] keyBytes) {
        assert PRIVATE_PREFIX.length == PUBLIC_PREFIX.length : "Private and Public keys should have prefixes of the same length";

        if (keyBytes == null || keyBytes.length < PREFIX_LENGTH) {
            throw new IllegalArgumentException();
        }

        final InputStream is = new ByteArrayInputStream(keyBytes);
        final DataInputStream dis = new DataInputStream(is);

        try {

            final byte[] prefixBuffer = new byte[PREFIX_LENGTH];
            final byte[] keyBuffer = new byte[keyBytes.length - PREFIX_LENGTH];

            dis.read(prefixBuffer);
            dis.read(keyBuffer);

            if (Arrays.equals(prefixBuffer, PUBLIC_PREFIX)) {
                key = KeyFactory.getInstance(CRYPTO_ALG).generatePublic(new X509EncodedKeySpec(keyBuffer));
            } else if (Arrays.equals(prefixBuffer, PRIVATE_PREFIX)) {
                key = KeyFactory.getInstance(CRYPTO_ALG).generatePrivate(new PKCS8EncodedKeySpec(keyBuffer));
            } else {
                throw new IllegalArgumentException();
            }
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException();
        } catch (IOException e) {
            throw new IllegalArgumentException();
        } catch (InvalidKeySpecException e) {
            throw new IllegalArgumentException();
        }
    }


    @Override
    public byte[] getEncoded() {
        final int bufferLength = PREFIX_LENGTH + key.getEncoded().length;
        final ByteBuffer buffer = ByteBuffer.allocate(bufferLength);


        if (key instanceof PublicKey) {
            buffer.put(PUBLIC_PREFIX);
        } else if (key instanceof PrivateKey) {
            buffer.put(PRIVATE_PREFIX);
        }

        buffer.put(key.getEncoded());

        return buffer.array();
    }

    @Override
    public Key getKey() {
        return key;
    }


    /**
     * Indicate whether the given bytes represent a valid encoded RSA key
     *
     * @param encodedBytes input bytes
     * @return <code>true</code> if the input bytes are a valid RSA key, otherwise <code>false</code>
     */
    public static boolean supports(byte[] encodedBytes) {
        boolean result = false;

        if (encodedBytes != null && encodedBytes.length > PREFIX_LENGTH) {
            if (Arrays.equals(Arrays.copyOf(encodedBytes, PREFIX_LENGTH), PRIVATE_PREFIX)) {
                result = true;
            } else if (Arrays.equals(Arrays.copyOf(encodedBytes, PREFIX_LENGTH), PUBLIC_PREFIX)) {
                result = true;
            }
        }

        return result;
    }

}
