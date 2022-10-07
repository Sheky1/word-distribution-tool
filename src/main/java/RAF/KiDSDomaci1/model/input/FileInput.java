package RAF.KiDSDomaci1.model.input;

import RAF.KiDSDomaci1.app.Config;
import RAF.KiDSDomaci1.model.Directory;
import RAF.KiDSDomaci1.model.Disk;
import RAF.KiDSDomaci1.view.FileInputView;
import RAF.KiDSDomaci1.view.MainView;
import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class FileInput extends FileInputAbstract {

    private BlockingQueue<File> files;
    private CopyOnWriteArrayList<Directory> directories;
    private final Disk disk;
    private FileInputView fileInputView;
    private AtomicBoolean working;
    private final Object workingLock = new Object();
    private Map<String, Long> oldFiles;

    public FileInput(Disk disk) {
        this.disk = disk;
        files = new LinkedBlockingQueue<>();
        directories = new CopyOnWriteArrayList<>();
        working = new AtomicBoolean(false);
        oldFiles = new ConcurrentHashMap<>();
    }

    public Disk getDisk() {
        return disk;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (workingLock) {
                try {
                    if (!working.get()) {
                        workingLock.wait();
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (Directory directory : directories) traverseDirectory(directory);
            try {
                Thread.sleep(Integer.parseInt(Config.getProperty("file_input_sleep_time")));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void traverseDirectory(Directory directory) {
        try {
            Files.walk(Paths.get(directory.getDirectory().getPath()))
                    .filter(Files::isRegularFile)
                    .forEach(filePath -> {
                        String fullPath = filePath.toString();
                        String extension = fullPath.substring(fullPath.length() - 4);
                        if(extension.equals(".txt")) {
                            File file = new File(filePath.toString());
                            if (oldFiles.containsKey(filePath.toString())) {
                                if (file.lastModified() != oldFiles.get(file.getPath())) {
                                    try {
                                        files.put(file);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    oldFiles.put(file.getPath(), file.lastModified());
                                }
                            } else {
                                try {
                                    files.put(file);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                oldFiles.put(filePath.toString(), file.lastModified());
                            }
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BlockingQueue<File> getFiles() {
        return files;
    }

    public CopyOnWriteArrayList<Directory> getDirectories() {
        return directories;
    }

    public FileInputView getFileInputView() {
        return fileInputView;
    }

    public void setFileInputView(FileInputView fileInputView) {
        this.fileInputView = fileInputView;
    }

    public AtomicBoolean getWorking() {
        return working;
    }

    public Object getWorkingLock() {
        return workingLock;
    }
}
