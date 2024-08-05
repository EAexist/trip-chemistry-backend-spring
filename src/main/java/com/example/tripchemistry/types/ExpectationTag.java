package com.example.tripchemistry.types;

public enum ExpectationTag {
    HEAL(0),
    COMPACT(1),
    FULLFILL(2),
    MEMORY(3),
    RELAX(4),
    COMFORT(5),
    ADVENTURE(6),
    NEW(7),
    DIGITAL_DETOX(8),
    REST(9),
    VIEW(10),  
    FRIENDSHIP(11);

    private int value;

    public int getValue() {
        return value;
    }
    private ExpectationTag( int value ) {
        this.value = value;
    }
};