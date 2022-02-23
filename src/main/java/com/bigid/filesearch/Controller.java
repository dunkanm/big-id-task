package com.bigid.filesearch;

import com.bigid.filesearch.aggregator.Aggregator;
import com.bigid.filesearch.consumer.Occurrence;
import com.bigid.filesearch.consumer.RegexTextChunkProcessor;
import com.bigid.filesearch.consumer.TextChunkConsumer;
import com.bigid.filesearch.consumer.TextChunkProcessor;
import com.bigid.filesearch.producer.TextChunkProducer;
import com.bigid.filesearch.producer.TextChunk;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

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
        supplierExecutorService.submit(new TextChunkProducer(file, broker));

        ExecutorService matchersExecutorService = Executors.newFixedThreadPool(CONSUMER_THREAD_POOL_SIZE);
        TextChunkProcessor chunkProcessor = new RegexTextChunkProcessor(searchTerms);
        for (int i = 0; i < CONSUMER_THREAD_POOL_SIZE; i++) {
            matchersExecutorService.submit(new TextChunkConsumer(chunkProcessor, broker));
        }

        ExecutorService aggregatorExecutorService = Executors.newSingleThreadExecutor();
        aggregatorExecutorService.submit(new Aggregator(broker));

        matchersExecutorService.shutdown();
        supplierExecutorService.shutdown();
        aggregatorExecutorService.shutdown();
    }

}
