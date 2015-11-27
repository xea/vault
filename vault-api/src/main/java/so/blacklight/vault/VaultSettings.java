package so.blacklight.vault;

import java.io.Serializable;

/**
 * Represents the possible configuration settings of a vault object.
 */
public class VaultSettings implements Serializable {

	private static final long serialVersionUID = -6893359789683865895L;

	private final boolean generateRecovery;

    private final boolean generateDegraded;

    /**
     * Initialise a new setting object with default values
     */
    public VaultSettings() {
        generateRecovery = false;
        generateDegraded = false;
    }

    public VaultSettings(final boolean generateRecovery, final boolean generateDegraded) {
        this.generateRecovery = generateRecovery;
        this.generateDegraded = generateDegraded;
    }

    /**
     * Indicate if a recovery segment should be generated for the current vault
     * @return <code>true</code> if the segment should be generated, otherwise <code>false</code>
     */
    public boolean isGenerateRecovery() {
        return generateRecovery;
    }

    /**
     * Indicate if a degraded segment should be generated for the current vault
     * @return <code>true</code> if the segment should be generated, otherwise <code>false</code>
     */
    public boolean isGenerateDegraded() {
        return generateDegraded;
    }
}
