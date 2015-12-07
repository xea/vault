package so.blacklight.vault.command;

import so.blacklight.vault.cli.Options;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;

public class GenerateKeys extends VaultCommand {

    public GenerateKeys(final Options options) {
        super(options);
    }

    @Override
    public boolean execute() {
        final String keyType = options.getKeyType().get().toLowerCase();

        if ("aes".equals(keyType)) {
            generateAESKey();
        } else if ("rsa".equals(keyType)) {
            generateRSAKeys();
        } else {
            console.error("Unknown key type: " + keyType);
        }
        return false;
    }

    @Override
    public boolean undo() {
        return false;
    }

    @Override
    public boolean isUndoable() {
        return false;
    }

    @Override
    public Optional<String> validate() {
        return Optional.empty();
    }

    private void generateRSAKeys() {
        try {
            final String publicPath = console.askInput("Public key save path");
            final String privatePath = console.askInput("Private key save path");

            final File publicFile = new File(publicPath);
            final File privateFile = new File(privatePath);

            if (publicFile.exists()) {
                console.error("File already exists: " + publicFile.getAbsolutePath());
            } else if (privateFile.exists()) {
                console.error("File already exists: " + privateFile.getAbsolutePath());
            } else {
                final KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
                generator.initialize(4096);

                final KeyPair keyPair = generator.generateKeyPair();

                Files.write(privateFile.toPath(), Base64.getEncoder().encode(keyPair.getPrivate().getEncoded()));
                Files.write(publicFile.toPath(), keyPair.getPublic().getEncoded());

                console.out("Keys have been created");
            }
        } catch (NoSuchAlgorithmException e) {
            console.error(e.getMessage());
        } catch (IOException e) {
            console.error(e.getMessage());
        }
    }

    private void generateAESKey() {
        try {
            final String keyPath = console.askInput("Key save path");
            final File keyFile = new File(keyPath);

            if (keyFile.exists()) {
                console.error("File already exists: " + keyFile.getAbsolutePath());
            } else {
                final KeyGenerator generator = KeyGenerator.getInstance("AES");
                generator.init(256);
                final SecretKey secretKey = generator.generateKey();

                Files.write(keyFile.toPath(), secretKey.getEncoded());
            }
        } catch (NoSuchAlgorithmException e) {
            console.error(e.getMessage());
        } catch (IOException e) {
            console.error(e.getMessage());
        }
    }

}
