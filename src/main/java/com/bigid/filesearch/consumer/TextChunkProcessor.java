package com.bigid.filesearch.consumer;

import com.bigid.filesearch.producer.TextChunk;

import java.util.List;

public interface TextChunkProcessor {

    List<Occurrence> processTextChunk(TextChunk textChunk);
}
