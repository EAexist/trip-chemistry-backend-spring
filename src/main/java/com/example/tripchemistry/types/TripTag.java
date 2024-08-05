package com.example.tripchemistry.types;

public enum TripTag {
    DEFAULT(0),
    PHOTO(1),
    EAT(2),
    FRIENDSHIP(3),
    PHYSICAL(4),
    REST(5),
    INFLUENCER(6),
    COFFEE(7),
    CULTURE(8),
    ADVENTURE(9),
    PASSION(10),
    REFRESH(11);

    private int value;

    public int getValue() {
        return value;
    }
    private TripTag( int value ) {
        this.value = value;
    }
};