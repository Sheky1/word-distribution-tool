package RAF.KiDSDomaci1.model.input;

import java.io.File;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.util.concurrent.Callable;

public class FileInputTask implements Callable<String> {

    private File file;

    public FileInputTask(File file) {
        this.file = file;
    }

    @Override
    public String call() throws Exception {
        return Files.asCharSource(file, Charsets.UTF_8).read();
    }
}
