package so.blacklight.vault;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class VaultStoreImplTest {

    public void savedStreamShouldBeginWithMagicBytes() {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        final Credentials credentials = new Credentials();

        final VaultStore store = new VaultStoreImpl();
        final Vault vault = new Vault();
        store.save(vault, credentials, os);

        assertEquals(VaultStoreImpl.MAGIC_BYTES, Arrays.copyOf(os.toByteArray(), 4));
    }
}
