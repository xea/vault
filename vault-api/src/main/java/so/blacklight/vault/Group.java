package so.blacklight.vault;

import java.time.Instant;
import java.util.List;

public class Group implements Entry, Metadata {

    public static final long serialVersionUID = -74737346276L;

    List<SecretEntry> getEntries() {
        return null;
    }

    @Override
    public String getAlias() {
        return null;
    }

    @Override
    public Instant getCreateTime() {
        return null;
    }

    @Override
    public Instant getModifyTime() {
        return null;
    }

    @Override
    public Instant getAccessTime() {
        return null;
    }

    @Override
    public String getComment() {
        return null;
    }
}
