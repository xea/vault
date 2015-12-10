package so.blacklight.vault;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;

public class RSATester {

    public static void main2(final String[] args) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
        final KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(4096);
        KeyPair keyPair = kpg.generateKeyPair();

        System.out.println(keyPair.getPrivate().getFormat());
        System.out.println(keyPair.getPublic().getFormat());

        final File outPub = new File("/Users/specsi/workspaces/java/vault/out_public.der");
        final File outPriv = new File("/Users/specsi/workspaces/java/vault/out_private.pk8");

        final OutputStream ospub = new FileOutputStream(outPub);
        final OutputStream ospriv = new FileOutputStream(outPriv);

        ospriv.write(Base64.encodeBase64(keyPair.getPrivate().getEncoded()));
        ospub.write(keyPair.getPublic().getEncoded());

        ospub.close();
        ospriv.close();
    }

    public static void main(final String[] args) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
        final File privFile = new File("/Users/specsi/workspaces/java/vault/out_private.pk8");
        final File pubFile = new File("/Users/specsi/workspaces/java/vault/out_public.der");
        final byte[] privBytes = Base64.decodeBase64(Files.readAllBytes(privFile.toPath()));
        final byte[] pubBytes = Files.readAllBytes(pubFile.toPath());
        final KeySpec publicKeySpec = new X509EncodedKeySpec(pubBytes);
        final KeySpec privateKeySpec = new PKCS8EncodedKeySpec(privBytes);

        final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        final KeyPairGenerator kpGenerator = KeyPairGenerator.getInstance("RSA");

        final Key publicKey = keyFactory.generatePublic(publicKeySpec);
        final Key privateKey = keyFactory.generatePrivate(privateKeySpec);

        final Cipher encCipher = Cipher.getInstance("RSA");
        encCipher.init(Cipher.ENCRYPT_MODE, publicKey);

        final File cipherFile = new File("/Users/specsi/workspaces/java/vault/output.enc");
        final FileOutputStream fos = new FileOutputStream(cipherFile);
        final CipherOutputStream cos = new CipherOutputStream(fos, encCipher);

        cos.write("Hello bello".getBytes());

        cos.close();
        fos.close();

        final Cipher deCipher = Cipher.getInstance("RSA");
        deCipher.init(Cipher.DECRYPT_MODE, privateKey);

        final FileInputStream fis = new FileInputStream(cipherFile);
        final CipherInputStream cis = new CipherInputStream(fis, deCipher);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(cis));

        final String line = reader.readLine();

        System.out.println(line);

        reader.close();
        cis.close();
        fis.close();
    }


}

