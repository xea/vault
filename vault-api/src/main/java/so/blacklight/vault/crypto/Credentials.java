package so.blacklight.vault.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * A collection of user-supplied credentials, such as passwords, private keys, etc. that
 * can be used to derive encryption/decryption keys.
 */
public class Credentials {

    private Set<Credential> credentials;

    /**
     * Initialise an empty collection.
     */
    public Credentials() {
        final Comparator<Credential> comparator = (c1, c2) -> {
            final String HASH_ALGORITHM = "SHA-256";

            try {
                final MessageDigest md1 = MessageDigest.getInstance(HASH_ALGORITHM);
                final MessageDigest md2 = MessageDigest.getInstance(HASH_ALGORITHM);

                md1.update(c1.getBytes());
                md2.update(c2.getBytes());

                final String digest1 = new String(md1.digest());
                final String digest2 = new String(md2.digest());

                return digest1.compareTo(digest2);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            return 0;
        };

        credentials = new TreeSet<>(comparator);
    }
    
    /**
     * Initialise a new credentials collection with the given items
     * 
     * @param newCredentials new credentials
     */
    public Credentials(final Collection<Credential> newCredentials) {
    	this();
    	credentials.addAll(newCredentials);
    }

    /**
     * Add a new credential to the collection
     *
     * @param credential credential to add
     * @return <code>true</code> if the item was added, otherwise <code>false</code>
     */
    public boolean add(final Credential credential) {
        return credentials.add(credential);
    }

    /**
     * Return a sorted set of credentials.
     *
     * Please note that the sorting can be done arbitrarily as long as it's consistent.
     *
     * @return sorted set of credentials
     */
    public Set<Credential> getCredentials() {
        return credentials;
    }

}
