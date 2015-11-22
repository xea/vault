package so.blacklight.vault.fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class VaultFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("auth.fxml"));

        Scene scene = new Scene(root, 400, 240);

        primaryStage.setTitle("Open vault");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
