package so.blacklight.vault.crypto;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EncryptionParameterTest {

    @Test
    public void aRandomIVandSaltIsGeneratedWhenNotPassingThemExplicitly() {
        final EncryptionParameter ep = new EncryptionParameter(new Password("test".toCharArray()));
        assertNotNull(ep.getIv());
        assertEquals(16, ep.getIv().length);
    }
}
