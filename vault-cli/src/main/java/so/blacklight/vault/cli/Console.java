package so.blacklight.vault.cli;

import so.blacklight.vault.locale.I18n;
import so.blacklight.vault.locale.Message;

import java.io.*;
import java.util.Arrays;
import java.util.Optional;

/**
 * Provides easy access to some frequently used console-related functions.
 */
public class Console {

    private I18n i18n = new I18n();

    /**
     * Display a prompt message and ask the user for an answer
     *
     * @param prompt text to display
     * @return user input
     */
    public String askInput(final String prompt) {
        System.out.print(prompt + ": ");

        if (System.console() != null) {
            return System.console().readLine();
        } else {
            try {
                final String input = readLine();

                return input;
            } catch (IOException e) {
                error(e.getMessage());
            }
        }

        return null;
    }

    /**
     * Display a prompt message and ask the user for an answer but the answer is not echoed on the screen.
     *
     * @param prompt text to display
     * @return user input
     */
    public Optional<char[]> askPassword(final String prompt) {
        System.out.print(prompt + ": ");

        if (System.console() != null) {
            final char[] password = System.console().readPassword();

            return Optional.of(password);
        } else {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            try {
                final String input = reader.readLine();

                return Optional.of(input.toCharArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return Optional.empty();
    }

    public void out(final String... messages) {
        Arrays.asList(messages).forEach(System.out::println);
    }

    public void error(final String... messages) {
        Arrays.asList(messages).forEach(msg -> out(i18n.t(Message.GENERIC_ERROR, msg)));
    }

    protected String readLine() throws IOException {
        final InputStream in = System.in;

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int c;

        while ((c = in.read()) != -1) {
            if (c == '\n') {
                // newline found
                break;
            } else if (c != '\r') {
                baos.write(c);
            }
        }

        return baos.toString().trim();
    }
}
