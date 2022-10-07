package RAF.KiDSDomaci1.model.output;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class SingleOutputWorker implements Callable<Map<String, Integer>> {

    private final Map<String, Integer> jobToSort;

    public SingleOutputWorker(Map<String, Integer> jobToSort) {
        this.jobToSort = jobToSort;
    }

    @Override
    public Map<String, Integer> call() throws Exception {
        return jobToSort.entrySet().stream().sorted((i1, i2) -> i2.getValue().compareTo(i1.getValue())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }
}
