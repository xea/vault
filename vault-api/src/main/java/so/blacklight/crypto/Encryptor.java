package so.blacklight.crypto;

import so.blacklight.vault.SecretEntry;

@FunctionalInterface
public interface Encryptor<T> {

    SecretEntry encrypt(T entry, Credentials credentials);
}
