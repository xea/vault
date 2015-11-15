package so.blacklight.vault.store;

import fj.data.Either;
import org.junit.Test;
import so.blacklight.vault.Credentials;
import so.blacklight.vault.Password;
import so.blacklight.vault.Vault;
import so.blacklight.vault.VaultStore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StreamVaultStoreTest {

    @Test
    public void comleteTestSuite() {
        final VaultStore store = new StreamVaultStore();
        final Vault vault = new Vault();

        final Credentials credentials = new Credentials();
        credentials.add(new Password("secret".toCharArray()));
        credentials.add(new Password("password".toCharArray()));
        credentials.add(new Password("sesame".toCharArray()));

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        store.save(vault, credentials, out);

        final ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        final Either<String, Vault> load = store.load(credentials, in);
        assertTrue(load.isRight());

        final Vault loadedVault = load.right().value();
        assertEquals(vault.getUuid(), loadedVault.getUuid());
    }
}
