package so.blacklight.vault;

import fj.data.Either;

import java.io.*;

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Either<String, Vault> load(Credentials credentials, File vaultFile) {
        return null;
    }

    @Override
    public Either<String, Vault> load(Credentials credentials, InputStream inputStream) {
        return null;
    }
}
