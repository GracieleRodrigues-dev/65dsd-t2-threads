package com.threads.controllers;

import com.threads.models.Position;
import com.threads.models.RoadMap;
import com.threads.models.SegmentType;
import com.threads.models.Vehicle;
import com.threads.services.SseEmitterService;
import com.threads.strategy.MutualExclusionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VehicleController extends Thread {
	private final Vehicle vehicle;
	private final RoadMap roadMap;
	private final MutualExclusionTemplate mutualExclusion;
	private final Random random = new Random();
	private List<Position> crossingPath;
	private final SseEmitterService sseEmitterService;

	public VehicleController(Vehicle vehicle, RoadMap roadMap, MutualExclusionTemplate mutualExclusion,
	        SseEmitterService sseEmitterService
	) {
	    this.vehicle = vehicle;
	    this.roadMap = roadMap;
	    this.mutualExclusion = mutualExclusion;
	    this.sseEmitterService = sseEmitterService;
	    this.start();
	}

	public void notifySSE(Vehicle vehicle) {
		try {
			sseEmitterService.sendPositionUpdate(vehicle);
		} catch (Exception e) {
			System.err.println("Erro ao notificar SSE: " + e.getMessage());
		}
	}

	@Override
	public void run() {
		while (vehicle.isActive()) {
			try {
				//Calculate the next position based on current road/crossing
				Position nextPosition = calculateNextPosition();

				// Special handling when entering a crossing
				if (isEnteringCrossing(nextPosition)) {
					//Calculate full path through the crossing
					List<Position> path = calculateCrossingPath(nextPosition);

					//Try to reserve all positions in the crossing path
					if (!reserveFullPath(path)) {
						//If reservation fails, wait and retry in next iteration
						sleep(vehicle.getSpeed());
						continue;
					}
				}

				//Try to acquire the next position
				if (mutualExclusion.tryAcquire(nextPosition)) {
					// If successful, move the vehicle to new position
					moveVehicle(nextPosition);
				}
				// Respect the vehicle's speed by sleeping
				sleep(vehicle.getSpeed());
			} catch (Exception e) {
				vehicle.setActive(false);
				System.err.println("Erro na thread do veículo: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private Position calculateNextPosition() {
		if (crossingPath != null && !crossingPath.isEmpty()) {
			return crossingPath.get(0);
		}
		return roadMap.getNextVehiclePosition(vehicle.getCurrentPosition());
	}

	private boolean isEnteringCrossing(Position nextPos) {
		if (nextPos == null || crossingPath != null) return false;
		SegmentType nextType = roadMap.getSegment(nextPos.getX(), nextPos.getY());
		return nextType.isCross();
	}

	private List<Position> calculateCrossingPath(Position entryPos) {
		List<Position> path = new ArrayList<>();
		Position current = entryPos;
		while (roadMap.getSegment(current.getX(), current.getY()).isCross()) {
			path.add(current);
			current = roadMap.getNextVehiclePosition(current);
		}
		path.add(current);
		return path;
	}

	private boolean reserveFullPath(List<Position> path) {
		List<Position> reservedPositions = new ArrayList<>();
		for (Position pos : path) {
			if (mutualExclusion.tryAcquire(pos)) {
				reservedPositions.add(pos);
			} else {
				reservedPositions.forEach(mutualExclusion::release);
				return false;
			}
		}
		this.crossingPath = path;
		return true;
	}

	private void moveVehicle(Position newPos) {
		Position current = vehicle.getCurrentPosition();

		System.out.println("Vehicle " + vehicle.getId() + " moving from " +
				current + " to " + newPos);

		mutualExclusion.release(current);
		vehicle.setCurrentPosition(newPos);
		notifySSE(vehicle);

		if (crossingPath != null && !crossingPath.isEmpty()
				&& crossingPath.get(0).equals(newPos)) {
			crossingPath.remove(0);
		}
	}

}
