package so.blacklight.vault;

import javax.crypto.Cipher;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class VaultStore {

    public static final int VAULT_MAGIC = 0xEAFF60;

    public static final byte[] magicBytes = ByteBuffer.allocate(4).putInt(VAULT_MAGIC).array();

}
