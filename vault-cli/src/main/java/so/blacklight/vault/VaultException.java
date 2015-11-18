package so.blacklight.vault;

public class VaultException extends Exception {

	private static final long serialVersionUID = 1996436409038879397L;

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
