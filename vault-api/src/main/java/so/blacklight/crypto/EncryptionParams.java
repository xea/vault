package so.blacklight.crypto;

import java.io.Serializable;

public class EncryptionParams implements Serializable {

    public static final long serialVersionUID = -777823521623L;

    private byte[] iv;

    private byte[] salt;

    public EncryptionParams(final byte[] iv, final byte[] salt) {
        this.iv = iv;
        this.salt = salt;
    }

    public byte[] getSalt() {
        return salt;
    }

    public byte[] getIv() {
        return iv;
    }

}
