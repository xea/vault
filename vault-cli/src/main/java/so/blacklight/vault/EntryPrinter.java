package so.blacklight.vault;

import so.blacklight.vault.entry.*;

public class EntryPrinter {

    public void print(final Entry entry) {
        if (entry instanceof Folder) {
            print((Folder) entry);
        } else if (entry instanceof SecretEntry) {
            print((SecretEntry) entry);
        } else if (entry instanceof PasswordEntry) {
            print((PasswordEntry) entry);
        }
    }

    public void print(final Folder folder) {
        final String alias = folder.getMetadata().getAlias();
        final int size = folder.getEntries().size();

        System.out.println(alias + " is a folder with " + size + " entry");
    }

    public void print(final SecretEntry secret) {
        final String alias = secret.getMetadata().getAlias();

        System.out.println(alias + " is a secret entry");
    }

    public void print(final PasswordEntry password) {
        final Metadata meta = password.getMetadata();
        final String alias = meta.getAlias();

        System.out.println(String.format("%s, %s, %s", alias, meta.getExpirationTime().toEpochMilli(), password.getClass().getName()));
        System.out.println("");
    }
}
