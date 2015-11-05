package so.blacklight.vault;

import so.blacklight.crypto.Decryptor;
import so.blacklight.crypto.Encryptor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DefaultVault implements Vault, Entry {

    private List<SecretEntry> entries;

    private UUID uuid;

    public DefaultVault() {
        entries = new ArrayList<>();
        uuid = UUID.randomUUID();
    }

    @Override
    public boolean addEntry(SecretEntry secretEntry) {
        return entries.add(secretEntry);
    }

    @Override
    public boolean addEntry(Entry entry, Encryptor encryptor) {
        return false;
    }

    @Override
    public boolean removeEntry(String alias) {
        entries.stream().filter(e -> e.getAlias().equals(alias)).forEach(entries::remove);
        return true;
    }

    @Override
    public boolean removeEntry(SecretEntry secretEntry) {
        return entries.remove(secretEntry);
    }

    @Override
    public List<SecretEntry> getEntries() {
        return entries;
    }

    @Override
    public Entry getEntry(String alias) {
        return entries.stream().filter(e -> e.getAlias().equals(alias)).findFirst().get();
    }

    @Override
    public Entry getEntry(String alias, Decryptor decryptor) {
        return null;
    }

    @Override
    public String getAlias() {
        return uuid.toString();
    }
}
