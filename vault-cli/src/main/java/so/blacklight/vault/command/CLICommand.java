package so.blacklight.vault.command;

import so.blacklight.vault.VaultException;

import java.util.Optional;

/**
 * Represents an arbitrary atomic (and possibly reversible) operation
 */
public interface CLICommand {

    /**
     * Perform the action defined by the implementation.
     *
     * @return <code>true</code> if execution was successful, otherwise <code>false</code>
     * @throws VaultException
     */
    boolean execute() throws VaultException;

    /**
     * Perform an undo operation that aims for reversing a previous invocation of <code>execute</code>
     * on the same instance.
     *
     * Note: <code>isUndoable()</code> should indicate if this method supports undoing.
     *
     * @return <code>true</code> if reversing was successful, otherwise <code>false</code>
     */
    boolean undo();

    /**
     * Indicates if this operation is reversible.
     *
     * If calling this method returns <code>true</code> then calling <code>undo()</code> will
     * undo the effect of a previous <code>execute()</code> call.
     *
     * @return <code>true</code> if undoing was successful, otherwose <code>false</code>
     */
    boolean isUndoable();

    /**
     * Attempt to validate the passed command-line options and return a list of error
     * messages.properties in case the validation fails.
     *
     * @return <code>Optional.empty()</code> if no errors were found, otherwise a String describing
     * the errors
     */
    Optional<String> validate();
}
