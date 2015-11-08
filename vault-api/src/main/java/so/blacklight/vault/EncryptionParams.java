package so.blacklight.vault;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class EncryptionParams {

    public static final long serialVersionUID = -662315146L;

    private byte[] iv;

    private byte[] salt;

    public EncryptionParams() {
        iv = new byte[16];
        salt = new byte[16];

        SecureRandom random;

        try {
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            random = new SecureRandom();
        }

        random.nextBytes(iv);
        random.nextBytes(salt);
    }

    public EncryptionParams(final byte[] iv, final byte[] salt) {
        this.iv = iv;
        this.salt = salt;
    }

    public byte[] getIv() {
        return iv;
    }

    public byte[] getSalt() {
        return salt;
    }
}
