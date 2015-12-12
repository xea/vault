package so.blacklight.vault.fx;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class BaseController {

    public void switchScene(final ActionEvent event, final VaultScene scene) throws IOException {
        switchScene(event, scene, scene.getDefaultTitle());
    }

    public void switchScene(final ActionEvent event, final VaultScene scene, final String title) throws IOException {
        switchScene(event, scene.getXmlName(), title);
    }

    private void switchScene(final ActionEvent event, final String fxmlName, final String title) throws IOException {
        final Parent sceneGraph = FXMLLoader.load(getClass().getResource(fxmlName));
        final Scene authScene = new Scene(sceneGraph);
        final Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        appStage.setTitle(title);
        appStage.setScene(authScene);
        appStage.show();
    }
}
