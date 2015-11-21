package so.blacklight.vault.entry;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Flat folders allow containing entries that are not folders themselves.
 */
public class FlatFolder implements Folder {

    public static final long serialVersionUID = 3889125L;

    private final EntryMetadata metadata;

    private final List<Entry> entries;

    public FlatFolder(final String name) {
        this.metadata = new EntryMetadata(name);
        this.entries = new CopyOnWriteArrayList<>();
    }

    protected FlatFolder(final String name, final EntryMetadata metadata) {
        this.metadata = metadata.setModifyTime(Instant.now());
        this.entries = new CopyOnWriteArrayList<>();
    }

    protected FlatFolder(final FlatFolder copy, final EntryMetadata metadata) {
        this.entries = new CopyOnWriteArrayList<>(copy.getEntries());
        this.metadata = metadata.setModifyTime(Instant.now());
    }

    protected FlatFolder(final Collection<Entry> entries, final EntryMetadata metadata) {
        this.entries = new CopyOnWriteArrayList<>(entries);
        this.metadata = metadata.setModifyTime(Instant.now());
    }

    public FlatFolder setName(final String name) {
        return new FlatFolder(name, metadata);
    }

    @Override
    public final FlatFolder updateEntries(final Collection<Entry> newEntries) {
        if (newEntries == null || newEntries.equals(entries)) {
            return this;
        } else {
            return new FlatFolder(newEntries, metadata);
        }
    }

    @Override
    public List<Entry> getEntries() {
        return new CopyOnWriteArrayList<>(entries);
    }

    @Override
    public EntryMetadata getMetadata() {
        return metadata;
    }
}
