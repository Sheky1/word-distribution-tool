package RAF.KiDSDomaci1.model.output;

import RAF.KiDSDomaci1.model.cruncher.CruncherAbstract;

import java.util.Map;
import java.util.concurrent.*;

public class FileOutput extends FileOutputAbstract {
    private CopyOnWriteArrayList<CruncherAbstract> crunchers;
    private ConcurrentHashMap<String, Map<String, Integer>> jobsDone;
    private ConcurrentHashMap<String, Future<Map<String, Integer>>> futures;

    public FileOutput() {
        crunchers = new CopyOnWriteArrayList<>();
        jobsDone = new ConcurrentHashMap<>();
        futures = new ConcurrentHashMap<>();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Map<String, Future<Map<String, Integer>>> countFutureMap = getOutputProcessQueue().take();
                futures.putAll(countFutureMap);
                CruncherAbstract cruncher = null;
                for (Map.Entry<String, Future<Map<String, Integer>>> pair : countFutureMap.entrySet()) {
                    for (CruncherAbstract currentCruncher : getCrunchers())
                        if (currentCruncher.getArity() == Integer.parseInt(pair.getKey().split("-arity")[1]))
                            cruncher = currentCruncher;
                }
                Thread resultGetterThread = new Thread(new ResultGetter(countFutureMap, cruncher, this));
                resultGetterThread.start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Map<String, Integer> take(String fileName) {
        if(fileName.charAt(fileName.length() - 1) == '*') fileName = fileName.substring(0, fileName.length() - 1);
        if(jobsDone.containsKey(fileName)) return jobsDone.get(fileName);
        if (futures.get(fileName).isDone()) {
            return jobsDone.get(fileName);
        } else {
            try {
                return futures.get(fileName).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Map<String, Integer> poll(String fileName) {
        try {
            if(fileName.charAt(fileName.length() - 1) == '*') fileName = fileName.substring(0, fileName.length()-1);
            if(jobsDone.containsKey(fileName)) return jobsDone.get(fileName);
            if (futures.get(fileName).isDone()) return futures.get(fileName).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public CopyOnWriteArrayList<CruncherAbstract> getCrunchers() {
        return crunchers;
    }

    public ConcurrentHashMap<String, Future<Map<String, Integer>>> getFutures() {
        return futures;
    }

    public ConcurrentHashMap<String, Map<String, Integer>> getJobsDone() {
        return jobsDone;
    }
}
