package com.bigid.filesearch.consumer;

public class Occurrence {

    private final String pattern;
    private final Coordinates coordinates;

    public Occurrence(String substring, long lineNumber, int offset) {
        this.pattern = substring;
        this.coordinates = new Coordinates(lineNumber, offset);
    }

    public record Coordinates(long lineNumber, int offset){}

    public String getPattern() {
        return pattern;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    @Override
    public String toString() {
        return "Occurrence{" +
                "pattern='" + pattern + '\'' +
                ", coordinates=" + coordinates +
                '}';
    }
}
