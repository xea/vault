package so.blacklight.vault.crypto;

import fj.data.Either;

import javax.crypto.SealedObject;
import java.io.Serializable;
import java.util.List;

/**
 * Defines functionality for an encryption and decryption service.
 */
public interface Crypto<T extends Serializable> {

    /**
     * Encrypt an arbitrary serializable object in a single pass as specified by the passed <code>params</code> argument.
     *
     * @param secret object to encrypt
     * @param params encryption parameters
     * @return <code>Either</code> object where <code>Right</code> value contains the encrypted object or
     *  <code>Left</code> contains an error message if encryption was unsuccessful
     */
    Either<String, byte[]> encrypt(T secret, EncryptionParameter params);

    /**
     * Encrypt an arbitrary serializable object in multiple passes as specified by the passed <code>params</code> argument.
     *
     * Note: for a subsequent decryption, one must pass <code>params</code> in a reverse order.
     *
     * @param secret object to encrypt
     * @param params encryption parameters
     * @return <code>Either</code> object where <code>Right</code> value contains the encrypted object or
     *  <code>Left</code> contains an error message if encryption was unsuccessful
     */
    Either<String, byte[]> encrypt(T secret, List<EncryptionParameter> params);

    /**
     * Attempt to decrypt a byte array that contains an instance of T that had been encrypted.
     *
     * @param encrypted encrypted byte array
     * @param params encryption parameters
     * @return <code>Either#right</code> if decryption was successful otherwise <code>Either#left</code> containing
     * an error message
     */
    Either<String, T> decrypt(byte[] encrypted, EncryptionParameter params);

    /**
     * Attempt to decrypt a byte array that contains an instance of T that had been encrypted.
     *
     * @param encrypted encrypted byte array
     * @param params list of encryption parameters
     * @return <code>Either#right</code> if decryption was successful otherwise <code>Either#left</code> containing
     * the error message
     */
    Either<String, T> decrypt(byte[] encrypted, List<EncryptionParameter> params);

    /**
     * Encrypt an arbitrary serializable object in a single pass and return a SealedObject instance holding
     * the encrypted object
     *
     * @param secret object to encrypt
     * @param param encryption parameters
     * @return <code>Either#right</code> if the encryption was successful, otherwise <code>Either#left</code> containing
     * the error message
     */
    Either<String, SealedObject> seal(T secret, EncryptionParameter param);

    /**
     * Decrypt a sealed object in a single pass and return the object contained within.
     *
     * @param sealedObject sealed object
     * @param param encryption parameter
     * @return <code>Either#right</code> holding the unencrypted object if the decryption was successful, otherwise
     * <code>Either#left</code> holding the error message
     */
    Either<String, T> unseal(SealedObject sealedObject, EncryptionParameter param);
}
