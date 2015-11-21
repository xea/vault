package so.blacklight.vault;

import java.io.Serializable;

public interface Entry extends Serializable {

    Metadata getMetadata();

}
