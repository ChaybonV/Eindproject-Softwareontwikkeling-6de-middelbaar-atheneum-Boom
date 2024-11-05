package application.javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
  @Override
  public void start(Stage primaryStage) throws Exception {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("container.fxml"));
    Parent root = loader.load();
    Controller controller = loader.getController();
    Scene scene = new Scene(root);

    controller.initialize(scene);
    primaryStage.setTitle("Eindwerk Java RPG Game");
    primaryStage.setScene(scene);
    primaryStage.show();
  }
}