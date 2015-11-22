package so.blacklight.vault.fx;

import fj.data.Either;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import so.blacklight.vault.*;
import so.blacklight.vault.store.StreamVaultStore;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Optional;
import java.util.ResourceBundle;

import static javafx.stage.FileChooser.ExtensionFilter;

public class AuthController implements Initializable {

    @FXML
    private Label lblVaultPath;

    @FXML
    private Label lblPrivateKeyPath;

    @FXML
    private Button btnBrowseVault;

    @FXML
    private Button btnBrowsePrivateKey;

    @FXML
    private Button btnOk;

    @FXML
    private Button btnQuit;

    @FXML
    private Button btnHelp;

    @FXML
    private PasswordField pwdPassword;

    @FXML
    private PasswordField pwdOtp;

    @FXML
    private GridPane gridAuth;

    private Optional<File> maybeVaultFile = Optional.empty();

    private Optional<File> maybePrivateKeyFile = Optional.empty();

    public void browseVaultFile(final ActionEvent event) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open vault file");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Vault Files", "*.vlt"),
                new ExtensionFilter("All Files", "*.*"));

        File selectedFile = fileChooser.showOpenDialog(btnHelp.getScene().getWindow());

        if (selectedFile != null) {
            maybeVaultFile = Optional.of(selectedFile);
            lblVaultPath.setText(selectedFile.getAbsolutePath());
        }
    }

    public void browsePrivateKey(final ActionEvent event) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open private key file");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Private Key Files", "*.key"),
                new ExtensionFilter("All Files", "*.*"));

        File selectedFile = fileChooser.showOpenDialog(btnHelp.getScene().getWindow());

        if (selectedFile != null) {
            maybePrivateKeyFile = Optional.of(selectedFile);
            lblPrivateKeyPath.setText(selectedFile.getAbsolutePath());
        }
    }

    public void openVault(final ActionEvent event) {
        if (maybeVaultFile.isPresent() && maybeVaultFile.get().exists()) {
            try {
                final VaultStore store = new StreamVaultStore();

                final Credentials credentials = new Credentials();
                credentials.add(new Password(pwdPassword.getText().toCharArray()));
                credentials.add(new PrivateKey(Files.readAllBytes(maybeVaultFile.get().toPath())));

                Either<String, Vault> load = store.load(credentials, maybeVaultFile.get());

                if (load.isRight()) {
                    
                } else {
                    final Alert alert = new Alert(Alert.AlertType.ERROR, "Error during opening vault: " + load.left().value(), ButtonType.OK);
                    alert.show();
                }
            } catch (IOException e) {
                final Alert alert = new Alert(Alert.AlertType.ERROR, "Couldn't read selected vault file", ButtonType.OK);
                alert.show();
            }
        } else {
            final Alert alert = new Alert(Alert.AlertType.ERROR, "Couldn't find selected vault file", ButtonType.OK);
            alert.show();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }


}
