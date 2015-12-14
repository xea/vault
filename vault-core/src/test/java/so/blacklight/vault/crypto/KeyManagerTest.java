package so.blacklight.vault.crypto;

import fj.data.Either;
import org.junit.Before;
import org.junit.Test;
import so.blacklight.vault.collection.Tuple2;

import java.io.File;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import static org.junit.Assert.*;

public class KeyManagerTest {

    KeyManager keyManager;

    @Before
    public void setup() {
        keyManager = new KeyManager();
    }

    @Test
    public void shouldGenerateValidAESKeys() {
        fail("Not implemented");
    }

    @Test
    public void shouldGenerateValidRSAKeyPair() throws NoSuchAlgorithmException {
        final Tuple2<RSAPrivateKey, RSAPublicKey> keyPair = keyManager.generateRSAKeyPair(1024);
        final Crypto<String> crypto = new CryptoImpl<>();

        Either<String, byte[]> secret = crypto.encrypt("secret", new EncryptionParameter(keyPair.second()));

        assertNotNull(secret);
        assertTrue(secret.isRight());

        Either<String, String> decrypted = crypto.decrypt(secret.right().value(), new EncryptionParameter(keyPair.first()));

        assertNotNull(decrypted);
        assertTrue(decrypted.isRight());
        assertEquals("secret", decrypted.right().value());
    }

    @Test
    public void canLoadPK8PrivateKeysWithHeaders() {
        fail("Not implemented");
    }

    @Test
    public void canLoadPK8PrivateKeysWithoutHeaders() {
        fail("Not implemented");
    }

    @Test
    public void canLoadDERPublicKeys() {
        fail("Not implemented");
    }

    @Test
    public void canLoadBase64EncodedAESKeys() {
        fail("Not implemented");
    }

    @Test
    public void canDetectKeyFileFormat() {
        fail("Not implemented");
    }

    @Test
    public void rsaPrivateKeysAreSavedInPKCS8Format() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        final Tuple2<RSAPrivateKey, RSAPublicKey> pair = keyManager.generateRSAKeyPair(1024);
        final File privateTmp = File.createTempFile("junit", "private-vlt");
        keyManager.saveRSAPrivateKey(pair.first(), privateTmp);

        assertTrue(privateTmp.exists());
        assertTrue(privateTmp.length() > 0);

        final RSAPrivateKey privateKey = keyManager.loadRSAPrivateKey(privateTmp);
        assertNotNull(privateKey);

        final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        final PrivateKey rsaPrivateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKey.getBytes()));

        assertNotNull(rsaPrivateKey);
        assertEquals("RSA", rsaPrivateKey.getAlgorithm());
        assertEquals("PKCS#8", rsaPrivateKey.getFormat());

        final Crypto<String> crypto = new CryptoImpl<>();
        Either<String, byte[]> secret = crypto.encrypt("secret", new EncryptionParameter(keyManager.loadRSAPrivateKey(privateTmp)));

        assertNotNull(secret);
        assertTrue(secret.isRight());

        Either<String, String> decrypt = crypto.decrypt(secret.right().value(), new EncryptionParameter(privateKey));

        assertNotNull(decrypt);
        assertTrue(decrypt.isRight());
        assertEquals("secret", decrypt.right().value());
    }

}
