package RAF.KiDSDomaci1.model.input;

import RAF.KiDSDomaci1.model.cruncher.CruncherAbstract;

import java.util.concurrent.CopyOnWriteArrayList;

public abstract class FileInputAbstract implements Runnable {

    CopyOnWriteArrayList<CruncherAbstract> crunchers;

    public FileInputAbstract() {
        crunchers = new CopyOnWriteArrayList<>();
    }

    public CopyOnWriteArrayList<CruncherAbstract> getCrunchers() {
        return crunchers;
    }
}
