package so.blacklight.vault.store;

import fj.data.Either;
import fj.data.List;
import so.blacklight.vault.*;
import so.blacklight.vault.io.VaultInputStream;
import so.blacklight.vault.io.VaultOutputStream;
import so.blacklight.vault.io.VaultRecord;

import java.io.*;

public class StreamVaultStore implements VaultStore {

    @Override
    public void save(Vault vault, Credentials credentials, File vaultFile) {
        if (!vaultFile.exists() || vaultFile.canWrite()) {
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
            final ByteArrayOutputStream safetyBuffer = new ByteArrayOutputStream();
            final VaultOutputStream vos = new VaultOutputStream(safetyBuffer);
            final List<VaultRecord> records = generateRecords(vault, credentials);
            final Layout layout = new Layout(vault, credentials);

            vos.writeMagicBytes();
            vos.writeLayout(layout);
            records.forEach(record -> writeBlock(vos, record));

            vos.close();
            safetyBuffer.write(safetyBuffer.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeBlock(final VaultOutputStream out, VaultRecord record) {
        try {
            out.writeBlock(record);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<VaultRecord> generateRecords(Vault vault, Credentials credentials) {
        final Crypto<Vault> crypto = new CryptoImpl<>();


        return null;
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
        }

        return Either.left("ERROR: File not found or not readable: " + vaultFile.getAbsolutePath());
    }

    @Override
    public Either<String, Vault> load(Credentials credentials, InputStream inputStream) {
        try {
            final VaultInputStream vis = new VaultInputStream(inputStream);

            List<VaultRecord> records = vis.readAll();

            vis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
