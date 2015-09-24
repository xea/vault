package so.blacklight.vault;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class VaultTest {

    @Test
    public void testNewlyCreatedVaultsShouldBeEmpty() {
        final Vault vault = new Vault();
        assertNotNull(vault.getFolderNames());
        assertEquals(vault.getFolderNames().size(), 0);
    }

    @Test
    public void testNewlyCreatedVaultsShouldBeUnlocked() {
        final Vault vault = new Vault();
        assertEquals(vault.getStatus(), VaultStatus.UNLOCKED);
    }
}
