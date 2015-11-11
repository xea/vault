package so.blacklight.vault;

import java.nio.charset.StandardCharsets;

public class Password implements Credential {

    private char[] password;

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
