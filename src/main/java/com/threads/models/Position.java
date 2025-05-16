package com.threads.models;

import java.util.Objects;

public class Position {
    private final int x;
    private final int y;
    private final SegmentType type ;

    public Position(int x, int y, SegmentType type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public SegmentType getPositionType() {
        return type;
    }
}