package so.blacklight.io;

import fj.data.Either;
import so.blacklight.crypto.Credentials;
import so.blacklight.vault.Vault;

import java.io.InputStream;
import java.io.OutputStream;

public interface VaultStore {

    Either<String, Vault> load(InputStream inputStream, Credentials credentials);

    boolean save(OutputStream outputStream, Vault vault, Credentials credentials);

}
