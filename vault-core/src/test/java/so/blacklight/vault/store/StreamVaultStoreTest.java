package so.blacklight.vault.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import fj.data.Either;
import so.blacklight.vault.*;
import so.blacklight.vault.crypto.EncryptionParameter;
import so.blacklight.vault.crypto.Password;

public class StreamVaultStoreTest {

    @BeforeClass
    public static void setup() {
        EncryptionParameter.N = 1024;
    }

    @Test
    public void savedVaultsShouldBeLoadable() {
        final VaultStore store = new StreamVaultStore();
        final VaultSettings settings = new VaultSettings(true, true);
        final Vault vault = new Vault(settings);

        final Credentials credentials = new Credentials();
        credentials.add(new Password("secret".toCharArray()));
        credentials.add(new Password("password".toCharArray()));
        credentials.add(new Password("sesame".toCharArray()));
        credentials.add(new Password("mushroom".toCharArray()));

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        store.save(vault, credentials, out);

        final ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        final Either<String, Vault> load = store.load(credentials, in);
        assertTrue(load.isRight());

        final Vault loadedVault = load.right().value();
        assertEquals(vault.getUuid(), loadedVault.getUuid());
    }
}
