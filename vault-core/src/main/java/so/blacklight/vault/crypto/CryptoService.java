package so.blacklight.vault.crypto;

import so.blacklight.crypto.Credentials;
import so.blacklight.crypto.Decryptor;
import so.blacklight.crypto.EncryptionParams;
import so.blacklight.crypto.Encryptor;
import so.blacklight.vault.Entry;
import so.blacklight.vault.SecretEntry;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class CryptoService<T extends Entry> implements Encryptor<T>, Decryptor<T> {

    @Override
    public T decrypt(SecretEntry secret, Credentials credentials) {
        final Cipher cipher;
        try {
            final EncryptionParams params = secret.getParams();
            final char[] password = null; //credentials.getKey();
            final byte[] salt = params.getSalt();
            final byte[] iv = params.getIv();
            final SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            final KeySpec keySpec = new PBEKeySpec(password, salt, 65536, 256);
            final SecretKey tmpKey = factory.generateSecret(keySpec);
            final SecretKey secretKey = new SecretKeySpec(tmpKey.getEncoded(), "AES");
            final IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            final Object readObject = secret.getSecret().getObject(cipher);

            return (T) readObject;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public SecretEntry encrypt(T entry, Credentials credentials) {
        try {
            final EncryptionParams params = credentials.generateParams();
            final byte[] password = credentials.generateKey(params.getSalt()); //credentials.getKey();
            final byte[] salt = params.getSalt();
            final byte[] iv = params.getIv();
            final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            final KeySpec keySpec = new
            //final KeySpec keySpec = new PBEKeySpec(password, salt, 65536, 256);
            final SecretKey tmpKey = keyFactory.generateSecret(keySpec);
            final SecretKey secretKey = new SecretKeySpec(tmpKey.getEncoded(), "AES");
            final IvParameterSpec ivSpec = new IvParameterSpec(iv);
            final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

            final SealedObject sealedObject = new SealedObject(entry, cipher);
            final SecretEntry secretEntry = new SecretEntry(entry.getAlias(), sealedObject, params);

            return secretEntry;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        return null;
    }
}
