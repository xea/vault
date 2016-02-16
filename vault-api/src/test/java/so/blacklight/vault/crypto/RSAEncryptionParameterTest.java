package so.blacklight.vault.crypto;

import org.junit.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RSAEncryptionParameterTest {

    @Test
    public void encodedBytesShouldHaveAPrefix() throws NoSuchAlgorithmException {
        final KeyPair kpg = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        final RSAEncryptionParameter param = new RSAEncryptionParameter(kpg.getPrivate());
        final byte[] encoded = param.getEncoded();

        assertNotNull(encoded);
        assertArrayEquals(RSAEncryptionParameter.PRIVATE_PREFIX, Arrays.copyOf(encoded, RSAEncryptionParameter.PRIVATE_PREFIX.length));
    }

    @Test
    public void shouldBeAbleToParseGeneratedEncodedBytes() throws NoSuchAlgorithmException {
        final KeyPair kpg = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        final RSAEncryptionParameter param = new RSAEncryptionParameter(kpg.getPrivate());
        final byte[] encoded = param.getEncoded();

        final RSAEncryptionParameter paramNew = new RSAEncryptionParameter(encoded);

        assertArrayEquals(encoded, paramNew.getEncoded());
    }
}
