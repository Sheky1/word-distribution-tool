package RAF.KiDSDomaci1.model.output;

import RAF.KiDSDomaci1.view.MainView;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class SumOutputWorker implements Callable<Map<String, Integer>> {
    @Override
    public Map<String, Integer> call() throws Exception {
        Map<String, Integer> allJobs = new HashMap<>();
        for(String item: MainView.getInstance().getResults().getSelectionModel().getSelectedItems()) {
            Map<String, Integer> selectedJobDone = MainView.getInstance().getFileOutput().take(item);
            for(String key: selectedJobDone.keySet()) {
                if(allJobs.containsKey(key)) allJobs.put(key, allJobs.get(key) + selectedJobDone.get(key));
                else allJobs.put(key, selectedJobDone.get(key));
            }
        }
        return allJobs;
    }
}
