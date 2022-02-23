package com.bigid.filesearch.consumer;

import com.bigid.filesearch.Broker;
import com.bigid.filesearch.producer.TextChunk;

import java.util.List;
import java.util.concurrent.Callable;

public class TextChunkConsumer implements Callable<Void> {

    private Broker broker;
    private TextChunkProcessor processor;

    public TextChunkConsumer(TextChunkProcessor processor, Broker broker) {
        this.processor = processor;
        this.broker = broker;
    }

    @Override
    public Void call() throws Exception {
        TextChunk nextChunk;
        while (!broker.isFileFinished() || broker.hasMoreWork()) {
            nextChunk = broker.pollForWork(100);
            if (nextChunk != null) {
                List<Occurrence> occurrences = processor.processTextChunk(nextChunk);
                broker.submitResult(occurrences);
                System.out.println("Chunk processed: " + nextChunk.getLineOffset());
            }
        }
        return null;
    }

}
