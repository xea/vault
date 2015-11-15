package so.blacklight.vault.io;

import org.junit.Test;
import so.blacklight.vault.Vault;
import so.blacklight.vault.VaultStoreImpl;
import so.blacklight.vault.store.Layout;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class VaultOutputStreamTest {

    @Test
    public void writtenStreamShouldBeReadable() throws IOException {
        final Vault vault = new Vault();

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final VaultOutputStream vos = new VaultOutputStream(baos);

        final byte[][] ivs = new byte[][] { { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 } };
        final byte[][] salts = new byte[][] { { 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55 } };
        final byte[] block = new byte[] { 80, 81, 82, 83, 84, 85, 86, 87 };

        final VaultRecord record = new VaultRecord(ivs, salts, block);

        vos.writeMagicBytes();
        vos.writeLayout(new Layout(1, 0, 0));
        vos.writeBlock(record);

        final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        final VaultInputStream vis = new VaultInputStream(bais);

        final VaultRecord readRecord = vis.readRecord();

        assertArrayEquals(ivs, readRecord.getIvs());
        assertArrayEquals(salts, readRecord.getSalts());
        assertArrayEquals(block, readRecord.getBlock());
    }
}
