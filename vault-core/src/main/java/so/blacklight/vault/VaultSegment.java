package so.blacklight.vault;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class VaultSegment implements Serializable {

    public static final long serialVersionUID = 10010l;

    private List<Folder> folders;

    public VaultSegment() {
        folders = new CopyOnWriteArrayList<>();
    }

    public List<Folder> getFolders() {
        return folders;
    }
}
