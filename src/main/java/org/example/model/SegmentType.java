package org.example.model;

public enum SegmentType {
    EMPTY(0, "Nada"),
    ROAD_UP(1, "Estrada Cima"),
    ROAD_RIGHT(2, "Estrada Direita"),
    ROAD_DOWN(3, "Estrada Baixo"),
    ROAD_LEFT(4, "Estrada Esquerda"),
    CROSS_UP(5, "Cruzamento Cima"),
    CROSS_RIGHT(6, "Cruzamento Direita"),
    CROSS_DOWN(7, "Cruzamento Baixo"),
    CROSS_LEFT(8, "Cruzamento Esquerda"),
    CROSS_UP_RIGHT(9, "Cruzamento Cima e Direita"),
    CROSS_UP_LEFT(10, "Cruzamento Cima e Esquerda"),
    CROSS_RIGHT_DOWN(11, "Cruzamento Direita e Baixo"),
    CROSS_DOWN_LEFT(12, "Cruzamento Baixo e Esquerda");

    private final int value;
    private final String description;

    SegmentType(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public static SegmentType fromValue(int value) {
        for (SegmentType type : SegmentType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid segment type value: " + value);
    }

    public boolean isRoad() {
        return this == ROAD_UP || this == ROAD_RIGHT ||
                this == ROAD_DOWN || this == ROAD_LEFT;
    }

    public boolean isCross() {
        return this.getValue() >= 5 && this.getValue() <= 12;
    }
}