package com.example.tripchemistry.types;

public enum ActivityTag {
    PHOTO(0),
    INSTA(1),
    NETWORK(2),
    EXTREME(3),
    SWIM(4),
    DRIVE(5),
    WALK(6),
    THEMEPARK(7),
    MARKET(8),
    HOTEL(9),
    VLOG(10),
    EAT(11),
    BAR(12),
    CAFE(13),
    SHOPPING(14),
    SHOW(15),
    MUSEUM(16);

    private int value;

    public int getValue() {
        return value;
    }
    private ActivityTag( int value ) {
        this.value = value;
    }
};