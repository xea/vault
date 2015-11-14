package so.blacklight.vault.io;

import fj.data.List;
import so.blacklight.vault.EncryptionParameters;
import so.blacklight.vault.VaultStoreImpl;
import so.blacklight.vault.store.Layout;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static fj.data.List.list;

public class VaultInputStream extends DataInputStream {

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
        final int c = getHeaderCount();

        final byte[][] ivs = new byte[c][EncryptionParameters.IV_LENGTH];
        final byte[][] salts = new byte[c][EncryptionParameters.SALT_LENGTH];

        for (int i = 0; i < c; i++) {
            in.read(ivs[i]);
            in.read(salts[i]);
        }

        final int blockLength = readInt();
        final byte[] block = new byte[blockLength];

        read(block);

        final VaultRecord record = new VaultRecord(ivs, salts, block);

        idx += 1;
        return record;
    }

    private int getHeaderCount() {
        final int plc = layout.getPrimaryLayers();
        final int rlc = layout.getRecoveryLayers();
        final int dlc = layout.getDegradedLayers();

        if (idx == 0) {
            return plc;
        } else if (rlc > 0 && idx < plc + rlc) {
            return plc - 1;
        } else if (dlc > 0 && idx >= (plc + rlc) && idx < (plc + rlc + dlc)) {
            return plc - 2;
        }

        return 0;
    }

    public boolean skip() throws IOException {
        return skip(1) > 0;
    }

    public int skip(int n) throws IOException {
        for (int i = 0; i < n; i++) {
            nextRecord();
        }
        return 0;
    }

    public List<VaultRecord> readAll() {
        return list();
    }
}
