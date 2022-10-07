package RAF.KiDSDomaci1.model.cruncher;

import RAF.KiDSDomaci1.app.Config;
import RAF.KiDSDomaci1.model.input.ObjectToProcess;
import RAF.KiDSDomaci1.view.MainView;

import java.util.*;
import java.util.concurrent.RecursiveTask;

public class CruncherTask extends RecursiveTask<Map<String, Integer>> {

    private ObjectToProcess objectToProcess;
    private int startingIndex;
    private int endingIndex;
    private int arity;

    public CruncherTask(ObjectToProcess objectToProcess, int startingIndex, int endingIndex, int arity) {
        this.objectToProcess = objectToProcess;
        this.startingIndex = startingIndex;
        this.endingIndex = endingIndex;
        this.arity = arity;
    }

    @Override
    protected Map<String, Integer> compute() {
        try {
            if (Integer.parseInt(Config.getProperty("counter_data_limit")) < Math.abs(startingIndex - endingIndex)) {
                int mid = Math.abs(startingIndex - endingIndex) / 2;
                while(!Character.isWhitespace(objectToProcess.getContent().charAt(mid))) mid--;

                CruncherTask leftSide = new CruncherTask(objectToProcess, startingIndex, startingIndex + mid, arity);
                CruncherTask rightSide = new CruncherTask(objectToProcess, startingIndex + mid, endingIndex, arity);
                rightSide.fork();

                Map<String, Integer> leftJobDone = leftSide.compute();
                Map<String, Integer> rightJobDone = rightSide.join();

                rightJobDone.keySet().forEach(key -> leftJobDone.put(key, leftJobDone.containsKey(key) ? leftJobDone.get(key) + rightJobDone.get(key) : rightJobDone.get(key)));

                return leftJobDone;
            } else {
                Map<String, Integer> mapToProcess = new HashMap<>();

                LinkedList<String> bag = new LinkedList<>();

                int startOfWord = startingIndex;
                if(Character.isWhitespace(objectToProcess.getContent().charAt(startOfWord))) startOfWord++;
                for (int i = startOfWord; i < endingIndex; i++) {
                    if (Character.isWhitespace(objectToProcess.getContent().charAt(i))) {
                        bag.add(objectToProcess.getContent().substring(startOfWord, i));
                        startOfWord = i + 1;
                        if (bag.size() == arity) {
                            ArrayList<String> sortedWords = new ArrayList<>(bag);
                            Collections.sort(sortedWords);
                            String key = String.join("", sortedWords);
                            mapToProcess.put(key, mapToProcess.containsKey(key) ? mapToProcess.get(key) + 1 : 1);
                            bag.remove(0);
                        }
                    }
                }

                return mapToProcess;
            }
        } catch (OutOfMemoryError e) {
            MainView.getInstance().quitApplication();
        }
        return null;
    }
}
