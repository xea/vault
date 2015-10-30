package so.blacklight.vault.entry;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FlatFolderTest {

    @Test
    public void newFoldersShouldBeEmpty() {
        final Folder folder = new FlatFolder("testFolder");
        assertNotNull(folder.getEntries());
        assertEquals(0, folder.getEntries().size());
    }

    @Test
    public void setNameDoesntChangeContents() {
        final Folder folder = new FlatFolder("name1");
        final List<Entry> entries = folder.getEntries();
        entries.add(new PasswordEntry("id", "password"));

        final Folder newFolder = folder.updateEntries(entries);
        final Folder newFolder2 = newFolder.setName("name2");
        assertNotNull(newFolder2.getEntries());
        assertEquals(newFolder.getEntries(), newFolder2.getEntries());
        assertEquals("name2", newFolder2.getMetadata().getTitle());
    }
}
