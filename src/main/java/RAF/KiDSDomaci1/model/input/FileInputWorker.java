package RAF.KiDSDomaci1.model.input;

import RAF.KiDSDomaci1.view.MainView;
import javafx.concurrent.Task;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class FileInputWorker extends Task<String> {

    private final FileInput fileInput;

    public FileInputWorker(FileInput fileInput) {
        this.fileInput = fileInput;
    }

    @Override
    public void run() {
        while (true) {
            try {
                updateMessage("Idle");
                File file = fileInput.getFiles().take();
                updateMessage("Reading " + file.getPath().split("\\\\")[file.getPath().split("\\\\").length - 1]);
//                System.out.println("FileInputWorker working on " + file.getPath().split("/")[file.getPath().split("/").length - 1]);

                Future<String> fileJob = MainView.getInstance().getInputThreadPool().submit(new FileInputTask(file));
                String jobDone = fileJob.get();
                if(jobDone == null) break;

                ObjectToProcess objectToProcess = new ObjectToProcess(file.getAbsolutePath(), jobDone);

                fileInput.getCrunchers().forEach(cruncher -> {
                    try {
                        cruncher.getProcessQueue().put(objectToProcess);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
                updateMessage("Idle");

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected String call() throws Exception {
        return null;
    }
}
