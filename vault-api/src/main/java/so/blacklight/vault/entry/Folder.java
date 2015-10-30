package so.blacklight.vault.entry;


import so.blacklight.vault.entry.Entry;

import java.util.Collection;
import java.util.List;

/**
 * Folders are organisation units within a vault. An implementation of this
 * interface may decide if it allows storing other folders or not.
 */
public interface Folder extends Entry {

    /**
     * Return a set of stored <code>Entry</code> instances.
     *
     * @return stored entries
     */
    List<Entry> getEntries();

    /**
     * Updates the entry set stored in this folder with the passed argument;
     *
     * @param entries new entries
     * @return <code>true</code> if updating was successful, otherwise <code>false</code>
     */
    Folder updateEntries(Collection<Entry> entries);

    Folder setName(String name);

}
