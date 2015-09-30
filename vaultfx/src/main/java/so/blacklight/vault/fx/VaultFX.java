package so.blacklight.vault.fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;

public class VaultFX extends Application {

    public static final String APPLICATION_TITLE = "VaultFX";

    public static void main(final String[] args) throws Exception {
        final List<String> argList = Arrays.asList(args);
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {
        final Parent root = FXMLLoader.load(getClass().getResource("/welcome.fxml"));
        final Scene scene = new Scene(root, 480, 300);

        primaryStage.setTitle(APPLICATION_TITLE);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
