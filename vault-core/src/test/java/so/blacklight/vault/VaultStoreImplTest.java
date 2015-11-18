package so.blacklight.vault;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.junit.Test;

import fj.data.Either;

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
        credentials.add(new Password("otherPassword".toCharArray()));
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
