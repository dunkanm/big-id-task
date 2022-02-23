package com.bigid.filesearch.producer;

import com.bigid.filesearch.Broker;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class TextChunkProducer implements Runnable {

    private static final int CHUNK_SIZE = 1000;
    private final File sourceFile;
    private final Broker broker;
    private long currentOffset = 0;

    public TextChunkProducer(File sourceFile, Broker broker) {
        this.sourceFile = sourceFile;
        this.broker = broker;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new FileReader(sourceFile))) {
            TextChunk textChunk;
            while ((textChunk = getNextChunk(reader)) != null) {
                broker.submitWork(textChunk);
            }
            broker.setFileFinished();
        } catch (IOException e) {
            System.out.println("Could not access source file.");
            e.printStackTrace();
        }
    }

    private TextChunk getNextChunk(BufferedReader reader) throws IOException {
        List<String> lines = new ArrayList<>();
        TextChunk textChunk = null;
        String line;
        int linesRead = 0;
        while (linesRead < CHUNK_SIZE && (line = reader.readLine()) != null) {
            lines.add(line);
            linesRead++;
        }
        if (linesRead > 0) {
            textChunk = new TextChunk(currentOffset, lines);
            currentOffset = currentOffset + linesRead;
            System.out.println("Chunk supplied: " + textChunk);
        }
        return textChunk;
    }
}
