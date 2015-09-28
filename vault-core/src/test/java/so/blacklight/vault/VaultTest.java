package so.blacklight.vault;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class VaultTest {

    @Test
    public void testNewlyCreatedVaultsShouldBeEmpty() {
        final Vault vault = new Vault();
        assertNotNull(vault.getFolderNames());
        assertEquals(0, vault.getFolderNames().size());
    }

    @Test
    public void testNewlyCreatedVaultsShouldBeUnlocked() {
        final Vault vault = new Vault();
        assertEquals(VaultStatus.UNLOCKED, vault.getStatus());
    }

    @Test
    public void testLockingANewVaultClearsPlainData() {
        final Vault vault = new Vault();
        final Credentials testCredentials = new Credentials("passphrase", "otp");
        vault.lock(testCredentials);

        assertNotNull(vault.getFolderNames());
        assertEquals(0, vault.getFolderNames().size());
    }

    @Test
    public void testLockingAVaultSetsStatusToLocked() {
        final Vault vault = new Vault();
        final Credentials testCredentials = new Credentials("passphrase", "onetimepassword");
        vault.lock(testCredentials);

        assertEquals(VaultStatus.LOCKED, vault.getStatus());
    }

}
