package com.bigid.filesearch.aggregator;

import com.bigid.filesearch.Broker;
import com.bigid.filesearch.consumer.Occurrence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class Aggregator implements Callable<Map<String, List<Occurrence.Coordinates>>> {

    private final Broker broker;

    public Aggregator(Broker broker) {
        this.broker = broker;
    }

    @Override
    public Map<String, List<Occurrence.Coordinates>> call() throws Exception {
        Map<String, List<Occurrence.Coordinates>> aggregatedResult = new HashMap<>();
        Occurrence nextOccurrence;
        while ((nextOccurrence = broker.getOutputItem()) != null) {
            String pattern = nextOccurrence.getPattern();
            if (!aggregatedResult.containsKey(pattern)) {
                aggregatedResult.put(nextOccurrence.getPattern(), new ArrayList<>());
            }
            List<Occurrence.Coordinates> coordinates = aggregatedResult.get(pattern);
            coordinates.add(nextOccurrence.getCoordinates());
        }

        aggregatedResult.keySet().forEach(pattern -> System.out.println(pattern + "--> " + aggregatedResult.get(pattern)));

        return aggregatedResult;
    }
}
