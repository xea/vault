package so.blacklight.vault;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

public class VaultStoreTest {

    @Test
    public void testShouldLoadSavedVaults() throws IOException, ClassNotFoundException {
        final VaultStore store = new VaultStore();
        final Vault vault = new Vault();

        final File tmpFile = File.createTempFile("test", "vaultstore");
        store.save(vault, tmpFile);

        final Optional<Vault> loadedVault = store.load(tmpFile);
        tmpFile.delete();

        assertTrue(loadedVault.isPresent());
    }
}
