package so.blacklight.vault.cli.locale;

import org.junit.Test;
import so.blacklight.vault.locale.I18n;
import so.blacklight.vault.locale.Message;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class I18nTest {

    @Test
    public void i18nShouldReturnTheDefaultTextIfNoLocaleWasSpecified() {
        final I18n i = new I18n();

        final String result = i.t(Message.GENERIC_EXCEPTION, new NullPointerException());
        assertNotNull(result);
        assertTrue(result.matches(".*NullPointerException.*"));
    }
}
