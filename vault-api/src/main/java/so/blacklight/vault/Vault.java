package so.blacklight.vault;

import java.io.Serializable;
import java.util.UUID;

public class Vault implements Serializable {

    public static final long serialVersionUID = -7671254481L;

    private UUID uuid;

    public Vault() {
        uuid = UUID.randomUUID();
    }

    public boolean isWritable() {
        return true;
    }

    public UUID getUuid() {
        return uuid;
    }
}
