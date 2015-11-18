package so.blacklight.vault.store;

import fj.data.Either;
import org.junit.Test;
import so.blacklight.vault.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StreamVaultStoreTest {

    @Test
    public void savedVaultsShouldBeLoadable() {
        final VaultStore store = new StreamVaultStore();
        final VaultSettings settings = new VaultSettings(true, true);
        final Vault vault = new Vault(settings);

        final Credentials encryptCredentials = new Credentials();
        encryptCredentials.add(new Password("secret".toCharArray()));
        encryptCredentials.add(new Password("password".toCharArray()));
        encryptCredentials.add(new Password("sesame".toCharArray()));
        encryptCredentials.add(new Password("mushroom".toCharArray()));

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        store.save(vault, encryptCredentials, out);

        final Credentials decryptCredentials = new Credentials();
        decryptCredentials.add(new Password("mushroom".toCharArray()));
        decryptCredentials.add(new Password("sesame".toCharArray()));
        decryptCredentials.add(new Password("password".toCharArray()));
        decryptCredentials.add(new Password("secret".toCharArray()));

        final ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        final Either<String, Vault> load = store.load(decryptCredentials, in);
        assertTrue(load.isRight());

        final Vault loadedVault = load.right().value();
        assertEquals(vault.getUuid(), loadedVault.getUuid());
    }
}
