package com.bigid.filesearch;

import com.bigid.filesearch.aggregator.Aggregator;
import com.bigid.filesearch.consumer.Occurrence;
import com.bigid.filesearch.consumer.RegexTextChunkProcessor;
import com.bigid.filesearch.consumer.TextChunkConsumer;
import com.bigid.filesearch.consumer.TextChunkProcessor;
import com.bigid.filesearch.producer.TextChunkProducer;
import com.bigid.filesearch.producer.TextChunk;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class Controller {

    private static final int CONSUMER_THREAD_POOL_SIZE = 5;

    private final Broker broker;

    public Controller() {
        BlockingQueue<Occurrence> outputQueue = new LinkedBlockingQueue<>();
        BlockingQueue<TextChunk> workQueue = new LinkedBlockingQueue<>(CONSUMER_THREAD_POOL_SIZE * 2);
        broker = new Broker(workQueue, outputQueue);
    }

    /**
     * Finds all occurrences of search terms in a file
     *
     * @param file file to search in
     * @param searchTerms list of comma separated patterns
     */
    public void startSearch(File file, String searchTerms) {

        ExecutorService supplierExecutorService = Executors.newSingleThreadExecutor();
        supplierExecutorService.execute(new TextChunkProducer(file, broker));
        supplierExecutorService.shutdown();

        ExecutorService matchersExecutorService = Executors.newFixedThreadPool(CONSUMER_THREAD_POOL_SIZE);
        TextChunkProcessor chunkProcessor = new RegexTextChunkProcessor(searchTerms);
        for (int i = 0; i < CONSUMER_THREAD_POOL_SIZE; i++) {
            matchersExecutorService.execute(new TextChunkConsumer(chunkProcessor, broker));
        }
        matchersExecutorService.shutdown();

        ExecutorService aggregatorExecutorService = Executors.newSingleThreadExecutor();
        Future<Map<String, List<Occurrence.Coordinates>>> aggregateFuture = aggregatorExecutorService.submit(new Aggregator(broker));
        aggregatorExecutorService.shutdown();

        try {
            Map<String, List<Occurrence.Coordinates>> result = aggregateFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

}
