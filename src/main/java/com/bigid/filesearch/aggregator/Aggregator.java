package com.bigid.filesearch.aggregator;

import com.bigid.filesearch.Broker;
import com.bigid.filesearch.consumer.Occurrence;
import com.bigid.filesearch.util.BlockingQueueSpliterator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.StreamSupport;

public class Aggregator implements Callable<Void> {

    private final Broker broker;

    public Aggregator(Broker broker) {
        this.broker = broker;
    }

    @Override
    public Void call() throws Exception {
        Map<String, List<Occurrence.Coordinates>> aggregatedResult = new HashMap<>();
        StreamSupport.stream(new BlockingQueueSpliterator<>(broker.getOutputQueue(), 100L), false)
                .forEach(occurrence -> {
                    List<Occurrence.Coordinates> coordinates = aggregatedResult.get(occurrence.getPattern());
                    if (coordinates == null) {
                        coordinates = new ArrayList<>();
                    }
                    coordinates.add(occurrence.getCoordinates());
                    aggregatedResult.put(occurrence.getPattern(), coordinates);
                });
        aggregatedResult.keySet().forEach(pattern -> System.out.println(pattern + "--> " + aggregatedResult.get(pattern)));
        return null;
    }
}
