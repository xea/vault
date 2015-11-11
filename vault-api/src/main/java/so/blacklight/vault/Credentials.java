package so.blacklight.vault;

import java.util.ArrayList;
import java.util.List;

public class Credentials {

    private List<Credential> credentials;

    public Credentials() {
        credentials = new ArrayList<>();
    }

    public boolean add(final Credential credential) {
        sortCredentials();
        return credentials.add(credential);
    }

    public List<Credential> getCredentials() {
        return credentials;
    }

    protected void sortCredentials() {
        credentials.sort((a, b) -> (Integer.valueOf(a.hashCode()).compareTo(Integer.valueOf(b.hashCode()))));
    }
}
