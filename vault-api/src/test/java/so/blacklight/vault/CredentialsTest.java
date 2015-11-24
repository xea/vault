package so.blacklight.vault;

import junit.framework.Assert;
import org.junit.Test;
import so.blacklight.vault.crypto.Password;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CredentialsTest {

    @Test
    public void addedCredentialsShouldAlwaysGoInTheSameOrder() {
        final Credentials c1 = new Credentials();
        c1.add(new Password(new String("alma").toCharArray()));
        c1.add(new Password(new String("korte").toCharArray()));
        c1.add(new Password(new String("repa").toCharArray()));
        final Credentials c2 = new Credentials();
        c2.add(new Password(new String("alma").toCharArray()));
        c2.add(new Password(new String("korte").toCharArray()));
        c2.add(new Password(new String("repa").toCharArray()));

        for (int i = 0; i < c1.getCredentials().size(); i++) {
            final Credential cl1 = c1.getCredentials().get(i);
            final Credential cl2 = c2.getCredentials().get(i);
            assertArrayEquals(cl1.getBytes(), cl2.getBytes());
        }
    }
}
