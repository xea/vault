package so.blacklight.vault;

import java.io.File;

public class Credentials {

    private char[] password;

    private char[] staticKey;

    private char[] otp;

    private File privateKey;

    public Credentials() {
    }

    public Credentials(final char[] password, final char[] staticKey, final char[] otp, final File privateKey) {
        this.password = password;
        this.otp = otp;
        this.staticKey = staticKey;
        this.privateKey = privateKey;
    }

    public void clear() {
        for (int i = 0; i < password.length; i++) {
            password[i] = 0;
        }

        for (int i = 0; i < otp.length; i++) {
            otp[i] = 0;
        }

        for (int i = 0; i < staticKey.length; i++) {
            staticKey[i] = 0;
        }

        privateKey = null;
    }

}
