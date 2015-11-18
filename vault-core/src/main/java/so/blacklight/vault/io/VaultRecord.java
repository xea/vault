package so.blacklight.vault.io;

import so.blacklight.vault.collection.Tuple2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VaultRecord {

    private final List<byte[]> ivs;

    private final List<byte[]> salts;

    private final byte[] block;

    public VaultRecord(byte[] block) {
        this.ivs = new ArrayList<>();
        this.salts = new ArrayList<>();
        this.block = block;
    }

    public VaultRecord(byte[][] ivs, byte[][] salts, byte[] block) {
        this.ivs = Arrays.asList(ivs);
        this.salts = Arrays.asList(salts);
        this.block = block;
    }

    public VaultRecord(final List<byte[]> ivs, final List<byte[]> salts, byte[] block) {
        this.ivs = new ArrayList<>(ivs);
        this.salts = new ArrayList<>(salts);
        this.block = block;
    }

    public boolean addIvs(final List<byte[]> ivs) {
        return this.ivs.addAll(ivs);
    }

    public boolean addSalts(final List<byte[]> salts) {
        return this.salts.addAll(salts);
    }

    public boolean addIv(final byte[] iv) {
        return ivs.add(iv);
    }

    public boolean addSalt(final byte[] salt) {
        return salts.add(salt);
    }

    public byte[][] getIvs() {
        final byte[][] dummy = new byte[][] {};
        return ivs.toArray(dummy);
    }

    public byte[][] getSalts() {
        final byte[][] dummy = new byte[][] {};
        return salts.toArray(dummy);
    }

    public byte[] getBlock() {
        return block;
    }

    public int count() {
        return ivs.size();
    }
}
