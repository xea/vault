package so.blacklight.vault.io;

import so.blacklight.vault.EncryptionParameters;
import so.blacklight.vault.VaultStoreImpl;
import so.blacklight.vault.store.Layout;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class VaultInputStream extends DataInputStream {

    private boolean initialised = false;

    private Layout layout;

    private int idx;

    /**
     * Creates a DataInputStream that uses the specified
     * underlying InputStream.
     *
     * @param in the specified input stream
     */
    public VaultInputStream(InputStream in) throws IOException {
        super(in);

        final byte[] magicBytes = new byte[VaultStoreImpl.MAGIC_BYTES.length];
        read(magicBytes);

        if (!Arrays.equals(magicBytes, VaultStoreImpl.MAGIC_BYTES)) {
            throw new IOException("Stream does not contain a Vault");
        }

        layout = new Layout(in);
        idx = 0;
    }

    public VaultRecord nextRecord() throws IOException {
        final int plc = layout.getPrimaryLayers();
        final int rlc = plc + layout.getRecoveryLayers();
        final int dlc = rlc + layout.getDegradedLayers();

        final int ivBytesCount;
        final int saltBytesCount;
        if (idx < plc) {
            ivBytesCount = EncryptionParameters.IV_LENGTH * layout.getPrimaryLayers();
            saltBytesCount = EncryptionParameters.SALT_LENGTH * layout.getPrimaryLayers();
        } else if (idx < rlc) {
            ivBytesCount = EncryptionParameters.IV_LENGTH * layout.getRecoveryLayers();
            saltBytesCount = EncryptionParameters.SALT_LENGTH * layout.getRecoveryLayers();
        } else if (idx < dlc) {
            ivBytesCount = EncryptionParameters.IV_LENGTH * layout.getDegradedLayers();
            saltBytesCount = EncryptionParameters.SALT_LENGTH * layout.getDegradedLayers();
        } else {
            throw new IOException("Invalid Vault format");
        }

        final int length = readInt();
        final byte[] ivBuffer = new byte[ivBytesCount];
        final byte[] saltBuffer = new byte[saltBytesCount];
        final byte[] recordBuffer = new byte[length];

        read(recordBuffer);

        final VaultRecord record = new VaultRecord(ivBuffer, saltBuffer, recordBuffer);

        return record;
    }

    public boolean skip() {
        return false;
    }

    public int skip(int n) {
        return 0;
    }
}
