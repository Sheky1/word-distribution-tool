package RAF.KiDSDomaci1.model.output;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class FileOutputAbstract implements Runnable{

    private BlockingQueue<Map<String, Future<Map<String, Integer>>>> outputProcessQueue;

    public FileOutputAbstract() {
        this.outputProcessQueue = new LinkedBlockingQueue<>();
    }

    public BlockingQueue<Map<String, Future<Map<String, Integer>>>> getOutputProcessQueue() {
        return outputProcessQueue;
    }
}
