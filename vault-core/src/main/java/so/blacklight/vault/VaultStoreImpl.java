package so.blacklight.vault;

import fj.data.Either;
import so.blacklight.vault.store.Layout;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class VaultStoreImpl implements VaultStore {

    public static final byte[] MAGIC_BYTES = { 0, 116, 127, 113 };

    @Override
    public void save(Vault vault, Credentials credentials, File vaultFile) {
        if (vaultFile.exists() && !vaultFile.canWrite()) {
            // Can't write vault
        } else if (!vaultFile.exists() && vaultFile.canWrite()) {
            // Win
            try {
                final FileOutputStream fos = new FileOutputStream(vaultFile);

                save(vault, credentials, fos);

                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void save(Vault vault, Credentials credentials, OutputStream outputStream) {
        try {
            outputStream.write(MAGIC_BYTES);

            final Stream<Credential> stream = credentials.getCredentials().stream();
            final List<EncryptionParameters> params = stream.map(c -> new EncryptionParameters(c)).collect(Collectors.toList());

            final Crypto<Vault> crypto = new CryptoImpl<>();
            final Either<String, byte[]> encrypt = crypto.encrypt(vault, params);

            if (encrypt.isRight()) {
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                final DataOutputStream dos = new DataOutputStream(baos);

                final int n = credentials.getCredentials().size();
                final Layout layout = new Layout(n, n - 1, n - 2);
                dos.write(layout.toByteArray());

                for (final EncryptionParameters param : params) {
                    dos.write(param.getIv());
                    dos.write(param.getSalt());
                }

                dos.writeInt(encrypt.right().value().length);
                dos.write(encrypt.right().value());

                outputStream.write(baos.toByteArray());

                dos.close();
                baos.close();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Either<String, Vault> load(Credentials credentials, File vaultFile) {
        if (vaultFile.exists() && vaultFile.canRead()) {
            try {
                final FileInputStream fis = new FileInputStream(vaultFile);
                final Either<String, Vault> maybeVault = load(credentials, fis);
                fis.close();

                return maybeVault;
            } catch (Exception e) {
                return Either.left(e.getMessage());
            }
        } else {
            return Either.left("ERROR: Can't open vault file: " + vaultFile.getAbsolutePath());
        }
    }

    @Override
    public Either<String, Vault> load(Credentials credentials, InputStream inputStream) {
        final byte[] magicBytes = new byte[4];

        try {
            inputStream.read(magicBytes);

            if (Arrays.equals(MAGIC_BYTES, magicBytes)) {
                final Layout layout = new Layout(inputStream);

                int credentialsCount = credentials.getCredentials().size();
                // load primary segment
                if (credentialsCount == layout.getPrimaryLayers()) {
                    List<EncryptionParameters> params = IntStream.range(0, layout.getPrimaryLayers())
                            .mapToObj(i -> readHeaders(inputStream).apply(credentials.getCredentials().get(i)))
                            .collect(Collectors.toList());

                    final DataInputStream dis = new DataInputStream(inputStream);
                    final int blockLength = dis.readInt();
                    dis.close();

                    final byte[] encryptedBlock = new byte[blockLength];
                    inputStream.read(encryptedBlock);

                    final Crypto<Vault> crypto = new CryptoImpl<>();

                    Collections.reverse(params);
                    final Either<String, Vault> decrypted = crypto.decrypt(encryptedBlock, params);

                    return decrypted;
                }

                return Either.left("WTF. No, dude, seriously. WTF.");
            } else {
                return Either.left("ERROR: invalid vault file");
            }
        } catch (IOException e) {
            return Either.left(e.getMessage());
        }
    }


    private Function<Credential, EncryptionParameters> readHeaders(final InputStream is) {
        final byte[] iv = new byte[16];
        final byte[] salt = new byte[16];

        try {
            is.read(iv);
            is.read(salt);
        } catch (final Exception e) {
            e.printStackTrace();
        }

        return (c) -> new EncryptionParameters(c, iv, salt);
    }
}
