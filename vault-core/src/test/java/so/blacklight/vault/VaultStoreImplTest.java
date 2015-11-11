package so.blacklight.vault;

import fj.data.Either;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Arrays;

import static org.junit.Assert.*;

public class VaultStoreImplTest {

    @Test
    public void savedStreamShouldBeginWithMagicBytes() {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        final Credentials credentials = new Credentials();

        final VaultStore store = new VaultStoreImpl();
        final Vault vault = new Vault();
        store.save(vault, credentials, os);

        assertArrayEquals(VaultStoreImpl.MAGIC_BYTES, Arrays.copyOf(os.toByteArray(), 4));
    }
    
    @Test
    public void ivBytesShouldFollowMagicBytes() {
    	
    }

    @Test
    public void encryptedVaultsShouldBeDecryptable() {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        final Credentials credentials = new Credentials();
        credentials.add(new Password("secretPassword".toCharArray()));
        credentials.add(new PrivateKey("0123456789ABCDEF".getBytes()));

        final VaultStore store = new VaultStoreImpl();
        final Vault vault = new Vault();

        store.save(vault, credentials, os);

        final ByteArrayInputStream bais = new ByteArrayInputStream(os.toByteArray());
        final Either<String, Vault> loaded = store.load(credentials, bais);

        assertTrue(loaded.isRight());
        assertNotNull(loaded.right().value());
    }
}
