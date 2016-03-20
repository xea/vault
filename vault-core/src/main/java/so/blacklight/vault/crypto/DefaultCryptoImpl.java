package so.blacklight.vault.crypto;

import fj.data.Either;

import javax.crypto.SealedObject;
import java.io.Serializable;
import java.util.List;

/**
 * An implementation of <code>Crypto</code> that uses only minimal functional java in favour of standard
 * Java features.
 */
public class DefaultCryptoImpl<T extends Serializable> implements Crypto<T> {

    @Override
    public Either<String, byte[]> encrypt(T secret, EncryptionParameter params) {
        return null;
    }

    @Override
    public Either<String, byte[]> encrypt(T secret, List<EncryptionParameter> params) {
        return null;
    }

    @Override
    public Either<String, T> decrypt(byte[] encrypted, EncryptionParameter params) {
        return null;
    }

    @Override
    public Either<String, T> decrypt(byte[] encrypted, List<EncryptionParameter> params) {
        return null;
    }

    @Override
    public Either<String, SealedObject> seal(T secret, EncryptionParameter param) {
        return null;
    }

    @Override
    public Either<String, T> unseal(SealedObject sealedObject, EncryptionParameter param) {
        return null;
    }

}
