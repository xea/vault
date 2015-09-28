package so.blacklight.vault;

import so.blacklight.vault.entry.VaultEntry;

import java.io.Serializable;
import java.util.List;

public class Folder implements Serializable {

    public static final long serialVersionUID = 10010;

    private String name;

    private List<VaultEntry> entries;
}
