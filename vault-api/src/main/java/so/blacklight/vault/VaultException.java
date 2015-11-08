package so.blacklight.vault;

public class VaultException extends Exception {

    public VaultException(final String message) {
        this(message, null);
    }

    public VaultException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
