package so.blacklight.vault.io;

import fj.data.List;
import so.blacklight.vault.crypto.EncryptionParameter;
import so.blacklight.vault.VaultStore;
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

        final byte[] magicBytes = new byte[VaultStore.MAGIC_BYTES.length];
        read(magicBytes);

        if (!Arrays.equals(magicBytes, VaultStore.MAGIC_BYTES)) {
            throw new IOException("Stream is not a valid stream (invalid magic bytes)");
        }

        layout = new Layout(in);
        idx = 0;
    }

    public VaultRecord readRecord() throws IOException {
        final int c = getHeaderCount();

        if (c > 0) {

            final byte[][] ivs = new byte[c][EncryptionParameter.IV_LENGTH];
            final byte[][] salts = new byte[c][EncryptionParameter.SALT_LENGTH];

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
        } else {
            throw new IOException("Attempt to read after end of stream");
        }
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
            readRecord();
        }
        return 0;
    }

    /**
     * Attempt to read every available block from the input stream.
     *
     * Note: this implementation reads at most 255 blocks which is way beyond any reasonable value.
     *
     * @return list of read records
     */
    public List<VaultRecord> readAll() {
        List<VaultRecord> records = list();
        try {
            int i = 255;
            while (i-- > 0) {
                final VaultRecord record = readRecord();
                records = records.cons(record);
            }
        } catch (final Exception e) {
            // We'll eventually run into an exception when reaching the end of the block, so ignoring this.
        }

        return records;
    }

    public Layout getLayout() {
        return layout;
    }
}
