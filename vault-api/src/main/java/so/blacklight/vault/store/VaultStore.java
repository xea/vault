package so.blacklight.vault.store;

import fj.data.Either;
import so.blacklight.vault.Vault;
import so.blacklight.vault.crypto.Credentials;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Implementations of VaultStore are responsible for storing and retrieving Vault instances from persistent
 * storages, eg. file systems. Operations do not throw exceptions on errors but return Either#left() instead, containing
 * the error message.
 *
 * Persisted vaults should begin with a byte sequence of <code>MAGIC_BYTES</code>.
 */
public interface VaultStore {

    /**
     * Magic bytes used to recognise a persistent vault stream.
     */
    byte[] MAGIC_BYTES = { 0, 116, 127, 113 };

    /**
     * Save to persistent storage a vault object. The persisted byte stream shall be encrypted according to the
     * vault settings and the passed credentials.
     *
     * @param vault vault
     * @param credentials encryption credentials
     * @param vaultFile output file
     * @return <code>Either#right</code> if the operation was successful, otherwise <code>Either#left</code> containing
     * the error message
     */
    Either<String, Boolean> save(Vault vault, Credentials credentials, File vaultFile);

    /**
     * Save to persistent storage a vault object. The persisted byte stream shall be encrypted according to the
     * vault settings and the passed credentials.
     *
     * @param vault vault
     * @param credentials encryption credentials
     * @param outputStream output stream
     * @return <code>Either#right</code> if the operation was successful, otherwise <code>Either#left</code> containing
     * the error message
     */
    Either<String, Boolean> save(Vault vault, Credentials credentials, OutputStream outputStream);

    /**
     * Load a vault instance from persistent storage.
     *
     * @param credentials decryption credentials
     * @param vaultFile source file
     * @return <code>Either#right</code> if the operation was successful, otherwise <code>Either#left</code> containing
     * the error message
     */
    Either<String, Vault> load(Credentials credentials, File vaultFile);

    /**
     * Load a vault instance from persistent storage.
     *
     * @param credentials decryption credentials
     * @param inputStream source stream
     * @return <code>Either#right</code> if the operation was successful, otherwise <code>Either#left</code> containing
     * the error message
     */
    Either<String, Vault> load(Credentials credentials, InputStream inputStream);
}