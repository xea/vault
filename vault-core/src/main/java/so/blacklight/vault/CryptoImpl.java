package so.blacklight.vault;

import fj.data.Either;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class CryptoImpl<T extends Serializable> implements Crypto<T> {

    private final static String DEFAULT_ERROR = "Error :(";

    private final static String AES_CIPHER_NAME = "AES/CBC/PKCS5Padding";
    @Override
    public Either<String, byte[]> encrypt(T secret, EncryptionParameters params) {
        return encrypt(secret, Arrays.<EncryptionParameters>asList(params));
    }

    @Override
    public Either<String, byte[]> encrypt(T secret, List<EncryptionParameters> params) {
        return crypt(Cipher.ENCRYPT_MODE, secret, (T a, List<Cipher> b) -> handleEncrypt(a, b), params);
    }

    @Override
    public Either<String, T> decrypt(byte[] encrypted, EncryptionParameters params) {
        return decrypt(encrypted, Arrays.<EncryptionParameters>asList(params));
    }

    @Override
    public Either<String, T> decrypt(byte[] encrypted, List<EncryptionParameters> params) {
        return crypt(Cipher.DECRYPT_MODE, encrypted, (byte[] a, List<Cipher> b) -> handleDecrypt(a, b), params);
    }

    protected <U, R> Either<String, R> crypt(int mode, U data, BiFunction<U, List<Cipher>, Either<String, R>> f, List<EncryptionParameters> params) {
        List<Either<String, Cipher>> maybeCiphers = params.stream().map(p -> getCipher(mode, p))
                .collect(Collectors.toList());

        if (maybeCiphers.stream().allMatch(e -> e.isRight())) {
            List<Cipher> ciphers = maybeCiphers.stream().map(e -> e.right().value()).collect(Collectors.toList());

            try {
                Either<String, R> value = f.apply(data, ciphers);
                return value;
            } catch (Throwable e) {
                return Either.left(e.getMessage());
            }
        } else {
            final Either<String, Cipher> errorCipher = maybeCiphers.stream().filter(e -> e.isLeft()).findFirst().get();

            return Either.left(errorCipher.left().orValue(DEFAULT_ERROR));
        }
    }

    protected Either<String, Cipher> getCipher(final int mode, final EncryptionParameters params) {
        try {
            return Either.right(getAESCipher(mode, params));
        } catch (final Exception e) {
            return Either.left(e.getMessage());
        }
    }

    private Cipher getAESCipher(final int mode, final EncryptionParameters params) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        final Cipher cipher = Cipher.getInstance(AES_CIPHER_NAME);
        final IvParameterSpec ivSpec = new IvParameterSpec(params.getIv());
        cipher.init(mode, params.getKey(), ivSpec);

        return cipher;
    }

    private Either<String, byte[]> handleEncrypt(final T secret, final List<Cipher> ciphers) {
        try {
            final byte[] encrypted = multiEncrypt(secret, ciphers);
            return Either.right(encrypted);
        } catch (IOException e) {
            return Either.left(e.getMessage());
        }
    }

    private Either<String, T> handleDecrypt(final byte[] encrypted, final List<Cipher> ciphers) {
        try {
            final Either<String, T> maybeDecrypted = multiDecrypt(encrypted, ciphers);
            return maybeDecrypted;
        } catch (IOException e) {
            return Either.left(e.getMessage());
        }
    }

    // Encrypt a secret in pultiple passes
    private byte[] multiEncrypt(final T secret, final List<Cipher> ciphers) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(secret);
        oos.close();

        final byte[] objectBytes = baos.toByteArray();

        return multiEncrypt(objectBytes, ciphers);
    }

    // Encrypt a secret in multiple passes
    private byte[] multiEncrypt(final byte[] secret, final List<Cipher> ciphers) throws IOException {
        if (ciphers.isEmpty()) {
            return secret;
        } else {
            final Cipher cipher = ciphers.get(0);

            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final CipherOutputStream cos = new CipherOutputStream(baos, cipher);
            cos.write(secret);
            cos.close();

            final byte[] encrypted = baos.toByteArray();

            return multiEncrypt(encrypted, ciphers.subList(1, ciphers.size()));
        }
    }

    private Either<String, T> multiDecrypt(final byte[] encrypted, final List<Cipher> ciphers) throws IOException {
        final byte[] decrypted = multiDecryptInternal(encrypted, ciphers);
        final ByteArrayInputStream bais = new ByteArrayInputStream(decrypted);
        final ObjectInputStream ois = new ObjectInputStream(bais);

        try {
            final Object readObject = ois.readObject();

            // we're suppressing this warning because we don't have many options determining the correct type
            @SuppressWarnings("unchecked")
			final T result = (T) readObject;

            return Either.right(result);
        } catch (ClassNotFoundException e) {
            return Either.left(e.getMessage());
        } catch (ClassCastException e) {
            return Either.left(e.getMessage());
        }
    }

    private byte[] multiDecryptInternal(final byte[] encrypted, final List<Cipher> ciphers) throws IOException {
        if (ciphers.isEmpty()) {
            return encrypted;
        } else {
            final Cipher cipher = ciphers.get(0);

            final ByteArrayInputStream bais = new ByteArrayInputStream(encrypted);
            final CipherInputStream cis = new CipherInputStream(bais, cipher);
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();


            // Let's try reading the encrypted data in 64k chunks
            final byte[] buffer = new byte[65536];
            int length;

            while ((length = cis.read(buffer)) >= 0) {
                baos.write(buffer, 0, length);
            }

            cis.close();

            return multiDecryptInternal(baos.toByteArray(), ciphers.subList(1, ciphers.size()));
        }
    }
}
