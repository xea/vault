package so.blacklight.vault.crypto;

import fj.data.Either;
import org.junit.Before;
import org.junit.Test;
import so.blacklight.vault.crypto.*;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class CryptoImplTest {

    private Crypto<String> crypto;
    private List<EncryptionParameter> params;
    private KeyPair keyPair;

    @Before
    public void setup() throws NoSuchAlgorithmException {
        crypto = new CryptoImpl<>();
        params = new ArrayList<>();
        final KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(1024);
        keyPair = kpg.generateKeyPair();
    }

    @Test
    public void testSinglePassPasswordEncryption() {
        params.add(new EncryptionParameter(new Password("secret".toCharArray())));
        Either<String, byte[]> encrypted = crypto.encrypt("secret", params);

        assertFalse(encrypted.isLeft());

        Either<String, String> decrypt = crypto.decrypt(encrypted.right().value(), params);

        assertTrue(decrypt.isRight());

        String value = decrypt.right().value();
        assertEquals("secret", value);
    }

    @Test
    public void testMultiPassPasswordEncryption() {
        params.add(new EncryptionParameter(new Password("secret".toCharArray())));
        params.add(new EncryptionParameter(new Password("password".toCharArray())));

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
        params.add(new EncryptionParameter(new AESKey("aaaaaaaaaaaaaaaa".getBytes())));

        Either<String, byte[]> encrypted = crypto.encrypt("secret", params);
        assertTrue(encrypted.isRight());

        Either<String, String> decrypted = crypto.decrypt(encrypted.right().value(), params);

        assertTrue(decrypted.isRight());

        String value = decrypted.right().value();
        assertEquals("secret", value);
    }

    @Test
    public void testMultiPassKeyEncryption() {
        params.add(new EncryptionParameter(new AESKey("1234567890abcdef".getBytes())));
        params.add(new EncryptionParameter(new AESKey("fedcba0987654321".getBytes())));

        Either<String, byte[]> encrypted = crypto.encrypt("secret", params);
        assertTrue(encrypted.isRight());

        assertNotEquals("secret", encrypted.right().value());

        Collections.reverse(params);
        Either<String, String> decrypted = crypto.decrypt(encrypted.right().value(), params);

        assertTrue(decrypted.isRight());
        String value = decrypted.right().value();
        assertEquals("secret", value);
    }

    @Test
    public void testRSAEncryptionWithPublicKey() throws NoSuchAlgorithmException {
        params.add(new EncryptionParameter(new RSAPublicKey(keyPair.getPublic().getEncoded())));

        Either<String, byte[]> encrypted = crypto.encrypt("secret", params);

        assertNotNull(encrypted);
        assertTrue(encrypted.isRight());

        assertNotEquals("secret", encrypted.right().value());
    }

    @Test
    public void testRSAEncryptionWithPrivateKey() throws NoSuchAlgorithmException {
        params.add(new EncryptionParameter(new RSAPrivateKey(keyPair.getPrivate().getEncoded())));

        Either<String, byte[]> encrypted = crypto.encrypt("secret", params);

        assertNotNull(encrypted);
        assertTrue(encrypted.isRight());

        assertNotEquals("secret", encrypted.right().value());
    }
    @Test
    public void testRSADecryption() throws NoSuchAlgorithmException {
        params.add(new EncryptionParameter(new RSAPublicKey(keyPair.getPublic().getEncoded())));

        Either<String, byte[]> encrypted = crypto.encrypt("secret", params);
        assertNotNull(encrypted);
        assertTrue(encrypted.isRight());
        assertNotEquals("secret", encrypted.right().value());

        params.clear();

        params.add(new EncryptionParameter(new RSAPrivateKey(keyPair.getPrivate().getEncoded())));

        Either<String, String> decrypted = crypto.decrypt(encrypted.right().value(), params);
        assertNotNull(decrypted);
        assertTrue(decrypted.isRight());
        assertEquals("secret", decrypted.right().value());
    }

    @Test
    public void testRSAAndAESEncryption() throws NoSuchAlgorithmException {
        params.add(new EncryptionParameter(new RSAPrivateKey(keyPair.getPrivate().getEncoded())));
        params.add(new EncryptionParameter(new Password("Password1".toCharArray())));

        Either<String, byte[]> encrypted = crypto.encrypt("secret", params);
        assertNotNull(encrypted);
        assertTrue(encrypted.isRight());
        assertNotEquals("secret", encrypted.right().value());
    }
}
