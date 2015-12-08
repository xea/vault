package so.blacklight.vault.locale;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Provides a simple interface for supporting internationalisation within the application.
 *
 */
public class I18n {

    public static final String BUNDLE_NAME = "messages";

    private final ResourceBundle resourceBundle;

    private final Locale locale;

    public I18n() {
        this(Locale.getDefault());
    }

    public I18n(final String language, final String country) {
        this(new Locale(language, country));
    }

    public I18n(final Locale locale) {
        resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
        this.locale = locale;
    }

    public String t(final Message msgId, final Object... args) {
        return t(msgId.getKey(), args);
    }

    private String t(final String key, final Object... args) {
        final MessageFormat format = new MessageFormat(resourceBundle.getString(key), locale);

        final String message = format.format(args);

        return message;
    }

    public Locale getLocale() {
        return locale;
    }
}
