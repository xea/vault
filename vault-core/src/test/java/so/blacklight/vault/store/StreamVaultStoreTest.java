package so.blacklight.vault.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Test;

import fj.data.Either;
import so.blacklight.vault.Credentials;
import so.blacklight.vault.Password;
import so.blacklight.vault.Vault;
import so.blacklight.vault.VaultSettings;
import so.blacklight.vault.VaultStore;

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
        long bs = System.currentTimeMillis();
        store.save(vault, encryptCredentials, out);
        System.out.println("Save: " + (System.currentTimeMillis() - bs));

        final Credentials decryptCredentials = encryptCredentials.reverse();

        final ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        long bl = System.currentTimeMillis();
        final Either<String, Vault> load = store.load(decryptCredentials, in);
        System.out.println("Load: " + (System.currentTimeMillis() - bl));
        assertTrue(load.isRight());

        final Vault loadedVault = load.right().value();
        assertEquals(vault.getUuid(), loadedVault.getUuid());
    }
}
