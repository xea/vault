package so.blacklight.vault.store;

import java.io.IOException;
import java.io.InputStream;

public class Layout {

    private int primaryLayers;

    private int recoveryLayers;

    private int degradedLayers;

    public Layout(final InputStream is) throws IOException {
        final byte[] buffer = new byte[4];
        is.read(buffer);

        final byte reserved = buffer[0];
        primaryLayers = buffer[1];
        recoveryLayers = buffer[2];
        degradedLayers = buffer[3];
    }

    public Layout(final byte[] layoutBytes) {
        final byte reserved = layoutBytes[0];
        primaryLayers = layoutBytes[1];
        recoveryLayers = layoutBytes[2];
        degradedLayers = layoutBytes[3];
    }

    public Layout(int primaryLayers, int recoveryLayers, int degradedLayers) {
        this.primaryLayers = sanitize(primaryLayers);
        this.recoveryLayers = sanitize(recoveryLayers);
        this.degradedLayers = sanitize(degradedLayers);
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

    public byte[] toByteArray() {
        return new byte[] { 0, (byte) primaryLayers, (byte) recoveryLayers, (byte) degradedLayers};
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
