package com.example.tripchemistry.types;

public enum CityTag {
    QUIET(0),
    LOUD(1),
    ACTIVE(2),
    HISTORY(3),
    MODERN(4),
    FAMOUS(5),
    HIDDEN(6),
    NATURE(7),
    INTERNATIONAL(8);

    private int value;

    public int getValue() {
        return value;
    }
    private CityTag( int value ) {
        this.value = value;
    }
};