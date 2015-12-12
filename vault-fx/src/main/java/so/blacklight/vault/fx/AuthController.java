package so.blacklight.vault.fx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListView;

import java.io.IOException;

public class AuthController extends BaseController {

    @FXML
    private Button btnOk;

    @FXML
    private Button btnCancel;

    @FXML
    private Hyperlink linkAddPassword;

    @FXML
    private Hyperlink linkAddRSAKey;

    @FXML
    private Hyperlink linkAddHMAC;

    @FXML
    private ListView listAuthOptions;

    public void addPasswordProtection(final ActionEvent event) {

    }

    public void addRSAProtection(final ActionEvent event) {

    }

    public void addHMACProtection(final ActionEvent event) {

    }

    public void openVault(final ActionEvent event) {

    }

    public void cancelAuth(final ActionEvent event) throws IOException {
        switchScene(event, VaultScene.WELCOME);
    }
}
