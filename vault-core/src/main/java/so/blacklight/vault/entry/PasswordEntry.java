package so.blacklight.vault.entry;

import java.time.Instant;
import java.util.Optional;

public class PasswordEntry extends VaultEntry {

    private String user;

    private String password;

    public PasswordEntry(final String user, final String password, final String recoveryInfo) {
        super(null, recoveryInfo);

        this.user = user;
        this.password = password;
    }

    public PasswordEntry(final String user, final String password, final Instant expirationTime, final String recoveryInfo) {
        super(expirationTime, recoveryInfo);

        this.user = user;
        this.password = password;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

}
