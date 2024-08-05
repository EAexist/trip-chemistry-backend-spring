package com.example.tripchemistry.types;

public enum NationId {
    kr("한국"),
    hk("홍콩"),
    jp("일본");

    private final String label;

    NationId(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }
};