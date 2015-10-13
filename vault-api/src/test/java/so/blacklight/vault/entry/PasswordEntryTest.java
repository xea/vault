package so.blacklight.vault.entry;

import org.junit.Test;

import static org.junit.Assert.*;

public class PasswordEntryTest {

    @Test
    public void testPasswordEntriesShouldBeImmutable() {
        final PasswordEntry entry = new PasswordEntry("user", "password");
        final PasswordEntry modified = entry.setId("modified");
        assertNotEquals(entry, modified);
        assertEquals("modified", modified.getId());
        assertEquals("user", entry.getId());
    }

    @Test
    public void testShouldInitializeProperties() {
        final PasswordEntry entry = new PasswordEntry("user", "password");
        assertNotNull(entry.getId());
        assertEquals("user", entry.getId());
    }

    @Test
    public void testModifiedTimeShouldBeUpdatedWhenChanges() throws InterruptedException {
        final PasswordEntry entry = new PasswordEntry("user", "password");
        Thread.sleep(100);
        final PasswordEntry changes = entry.setPassword("my password");
        assertNotEquals(entry.getMetadata().getModifyTime(), changes.getMetadata().getModifyTime());
        assertTrue(entry.getMetadata().getModifyTime().isBefore(changes.getMetadata().getModifyTime()));
    }
}
