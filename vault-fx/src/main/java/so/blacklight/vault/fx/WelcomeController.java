package so.blacklight.vault.fx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.stage.FileChooser;

import java.io.File;

import static javafx.stage.FileChooser.ExtensionFilter;

public class WelcomeController {

    @FXML
    private Hyperlink linkCreateNew;

    @FXML
    private Hyperlink linkOpenVault;

    public void createNewVault(final ActionEvent event) {
        final FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Create new Vault");
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Vault Files", "*.vlt"), new ExtensionFilter("All Files", "*.*"));

        File selectedFile = fileChooser.showSaveDialog(linkCreateNew.getScene().getWindow());

    }

    public void openVault(final ActionEvent event) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Vault");
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Vault Files", "*.vlt"), new ExtensionFilter("All Files", "*.*"));

        File selectedFile = fileChooser.showOpenDialog(linkOpenVault.getScene().getWindow());

        if (selectedFile != null) {
            if (selectedFile.exists()) {
                openMain(selectedFile);
            } else {
                final Alert alert = new Alert(Alert.AlertType.ERROR, "File does not exist", ButtonType.OK);
                alert.showAndWait();
            }
        }
    }

    protected void openMain(final File vaultFile) {

    }

}
