package so.blacklight.vault.fx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.time.Instant;

import static javafx.stage.FileChooser.ExtensionFilter;

public class WelcomeController extends BaseController {

    @FXML
    private Hyperlink linkCreateNew;

    @FXML
    private Hyperlink linkOpenVault;

    @FXML
    private ListView listRecentEntries;

    public void createNewVault(final ActionEvent event) {
        final FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Create new Vault");
        fileChooser.setInitialDirectory(new File("."));
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Vault Files", "*.vlt"), new ExtensionFilter("All Files", "*.*"));

        File selectedFile = fileChooser.showSaveDialog(linkCreateNew.getScene().getWindow());

        listRecentEntries.getItems().add(Instant.now().toString());
    }

    public void openVault(final ActionEvent event) throws IOException {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Vault");
        fileChooser.setInitialDirectory(new File("."));
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Vault Files", "*.vlt"), new ExtensionFilter("All Files", "*.*"));

        File selectedFile = fileChooser.showOpenDialog(linkOpenVault.getScene().getWindow());

        if (selectedFile != null) {
            if (selectedFile.exists()) {
                switchScene(event, VaultScene.AUTHENTICATION);
            } else {
                final Alert alert = new Alert(Alert.AlertType.ERROR, "File does not exist", ButtonType.OK);
                alert.showAndWait();
            }
        }
    }


}
