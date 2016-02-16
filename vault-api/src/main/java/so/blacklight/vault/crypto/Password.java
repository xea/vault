package so.blacklight.vault.crypto;

import java.nio.charset.StandardCharsets;

/**
 * Represents a user password.
 */
public class Password implements Credential {

    private char[] password;

    /**
     * Note: the use of this constructor is discouraged because it makes erasing passwords
     * from the memory more difficult.
     */
    public Password(final String password) {
        this.password = password.toCharArray();
    }

    public Password(final char[] password) {
        this.password = password;
    }

    @Override
    public boolean isUserInput() {
        return true;
    }

    @Override
    public byte[] getBytes() {
        return new String(password).getBytes(StandardCharsets.UTF_8);
    }
}
