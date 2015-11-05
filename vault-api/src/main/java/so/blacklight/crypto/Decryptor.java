package so.blacklight.crypto;

import so.blacklight.vault.SecretEntry;

@FunctionalInterface
public interface Decryptor<T> {

    T decrypt(SecretEntry secret, Credentials credentials);

}
