package so.blacklight.vault.locale;

public enum Message {
    APPLICATION_NAME("application.name"),
    APPLICATION_VERSION("application.version"),
    GENERIC_ERROR("error.generic"),
    GENERIC_EXCEPTION("error.exception.generic");

    private String key;

    Message(final String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
