package so.blacklight.vault.crypto;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class AESEncryptionParameterTest {

    @BeforeClass
    public static void setup() {
        // degrading N in order to improve test performance
        AESEncryptionParameter.N = 1024;
    }

    @Test
    public void generatedObjectShouldHavePrefixEncoded() {
        final EncryptionParameter param = new AESEncryptionParameter("password".toCharArray());
        assertNotNull(param.getEncoded());
        assertTrue(param.getEncoded().length > AESEncryptionParameter.PREFIX.length);
        assertArrayEquals(Arrays.copyOf(param.getEncoded(), AESEncryptionParameter.PREFIX.length), AESEncryptionParameter.PREFIX);
    }

    @Test
    public void supportsSelfGeneratedEncodedBytes() {
        final EncryptionParameter param = new AESEncryptionParameter("password".toCharArray());
        assertTrue(AESEncryptionParameter.supports(param.getEncoded()));
    }

    @Test
    public void canParseSelfGeneratedEncodedBytes() {
        final AESEncryptionParameter paramOld = new AESEncryptionParameter("password".toCharArray());
        final byte[] encoded = paramOld.getEncoded();

        final AESEncryptionParameter paramNew = new AESEncryptionParameter(new Password("password"), encoded);

        assertArrayEquals(paramOld.getIv(), paramNew.getIv());
        assertArrayEquals(paramOld.getSalt(), paramNew.getSalt());
    }

    @Test
    public void generatesRandomValueWhenArgumentsAreMissing() {
        final AESEncryptionParameter param = new AESEncryptionParameter(new Password("password"));
        assertNotNull(param.getIv());
        assertNotNull(param.getSalt());
        assertNotNull(param.getKey());
    }
}
