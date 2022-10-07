package RAF.KiDSDomaci1.app;

import RAF.KiDSDomaci1.view.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
    	BorderPane root = new BorderPane();
		Scene scene = new Scene(root, 1300, 800);
		scene.getRoot().setStyle("-fx-font-family: 'serif'");
		MainView mainView = MainView.getInstance();
		mainView.initMainView(root, stage);
		stage.setScene(scene);
		stage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, MainView.getInstance()::closeWindowEvent);
		stage.show();
    }

}