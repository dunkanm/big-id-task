package com.bigid.filesearch.consumer;

import com.bigid.filesearch.producer.TextChunk;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTextChunkProcessor implements TextChunkProcessor {

    private final List<Pattern> patterns;

    public RegexTextChunkProcessor(String searchTerms) {
        this.patterns = compileSearchTerms(searchTerms);
    }

    @Override
    public List<Occurrence> processTextChunk(TextChunk nextChunk) {
        List<Occurrence> occurrences = new ArrayList<>();
        long lineNumber = nextChunk.getLineOffset() + 1;
        for (String line : nextChunk.getLines()) {
            for (Pattern pattern : patterns) {
                Matcher matcher = pattern.matcher(line);
                while (matcher.find()) {
                    occurrences.add(new Occurrence(pattern.pattern(), lineNumber, matcher.start(0)));
                }
            }
            lineNumber++;
        }
        return occurrences;
    }

    private List<Pattern> compileSearchTerms(String searchTerms) {
        List<Pattern> patterns = new ArrayList<>();
        String[] targets = searchTerms.split(",");
        for (String target : targets) {
            patterns.add(Pattern.compile(target));
        }
        return patterns;
    }

}
