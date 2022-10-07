package RAF.KiDSDomaci1.model.cruncher;

import RAF.KiDSDomaci1.model.input.ObjectToProcess;
import javafx.concurrent.Task;
import javafx.scene.text.Text;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class CruncherAbstract extends Task<String> {

    private BlockingQueue<ObjectToProcess> processQueue;
    private int arity;

    public CruncherAbstract(int arity) {
        this.arity = arity;
        this.processQueue = new LinkedBlockingQueue<>();
    }

    public BlockingQueue<ObjectToProcess> getProcessQueue() {
        return processQueue;
    }

    public void updateLabel(String text) {
        updateMessage(text);
    }

    public int getArity() {
        return arity;
    }
}
