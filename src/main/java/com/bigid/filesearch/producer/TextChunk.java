package com.bigid.filesearch.producer;

import java.util.List;

public class TextChunk {

    private long lineOffset;

    private List<String> lines;

    public TextChunk(long offset, List<String> lines) {
        this.lineOffset = offset;
        this.lines = lines;
    }

    public long getLineOffset() {
        return lineOffset;
    }

    public void setLineOffset(long lineOffset) {
        this.lineOffset = lineOffset;
    }

    public List<String> getLines() {
        return lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }

    @Override
    public String toString() {
        return "TextChunk{" +
                "offset=" + lineOffset +
                '}';
    }
}
