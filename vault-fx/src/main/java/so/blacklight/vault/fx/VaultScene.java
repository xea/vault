package so.blacklight.vault.fx;

/**
 * Created by specsi on 10/12/2015.
 */
public enum VaultScene {

    WELCOME("welcome.fxml", "Welcome"),
    AUTHENTICATION("auth.fxml", "Authentication");

    private final String xmlName;

    private final String defaultTitle;

    VaultScene(final String xmlName, final String defaultTitle) {
        this.xmlName = xmlName;
        this.defaultTitle = defaultTitle;
    }

    public String getXmlName() {
        return xmlName;
    }

    public String getDefaultTitle() {
        return defaultTitle;
    }
}

