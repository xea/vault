package so.blacklight.crypto;

public interface Credentials {
    
    EncryptionParams generateParams();

    byte[] generateKey(byte[] seed);

}
