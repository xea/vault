package so.blacklight.vault.fx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import so.blacklight.vault.Vault;
import so.blacklight.vault.VaultStore;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class WelcomeController implements Initializable {

    @FXML
    private Button btnNewVault;

    @FXML
    private Button btnBrowseVault;

    @FXML
    private Button btnBrowseKey;

    @FXML
    private Button btnUnlockVault;

    @FXML
    private TextField txtVaultPath;

    @FXML
    private TextField txtPrivateKeyPath;

    @FXML
    private PasswordField pwdOtp;

    @FXML
    private PasswordField pwdPassword;

    public void unlockVault(final ActionEvent event) throws IOException, ClassNotFoundException {
        final File vaultFile = new File(txtVaultPath.getText());

        if (vaultFile.exists()) {
            final Optional<Vault> vault = new VaultStore().load(vaultFile);

            if (vault.isPresent()) {

            }
        } else {
            // ERROR File does not exist
        }
    }

    public void createVault(final ActionEvent event) {
        final Vault vault = new Vault();
        openVault(vault);
    }

    public void browseVault(final ActionEvent event) {
    }

    public void browseKey(final ActionEvent event) {

    }

    protected void openVault(final Vault vault) {
        try {
            final Parent root = FXMLLoader.load(getClass().getResource("/vault.fxml"));
            final Scene scene = new Scene(root);
            final Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("initialize");
    }
}
