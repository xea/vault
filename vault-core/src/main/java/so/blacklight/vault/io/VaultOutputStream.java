package so.blacklight.vault.io;

import so.blacklight.vault.VaultStoreImpl;
import so.blacklight.vault.store.Layout;

import java.io.*;

public class VaultOutputStream extends DataOutputStream {

    /**
     * Creates a new data output stream to write data to the specified
     * underlying output stream. The counter <code>written</code> is
     * set to zero.
     *
     * @param out the underlying output stream, to be saved for later
     *            use.
     * @see FilterOutputStream#out
     */
    public VaultOutputStream(OutputStream out) {
        super(out);
    }

    public void writeMagicBytes() throws IOException {
        write(VaultStoreImpl.MAGIC_BYTES);
    }

    public void writeLayout(final Layout layout) throws IOException {
        write(layout.toByteArray());
    }

    public void writeBlock(final VaultRecord record) throws IOException {
        for (int i = 0; i < record.getIvs().length; i++) {
            out.write(record.getIvs()[i]);
            out.write(record.getSalts()[i]);
        }

        writeInt(record.getBlock().length);
        write(record.getBlock());
    }
}
