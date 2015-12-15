package so.blacklight.vault.crypto;

import so.blacklight.vault.collection.Tuple2;

import java.io.*;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Optional;

public class KeyManager {

    public Tuple2<RSAPrivateKey, RSAPublicKey> generateRSAKeyPair(final int keyLength) throws NoSuchAlgorithmException {
        final KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(keyLength);
        final KeyPair keyPair = generator.generateKeyPair();

        final RSAPrivateKey privateKey = new RSAPrivateKey(keyPair.getPrivate().getEncoded());
        final RSAPublicKey publicKey = new RSAPublicKey(keyPair.getPublic().getEncoded());

        final Tuple2<RSAPrivateKey, RSAPublicKey> result = new Tuple2<>(privateKey, publicKey);

        return result;
    }

    public RSAPublicKey loadRSAPublicKey(File source) throws IOException {
        final byte[] rawBytes = Files.readAllBytes(source.toPath());

        final RSAPublicKey publicKey = new RSAPublicKey(rawBytes);
        return publicKey;
    }

    public RSAPrivateKey loadRSAPrivateKey(final File source) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        final byte[] rawBytes = Base64.getDecoder().decode(stripPemHeaders(Files.readAllBytes(source.toPath())));
        /*final KeySpec keySpec = new PKCS8EncodedKeySpec(rawBytes);

        final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        final PrivateKey privateKey = keyFactory.generatePrivate(keySpec);*/

        final RSAPrivateKey result = new RSAPrivateKey(rawBytes);

        return result;
    }

    public void saveRSAPublicKey(RSAPublicKey key, File target) throws IOException {
        Files.write(target.toPath(), key.getBytes());
    }


    public void saveRSAPrivateKey(final RSAPrivateKey key, final File target) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final String header = "-----BEGIN RSA PRIVATE KEY-----\n";
        final String footer = "\n-----END RSA PRIVATE KEY-----";
        baos.write(header.getBytes());
        baos.write(Base64.getEncoder().encode(key.getBytes()));
        baos.write(footer.getBytes());
        baos.flush();
        Files.write(target.toPath(), baos.toByteArray());
        baos.close();;
    }

    private byte[] stripPemHeaders(final byte[] input) {
        if (input[0] == '-') {
            final ByteArrayInputStream baos = new ByteArrayInputStream(input);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(baos));

            // eg: -----BEGIN RSA PRIVATE KEY-----
            final String headerPattern = "^-----(BEGIN|END) ([A-Z0-9-]+) PRIVATE KEY-----";

            final Optional<String> key = reader.lines().filter(line -> !line.matches(headerPattern)).reduce((a, b) -> a + b);

            if (key.isPresent()) {
                return key.get().getBytes();
            } else {
                return input;
            }
        } else {
            return input;
        }
    }

}
