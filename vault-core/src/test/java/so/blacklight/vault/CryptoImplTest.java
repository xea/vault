package so.blacklight.vault;

import fj.data.Either;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class CryptoImplTest {

    private Crypto<String> crypto;
    private List<EncryptionParameters> params;

    @Before
    public void setup() {
        crypto = new CryptoImpl<>();
        params = new ArrayList<>();
    }

    @Test
    public void testSinglePassPasswordEncryption() {
        params.add(new EncryptionParameters(new Password("secret".toCharArray())));
        Either<String, byte[]> encrypted = crypto.encrypt("secret", params);

        assertFalse(encrypted.isLeft());

        Either<String, String> decrypt = crypto.decrypt(encrypted.right().value(), params);

        assertTrue(decrypt.isRight());

        String value = decrypt.right().value();
        assertEquals("secret", value);
    }

    @Test
    public void testMultiPassPasswordEncryption() {
        params.add(new EncryptionParameters(new Password("secret".toCharArray())));
        params.add(new EncryptionParameters(new Password("password".toCharArray())));

        Either<String, byte[]> encrypted = crypto.encrypt("secret", params);
        assertFalse(encrypted.isLeft());

        Collections.reverse(params);
        Either<String, String> decrypt = crypto.decrypt(encrypted.right().value(), params);

        assertTrue(decrypt.isRight());

        String value = decrypt.right().value();
        assertEquals("secret", value);
    }

    @Test
    public void testSinglePassKeyEncryption() {
        params.add(new EncryptionParameters(new PrivateKey("aaaaaaaaaaaaaaaa".getBytes())));

        Either<String, byte[]> encrypted = crypto.encrypt("secret", params);
        assertTrue(encrypted.isRight());

        Either<String, String> decrypted = crypto.decrypt(encrypted.right().value(), params);

        assertTrue(decrypted.isRight());

        String value = decrypted.right().value();
        assertEquals("secret", value);
    }

    @Test
    public void testMultiPassKeyEncryption() {
        params.add(new EncryptionParameters(new PrivateKey("1234567890abcdef".getBytes())));
        params.add(new EncryptionParameters(new PrivateKey("fedcba0987654321".getBytes())));

        Either<String, byte[]> encrypted = crypto.encrypt("secret", params);
        assertTrue(encrypted.isRight());

        assertNotEquals("secret", encrypted.right().value());

        Collections.reverse(params);
        Either<String, String> decrypted = crypto.decrypt(encrypted.right().value(), params);

        assertTrue(decrypted.isRight());
        String value = decrypted.right().value();
        assertEquals("secret", value);
    }
}
