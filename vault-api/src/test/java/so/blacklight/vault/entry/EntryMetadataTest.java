package so.blacklight.vault.entry;

import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.*;

public class EntryMetadataTest {

    @Test
    public void testNewMetadataShouldBeInitialized() {
        final EntryMetadata metadata = new EntryMetadata("title");
        assertNotNull(metadata.getTitle());
        assertEquals("title", metadata.getTitle());
        assertNotNull(metadata.getCreateTime());
        assertNotNull(metadata.getModifyTime());
        assertEquals(EntryMetadata.DEFAULT_COMMENT, metadata.getComment());
    }

    @Test
    public void testMetadataShouldBeImmutable() {
        final EntryMetadata metadata = new EntryMetadata("title");
        final EntryMetadata changed = metadata.setTitle("new title");

        assertNotNull(changed);
        assertNotEquals(changed, metadata);
        assertEquals("title", metadata.getTitle());
        assertEquals("new title", changed.getTitle());

        final EntryMetadata changed2 = metadata.setExpirationTime(Instant.now().plus(10, ChronoUnit.HOURS));
        assertNotEquals(changed2.getExpirationTime(), metadata.getExpirationTime());
    }

    @Test
    public void testModifyTimeShouldBeUpdatedWhenChanges() {
        final EntryMetadata metadata = new EntryMetadata("title");
        final EntryMetadata changed = metadata.setComment("new comment");

        assertNotEquals(changed.getModifyTime(), metadata.getModifyTime());
        assertTrue(changed.getModifyTime().isAfter(metadata.getModifyTime()));
    }
}
