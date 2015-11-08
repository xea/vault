package so.blacklight.vault;

import com.lambdaworks.crypto.SCrypt;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.*;

public class Crypto {

    public SecretKey createKey() {
        final byte[] keyBytes = new byte[16];

        // TODO this is ugly
        try {
            SecureRandom.getInstance("SHA1PRNG").nextBytes(keyBytes);
        } catch (NoSuchAlgorithmException e) {
            new SecureRandom().nextBytes(keyBytes);
        }

        return createKey(keyBytes);
    }

    public SecretKey createKey(final byte[] key) {
        final SecretKey secretKey = new SecretKeySpec(key, "AES");

        return secretKey;
    }

    public SecretKey createKey(final char[] password, final EncryptionParams params) {
        try {
            int N = 16384;
            int r = 8;
            int p = 1;
            byte[] e = params.getSalt();
            byte[] derived = SCrypt.scrypt(new String(password).getBytes("UTF-8"), e, N, r, p, 16);
            final SecretKey secretKey = new SecretKeySpec(derived, "AES");

            return secretKey;
        } catch (UnsupportedEncodingException var8) {
            throw new IllegalStateException("JVM doesn\'t support UTF-8?");
        } catch (GeneralSecurityException var9) {
            throw new IllegalStateException("JVM doesn\'t support SHA1PRNG or HMAC_SHA256?");
        }
    }
    /*
    protected Cipher getCipher(int mode, final SecretKey secretKey) {
        try {
            final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            final IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(mode, secretKey, ivSpec);

            return cipher;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected SecretKey createKey(final char[] password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        final SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        final KeySpec keySpec = new PBEKeySpec(password, salt, 65536, 256);
        final SecretKey tmp = factory.generateSecret(keySpec);
        final SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

        return secret;
    }

    protected SecretKey createKey(final byte[] keyBytes) {
        final SecretKey secret = new SecretKeySpec(keyBytes, "AES");

        return secret;
    }
*/

    private byte[] encrypt(byte[] data, Cipher cipher) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final CipherOutputStream cos = new CipherOutputStream(baos, cipher);

        cos.write(data);
        cos.close();

        // No need to close this (see API docs)
        return baos.toByteArray();
    }

    private byte[] encrypt(byte[] data, List<Cipher> ciphers) throws IOException {
        if (ciphers.isEmpty()) {
            return data;
        } else {
            final Cipher cipher = ciphers.remove(0);
            final byte[] encrypted = encrypt(data, cipher);
            return encrypt(encrypted, ciphers);
        }
    }

    private byte[] decrypt(byte[] data, Cipher cipher) throws IOException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(data);
        final CipherInputStream cis = new CipherInputStream(bais, cipher);
        final byte[] decrypted = new byte[data.length];
        cis.read(decrypted);
        cis.close();
        // No need to close bytearrayinputstreams (see API docs)

        return decrypted;
    }

    private byte[] decrypt(byte[] data, List<Cipher> ciphers) throws IOException {
        if (ciphers.isEmpty()) {
            return data;
        } else {
            final Cipher cipher = ciphers.remove(0);
            final byte[] decrypted = decrypt(data, cipher);
            return decrypt(decrypted, ciphers);
        }
    }


    /*
    public static void displayAlgorithms() {
        try {
            Provider p[] = Security.getProviders();

            for (final Provider provider : p) {
                final ArrayList<String> algorithms = new ArrayList(provider.keySet());
                Collections.sort(algorithms);

                System.out.println(provider.getName());
                for (final String algorithm : algorithms) {
                    if (algorithm.startsWith("Cipher")) {
                        System.out.println("\t" + algorithm);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    */

}
