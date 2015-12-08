package so.blacklight.vault.cli;

import java.util.Optional;

import com.github.jankroken.commandline.CommandLineParser;
import com.github.jankroken.commandline.OptionStyle;

import com.github.jankroken.commandline.domain.InvalidCommandLineException;
import com.github.jankroken.commandline.domain.UnrecognizedSwitchException;
import so.blacklight.vault.VaultException;
import so.blacklight.vault.command.*;
import so.blacklight.vault.locale.I18n;
import so.blacklight.vault.locale.Message;

/**
 * Main executable class, intended to be called from the command line.
 */
public class VaultCLI {

    private final Console console = new Console();

    private final I18n i18n = new I18n();

    public static void main(final String[] args) throws VaultException {
        final VaultCLI cli = new VaultCLI();
        final Options options = cli.processArgs(args);
        final CLICommand cliCommand = cli.processRequest(options);

        cli.runCommand(cliCommand);
    }

    public Options processArgs(final String[] args) {
        try {
            final Options options = CommandLineParser.parse(Options.class, args, OptionStyle.SIMPLE);

            return options;
        } catch (InvalidCommandLineException e) {
            console.error("Invalid command line exception " + e.getMessage());
        } catch (UnrecognizedSwitchException e) {
            console.error("Unrecognised switch exception " + e.getLocalizedMessage());
        } catch (Exception e) {
            console.error(i18n.t(Message.GENERIC_EXCEPTION, e.getLocalizedMessage()));
        }

        return new Options();
    }


    public CLICommand processRequest(final Options options) {
        final CLICommand cmd;

        if (options.isHelpRequested()) {
            cmd = new ShowHelp(options);
        } else {
            switch (options.getAction()) {
                case CREATE_VAULT:
                    cmd = new CreateVault(options);
                    break;
                case CREATE_ENTRY:
                    cmd = new CreateEntry(options);
                    break;
                case CREATE_FOLDER:
                    cmd = new CreateFolder(options);
                    break;
                case GENERATE_KEY:
                    cmd = new GenerateKeys(options);
                    break;
                case LIST_ENTRIES:
                    cmd = new ListEntries(options);
                    break;
                case SHOW_ENTRY:
                    cmd = new ShowEntry(options);
                    break;
                case SHOW_INFO:
                    cmd = new ShowInfo(options);
                    break;
                case DEFAULT_ACTION:
                default:
                    cmd = new ShowHelp(options);
                    break;
            }
        }

        return cmd;
    }

    public void runCommand(final CLICommand command) {
        final Optional<String> validationResult = command.validate();

        if (validationResult.isPresent()) {
            console.error(validationResult.get());
        } else {
            try {
                command.execute();
            } catch (VaultException e) {
                console.error(e.toLongString());
            }
        }
    }
}
