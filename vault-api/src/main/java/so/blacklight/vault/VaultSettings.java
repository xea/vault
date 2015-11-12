package so.blacklight.vault;

import java.io.Serializable;

public class VaultSettings implements Serializable {

    private boolean generateRecovery;

    private boolean generateDegraded;

    public VaultSettings() {
        generateRecovery = false;
        generateDegraded = false;
    }
}
