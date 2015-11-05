package so.blacklight.vault;

import so.blacklight.crypto.Decryptor;
import so.blacklight.crypto.Encryptor;

import java.io.Serializable;
import java.util.List;

public interface Vault extends Entry, Serializable {

    boolean addEntry(SecretEntry secretEntry);

    boolean addEntry(Entry entry, Encryptor encryptor);

    boolean removeEntry(String alias);

    boolean removeEntry(SecretEntry secretEntry);

    List<SecretEntry> getEntries();

    Entry getEntry(String alias);

    Entry getEntry(String alias, Decryptor decryptor);
}
