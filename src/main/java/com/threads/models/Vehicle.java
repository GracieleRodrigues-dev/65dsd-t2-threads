package com.threads.models;

public class Vehicle {
    private final int id;
    private Position currentPosition;
    private Position nextPosition;
    private final int speed; // milliseconds between moves
    private boolean active;

    public Vehicle(int id, Position startPosition, int speed) {
        this.id = id;
        this.currentPosition = startPosition;
        this.nextPosition = null;
        this.speed = speed;
        this.active = true;
    }

    public int getId() {
        return id;
    }

    public Position getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(Position currentPosition) {
        this.currentPosition = currentPosition;
    }

    public Position getNextPosition() {
        return nextPosition;
    }

    public void setNextPosition(Position nextPosition) {
        this.nextPosition = nextPosition;
    }

    public int getSpeed() {
        return speed;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}