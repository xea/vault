package so.blacklight.vault.cli;

/**
 * Actions a user could possibly request
 */
public enum VaultAction {

    /**
     * Do nothing
     */
    NONE("none"),

    /**
     * Print usage to the screen
     */
    SHOW_USAGE("help"),

    /**
     * Create a new vault from scratch
     */
    CREATE_VAULT("create-vault"),

    /**
     * Create a new folder within the selected vault
     */
    CREATE_FOLDER("create-folder"),

    /**
     * Delete the specified folder from the selected vault
     */
    DELETE_FOLDER("delete-folder"),

    /**
     * Create a new vault entry
     */
    CREATE_ENTRY("create-entry"),

    /**
     * Delete the selected vault entry
     */
    DELETE_ENTRY("delete-entry"),

    /**
     * Display entries from the specified folder
     */
    SHOW_FOLDERS("list-folder"),

    /**
     * Copy the contents of a secret entry to the system clipboard
     */
    COPY_SECRET("copy-secret"),

    /**
     * Show debug information about the program
     */
    DEBUG("debug");


    private String actionCode;

    VaultAction(String code) {
        actionCode = code;
    }

    public String getActionCode() {
        return actionCode;
    }

}
