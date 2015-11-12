package so.blacklight.vault;

/**
 * A credential is a piece of information that proves a person's identity
 * and authority to access secure data.
 *
 * Note: implementations of this interface are expected to be immutable.
 */
public interface Credential {

    /**
     * Indicates whether the stored information is "user-generated" (aka passwords)
     * or "computer-generated" (aka secret keys, hashes, etc).
     *
     * This information should be used to decide if the stored data needs additional
     * processing (eg. hashing) before use.
     *
     * @return <code>true</code> if this data is user-generated, otherwise <code>false</code>
     */
    boolean isUserInput();

    /**
     * Return the stored credential in the form of a byte array.
     *
     * @return credential data
     */
    byte[] getBytes();

}
