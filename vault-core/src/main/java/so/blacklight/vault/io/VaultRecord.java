package so.blacklight.vault.io;

public class VaultRecord {

    private final byte[][] ivs;

    private final byte[][] salts;

    private final byte[] block;

    public VaultRecord(byte[][] ivs, byte[][] salts, byte[] block) {
        this.ivs = ivs;
        this.salts = salts;
        this.block = block;
    }

    public byte[][] getIvs() {
        return ivs;
    }

    public byte[][] getSalts() {
        return salts;
    }

    public byte[] getBlock() {
        return block;
    }
}
