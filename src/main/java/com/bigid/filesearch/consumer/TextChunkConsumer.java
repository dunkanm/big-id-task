package com.bigid.filesearch.consumer;

import com.bigid.filesearch.Broker;
import com.bigid.filesearch.producer.TextChunk;

import java.util.List;

public class TextChunkConsumer implements Runnable {

    private final Broker broker;
    private final TextChunkProcessor processor;

    public TextChunkConsumer(TextChunkProcessor processor, Broker broker) {
        this.processor = processor;
        this.broker = broker;
    }

    @Override
    public void run() {
        TextChunk nextChunk;
        while (!broker.isFileFinished() || broker.hasMoreWork()) {
            nextChunk = broker.pollForWork(100);
            if (nextChunk != null) {
                List<Occurrence> occurrences = processor.processTextChunk(nextChunk);
                broker.submitResult(occurrences);
                System.out.println("Chunk processed: " + nextChunk.getLineOffset());
            }
        }
    }

}
