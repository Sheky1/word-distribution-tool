package RAF.KiDSDomaci1.model.output;

import RAF.KiDSDomaci1.model.cruncher.Cruncher;
import RAF.KiDSDomaci1.model.cruncher.CruncherAbstract;
import RAF.KiDSDomaci1.view.MainView;
import javafx.application.Platform;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ResultGetter implements Runnable {
    private final Map<String, Future<Map<String, Integer>>> countFutureMap;
    private CruncherAbstract cruncher;
    private FileOutput fileOutput;


    public ResultGetter(Map<String, Future<Map<String, Integer>>> countFutureJob, CruncherAbstract cruncher, FileOutput fileOutput) {
        this.countFutureMap = countFutureJob;
        this.cruncher = cruncher;
        this.fileOutput = fileOutput;
    }

    @Override
    public void run() {

        try {
            for (Map.Entry<String, Future<Map<String, Integer>>> pair : countFutureMap.entrySet()) {
                if (cruncher != null) {
                    cruncher.updateLabel("Crunching " + pair.getKey().split("-arity")[0]);
                    Platform.runLater(() -> {
                        MainView.getInstance().getResults().getItems().remove(pair.getKey());
                        MainView.getInstance().getResults().getItems().add(pair.getKey() + "*");
                    });
                }
                Map<String, Integer> jobDone = countFutureMap.get(pair.getKey()).get();
                fileOutput.getJobsDone().put(pair.getKey(), jobDone);
                if (cruncher != null) {
                    cruncher.updateLabel("");
                    Platform.runLater(() -> {
                        MainView.getInstance().getResults().getItems().remove(pair.getKey() + "*");
                        MainView.getInstance().getResults().getItems().remove(pair.getKey());
                        MainView.getInstance().getResults().getItems().add(pair.getKey());
                    });
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }


    }
}
