package so.blacklight.vault;

import so.blacklight.vault.entry.Folder;

import java.util.Optional;

public class SimpleVault implements Vault {

    public static final long serialVersionUID = 87124623122L;

    private final Segment segment;

    private final String name;

    public SimpleVault(final String name) {
        this.name = name;
        segment = new Segment();
    }

    public String getName() {
        return name;
    }

}
