package com.threads.controllers;

import com.threads.models.Position;
import com.threads.models.RoadMap;
import com.threads.models.Vehicle;
import com.threads.strategy.MutualExclusionTemplate;

import java.util.List;
import java.util.Random;

public class VehicleController extends Thread {
    private final Vehicle vehicle;
    private final RoadMap roadMap;
    private final MutualExclusionTemplate mutualExclusion;
    private final Random random = new Random();
    private List<Position> crossingPath;

    public VehicleController(Vehicle vehicle, RoadMap roadMap,
                             MutualExclusionTemplate mutualExclusion) {
        this.vehicle = vehicle;
        this.roadMap = roadMap;
        this.mutualExclusion = mutualExclusion;
    }

    @Override
    public void run() {
        while (vehicle.isActive()) {
            try{
                // Calculate next position

                // For crossings, we need to reserve the entire path

                // Try to move to next position using tryAcquire

                // release position

                // Check if reached exit point

            } catch (Exception e) {
                vehicle.setActive(false);
                throw new RuntimeException(e);
            }
        }

    }
}