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
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import static org.junit.Assert.*;

public class KeyManagerTest {

    KeyManager keyManager;

    @Before
    public void setup() {
        keyManager = new KeyManager();
    }

    @Test
    public void shouldGenerateValidAESKeys() throws NoSuchAlgorithmException {
        final AESKey aesKey = keyManager.generateAESKey(256);

        assertNotNull(aesKey);
        assertNotNull(aesKey.getBytes());
        assertEquals(32, aesKey.getBytes().length);
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
    }

    @Test
    public void rsaPublicKeysAreSavedInDERFormat() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        final Tuple2<RSAPrivateKey, RSAPublicKey> pair = keyManager.generateRSAKeyPair(1024);
        final File publicTmp = File.createTempFile("junit", "public-vlt");
        keyManager.saveRSAPublicKey(pair.second(), publicTmp);

        assertTrue(publicTmp.exists());
        assertTrue(publicTmp.length() > 0);

        final RSAPublicKey publicKey = keyManager.loadRSAPublicKey(publicTmp);
        assertNotNull(publicKey);

        final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        final PublicKey rsaPublicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKey.getBytes()));

        assertNotNull(rsaPublicKey);
        assertEquals("RSA", rsaPublicKey.getAlgorithm());
        assertEquals("X.509", rsaPublicKey.getFormat());
    }

    @Test
    public void generatedKeysShouldBeUsableAfterSavingAndLoading() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        final Tuple2<RSAPrivateKey, RSAPublicKey> pair = keyManager.generateRSAKeyPair(1024);
        final File privateTmp = File.createTempFile("junit", "private-vlt");
        final File publicTmp = File.createTempFile("junit", "public-vlt");

        keyManager.saveRSAPrivateKey(pair.first(), privateTmp);
        keyManager.saveRSAPublicKey(pair.second(), publicTmp);

        assertTrue(privateTmp.exists());
        assertTrue(privateTmp.length() > 0);
        assertTrue(publicTmp.exists());
        assertTrue(publicTmp.length() > 0);

        final RSAPrivateKey privateKey = keyManager.loadRSAPrivateKey(privateTmp);
        final RSAPublicKey publicKey = keyManager.loadRSAPublicKey(publicTmp);

        final Crypto<String> crypto = new CryptoImpl<>();
        final Either<String, byte[]> secret = crypto.encrypt("secret", new EncryptionParameter(publicKey));

        assertNotNull(secret);
        assertTrue(secret.isRight());
        assertNotEquals("secret", secret.right().value());

        Either<String, String> decrypt = crypto.decrypt(secret.right().value(), new EncryptionParameter(privateKey));

        assertNotNull(decrypt);
        assertTrue(decrypt.isRight());
        assertEquals("secret", decrypt.right().value());

    }
}
