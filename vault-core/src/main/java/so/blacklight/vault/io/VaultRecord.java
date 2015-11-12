package so.blacklight.vault.io;

import java.io.InputStream;

public class VaultRecord {

    private byte[] ivBytes;

    private byte[] saltBytes;

    private byte[] blockBytes;

    public VaultRecord(byte[] ivBuffer, byte[] saltBuffer, byte[] recordBuffer) {
        ivBytes = ivBuffer;
        saltBytes = saltBuffer;
        blockBytes = recordBuffer;
    }
}
