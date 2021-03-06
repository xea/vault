package so.blacklight.vault.locale;

public enum Message {
    APPLICATION_NAME("application.name"),
    APPLICATION_VERSION("application.version"),
    ERROR_CANNOT_LOAD("error.cannot_load"),
    ERROR_CANNOT_READ("error.cannot_read"),
    ERROR_PASSWORD_MISSING("prompt.password.error"),
    ERROR_RESTRICTED_ACCESS("error.restricted_access"),
    GENERIC_ERROR("error.generic"),
    GENERIC_EXCEPTION("error.exception.generic"),
    PROMPT_ENTER_PASSWORD("prompt.password");

    private String key;

    Message(final String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
