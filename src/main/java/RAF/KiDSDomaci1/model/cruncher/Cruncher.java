package RAF.KiDSDomaci1.model.cruncher;

import RAF.KiDSDomaci1.model.input.ObjectToProcess;
import RAF.KiDSDomaci1.view.CruncherView;
import RAF.KiDSDomaci1.view.MainView;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;

public class Cruncher extends CruncherAbstract {

	private String name;
	private static int cruncherAmount = 0;
	private CruncherView cruncherView;
	private CopyOnWriteArrayList<String> textFields;
	private CopyOnWriteArrayList<String> textComponents;

	public Cruncher(int arity) {
		super(arity);
		this.name = "Cruncher " + cruncherAmount++;
		textFields = new CopyOnWriteArrayList<>();
		textComponents = new CopyOnWriteArrayList<>();
	}
	
	@Override
	public String toString() {
		return name;
	}

	@Override
	public void run() {

		while(true) {

			try {
				ObjectToProcess objectToProcess = getProcessQueue().take();
//
				Future<Map<String, Integer>> countJob = MainView.getInstance().getCruncherThreadPool().submit(new CruncherTask(objectToProcess, 0, objectToProcess.getContent().length(), getArity()));
//
				Map<String, Future<Map<String, Integer>>> countFuture = new HashMap<>();
				countFuture.put(objectToProcess.getFilePath().split("\\\\")[objectToProcess.getFilePath().split("\\\\").length - 1] + "-arity" + getArity(), countJob);
				MainView.getInstance().getFileOutput().getOutputProcessQueue().put(countFuture);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	public CruncherView getCruncherView() {
		return cruncherView;
	}

	public void setCruncherView(CruncherView cruncherView) {
		this.cruncherView = cruncherView;
	}
	@Override
	protected String call() throws Exception {
		return null;
	}
}
