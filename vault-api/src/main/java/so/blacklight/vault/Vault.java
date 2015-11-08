package so.blacklight.vault;

import java.io.Serializable;
import java.util.UUID;

public class Vault implements Serializable {

    public static final long serialVersionUID = -66684432674L;

    private UUID uuid;

    public Vault() {
        reseed();
    }

    public void reseed() {
        uuid = UUID.randomUUID();
    }


}
