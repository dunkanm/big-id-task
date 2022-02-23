package com.bigid.filesearch;

import com.bigid.filesearch.consumer.Occurrence;
import com.bigid.filesearch.producer.TextChunk;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class Broker {

    private final BlockingQueue<TextChunk> workQueue;
    private final BlockingQueue<Occurrence> outputQueue;
    private final AtomicReference<Boolean> isFileFinished;

    public Broker(BlockingQueue<TextChunk> workQueue, BlockingQueue<Occurrence> outputQueue) {
        this.workQueue = workQueue;
        this.outputQueue = outputQueue;
        this.isFileFinished = new AtomicReference<>(false);
    }

    public void submitWork(TextChunk chunk) {
        try {
            workQueue.put(chunk);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public TextChunk pollForWork(long milliseconds) {
        try {
            return workQueue.poll(milliseconds, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public boolean hasMoreWork() {
        return !workQueue.isEmpty();
    }

    public void submitResult(Occurrence occurrence) {
        outputQueue.add(occurrence);
    }

    public void submitResult(List<Occurrence> occurrences) {
        outputQueue.addAll(occurrences);
    }

    public Occurrence getOutputItem() {
        try {
            while(!isFileFinished() || !outputQueue.isEmpty() || !workQueue.isEmpty()) {
                Occurrence occurrence = outputQueue.poll(100, TimeUnit.MILLISECONDS);
                if (occurrence == null) {
                    continue;
                }
                return occurrence;
            }
            return outputQueue.poll(200, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isFileFinished() {
        return isFileFinished.get();
    }

    public void setFileFinished() {
        isFileFinished.set(true);
    }
}
