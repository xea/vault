package so.blacklight.vault.crypto;

import com.lambdaworks.crypto.SCrypt;
import so.blacklight.crypto.Credentials;
import so.blacklight.crypto.EncryptionParams;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

public class PasswordCredentials implements Credentials {

    private transient char[] password;

    public PasswordCredentials(final char[] password) {
        this.password = password;
    }


    @Override
    public EncryptionParams generateParams() {
        final SecureRandom random = new SecureRandom();

        final byte[] salt = new byte[16];
        final byte[] iv = new byte[16];

        random.nextBytes(salt);
        random.nextBytes(iv);

        final EncryptionParams params = new EncryptionParams(salt, iv);

        return params;
    }

    @Override
    public byte[] generateKey(byte[] seed) {
        return deriveKey(password, seed);
    }

    protected byte[] deriveKey(final char[] passwd, final byte[] salt) {
        try {
            byte[] e = new byte[16];
            SecureRandom.getInstance("SHA1PRNG").nextBytes(e);
            final int N = 16384;
            final int r = 8;
            final int p = 1;

            byte[] derived = SCrypt.scrypt(new String(passwd).getBytes("UTF-8"), salt, N, r, p, 32);

            return derived;
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException("JVM doesn\'t support SHA1PRNG or HMAC_SHA256?");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }
}
