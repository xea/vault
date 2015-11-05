package so.blacklight.vault;

public class PasswordEntry implements Entry {

    private String alias;

    private byte[] userId;

    private byte[] password;

    private byte[] url;

    public PasswordEntry(final String alias, final byte[] userId, final byte[] password, final byte[] url) {
        this.alias = alias;
        this.userId = userId;
        this.password = password;
        this.url = url;
    }

    public byte[] getUserId() {
        return userId;
    }

    public void setUserId(byte[] userId) {
        this.userId = userId;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    public byte[] getUrl() {
        return url;
    }

    public void setUrl(byte[] url) {
        this.url = url;
    }

    @Override
    public String getAlias() {
        return alias;
    }
}
