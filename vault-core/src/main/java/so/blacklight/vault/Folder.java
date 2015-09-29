package so.blacklight.vault;

import so.blacklight.vault.entry.VaultEntry;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Folder implements Serializable {

    public static final long serialVersionUID = 10010;

    private String name;

    private List<VaultEntry> entries;

    public Folder(final String name) {
        this.name = name;
        entries = new CopyOnWriteArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void addEntry(final VaultEntry entry) {
        if (!entries.contains(entry)) {
            entries.add(entry);
        }
    }

    public List<VaultEntry> getEntries() {
        return new CopyOnWriteArrayList<>(entries);
    }
}
