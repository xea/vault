package so.blacklight.vault.crypto;

import fj.data.Either;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DefaultCryptoImplTest {

    @Test
    public void encryptedObjectsShouldBeDecryptable() {
        final Crypto<String> crypto = new DefaultCryptoImpl<>();
        final EncryptionParameter encparam = new AESEncryptionParameter(new Password("PASSWORD"));

        Either<String, byte[]> encrypted = crypto.encrypt("SECRET", encparam);

        assertTrue(encrypted.isRight());

        Either<String, String> decrypted = crypto.decrypt(encrypted.right().value(), encparam);

        assertTrue(decrypted.isRight());
        assertEquals("SECRET", decrypted.right().value());
    }

}
