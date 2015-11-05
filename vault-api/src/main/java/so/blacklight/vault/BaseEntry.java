package so.blacklight.vault;

import java.time.Instant;

public abstract class BaseEntry implements Entry, Metadata {

    private String alias;

    private String comment;

    private Instant createTime;

    private Instant modifyTime;

    private Instant accessTime;

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public Instant getCreateTime() {
        return createTime;
    }

    @Override
    public Instant getModifyTime() {
        return modifyTime;
    }

    @Override
    public Instant getAccessTime() {
        return accessTime;
    }

    @Override
    public String getComment() {
        return comment;
    }
}
