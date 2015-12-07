package so.blacklight.vault.command;

import so.blacklight.vault.cli.Options;

import java.util.Optional;

public class ShowHelp extends VaultCommand {

    public ShowHelp(final Options options) {
        super(options);
    }

    @Override
    public boolean execute() {
        if (options.getAction() == Options.Action.DEFAULT_ACTION) {
            final String[] messages = new String[] {
                    "Usage: ",
                    "    vault -create -v <vault> [OPTS]                                 Create new vault",
                    "    vault -list -v <vault> [OPTS]                                   List vault entries",
                    "    vault -create-entry -a <alias> -t <TYPE> -v <vault> [OPTS]      Create a new vault entry",
                    "    vault -info -v <vault>                                          Show information about the selected vault",
                    "    vault -show-entry -a <alias> -v <vault>                         Show the specified entry",
                    "", "",
                    "  Possible OPTS are:",
                    "    -f <folder alias>      Specify the current folder",
                    "    -r                     Activate recovery segment",
                    "    -k <keyfile>           Use keyfile for encryption/decryption",
                    "    -d                     Activate degraded segment",
                    "    -m [METHODS]           Use specified authentication methods",
                    "",
                    "  Possible METHODS are:",
                    "    pw                     Use password-based encryption",
                    "    aes                    Use AES key file-based encryption",
                    "    rsa                    Use RSA key file-based encryption",
                    "",
                    "  Possible TYPEs are:",
                    "    password"
            };

            console.out(messages);
        } else {
            console.out("HELP MESSAGE ABOUT: " + options.getAction().name());
        }
        return false;
    }

    @Override
    public boolean undo() {
        return false;
    }

    @Override
    public boolean isUndoable() {
        return false;
    }

    @Override
    public Optional<String> validate() {
        return Optional.empty();
    }

}
