package RAF.KiDSDomaci1.view;

import RAF.KiDSDomaci1.model.cruncher.Cruncher;
import RAF.KiDSDomaci1.model.input.FileInputWorker;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class CruncherView {
	private Cruncher cruncher;
	private Text status;

	private Pane main;

	public CruncherView(Cruncher cruncher) {
		this.cruncher = cruncher;
		initView();
		initThreads();
	}

	private void initThreads() {
		Thread thread = new Thread(cruncher);
		thread.start();
	}


	private void initView() {
		main = new VBox();

		Text text = new Text("Name: " + cruncher.toString());
		main.getChildren().add(text);
		VBox.setMargin(text, new Insets(0, 0, 2, 0));

		text = new Text("Arity: " + cruncher.getArity());
		main.getChildren().add(text);
		VBox.setMargin(text, new Insets(0, 0, 5, 0));

		Button remove = new Button("Remove cruncher");
		remove.setOnAction(e -> removeCruncher());
		main.getChildren().add(remove);
		VBox.setMargin(remove, new Insets(0, 0, 5, 0));

		status = new Text("");
		status.textProperty().bind(cruncher.messageProperty());
		main.getChildren().add(status);

		VBox.setMargin(main, new Insets(0, 0, 15, 0));
	}

	public Pane getMain() {
		return main;
	}

	public Pane getCruncherView() {
		return main;
	}

	private void removeCruncher() {
		MainView.getInstance().removeCruncher(this);
	}

	public Cruncher getCruncher() {
		return cruncher;
	}
}
