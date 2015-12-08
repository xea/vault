package so.blacklight.vault.cli;

import so.blacklight.vault.locale.I18n;
import so.blacklight.vault.locale.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Optional;

public class Console {

    private I18n i18n = new I18n();

    public String askInput(final String prompt) {
        System.out.print(prompt + ": ");

        if (System.console() != null) {
            return System.console().readLine();
        } else {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            try {
                final String input = reader.readLine();

                reader.close();

                return input;
            } catch (IOException e) {
                error(e.getMessage());
            }
        }

        return null;
    }

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
        Arrays.asList(messages).forEach(msg -> System.out.println(i18n.t(Message.GENERIC_ERROR, msg)));
    }
}
