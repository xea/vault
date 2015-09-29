package so.blacklight.vault;

import org.junit.Test;
import so.blacklight.vault.entry.PasswordEntry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class VaultTest {

    // Vault creation

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
        final Folder folder = vault.createFolder("testFolder");
        folder.addEntry(new PasswordEntry("myUsername", "myPassword", "recovery info"));
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

    @Test
    public void testUnlockingALockedVaultWithInvalidCredentialsDoesntRetreiveData() {
        final Credentials validCredentials = new Credentials("passphrase", "onetimepassword");
        final Credentials invalidCredentials = new Credentials("wrongphrase", "wrongpassword");
        final Vault vault = new Vault();
        final Folder folder = vault.createFolder("testFolder");
        folder.addEntry(new PasswordEntry("myUsername", "myPassword", "recovery info"));
        vault.lock(validCredentials);

        vault.unlock(invalidCredentials);
        assertEquals(0, vault.getFolders().size());
    }

    @Test
    public void testUnlockingALockedVaultRetreivesData() {
        final Credentials credentials = new Credentials("passphrase", "onetimepassword");
        final Vault vault = new Vault();
        final Folder folder = vault.createFolder("testFolder");
        folder.addEntry(new PasswordEntry("myUsername", "myPassword", "recovery info"));
        vault.lock(credentials);

        vault.unlock(credentials);
        assertEquals(1, vault.getFolders().size());
    }


}
