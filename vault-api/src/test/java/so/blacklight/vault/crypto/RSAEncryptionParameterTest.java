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
        final RSAEncryptionParameter privateParam = new RSAEncryptionParameter(kpg.getPrivate());
        final RSAEncryptionParameter publicParam = new RSAEncryptionParameter(kpg.getPublic());
        final byte[] privateEncoded = privateParam.getEncoded();
        final byte[] publicEncoded = publicParam.getEncoded();

        assertNotNull(privateEncoded);
        assertNotNull(publicEncoded);
        assertArrayEquals(RSAEncryptionParameter.PRIVATE_PREFIX, Arrays.copyOf(privateEncoded, RSAEncryptionParameter.PREFIX_LENGTH));
        assertArrayEquals(RSAEncryptionParameter.PUBLIC_PREFIX, Arrays.copyOf(publicEncoded, RSAEncryptionParameter.PREFIX_LENGTH));
    }

    @Test
    public void shouldBeAbleToParseGeneratedEncodedBytes() throws NoSuchAlgorithmException {
        final KeyPair kpg = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        final RSAEncryptionParameter privateParam = new RSAEncryptionParameter(kpg.getPrivate());
        final RSAEncryptionParameter publicParam = new RSAEncryptionParameter(kpg.getPublic());
        final byte[] privateEncoded = privateParam.getEncoded();
        final byte[] publicEncoded = publicParam.getEncoded();

        final RSAEncryptionParameter newPrivate = new RSAEncryptionParameter(privateEncoded);
        final RSAEncryptionParameter newPublic = new RSAEncryptionParameter(publicEncoded);

        assertArrayEquals(privateEncoded, newPrivate.getEncoded());
        assertArrayEquals(publicEncoded, newPublic.getEncoded());
    }
}
