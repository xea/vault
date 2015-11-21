package so.blacklight.vault.store;

import so.blacklight.vault.Credentials;
import so.blacklight.vault.Vault;

import java.io.IOException;
import java.io.InputStream;

public class Layout {

	private int reservedByte;
	
    private int primaryLayers;

    private int recoveryLayers;

    private int degradedLayers;

    public Layout(final InputStream is) throws IOException {
        final byte[] buffer = new byte[4];
        is.read(buffer);

        reservedByte = buffer[0];
        primaryLayers = buffer[1];
        recoveryLayers = buffer[2];
        degradedLayers = buffer[3];
    }

    public Layout(final byte[] layoutBytes) {
        reservedByte = layoutBytes[0];
        primaryLayers = layoutBytes[1];
        recoveryLayers = layoutBytes[2];
        degradedLayers = layoutBytes[3];
    }

    public Layout(int primaryLayers, int recoveryLayers, int degradedLayers) {
    	reservedByte = 0;
        this.primaryLayers = sanitize(primaryLayers);
        this.recoveryLayers = sanitize(recoveryLayers);
        this.degradedLayers = sanitize(degradedLayers);
    }

    public Layout(final Vault vault, final Credentials credentials) {
    	reservedByte = 0;
        primaryLayers = credentials.getCredentials().size();
        recoveryLayers = vault.getRecoverySegment().isPresent() ? primaryLayers - 1 : 0;
        degradedLayers = vault.getDegradedSegment().isPresent() ? primaryLayers - 2 : 0;
    }

    public int getPrimaryLayers() {
        return primaryLayers;
    }

    public int getRecoveryLayers() {
        return recoveryLayers;
    }

    public int getDegradedLayers() {
        return degradedLayers;
    }

    public int getLayers(final int idx) {
        final int layers;

        switch (idx) {
            case 0:
                layers = primaryLayers;
                break;
            case 1:
                layers = recoveryLayers;
                break;
            case 2:
                layers = degradedLayers;
                break;
            default:
                layers = 0;
                break;
        }

        return layers;
    }

    public byte[] toByteArray() {
        return new byte[] { (byte) reservedByte, (byte) primaryLayers, (byte) recoveryLayers, (byte) degradedLayers};
    }

    private int sanitize(int value) {
        if (value < 0) {
            return 0;
        } else if (value > 127) {
            return 127;
        } else {
            return value;
        }
    }
}
