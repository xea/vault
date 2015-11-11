package so.blacklight.vault;

import fj.data.Either;

import java.io.Serializable;
import java.util.List;

/**
 * Defines functionality for an encryption and decryption service.
 */
public interface Crypto<T extends Serializable> {

    /**
     * Encrypts an arbitrary serializable object in a single pass as specified by the passed <code>params</code> argument.
     *
     * @param secret object to encrypt
     * @param params encryption parameters
     * @return <code>Either</code> object where <code>Right</code> value contains the encrypted object or
     *  <code>Left</code> contains an error message if encryption was unsuccessful
     */
    Either<String, byte[]> encrypt(T secret, EncryptionParameters params);

    /**
     * Encrypts an arbitrary serializable object in multiple passes as specified by the passed <code>params</code> argument.
     *
     * Note: for a subsequent decryption, one must pass <code>params</code> in a reverse order.
     *
     * @param secret object to encrypt
     * @param params encryption parameters
     * @return <code>Either</code> object where <code>Right</code> value contains the encrypted object or
     *  <code>Left</code> contains an error message if encryption was unsuccessful
     */
    Either<String, byte[]> encrypt(T secret, List<EncryptionParameters> params);

    Either<String, T> decrypt(byte[] encrypted, EncryptionParameters params);

    Either<String, T> decrypt(byte[] encrypted, List<EncryptionParameters> params);
}
