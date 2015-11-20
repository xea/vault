package so.blacklight.vault;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A collection of user-supplied credentials, such as passwords, private keys, etc. that
 * can be used to derive encryption/decryption keys.
 */
public class Credentials {

    private List<Credential> credentials;

    /**
     * Initialise an empty collection.
     */
    public Credentials() {
        credentials = new ArrayList<>();
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
        sortCredentials();
        return credentials.add(credential);
    }

    /**
     * Return a sorted list of credentials.
     *
     * Please note that the sorting can be done arbitrarily as long as it's consistent.
     *
     * @return sorted list of credentials
     */
    public List<Credential> getCredentials() {
        return credentials;
    }
    
    private void sortCredentials() {
        credentials.sort( (a, b) ->
                (Integer.valueOf(a.hashCode()) .compareTo(Integer.valueOf(b.hashCode()))));
    }


}
