package so.blacklight.vault;

import java.io.Serializable;

public class VaultSettings implements Serializable {

    private final boolean generateRecovery;

    private final boolean generateDegraded;

    public VaultSettings() {
        generateRecovery = false;
        generateDegraded = false;
    }

    public VaultSettings(final boolean generateRecovery, final boolean generateDegraded) {
        this.generateRecovery = generateRecovery;
        this.generateDegraded = generateDegraded;
    }

    public boolean isGenerateRecovery() {
        return generateRecovery;
    }

    public boolean isGenerateDegraded() {
        return generateDegraded;
    }
}
