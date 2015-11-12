package so.blacklight.vault;

public class VaultException extends Exception {

    public VaultException() {
        super();
    }

    public VaultException(final String message) {
        super(message);
    }

    public VaultException(final Throwable cause) {
        super(cause);
    }

    public VaultException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
