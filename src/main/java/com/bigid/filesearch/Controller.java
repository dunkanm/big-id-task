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
    public static final int PRODUCER_THREAD = 1;
    public static final int AGGREGATOR_THREAD = 1;

    private final Broker broker;

    public Controller() {
        BlockingQueue<Occurrence> outputQueue = new LinkedBlockingQueue<>();
        BlockingQueue<TextChunk> workQueue = new LinkedBlockingQueue<>(CONSUMER_THREAD_POOL_SIZE * 2);
        broker = new Broker(workQueue, outputQueue, CONSUMER_THREAD_POOL_SIZE);
    }

    /**
     * Finds all occurrences of search terms in a file
     *  @param file file to search in
     * @param searchTerms list of comma separated patterns
     * @return
     */
    public Map<String, List<Occurrence.Coordinates>> startSearch(File file, String searchTerms) throws ExecutionException, InterruptedException {

        TextChunkProcessor chunkProcessor = new RegexTextChunkProcessor(searchTerms);
        ExecutorService controllerThreadExecutor = Executors.newFixedThreadPool(CONSUMER_THREAD_POOL_SIZE + PRODUCER_THREAD + AGGREGATOR_THREAD);

        controllerThreadExecutor.submit(new TextChunkProducer(file, broker));

        for (int i = 0; i < CONSUMER_THREAD_POOL_SIZE; i++) {
            controllerThreadExecutor.submit(new TextChunkConsumer(chunkProcessor, broker));
        }

        Future<Map<String, List<Occurrence.Coordinates>>> aggregateFuture = controllerThreadExecutor.submit(new Aggregator(broker));
        controllerThreadExecutor.shutdown();

        return aggregateFuture.get();
    }

}
