package so.blacklight.vault.io;

import fj.data.Either;
import so.blacklight.crypto.Credentials;
import so.blacklight.crypto.Decryptor;
import so.blacklight.crypto.Encryptor;
import so.blacklight.io.VaultStore;
import so.blacklight.vault.SecretEntry;
import so.blacklight.vault.Vault;
import so.blacklight.vault.crypto.CryptoService;

import java.io.*;

public class DefaultVaultStoreImpl implements VaultStore {

    @Override
    public Either<String, Vault> load(InputStream inputStream, Credentials credentials) {
        try {
            final ObjectInputStream ois = new ObjectInputStream(inputStream);
            final Object readObject = ois.readObject();
            ois.close();

            if (readObject instanceof SecretEntry) {
                final SecretEntry secretEntry = (SecretEntry) readObject;
                final Decryptor<Vault> decryptor = new CryptoService<>();
                final Vault vault = decryptor.decrypt(secretEntry, credentials);

                return Either.right(vault);
            } else {
                return Either.left("Cannot cast object to SecretEntry");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Either.left(e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return Either.left(e.getMessage());
        }
    }

    @Override
    public boolean save(OutputStream outputStream, final Vault vault, Credentials credentials) {
        try {
            final Encryptor<Vault> encryptor = new CryptoService<>();
            final SecretEntry secretEntry = encryptor.encrypt(vault, credentials);
            final ObjectOutputStream oos = new ObjectOutputStream(outputStream);
            oos.writeObject(secretEntry);
            oos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}
