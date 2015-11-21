package so.blacklight.vault;

public class VaultException extends Exception {

	private static final long serialVersionUID = 1996436409038879397L;

    private final String advice;

    private final String verboseDescription;

	public VaultException() {
        super();
        advice = null;
        verboseDescription = null;
    }

    public VaultException(final String message) {
        super(message);
        advice = null;
        verboseDescription = null;
    }

    public VaultException(final Throwable cause) {
        super(cause);
        advice = null;
        verboseDescription = null;
    }

    public VaultException(final String message, final Throwable cause) {
        super(message, cause);
        advice = null;
        verboseDescription = null;
    }

    public VaultException(final String message, final Throwable cause, final String advice, final String verboseDescription) {
        super(message, cause);
        this.advice = advice;
        this.verboseDescription = verboseDescription;
    }

    public String toLongString() {
        final StringBuffer sb = new StringBuffer();

        sb.append("       error: " + getMessage());
        sb.append(" description: " + verboseDescription);
        sb.append("      action: " + advice);

        return sb.toString();
    }
}
