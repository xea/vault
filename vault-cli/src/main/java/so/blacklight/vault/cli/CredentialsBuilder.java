package so.blacklight.vault.cli;

import fj.data.Either;
import so.blacklight.vault.Credential;
import so.blacklight.vault.crypto.AESKey;
import so.blacklight.vault.crypto.KeyManager;
import so.blacklight.vault.crypto.Password;
import so.blacklight.vault.locale.I18n;
import so.blacklight.vault.locale.Message;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class CredentialsBuilder {

    private final Console console = new Console();

    private final I18n i18n = new I18n();

    public Either<String, Credential> requirePassword() {
        final Optional<char[]> maybePassword = console.askPassword(i18n.t(Message.PROMPT_ENTER_PASSWORD));

        if (maybePassword.isPresent()) {
            return Either.right(new Password(maybePassword.get()));
        } else {
            return Either.left("Couldn't read password");
        }
    }

    public Either<String, Credential> requireAESKey() {
        final String privateKeyPath = console.askInput("Enter path to key file");
        final File keyFile = new File(privateKeyPath);

        if (keyFile.exists()) {
            try {
                final Path keyPath = keyFile.toPath();
                final byte[] bytes = Files.readAllBytes(keyPath);
                return Either.right(new AESKey(bytes));
            } catch (IOException e) {
                return Either.left(e.getLocalizedMessage());
            }
        } else {
            console.error("The specified keyfile does not exist");
        }

        return Either.left("AES keys not implemented");
    }

    public Either<String, Credential> requireRSAPrivateKey() {
        final String keyPath = console.askInput("Enter path to private key file");
        final File keyFile = new File(keyPath);

        if (keyFile.exists()) {
            try {
                final KeyManager keyManager = new KeyManager();
                return Either.right(keyManager.loadRSAPrivateKey(keyFile));
            } catch (IOException e) {
                return Either.left(e.getLocalizedMessage());
            } catch (Exception e) {
                return Either.left(e.getLocalizedMessage());
            }
        }

        return Either.left("Keyfile doesn't exist");
    }

    public Either<String, Credential> requireRSAPublicKey() {
        final String keyPath = console.askInput("Enter path to public key file");
        final File keyFile = new File(keyPath);

        if (keyFile.exists()) {
            try {
                final KeyManager keyManager = new KeyManager();
                return Either.right(keyManager.loadRSAPublicKey(keyFile));
            } catch (IOException e) {
                return Either.left(e.getLocalizedMessage());
            } catch (Exception e) {
                return Either.left(e.getLocalizedMessage());
            }
        }

        return Either.left("Keyfile doesn't exist");
    }

    public Either<String, Credential> requireHMACToken() {
        return Either.left("HMAC tokens aren't supported yet");
    }
}
