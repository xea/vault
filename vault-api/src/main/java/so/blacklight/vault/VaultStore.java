package so.blacklight.vault;

import fj.data.Either;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public interface VaultStore {

    void save(Vault vault, Credentials credentials, File vaultFile);

    void save(Vault vault, Credentials credentials, OutputStream outputStream);

    Either<String, Vault> load(Credentials credentials, File vaultFile);

    Either<String, Vault> load(Credentials credentials, InputStream inputStream);
}
